# dropwizard-pac4j-demo
Dropwizard demo to test the dropwizard-pac4j and jax-rs-pac4j security library


This application demonstrates several ways of integrating pac4j with
dropwizard, depending on the way dropwizard is used:
- to protect views (web pages) served via JAX-RS
- to protect a REST API served via JAX-RS (optionally with a single page frontend)
- to protect both servlets and JAX-RS resources

# Pac4J with Views (not REST API)

One way of integrating pac4j with dropwizard is when protecting an applications
with views returned by the JAX-RS resources.

Run the application with `mvn compile exec:exec` and access
`http://localhost:8080`.

`FormClient` and `FacebookClient` are demonstrated.

# Pac4J for a REST API

TO BE DONE

# Pac4J for both servlets and JAX-RS

TO BE DONE