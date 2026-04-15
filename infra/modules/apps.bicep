// Container Apps environment, managed identity, and all application services

param location string
param acrName string
param acrLoginServer string
param imageTag string

param postgresHost string

@secure()
param postgresAdminPassword string

param cosmosAccountName string

@secure()
param cosmosPrimaryKey string

param redisHost string

@secure()
param redisPassword string

param eventHubsBootstrap string

@secure()
param eventHubsSasConnectionString string

@secure()
param jwtSecret string

param smtpHost string
param smtpPort string
param smtpUser string

@secure()
param smtpPass string

param notificationFromEmail string

// ── Derived values ─────────────────────────────────────────────────────────────

// MongoDB URIs are composed from the Cosmos account credentials and per-service database name.
var cosmosBase = 'mongodb://${cosmosAccountName}:${cosmosPrimaryKey}@${cosmosAccountName}.mongo.cosmos.azure.com:10255'
var cosmosOpts = 'ssl=true&replicaSet=globaldb&retrywrites=false&maxIdleTimeMS=120000&appName=@${cosmosAccountName}@'

// Kafka SASL JAAS config required for Azure Event Hubs Kafka endpoint
var kafkaJaas = 'org.apache.kafka.common.security.plain.PlainLoginModule required username="$ConnectionString" password="${eventHubsSasConnectionString}";'

// ── Existing ACR (created by acr.bicep in the same RG) ────────────────────────

resource acr 'Microsoft.ContainerRegistry/registries@2023-11-01-preview' existing = {
  name: acrName
}

// ── Observability ──────────────────────────────────────────────────────────────

resource logAnalytics 'Microsoft.OperationalInsights/workspaces@2023-09-01' = {
  name: 'ims-logs'
  location: location
  properties: {
    sku: {
      name: 'PerGB2018'
    }
    retentionInDays: 30
  }
}

// ── Container Apps environment ─────────────────────────────────────────────────

resource caEnv 'Microsoft.App/managedEnvironments@2024-03-01' = {
  name: 'ims-env'
  location: location
  properties: {
    appLogsConfiguration: {
      destination: 'log-analytics'
      logAnalyticsConfiguration: {
        customerId: logAnalytics.properties.customerId
        sharedKey: logAnalytics.listKeys().primarySharedKey
      }
    }
  }
}

// ── Managed identity for ACR pull ──────────────────────────────────────────────

resource identity 'Microsoft.ManagedIdentity/userAssignedIdentities@2023-01-31' = {
  name: 'ims-identity'
  location: location
}

// AcrPull role definition ID (built-in)
var acrPullRoleId = '7f951dda-4ed3-4680-a7ca-43fe172d538d'

resource acrPullAssignment 'Microsoft.Authorization/roleAssignments@2022-04-01' = {
  name: guid(acr.id, identity.id, acrPullRoleId)
  scope: acr
  properties: {
    roleDefinitionId: subscriptionResourceId('Microsoft.Authorization/roleDefinitions', acrPullRoleId)
    principalId: identity.properties.principalId
    principalType: 'ServicePrincipal'
  }
}

// ── Shared secrets (same set for all backend services) ────────────────────────

var backendSecrets = [
  { name: 'db-pass', value: postgresAdminPassword }
  { name: 'redis-pass', value: redisPassword }
  { name: 'kafka-jaas', value: kafkaJaas }
  { name: 'jwt-secret', value: jwtSecret }
  { name: 'smtp-pass', value: smtpPass }
]

// ── Registry configuration (reused per app) ────────────────────────────────────

var registryConfig = [
  {
    server: acrLoginServer
    identity: identity.id
  }
]

// ── Backend microservices ──────────────────────────────────────────────────────
//
// pgDb     – PostgreSQL database name; empty string = service does not use Postgres
// mongoDb  – CosmosDB (MongoDB) database name; empty string = service does not use MongoDB
// useRedis – whether the service uses Redis

var backendServices = [
  {
    name: 'user-service'
    image: 'ims-user-service'
    port: 8081
    pgDb: 'ims_user'
    mongoDb: ''
    useRedis: true
  }
  {
    name: 'warehouse-service'
    image: 'ims-warehouse-service'
    port: 8082
    pgDb: 'ims_warehouse'
    mongoDb: 'ims_warehouse'
    useRedis: false
  }
  {
    name: 'market-service'
    image: 'ims-market-service'
    port: 8083
    pgDb: 'ims_market'
    mongoDb: 'ims_market'
    useRedis: false
  }
  {
    name: 'transfer-service'
    image: 'ims-transfer-service'
    port: 8084
    pgDb: 'ims_transfer'
    mongoDb: 'ims_transfer'
    useRedis: false
  }
  {
    name: 'scheduling-service'
    image: 'ims-scheduling-service'
    port: 8085
    pgDb: 'ims_scheduling'
    mongoDb: ''
    useRedis: false
  }
  {
    name: 'reporting-service'
    image: 'ims-reporting-service'
    port: 8086
    pgDb: ''
    mongoDb: 'ims_reporting'
    useRedis: false
  }
  {
    name: 'transaction-service'
    image: 'ims-transaction-service'
    port: 8087
    pgDb: 'ims_transaction'
    mongoDb: ''
    useRedis: false
  }
  {
    name: 'notification-service'
    image: 'ims-notification-service'
    port: 8088
    pgDb: ''
    mongoDb: ''
    useRedis: true
  }
]

resource backendApp 'Microsoft.App/containerApps@2024-03-01' = [
  for svc in backendServices: {
    name: svc.name
    location: location
    dependsOn: [acrPullAssignment]
    identity: {
      type: 'UserAssigned'
      userAssignedIdentities: {
        '${identity.id}': {}
      }
    }
    properties: {
      managedEnvironmentId: caEnv.id
      configuration: {
        ingress: {
          external: false
          targetPort: svc.port
          transport: 'http'
        }
        registries: registryConfig
        secrets: backendSecrets
      }
      template: {
        containers: [
          {
            name: svc.name
            image: '${acrLoginServer}/${svc.image}:${imageTag}'
            resources: {
              cpu: json('1.0')
              memory: '2.0Gi'
            }
            env: concat(
              // Common env vars for all backend services
              [
                { name: 'SPRING_PROFILES_ACTIVE', value: 'prod' }
                { name: 'JWT_SECRET', secretRef: 'jwt-secret' }
                { name: 'KAFKA_SERVERS', value: eventHubsBootstrap }
                { name: 'SPRING_KAFKA_PROPERTIES_SASL_MECHANISM', value: 'PLAIN' }
                { name: 'SPRING_KAFKA_PROPERTIES_SECURITY_PROTOCOL', value: 'SASL_SSL' }
                { name: 'SPRING_KAFKA_PROPERTIES_SASL_JAAS_CONFIG', secretRef: 'kafka-jaas' }
                { name: 'SMTP_HOST', value: smtpHost }
                { name: 'SMTP_PORT', value: smtpPort }
                { name: 'SMTP_USER', value: smtpUser }
                { name: 'SMTP_PASS', secretRef: 'smtp-pass' }
                { name: 'NOTIFICATION_FROM_EMAIL', value: notificationFromEmail }
              ],
              // PostgreSQL env vars — only for services that use it
              svc.pgDb != ''
                ? [
                    {
                      name: 'DB_URL'
                      // Azure PostgreSQL requires SSL
                      value: 'jdbc:postgresql://${postgresHost}:5432/${svc.pgDb}?sslmode=require'
                    }
                    { name: 'DB_USER', value: 'ims' }
                    { name: 'DB_PASS', secretRef: 'db-pass' }
                  ]
                : [],
              // MongoDB env vars — only for services that use it
              svc.mongoDb != ''
                ? [{ name: 'MONGO_URI', value: '${cosmosBase}/${svc.mongoDb}?${cosmosOpts}' }]
                : [],
              // Redis env vars — only for services that use it
              // Azure Redis requires TLS on port 6380
              svc.useRedis
                ? [
                    { name: 'REDIS_HOST', value: redisHost }
                    { name: 'REDIS_PORT', value: '6380' }
                    { name: 'SPRING_DATA_REDIS_SSL_ENABLED', value: 'true' }
                    { name: 'SPRING_DATA_REDIS_PASSWORD', secretRef: 'redis-pass' }
                  ]
                : []
            )
          }
        ]
        scale: {
          minReplicas: 1
          maxReplicas: 3
        }
      }
    }
  }
]

// ── API Gateway ────────────────────────────────────────────────────────────────
// External ingress — routes inbound requests to internal backend services.
// Service-to-service URLs use ACA's internal DNS (<app-name> on port 80).

resource apiGateway 'Microsoft.App/containerApps@2024-03-01' = {
  name: 'api-gateway'
  location: location
  dependsOn: [acrPullAssignment]
  identity: {
    type: 'UserAssigned'
    userAssignedIdentities: {
      '${identity.id}': {}
    }
  }
  properties: {
    managedEnvironmentId: caEnv.id
    configuration: {
      ingress: {
        external: true
        targetPort: 8080
        transport: 'http'
      }
      registries: registryConfig
      secrets: [
        { name: 'jwt-secret', value: jwtSecret }
      ]
    }
    template: {
      containers: [
        {
          name: 'api-gateway'
          image: '${acrLoginServer}/api-gateway:${imageTag}'
          resources: {
            cpu: json('0.5')
            memory: '1.0Gi'
          }
          env: [
            { name: 'SPRING_PROFILES_ACTIVE', value: 'prod' }
            { name: 'JWT_SECRET', secretRef: 'jwt-secret' }
            // Internal ACA DNS: <app-name> resolves to the Container App within the same environment
            { name: 'USER_SERVICE_URL', value: 'http://user-service' }
            { name: 'WAREHOUSE_SERVICE_URL', value: 'http://warehouse-service' }
            { name: 'MARKET_SERVICE_URL', value: 'http://market-service' }
            { name: 'TRANSFER_SERVICE_URL', value: 'http://transfer-service' }
            { name: 'SCHEDULING_SERVICE_URL', value: 'http://scheduling-service' }
            { name: 'REPORTING_SERVICE_URL', value: 'http://reporting-service' }
            { name: 'TRANSACTION_SERVICE_URL', value: 'http://transaction-service' }
          ]
        }
      ]
      scale: {
        minReplicas: 1
        maxReplicas: 3
      }
    }
  }
}

// ── Frontend ───────────────────────────────────────────────────────────────────

resource frontendApp 'Microsoft.App/containerApps@2024-03-01' = {
  name: 'frontend'
  location: location
  dependsOn: [acrPullAssignment]
  identity: {
    type: 'UserAssigned'
    userAssignedIdentities: {
      '${identity.id}': {}
    }
  }
  properties: {
    managedEnvironmentId: caEnv.id
    configuration: {
      ingress: {
        external: true
        targetPort: 80
        transport: 'http'
      }
      registries: registryConfig
    }
    template: {
      containers: [
        {
          name: 'frontend'
          image: '${acrLoginServer}/ims-frontend:${imageTag}'
          resources: {
            cpu: json('0.25')
            memory: '0.5Gi'
          }
          env: [
            {
              name: 'API_URL'
              value: 'https://${apiGateway.properties.configuration.ingress.fqdn}'
            }
          ]
        }
      ]
      scale: {
        minReplicas: 1
        maxReplicas: 3
      }
    }
  }
}

// ── Outputs ────────────────────────────────────────────────────────────────────

output gatewayUrl string = 'https://${apiGateway.properties.configuration.ingress.fqdn}'
output frontendUrl string = 'https://${frontendApp.properties.configuration.ingress.fqdn}'
