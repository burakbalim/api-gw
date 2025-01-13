
# API Gateway Project

## Overview
This project is designed and developed an API Gateway that operates both in monolithic and distributed environments. The system is highly extensible, secure, and performant, providing configuration flexibility and supporting various data sources.

### Functional Requirements:
- **Middleware Integration**: Easy integration of custom middleware.
- **Virtual Endpoints Support**: Virtualization of different services through a single entry point.
- **OAuth2 Authentication**:
  - **Comprehensive grant type support**: Authorization Code, Implicit, Password, Client Credentials.
  - **Basic Auth support**: Username and password authentication.
- **Rate Limiting**: Protection against excessive request load.
- **Dynamic Configuration Support**:
  - Ability to read configurations from files or MongoDB.
  - Easy integration of different data sources.
- **Powerful Configuration Capabilities**:
  - Configuration of desired clients with any grant type.

### Non-functional Requirements:
- **Performance**: Fast response times under high traffic.
- **Security**: Ensuring data and authentication security.
- **Flexibility**: Quick and easy transition between different data sources.
- **Scalability**: Seamless operation in both monolithic and distributed environments.

### High-level Design
The system consists of the following components:
1. **API Gateway Core**:
   - A central component directing incoming requests.
2. **Middleware Layer**:
   - Defines special processing steps to be executed on each request.
3. **Authentication Module**:
   - **OAuth2** enhanced authentication support:
     - **Grant Type Support**: Authorization Code, Implicit, Password, Client Credentials.
     - **Basic Authentication**.
4. **Configuration Manager**:
   - Ability to load configurations from files or MongoDB.

**Diagram (default)**:
- Gateway > Middleware > Authentication > Backend Services.


### Data Model
**Configuration Data Model**:
- `config_source`: Configuration source (e.g., file, MongoDB).
- `policies`: Rate limit rules and other rules.
- `auth_providers`: List of OAuth2 authentication providers.
- `clients`: Application authentication information and grant types.
- `virtual_endpoints`: Provide virtual endpoints via custom definition (implement VirtualEndpointProvider, it will support javascript in the future)
- `middleware`: Provide middleware via custom definition (implement related MiddleweProvider, it will support javascript in the future)

### APIs
- **OAuth2 Token Endpoint**:
  - `http://localhost:8092/gw/oauth2/token?grant_type=...`
  - Supports different grant types like Authorization Code, Implicit, Password, Client Credentials through the `grant_type` parameter.

- **Basic Authentication Endpoints**:
  - `@PostMapping("api/keys/{username}")`: Creates user definition.
  - `@PostMapping("api/apis/keys/basic/{username}")`: Defines Basic Auth keys.
  - `@GetMapping("api/keys")`: Lists existing keys.
  - `@DeleteMapping("api/key/{key-id}")`: Deletes a specific key.

- **Dynamic Client Configuration Endpoint**:
  - Allows configuration with desired clients working with specified grant types.

### System Components
- **Core Gateway**: Central component directing incoming requests.
- **Configuration Manager**:
  - Loads configurations from files or MongoDB.
- **Middleware**: Special processing steps.
- **Authentication Module**:
  - Provides OAuth2-based authentication via Spring Authorization Server.
  - Supports Basic Authentication.
- **Rate Limiter**: Controls rate limits against excessive requests.

### Workflow
1. The user sends a request to the API Gateway.
2. Special processing is done at the Middleware layer.
3. The Authentication Module verifies the request.
   - Uses OAuth2 endpoint for token generation.
   - Verifies username and password for Basic Authentication.
4. Rate limit check is performed.
5. The request is directed to the appropriate backend service according to the configuration.

### Scalability and Optimization
- **Scalability**:
  - Multiple Gateway instances can run in a distributed architecture.
- **Optimization**:
  - **Caching** for quick access to configuration data.
  - Efficient algorithms for rate limit controls.

### Error Handling and Fault Tolerance
- **Error Handling**:
  - User-friendly error messages for incomplete or incorrect requests.
- **Fault Tolerance**:
  - Failover mechanisms for the Configuration Manager.
  - Rate limit system resilient to high loads.

### Security Considerations
- **OAuth2 Usage**:
  - Comprehensive authentication and authorization.
  - Encryption and secure access for Basic Authentication.
- **Rate limiting**: Protection against DDoS attacks.
- **Secure storage** of configuration data.

### Trade-offs and Alternatives
- **Trade-offs**:
  - While MongoDB configuration offers flexibility, it may introduce more latency compared to file-based configurations.
- **Alternatives**:
  - Instead of developing its own OAuth2 solution, the system uses Spring Authorization Server.

### Conclusion
This API Gateway design combines extensibility, security, and performance to provide an effective solution in both monolithic and distributed environments. With its flexible configuration and middleware support, it meets long-term requirements efficiently.

### Future Plan

Routing Module
- **Static Configuration:**:
    - Currently, the routing module performs request forwarding to specific backend services via static configuration. This means that requests are directed to pre-defined routes based on their configured destinations.
- **Future Enhancements:**
  - Integration with Cloud Providers:
    - In the future, this routing module can be integrated with a cloud provider, such as a service discovery service, to dynamically learn about the status and locations of services. This will enable the system to become more dynamic and scalable.
  - Client-Based Load Balancing:
    - The module could also support client-based load balancing. Based on client requests, the routing module can distribute more requests to the appropriate servers. This can enhance performance and improve the overall efficiency of the system.
