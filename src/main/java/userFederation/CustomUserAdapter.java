package userFederation;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.*;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

public class CustomUserAdapter extends AbstractUserAdapterFederatedStorage {
    private final Customer customer;

    public CustomUserAdapter(KeycloakSession session, RealmModel realm, ComponentModel model, Customer customer) {
        super(session, realm, model);
        this.customer = customer;
    }

    @Override
    public String getUsername() {
        return customer.getEmail();
    }

    @Override
    public String getEmail() {
        return customer.getEmail();
    }

    @Override
    public String getFirstName() {
        return customer.getFirstName();
    }

    @Override
    public String getLastName() {
        return customer.getLastName();
    }

    @Override
    public void setUsername(String username) {
        // Not required for read-only federation
    }
    public String getMsisdn() {
        return customer.getMsisdn();
    }
}
