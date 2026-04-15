targetScope = 'subscription'

// ── Parameters ─────────────────────────────────────────────────────────────────

@description('Azure region for all resources.')
param location string = 'swedencentral'

@description('Container image tag to deploy (git SHA or "latest").')
param imageTag string = 'latest'

@secure()
@description('Password for the PostgreSQL admin account.')
param postgresAdminPassword string

@secure()
@description('JWT signing secret – must be at least 32 characters.')
param jwtSecret string

@description('SMTP host for the notification service.')
param smtpHost string = ''

@description('SMTP port for the notification service.')
param smtpPort string = '587'

@description('SMTP username for the notification service.')
param smtpUser string = ''

@secure()
@description('SMTP password for the notification service.')
param smtpPass string = ''

@description('From-address for outbound notification emails.')
param notificationFromEmail string = 'noreply@ims.local'

// ── Derived names ──────────────────────────────────────────────────────────────

var rgName       = 'ims'
var uniqueSuffix = take(uniqueString(subscription().subscriptionId, rgName), 6)
var acrName      = 'imsacr${uniqueSuffix}'

// ── Resource group ─────────────────────────────────────────────────────────────

resource rg 'Microsoft.Resources/resourceGroups@2023-07-01' = {
  name: rgName
  location: location
}

// ── Data layer (Postgres, Cosmos, Redis, Event Hubs) ──────────────────────────

module data 'modules/data.bicep' = {
  name: 'ims-data'
  scope: rg
  params: {
    location: location
    uniqueSuffix: uniqueSuffix
    postgresAdminPassword: postgresAdminPassword
  }
}

// ── Container registry ─────────────────────────────────────────────────────────

module acr 'modules/acr.bicep' = {
  name: 'ims-acr'
  scope: rg
  params: {
    location: location
    acrName: acrName
  }
}

// ── Container Apps environment and services ────────────────────────────────────

module apps 'modules/apps.bicep' = {
  name: 'ims-apps'
  scope: rg
  dependsOn: [data, acr]
  params: {
    location: location
    acrName: acrName
    acrLoginServer: acr.outputs.loginServer
    imageTag: imageTag
    postgresHost: data.outputs.postgresHost
    postgresAdminPassword: postgresAdminPassword
    cosmosAccountName: data.outputs.cosmosAccountName
    cosmosPrimaryKey: data.outputs.cosmosPrimaryKey
    redisHost: data.outputs.redisHost
    redisPassword: data.outputs.redisPassword
    eventHubsBootstrap: data.outputs.eventHubsBootstrap
    eventHubsSasConnectionString: data.outputs.eventHubsSasConnectionString
    jwtSecret: jwtSecret
    smtpHost: smtpHost
    smtpPort: smtpPort
    smtpUser: smtpUser
    smtpPass: smtpPass
    notificationFromEmail: notificationFromEmail
  }
}

// ── Outputs ────────────────────────────────────────────────────────────────────

output acrName string = acrName
output acrLoginServer string = acr.outputs.loginServer
output gatewayUrl string = apps.outputs.gatewayUrl
output frontendUrl string = apps.outputs.frontendUrl
