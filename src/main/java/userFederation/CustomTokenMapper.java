package userFederation;

import org.keycloak.models.*;
import org.keycloak.protocol.oidc.mappers.AbstractOIDCProtocolMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAccessTokenMapper;
import org.keycloak.protocol.oidc.mappers.OIDCIDTokenMapper;
import org.keycloak.protocol.oidc.mappers.UserInfoTokenMapper;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CustomTokenMapper extends AbstractOIDCProtocolMapper implements OIDCAccessTokenMapper, OIDCIDTokenMapper, UserInfoTokenMapper {
    private Logger log= LoggerFactory.getLogger(CustomTokenMapper.class);

    public static final String PROVIDER_ID = "custom-token-mapper";

    @Override
    protected void setClaim(IDToken token, ProtocolMapperModel mappingModel, UserSessionModel userSession,
                            KeycloakSession session, ClientSessionContext clientSessionCtx) {
        // Retrieve your custom information from the Customer model
        Map<String, Object> otherClaims = token.getOtherClaims();

        if (userSession != null && userSession.getUser() != null) {
            log.info("Processing user session: " + userSession.getUser().getUsername());
            String username = userSession.getUser().getUsername();
            String msisdn = userSession.getUser().getFirstAttribute("msisdn");
            String customer_First_Name = userSession.getUser().getFirstAttribute("FN");

            username = (username != null) ? username : "Unknown";
            msisdn = (msisdn != null) ? msisdn : "Unknown";
            customer_First_Name = (customer_First_Name != null) ? customer_First_Name : "Unknown";

//            Map<String, Object> otherClaims = token.getOtherClaims();
            if (otherClaims != null) {
                otherClaims.put("Email", username);
                otherClaims.put("MSISDN", msisdn);
                otherClaims.put("customer-firstName", customer_First_Name);
            }else {
                log.error("Token's other claims map is null.");
            }
        } else {
            // Log an error or handle it gracefully
            log.error("User session or user data is null.");
        }

    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        List<ProviderConfigProperty> configProperties = new ArrayList<>();

        ProviderConfigProperty addToIdToken = new ProviderConfigProperty();
        addToIdToken.setName("include.id.token");
        addToIdToken.setLabel("Add to ID Token");
        addToIdToken.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        addToIdToken.setDefaultValue("true");
        addToIdToken.setHelpText("Include the claim in the ID Token.");
        configProperties.add(addToIdToken);

        ProviderConfigProperty addToAccessToken = new ProviderConfigProperty();
        addToAccessToken.setName("include.access.token");
        addToAccessToken.setLabel("Add to Access Token");
        addToAccessToken.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        addToAccessToken.setDefaultValue("true");
        addToAccessToken.setHelpText("Include the claim in the Access Token.");
        configProperties.add(addToAccessToken);

        ProviderConfigProperty addToUserInfo = new ProviderConfigProperty();
        addToUserInfo.setName("include.user.info");
        addToUserInfo.setLabel("Add to UserInfo");
        addToUserInfo.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        addToUserInfo.setDefaultValue("true");
        addToUserInfo.setHelpText("Include the claim in the UserInfo endpoint.");
        configProperties.add(addToUserInfo);

        return configProperties;
    }

    @Override
    public String getDisplayCategory() {
        return "Token Mapper";
    }

    @Override
    public String getDisplayType() {
        return "Custom Token Mapper";
    }

    @Override
    public String getHelpText() {
        return "Adds custom claims to the token.";
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public AccessToken transformAccessToken(AccessToken token, ProtocolMapperModel mappingModel, KeycloakSession session, UserSessionModel userSession, ClientSessionContext clientSessionCtx) {
        Map<String, Object> claims = token.getOtherClaims();
        claims.put("MyClaim", "MyClaimValue");

        setClaim(token, mappingModel, userSession, session, clientSessionCtx);
        return token;
    }
}
