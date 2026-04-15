// Data layer: PostgreSQL, Cosmos DB (MongoDB), Redis, Event Hubs

param location string
param uniqueSuffix string

@secure()
param postgresAdminPassword string

// ── Resource names ─────────────────────────────────────────────────────────────

var postgresName = 'ims-postgres-${uniqueSuffix}'
var cosmosName   = 'ims-cosmos-${uniqueSuffix}'
var redisName    = 'ims-redis-${uniqueSuffix}'
var ehNamespace  = 'ims-eventhubs-${uniqueSuffix}'

// ── PostgreSQL Flexible Server ─────────────────────────────────────────────────

resource postgres 'Microsoft.DBforPostgreSQL/flexibleServers@2023-12-01-preview' = {
  name: postgresName
  location: location
  sku: {
    name: 'Standard_B2ms'
    tier: 'Burstable'
  }
  properties: {
    administratorLogin: 'ims'
    administratorLoginPassword: postgresAdminPassword
    version: '16'
    storage: {
      storageSizeGB: 32
    }
    backup: {
      backupRetentionDays: 7
      geoRedundantBackup: 'Disabled'
    }
    highAvailability: {
      mode: 'Disabled'
    }
    authConfig: {
      activeDirectoryAuth: 'Disabled'
      passwordAuth: 'Enabled'
    }
  }
}

// Allow Azure-internal traffic (covers Container Apps egress IPs)
resource postgresFirewall 'Microsoft.DBforPostgreSQL/flexibleServers/firewallRules@2023-12-01-preview' = {
  parent: postgres
  name: 'AllowAzureServices'
  properties: {
    startIpAddress: '0.0.0.0'
    endIpAddress: '0.0.0.0'
  }
}

var pgDatabases = [
  'ims_user'
  'ims_warehouse'
  'ims_market'
  'ims_transfer'
  'ims_scheduling'
  'ims_transaction'
]

resource pgDatabase 'Microsoft.DBforPostgreSQL/flexibleServers/databases@2023-12-01-preview' = [
  for db in pgDatabases: {
    parent: postgres
    name: db
    properties: {
      charset: 'UTF8'
      collation: 'en_US.utf8'
    }
  }
]

// ── Azure Cosmos DB for MongoDB (serverless) ───────────────────────────────────

resource cosmos 'Microsoft.DocumentDB/databaseAccounts@2024-05-15' = {
  name: cosmosName
  location: location
  kind: 'MongoDB'
  properties: {
    databaseAccountOfferType: 'Standard'
    capabilities: [
      { name: 'EnableMongo' }
      { name: 'EnableServerless' }
    ]
    locations: [
      {
        locationName: location
        failoverPriority: 0
        isZoneRedundant: false
      }
    ]
    consistencyPolicy: {
      defaultConsistencyLevel: 'Session'
    }
    apiProperties: {
      serverVersion: '7.0'
    }
  }
}

var cosmosDatabases = [
  'ims_warehouse'
  'ims_market'
  'ims_transfer'
  'ims_reporting'
]

resource cosmosDb 'Microsoft.DocumentDB/databaseAccounts/mongodbDatabases@2024-05-15' = [
  for db in cosmosDatabases: {
    parent: cosmos
    name: db
    properties: {
      resource: {
        id: db
      }
    }
  }
]

// ── Azure Cache for Redis ──────────────────────────────────────────────────────

resource redis 'Microsoft.Cache/redis@2024-03-01' = {
  name: redisName
  location: location
  properties: {
    sku: {
      name: 'Basic'
      family: 'C'
      capacity: 1
    }
    enableNonSslPort: false
    minimumTlsVersion: '1.2'
    redisVersion: '6'
  }
}

// ── Azure Event Hubs namespace (Kafka surface) ─────────────────────────────────

resource ehns 'Microsoft.EventHub/namespaces@2024-01-01' = {
  name: ehNamespace
  location: location
  sku: {
    // Standard required for Kafka endpoint
    name: 'Standard'
    tier: 'Standard'
    capacity: 1
  }
  properties: {
    kafkaEnabled: true
    minimumTlsVersion: '1.2'
  }
}

// Event Hub names mirror the Kafka topic names used by the services.
// Azure Event Hubs allows periods in entity names.
var eventHubs = [
  { name: 'ims.user.events', partitions: 3 }
  { name: 'ims.warehouse.events', partitions: 6 }
  { name: 'ims.warehouse.commands', partitions: 6 }
  { name: 'ims.market.events', partitions: 6 }
  { name: 'ims.market.commands', partitions: 6 }
  { name: 'ims.transfer.events', partitions: 6 }
  { name: 'ims.scheduling.events', partitions: 3 }
  { name: 'ims.reporting.events', partitions: 3 }
  { name: 'ims.notification.events', partitions: 3 }
  { name: 'ims.user.events.dlq', partitions: 3 }
  { name: 'ims.warehouse.events.dlq', partitions: 3 }
  { name: 'ims.market.events.dlq', partitions: 3 }
  { name: 'ims.transfer.events.dlq', partitions: 3 }
]

resource hub 'Microsoft.EventHub/namespaces/eventhubs@2024-01-01' = [
  for eh in eventHubs: {
    parent: ehns
    name: eh.name
    properties: {
      partitionCount: eh.partitions
      messageRetentionInDays: 7
    }
  }
]

resource ehAuthRule 'Microsoft.EventHub/namespaces/authorizationRules@2024-01-01' existing = {
  parent: ehns
  name: 'RootManageSharedAccessKey'
}

// ── Outputs ────────────────────────────────────────────────────────────────────

output postgresHost string = postgres.properties.fullyQualifiedDomainName

output cosmosAccountName string = cosmos.name

@secure()
output cosmosPrimaryKey string = cosmos.listKeys().primaryMasterKey

output redisHost string = redis.properties.hostName

@secure()
output redisPassword string = redis.listKeys().primaryKey

// Bootstrap endpoint for Spring Kafka (SASL_SSL)
output eventHubsBootstrap string = '${ehns.name}.servicebus.windows.net:9093'

@secure()
output eventHubsSasConnectionString string = ehAuthRule.listKeys().primaryConnectionString
