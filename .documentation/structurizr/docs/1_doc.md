# 

## Introduction and Goals
The game is a multiplayer online strategy game inspired by Tribal Wars, where players build villages, train armies, and compete for dominance in world. The system must support thousands of concurrent players, offer real-time interactions, and be scalable for future expansions.

### Requirements Overview
Features:
- Core Gameplay: Village building, resource management, army training, and strategic battles.
- Player Interaction: Alliance formation, diplomacy, and trading between players.
- Scalability: Handle up to 1000 concurrent users on a single server.
- Accessibility: Playable on desktop (browser-based).
- Fairness and Security: Ensure balanced gameplay with anti-cheating mechanisms.

### Quality Goals

| Goal        | Motivation/description                                            | 
|-------------|-------------------------------------------------------------------|
| Security    | Protect against cheating, unauthorized access, and data breaches. |
| Reliability | System ensuring minimal downtime during server maintenance.       |


### Stakeholders

| Role/Name           | Expectations                                                             |
|---------------------|--------------------------------------------------------------------------|
| Players             | Expect engaging gameplay and fair rules.                                 |
| Developers          | Need a maintainable and extensible codebase for future updates.          |
| Game Administrators | Tools to manage game worlds, monitor player activity, and enforce rules. |


## Architecture Constraints
### Technical Constraints

| Constraint          | Description                                                                                                |
|---------------------|------------------------------------------------------------------------------------------------------------|
| Database Technology | Integration with NoSQL solutions (e.g., MongoDB) for high-performance tasks like caching and leaderboards. |
| Observability       | System  must send metrics for further analysis                                                             |

### Organizational Constraints

| Constraint          | Description                                                                                                |
|---------------------|------------------------------------------------------------------------------------------------------------|

### Conventions

| Constraint                 | Description                                                          |
|----------------------------|----------------------------------------------------------------------|
| Architecture documentation | Terminology and structure according to the arc42 template.           | 
| Java applications          | Application written in Ports & Adapters (aka Hexagonal) architecture |


## Context and Scope
![](embed:systemContext_gameSystem)

## Solution Strategy

| Goal                 | Description                                                             |
|----------------------|-------------------------------------------------------------------------|
| Security             | Implement OAuth2 for secure player authentication                       |
| Development Strategy | Use GitHub Actions to automate testing, build and deployment pipelines. |
| Deployment Strategy  | Staging Environment or testing before pushing to production.            |

## Building Block View
Diagram from 3.1. section displaying all systems:
![](embed:systemContext_gameSystem)

| System                                      | Description                                                                    |
|---------------------------------------------|--------------------------------------------------------------------------------|
| Game system                                 | Main system responsible for gameplay.                                          |
| Identity and Access Management (IAM) system | System responsible for authentication and user management.                     |
| Observability System                        | System responsible for collecting telemetry signals: traces, metrics and logs. |


### Level 1 - Game system
The system architecture consists of three main components: the frontend, backend, and supporting infrastructure.
![](embed:container_gameSystem)

| System or components      | Description                                                                                                  |
|---------------------------|--------------------------------------------------------------------------------------------------------------|
| Frontend                  | Developed using Angular, providing a dynamic and responsive user interface for web-based access to the game. |
| Backend                   | The backend is implemented in Spring Framework                                                               |
| supporting infrastructure | The system uses MongoDB as the primary database. Additionally Apache Kafka is utilized as a messaging system |

To model the domain, the DDD (Domain-Driven Design) approach was applied, complemented by Event Storming. More information: [Event Storming](#game-system-modeling)

#### Level 2 - Backend
// TODO
#### Level 2 - Frontend
// :)

### Level 1 - Identity and Access Management (IAM) system
The system integrates Keycloak as the Identity and Access Management (IAM) solution, eliminating the need to develop custom authentication and authorization mechanisms.
This choice reduces development effort, enhances security, and ensures compliance with modern authentication standards like OAuth2 and OpenID Connect.

![](embed:container_iam)

### Level 1 - Observability System
The observability system is designed to provide comprehensive monitoring and troubleshooting capabilities by collecting, processing, and visualizing telemetry data. The core of the system leverages the OpenTelemetry (OTel) Collector to gather signals from various components of the application, including metrics, traces, and logs.
All observability data is integrated into Grafana, providing a unified dashboard for:
- Real-time metrics visualization (Prometheus).
- Viewing and analyzing distributed traces (Tempo).
- Querying and correlating logs (Loki).

![](embed:container_observabilitySystem)

## Runtime View
// TODO
## Deployment View
Deployment diagram - dev:
![](embed:development_dev)

## Cross-cutting Concepts

### Clean Architecture
Architecture is described in book: *Get Your Hands Dirty on Clean Architecture, by Tom Hombergs*.

## Architecture Decisions
[Link](/workspace/decisions)

## Quality Requirements

## Risks and Technical Debts
### Units, Buildings & Map 
I have decided to hardcode these values directly into the application because they are highly unlikely to change in the foreseeable future.
Additionally, I want to ensure that these values are not cached, as they are used throughout the application and should always remain consistent and readily accessible. 
This decision carries the risk of requiring manual code modifications if the values need to be updated, which could potentially introduce errors or require additional effort in the future.
However, given the current requirements and the expected stability of these values, I consider this trade-off acceptable.

## Glossary

| Term        | Definition                |
|-------------|---------------------------|
| Tribal Wars | InnoGames game from 2003. |
