# Use Keycloak 18.0.2 as the base image
FROM quay.io/keycloak/keycloak:21.1.2

# Switch to root to install custom provider
USER root

# Add the custom JAR to the Keycloak providers directory
COPY target/keycloak-federation-jar-with-dependencies.jar /opt/keycloak/providers/

# Set permissions for the JAR file
RUN chown -R 1000:1000 /opt/keycloak/providers/keycloak-federation-jar-with-dependencies.jar

# Switch back to the Keycloak user
USER 1000

# Configure the Keycloak startup to use the default settings
ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start-dev"]
