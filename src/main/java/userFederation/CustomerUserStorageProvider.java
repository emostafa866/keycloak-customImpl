package userFederation;


import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.*;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.naming.InitialContext;
import java.net.URLEncoder;


public class CustomerUserStorageProvider implements UserStorageProvider,
        UserLookupProvider, CredentialInputValidator {
    private  KeycloakSession session;
    private  ComponentModel model;
    private Logger log1= LoggerFactory.getLogger(CustomerUserStorageProvider.class);
    private InitialContext initCtx;

    private final BCryptPasswordEncoder passwordEncoder;
    private final String customerServiceUrl = "http://host.docker.internal:8080/api/v1/customers";
    private final RestTemplate restTemplate;

    public CustomerUserStorageProvider(KeycloakSession session, ComponentModel model) {
        this.session=session;
        this.model=model;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.restTemplate = new RestTemplate();
        try	{
            initCtx=new InitialContext();
        }
        catch(Exception e)	{
            log1.error("Cannot create InitialContext",e);
        }
    }

    @Override
    public void close() {
        log1.info("Closing FederationDB Provider");

    }


    @Override
    public boolean isConfiguredFor(RealmModel arg0, UserModel arg1, String credentialType) {

        return supportsCredentialType(credentialType);
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput credentialInput) {
        log1.info("enter isValid method with user :: "+user.getEmail() + "and password ::"+credentialInput.getChallengeResponse()+" and userId::  "+user.getId() +"  and"+user.getUsername());
        if (!(credentialInput instanceof UserCredentialModel)) return false;
        String rawPassword = credentialInput.getChallengeResponse();
        String email = user.getUsername();
        log1.info("email from isValid is ::  "+email);
        String url = UriComponentsBuilder.fromHttpUrl(customerServiceUrl)
                .pathSegment(email)
                .toUriString();

        try {

//             Use RestTemplate to make the GET request
            log1.info("[[ isValid Method :: URL is :: ]]"+url);
            Customer customer = restTemplate.getForObject(url, Customer.class);
            log1.info("customer msisdn from is valid method is ::"+customer.getMsisdn()+" and customer first name is :: "+customer.getFirstName());
            log1.info("password check result is ::"+passwordEncoder.matches(rawPassword, customer.getPassword()));
            if (customer != null) {

            }
            return customer != null && passwordEncoder.matches(rawPassword, customer.getPassword());
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions appropriately
            return false;
        }

    }

    @Override
    public boolean supportsCredentialType(String arg0) {

        return arg0.equals(CredentialModel.PASSWORD);
    }


    @Override
    public UserModel getUserByEmail(String email, RealmModel realmModel) {
        log1.info("enter getUserByEmail with email ::"+email);
        String cleanedEmail = email.contains(":") ? email.split(":")[2] : email;
        String url = UriComponentsBuilder.fromHttpUrl(customerServiceUrl)
                .pathSegment(cleanedEmail)
                .toUriString();

        log1.info("the url is ::"+url );


        try {
            // Use RestTemplate to make the GET request
            Customer customer = restTemplate.getForObject(url, Customer.class);

            if (customer == null) return null;
            log1.info("customer email is ::"+customer.getEmail()+" and customer first name is "+customer.getFirstName());

            return new CustomUserAdapter(session, realmModel, model, customer);
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions appropriately
            return null;
        }
    }



    @Override
    public UserModel getUserById(String s, RealmModel realmModel) {
        return getUserByEmail(realmModel, s);
    }

//    @Override
//    public UserModel getUserByUsername(RealmModel realm, String username) {
//        // TODO Auto-generated method stub
//        return getUserByEmail(realm, username);
//    }

    @Override
    public UserModel getUserByUsername(String s, RealmModel realmModel) {
        return getUserByEmail(realmModel, s);
    }

}
