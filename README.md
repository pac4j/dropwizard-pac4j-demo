# dropwizard-pac4j-demo

> This demo secures a Dropwizard application with **[dropwizard-pac4j](https://github.com/pac4j/dropwizard-pac4j)**, the Dropwizard implementation of **[pac4j](https://github.com/pac4j/pac4j)**, the security engine for Java.
> If it is useful to you, please ⭐ **[star pac4j on GitHub](https://github.com/pac4j/pac4j)**: it helps other developers discover it!

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
