# Azure Container Apps Deployment

This document covers the Azure infrastructure, how to do the one-time setup, and how the CI/CD pipeline deploys the application.

## Architecture

All resources are deployed into a resource group named `ims` on your existing Azure subscription.

| Layer | Resource | SKU | Notes |
|---|---|---|---|
| Compute | Azure Container Apps | Consumption | 10 apps in one environment |
| Registry | Azure Container Registry | Basic | Images pulled via managed identity |
| Database | Azure Database for PostgreSQL Flexible Server | Burstable B2ms, v16 | Single server, 6 databases |
| Document DB | Azure Cosmos DB for MongoDB | Serverless | 4 databases |
| Cache | Azure Cache for Redis | Basic C1 | TLS only, port 6380 |
| Messaging | Azure Event Hubs | Standard | Kafka surface, 13 topics |
| Observability | Log Analytics Workspace | PerGB2018 | 30-day retention |

### Container Apps

| App | Ingress | CPU | Memory | Replicas |
|---|---|---|---|---|
| `api-gateway` | External (HTTPS) | 0.5 | 1.0 Gi | 1–3 |
| `user-service` | Internal | 1.0 | 2.0 Gi | 1–3 |
| `warehouse-service` | Internal | 1.0 | 2.0 Gi | 1–3 |
| `market-service` | Internal | 1.0 | 2.0 Gi | 1–3 |
| `transfer-service` | Internal | 1.0 | 2.0 Gi | 1–3 |
| `scheduling-service` | Internal | 1.0 | 2.0 Gi | 1–3 |
| `reporting-service` | Internal | 1.0 | 2.0 Gi | 1–3 |
| `transaction-service` | Internal | 1.0 | 2.0 Gi | 1–3 |
| `notification-service` | Internal | 1.0 | 2.0 Gi | 1–3 |
| `frontend` | External (HTTPS) | 0.25 | 0.5 Gi | 1–3 |

Internal services communicate via ACA's built-in DNS: `http://<app-name>` resolves within the same environment.

### Bicep file structure

```
infra/
  main.bicep              # Subscription-scoped entry point; creates the RG
  modules/
    data.bicep            # PostgreSQL, Cosmos DB, Redis, Event Hubs
    acr.bicep             # Azure Container Registry
    apps.bicep            # Container Apps environment and all services
```

Resource names include a 6-character suffix derived from the subscription ID to guarantee global uniqueness (e.g. `imsacr<suffix>`, `ims-postgres-<suffix>`).

---

## One-time setup

### 1. Create a service principal for GitHub Actions (OIDC)

```bash
az ad sp create-for-rbac \
  --name ims-github-actions \
  --role Contributor \
  --scopes /subscriptions/<SUBSCRIPTION_ID>
```

Note the `clientId`, `tenantId`, and `subscriptionId` from the output.

### 2. Add a federated credential

Go to **Azure Portal → Azure Active Directory → App registrations → ims-github-actions → Certificates & secrets → Federated credentials → Add credential**.

| Field | Value |
|---|---|
| Federated credential scenario | GitHub Actions deploying Azure resources |
| Organisation | your GitHub username or org |
| Repository | `ims` |
| Entity type | Branch |
| Branch | `main` |

### 3. Add GitHub secrets

Go to **GitHub → repository → Settings → Secrets and variables → Actions**.

| Secret | Description |
|---|---|
| `AZURE_CLIENT_ID` | Service principal client ID from step 1 |
| `AZURE_TENANT_ID` | Azure AD tenant ID from step 1 |
| `AZURE_SUBSCRIPTION_ID` | Target subscription ID |
| `POSTGRES_ADMIN_PASSWORD` | PostgreSQL admin password — min 8 chars, must contain uppercase, number, and symbol |
| `JWT_SECRET` | JWT signing key — min 32 characters |
| `SMTP_PASS` | SMTP password (leave empty if not using email) |

### 4. Add GitHub variables (optional)

Go to **GitHub → repository → Settings → Secrets and variables → Actions → Variables**.

| Variable | Default | Description |
|---|---|---|
| `AZURE_LOCATION` | `swedencentral` | Azure region for all resources |
| `SMTP_HOST` | _(empty)_ | SMTP hostname for notification emails |
| `SMTP_PORT` | `587` | SMTP port |
| `SMTP_USER` | _(empty)_ | SMTP username |
| `NOTIFICATION_FROM_EMAIL` | `noreply@ims.local` | From address on outbound emails |

---

## CI/CD pipeline

### ci.yml — build and test

Runs on every push and pull request.

1. Build and test backend (Maven, with a Postgres service container)
2. Build and test frontend (npm, Angular)

### deploy.yml — build images and deploy

Runs automatically after CI passes on `main`, or manually via **Actions → Deploy → Run workflow**.

```
1. Azure OIDC login
2. az deployment sub create (Bicep)
      → creates/updates all Azure resources
      → outputs ACR login server and app URLs
3. docker build + push (9 backend images + frontend)
      → tagged as :<sha> and :latest
4. az containerapp update for each app
      → sets the new image tag
      → sets DEPLOYMENT_TAG env var to force a new revision
5. Print frontend and gateway URLs to the job summary
```

The Bicep deployment is idempotent. On the first run, Container Apps are created with the target image tag before images exist; `az containerapp update` in step 4 triggers the revision that actually pulls the now-available images.

---

## Kafka configuration

Azure Event Hubs exposes a Kafka-compatible endpoint. The services connect using SASL/SSL without any code changes — Spring Boot's environment variable binding maps the following env vars directly to Kafka client properties:

| Env var | Value |
|---|---|
| `KAFKA_SERVERS` | `<namespace>.servicebus.windows.net:9093` |
| `SPRING_KAFKA_PROPERTIES_SASL_MECHANISM` | `PLAIN` |
| `SPRING_KAFKA_PROPERTIES_SECURITY_PROTOCOL` | `SASL_SSL` |
| `SPRING_KAFKA_PROPERTIES_SASL_JAAS_CONFIG` | PLAIN login module with the Event Hubs connection string as the password |

Event Hub names match the Kafka topic names defined in the service configs (Azure Event Hubs allows periods in entity names).

---

## Redis configuration

Azure Cache for Redis enforces TLS. Two extra env vars are injected alongside the existing `REDIS_HOST`:

| Env var | Value |
|---|---|
| `REDIS_PORT` | `6380` (TLS port) |
| `SPRING_DATA_REDIS_SSL_ENABLED` | `true` |
| `SPRING_DATA_REDIS_PASSWORD` | primary access key (injected from a Container App secret) |

---

## PostgreSQL configuration

Azure Database for PostgreSQL requires SSL. The JDBC URL is constructed as:

```
jdbc:postgresql://<host>:5432/<database>?sslmode=require
```

The admin username is `ims`. The password is passed via the `DB_PASS` Container App secret sourced from `POSTGRES_ADMIN_PASSWORD`.

---

## Manual deployment

To deploy from your local machine:

```bash
az login
az deployment sub create \
  --name ims-manual \
  --location swedencentral \
  --template-file infra/main.bicep \
  --parameters \
    postgresAdminPassword="<password>" \
    jwtSecret="<secret>" \
    imageTag="latest"
```

To check the deployed URLs after deployment:

```bash
az deployment sub show \
  --name ims-manual \
  --query "properties.outputs" \
  --output table
```
