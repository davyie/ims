// Azure Container Registry — images are pulled by Container Apps via managed identity

param location string
param acrName string

resource acr 'Microsoft.ContainerRegistry/registries@2023-11-01-preview' = {
  name: acrName
  location: location
  sku: {
    name: 'Basic'
  }
  properties: {
    // Managed-identity pull used by Container Apps; admin credentials not needed
    adminUserEnabled: false
  }
}

output loginServer string = acr.properties.loginServer
output acrId string = acr.id
