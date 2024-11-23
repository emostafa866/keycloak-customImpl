package userFederation;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.*;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomUserAdapter extends AbstractUserAdapterFederatedStorage {
    private Logger log= LoggerFactory.getLogger(CustomUserAdapter.class);

    private final Customer customer;
    String FN;
    String LN;

    public CustomUserAdapter(KeycloakSession session, RealmModel realm, ComponentModel model, Customer customer) {
        super(session, realm, model);
        this.customer = customer;
        if (customer == null) {
            log.error("Customer is null!");
        } else {
            log.info("Customer initialized: " + customer.toString());
            log.info("Customer firstName : " + customer.getFirstName());
            log.info("Customer2 firstName : " + this.customer.getFirstName());
            log.info("Customer2 msisdn : " + this.customer.getMsisdn());
             FN = this.customer.getFirstName();
             LN = this.customer.getMsisdn();
        }
    }

    public Customer getCustomer() {
        return customer;
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
    public void setMsisdn(String msisdn) {}

    @Override
    public Map<String, List<String>> getAttributes() {
        Map<String, List<String>> attributes = new HashMap<>();
        log.info("firstName attribute added to the adapter: " + customer.getFirstName());

        // Add the attributes
        attributes.put("firstName", Collections.singletonList(customer.getFirstName() == null?"mustafa":customer.getFirstName()));
        attributes.put("lastName", Collections.singletonList(customer.getLastName()));
        attributes.put("email", Collections.singletonList(customer.getEmail()));
        attributes.put("msisdn", Collections.singletonList(customer.getMsisdn()));

        log.info("Attributes added to the adapter: " + attributes);

        // Add any additional attributes from federated storage
        Map<String, List<String>> federatedAttributes = super.getAttributes();
        if (federatedAttributes != null) {
            attributes.putAll(federatedAttributes);
        }

        return attributes;
    }

    @Override
    public String getFirstAttribute(String name) {
        if ("FN".equals(name)) {
            return FN == null ? "Unknown" : FN;
        } else if ("LN".equals(name)) {
            return LN == null ? "Unknown" : LN;
        } else if ("msisdn".equals(name)) {
            return customer.getMsisdn() == null ? "Unknown" : customer.getMsisdn();
        } else if ("email".equals(name)) {
            return customer.getEmail() == null ? "Unknown" : customer.getEmail();
        }
        return super.getFirstAttribute(name);
    }


}
