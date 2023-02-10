package org.wso2.carbon.identity.claim.metadata.mgt;

import com.hazelcast.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.wso2.carbon.identity.claim.metadata.mgt.dto.res.AttributeMappingResDTO;
import org.wso2.carbon.identity.claim.metadata.mgt.dto.res.ClaimDialectResDTO;
import org.wso2.carbon.identity.claim.metadata.mgt.dto.res.LocalClaimResDTO;
import org.wso2.carbon.identity.claim.metadata.mgt.dto.res.PropertyResDTO;
import org.wso2.carbon.identity.claim.metadata.mgt.exception.ClaimMetadataException;
import org.wso2.carbon.identity.claim.metadata.mgt.model.AttributeMapping;
import org.wso2.carbon.identity.claim.metadata.mgt.model.ClaimDialect;
import org.wso2.carbon.identity.claim.metadata.mgt.model.ExternalClaim;
import org.wso2.carbon.identity.claim.metadata.mgt.model.LocalClaim;
import org.wso2.carbon.user.core.UserCoreConstants;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DpClaimMetadataManagementServiceImpl implements ClaimMetadataManagementService {

    private static final Log log = LogFactory.getLog(DpClaimMetadataManagementServiceImpl.class);

    private final static String auth = "admin:admin";
    private final static byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
    private final static String authHeader = "Basic " + new String(encodedAuth);

    @Override
    public List<ClaimDialect> getClaimDialects(String tenantDomain) throws ClaimMetadataException {

        log.info("getClaimDialects invoked");

        final String URL = "https://localhost:9443/t/carbon.super/api/server/v1/claim-dialects";

        final HttpGet request = new HttpGet(URL);

        request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

        try (CloseableHttpClient client = HttpClientBuilder.create().build();
             CloseableHttpResponse response = (CloseableHttpResponse) client.execute(request)) {

            final int statusCode = response.getStatusLine().getStatusCode();
            log.info("getClaimDialects code : " + statusCode);

            ObjectMapper mapper = new ObjectMapper();
            List<ClaimDialectResDTO> claimDialectResDTOs =
                    Arrays.asList(mapper.readValue(response.getEntity().getContent(), ClaimDialectResDTO[].class));
            return claimDialectResDTOs.stream()
                    .map(claimDialectResDTO -> new ClaimDialect(claimDialectResDTO.getDialectURI()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addClaimDialect(ClaimDialect claimDialect, String tenantDomain) throws ClaimMetadataException {

    }

    @Override
    public void renameClaimDialect(ClaimDialect oldClaimDialect, ClaimDialect newClaimDialect, String tenantDomain)
            throws ClaimMetadataException {

    }

    @Override
    public void removeClaimDialect(ClaimDialect claimDialect, String tenantDomain) throws ClaimMetadataException {

    }

    @Override
    public List<LocalClaim> getLocalClaims(String tenantDomain) throws ClaimMetadataException {

        log.info("getLocalClaims invoked");

        final String URL = "https://localhost:9443/t/carbon.super/api/server/v1/claim-dialects/local/claims";

        final HttpGet request = new HttpGet(URL);

        request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

        try (CloseableHttpClient client = HttpClientBuilder.create().build();
             CloseableHttpResponse response = (CloseableHttpResponse) client.execute(request)) {

            final int statusCode = response.getStatusLine().getStatusCode();
            log.info("Get local claims status code : " + statusCode);

            ObjectMapper mapper = new ObjectMapper();
            List<LocalClaimResDTO> localClaimResDTOs =
                    Arrays.asList(mapper.readValue(response.getEntity().getContent(), LocalClaimResDTO[].class));
            return convertLocalClaimResDtoListToLocalClaimList(localClaimResDTOs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addLocalClaim(LocalClaim localClaim, String tenantDomain) throws ClaimMetadataException {

    }

    @Override
    public void updateLocalClaim(LocalClaim localClaim, String tenantDomain) throws ClaimMetadataException {

    }

    @Override
    public void removeLocalClaim(String localClaimURI, String tenantDomain) throws ClaimMetadataException {

    }

    @Override
    public List<ExternalClaim> getExternalClaims(String externalClaimDialectURI, String tenantDomain)
            throws ClaimMetadataException {

        return null;
    }

    @Override
    public void addExternalClaim(ExternalClaim externalClaim, String tenantDomain) throws ClaimMetadataException {

    }

    @Override
    public void updateExternalClaim(ExternalClaim externalClaim, String tenantDomain) throws ClaimMetadataException {

    }

    @Override
    public void removeExternalClaim(String externalClaimDialectURI, String externalClaimURI, String tenantDomain)
            throws ClaimMetadataException {

    }

    @Override
    public Set<ExternalClaim> getMappingsFromOtherDialectToCarbon(String otherDialectURI, Set<String> otherClaimURIs,
                                                                  String tenantDomain) throws ClaimMetadataException {

        log.info("getMappingsFromOtherDialectToCarbon invoked");

        Set<ExternalClaim> returnSet = new HashSet<ExternalClaim>();

        if (otherDialectURI == null) {
            String message = "Invalid argument: \'otherDialectURI\' is \'NULL\'";
            log.error(message);
            throw new ClaimMetadataException(message);
        }


        try {
            if (otherDialectURI.equals(UserCoreConstants.DEFAULT_CARBON_DIALECT)) {

                List<LocalClaim> localClaims = getLocalClaims(tenantDomain);

                if (otherClaimURIs == null || otherClaimURIs.isEmpty()) {

                    for (LocalClaim localClaim : localClaims) {
                        ExternalClaim claimMapping = new ExternalClaim(localClaim.getClaimDialectURI(), localClaim
                                .getClaimURI(), localClaim.getClaimURI());
                        returnSet.add(claimMapping);
                    }

                    return returnSet;

                } else {

                    for (LocalClaim localClaim : localClaims) {

                        if (otherClaimURIs.contains(localClaim.getClaimURI())) {

                            ExternalClaim claimMapping = new ExternalClaim(
                                    otherDialectURI, localClaim.getClaimURI(), localClaim.getClaimURI());
                            returnSet.add(claimMapping);

                        }
                    }

                    return returnSet;

                }

            } else {

                List<ExternalClaim> externalClaims = getExternalClaims(otherDialectURI,
                        tenantDomain);

                if (otherClaimURIs == null || otherClaimURIs.isEmpty()) {

                    returnSet = new HashSet<ExternalClaim>(externalClaims);

                } else {

                    for (ExternalClaim externalClaim : externalClaims) {

                        if (otherClaimURIs.contains(externalClaim.getClaimURI())) {
                            returnSet.add(externalClaim);
                        }
                    }

                }

                return returnSet;
            }


        } catch (ClaimMetadataException e) {
            throw new ClaimMetadataException(e.getMessage(), e);
        }
    }

    @Override
    public Map<String, String> getMappingsMapFromOtherDialectToCarbon(String otherDialectURI,
                                                                      Set<String> otherClaimURIs, String tenantDomain,
                                                                      boolean useCarbonDialectAsKey)
            throws ClaimMetadataException {

        log.info("getMappingsMapFromOtherDialectToCarbon invoked");

        Map<String, String> returnMap = new HashMap<>();
        Set<ExternalClaim> mappings = getMappingsFromOtherDialectToCarbon(
                otherDialectURI, otherClaimURIs, tenantDomain);
        for (ExternalClaim externalClaim : mappings) {
            if (useCarbonDialectAsKey) {
                returnMap.put(externalClaim.getMappedLocalClaim(), externalClaim.getClaimURI());
            } else {
                returnMap.put(externalClaim.getClaimURI(), externalClaim.getMappedLocalClaim());
            }
        }
        return returnMap;
    }

    private List<LocalClaim> convertLocalClaimResDtoListToLocalClaimList(List<LocalClaimResDTO> localClaimResDTOs) {
        return localClaimResDTOs.stream()
            .map(localClaimResDTO ->
                new LocalClaim(
                    localClaimResDTO.getClaimURI(),
                    getAttributeMappingListFromAttributeMappingResDTOList(localClaimResDTO.getAttributeMapping()),
                    getClaimPropertiesMapFromClaimPropertiesResList(localClaimResDTO.getProperties())
                ))
            .collect(Collectors.toList());
    }

    private List<AttributeMapping> getAttributeMappingListFromAttributeMappingResDTOList(List<AttributeMappingResDTO> attrMappingResDTOList) {
        return attrMappingResDTOList.stream()
            .map(attrMappingResDTO ->
                    new AttributeMapping(attrMappingResDTO.getUserstore(), attrMappingResDTO.getMappedAttribute()))
            .collect(Collectors.toList());
    }

    private Map<String, String> getClaimPropertiesMapFromClaimPropertiesResList(List<PropertyResDTO> propertyResDTOList) {
        Map<String, String> propertyMap = new HashMap<>();
        for (PropertyResDTO propertyResDTO: propertyResDTOList) {
            propertyMap.put(propertyResDTO.getKey(), propertyResDTO.getValue());
        }
        return propertyMap;
    }
}
