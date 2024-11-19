package userFederation;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.*;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.logging.Logger;

public class CustomUserFederationProvider implements UserStorageProvider, UserLookupProvider, CredentialInputValidator {

    private final KeycloakSession session;
    private final ComponentModel model;
    private final BCryptPasswordEncoder passwordEncoder;
    private final String customerServiceUrl = "http://localhost:8080/api/v1/customers";
    private final RestTemplate restTemplate;

    Logger logger = Logger.getLogger(CustomUserFederationProvider.class.getName());

    public CustomUserFederationProvider(KeycloakSession session, ComponentModel model) {
        this.session = session;
        this.model = model;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.restTemplate = new RestTemplate();
    }

    @Override
    public UserModel getUserById(RealmModel realm, String id) {
        return getUserByUsername(realm, id);
    }

    @Override
    public UserModel getUserByUsername(RealmModel realmModel, String email) {
        return getUserByEmail(realmModel, email);
    }

    @Override
    public UserModel getUserByEmail(RealmModel realmModel, String email) {
        String url = UriComponentsBuilder.fromHttpUrl(customerServiceUrl)
                .pathSegment(email)
                .toUriString();

        try {
            // Use RestTemplate to make the GET request
            Customer customer = restTemplate.getForObject(url, Customer.class);

            if (customer == null) return null;

            return new CustomUserAdapter(session, realmModel, model, customer);
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions appropriately
            return null;
        }
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return credentialType.equals(CredentialModel.PASSWORD);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        return supportsCredentialType(credentialType);
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput credentialInput) {
        if (!(credentialInput instanceof UserCredentialModel)) return false;
        String rawPassword = credentialInput.getChallengeResponse();
        String url = UriComponentsBuilder.fromHttpUrl(customerServiceUrl)
                .pathSegment(user.getEmail())
                .toUriString();

        try {
            // Use RestTemplate to make the GET request
            Customer customer = restTemplate.getForObject(url, Customer.class);

            return customer != null && passwordEncoder.matches(rawPassword, customer.getPassword());
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions appropriately
            return false;
        }
    }

    @Override
    public void close() {
        // No explicit closing required for RestTemplate
    }

    @Override
    public void preRemove(RealmModel realm) {
        UserStorageProvider.super.preRemove(realm);
    }

    @Override
    public void preRemove(RealmModel realm, GroupModel group) {
        UserStorageProvider.super.preRemove(realm, group);
    }

    @Override
    public void preRemove(RealmModel realm, RoleModel role) {
        UserStorageProvider.super.preRemove(realm, role);
    }
}
