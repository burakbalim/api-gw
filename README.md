## API Gateway

[Design And Requirements](./design.md)

#### Architecture Diagram

![API Gateway Architecture](./module_diagram.svg)

#### Components Overview

1. **Middleware Service**  
   Handles common operations like authentication, authorization, caching, and data transformations.

2. **Path Customization Service**  
   Manages dynamic routes and URIs based on user or request type.

3. **Policy Enforcement Engine**  
   Enforces system rules, including access control, rate limiting, and data validation.

4. **Virtual Endpoint Registry**  
   Manages virtual endpoints representing backend microservices, simplifying routing.

5. **Request Dispatcher**  
   Analyzes incoming requests and directs them to the appropriate backend service.

6. **Request Handler Service**  
   Prepares and processes incoming requests, ensuring they are ready for backend services.

7. **OAuth2 Service**  
   Manages dynamic credentials and secure authentication flows using Spring Authorization Server.

8. **Event Gateway Proxy**  
   Handles asynchronous communication, such as logging, event subscriptions, and statistics.

9. **API Gateway Controller**  
   Serves as the entry point for all API requests, handling validation and routing.

10. **Backend Microservices**  
   Independent services responsible for executing specific business logic.

11. **Endpoint Discovery Service**  
    Facilitates automatic discovery of backend services, enabling dynamic updates to the gateway.

#### How to Use
