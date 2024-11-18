workspace {
    name "Game system"
    description "description"

    !docs docs
    !adrs docs/adrs
    !identifiers hierarchical

    model {
        user = person "User"
        gameSystem = softwareSystem "Game system" {
            frontend = container "Game service Frontend"
            backend = container "Game service Backend" {
                description "description"
                technology "Spring"
            }
            database = container "MongoDB"
            kafka = container "Kafka"

            frontend -> backend "Uses"
            backend -> database "Uses"
            backend -> kafka "Uses"
        }
        iam = softwareSystem "Identity and Access Management (IAM)" {
                keycloak = container "Keycloak"
                keycloakDatabase = container "Keycloak Database"

                keycloak -> keycloakDatabase "Uses"
        }
        observabilitySystem = softwareSystem "Observability System" {
            otelCollector = container "otel Collector"
            prometheus = container "prometheus"
            grafana = container "grafana"
            zipkin = container "zipkin"
            tempo = container "tempo"
            loki = container "loki"

            prometheus -> otelCollector "Scrape metrics"
            otelCollector -> zipkin "Send traces"
            otelCollector -> tempo "Send traces"
            otelCollector -> loki "Send logs"

            grafana -> loki "Get data"
            grafana -> prometheus "Get data"
            grafana -> tempo "Get data"
        }

        user -> iam.keycloak "Authenticate"
        user -> gameSystem.frontend "Uses"

        gameSystem.backend -> observabilitySystem.otelCollector "Sent metrics"
        gameSystem.backend -> iam.keycloak "Take keys"

        dev = deploymentEnvironment "Development" {
            deploymentNode "Developer Laptop" {
                containerInstance gameSystem.frontend
                containerInstance gameSystem.backend
                deploymentNode "Docker" {
                    containerInstance gameSystem.database
                    containerInstance gameSystem.kafka

                    containerInstance iam.keycloak
                    containerInstance iam.keycloakDatabase

                    containerInstance observabilitySystem.otelCollector
                    containerInstance observabilitySystem.prometheus
                    containerInstance observabilitySystem.grafana
                    containerInstance observabilitySystem.zipkin
                    containerInstance observabilitySystem.tempo
                    containerInstance observabilitySystem.loki
                }
            }
        }
    }

    views {
        theme default
        systemContext gameSystem "systemContext_gameSystem" {
            include *
        }
        container gameSystem "container_gameSystem" {
            include *
        }
        container observabilitySystem "container_observabilitySystem" {
            include "element.parent==observabilitySystem"
        }
        container iam "container_iam" {
            include "element.parent==iam"
            include user
        }
        deployment * dev "development_dev"{
            include *
        }
    }
}