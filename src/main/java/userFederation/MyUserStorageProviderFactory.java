package userFederation;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.storage.UserStorageProviderFactory;
import org.springframework.web.client.RestTemplate;

public class MyUserStorageProviderFactory implements UserStorageProviderFactory<CustomUserFederationProvider> {

    public static final String PROVIDER_NAME = "custom-user-federation-provider";


    public MyUserStorageProviderFactory() {
        // Default constructor
    }


    @Override
    public CustomUserFederationProvider create(KeycloakSession session, ComponentModel model) {
        return new CustomUserFederationProvider(session, model);
    }


    @Override
    public String getId() {
        return PROVIDER_NAME;
    }

    @Override
    public void init(org.keycloak.Config.Scope config) {
        // Initialize any factory-specific resources here if needed
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Post-initialization actions, if needed
    }

    @Override
    public void close() {
        // Clean up resources if needed
    }
}
