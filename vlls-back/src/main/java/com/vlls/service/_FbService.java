//package com.vlls.service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.vlls.exception.ServerTechnicalException;
//import com.vlls.exception.UnauthorizedException;
//import com.vlls.jpa.domain.User;
//import com.vlls.service.model.FbResponse;
//import com.vlls.web.model.UserResponse;
//import org.apache.commons.io.IOUtils;
//import org.apache.commons.lang.StringUtils;
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.utils.URIBuilder;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.message.BasicNameValuePair;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by hiephn on 2014/09/02.
// */
//@Service
//public class FbService extends AbstractService {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(FbService.class);
//
//    private ObjectMapper objectMapper = new ObjectMapper();
//
//    @Autowired
//    private UserService userService;
//    @Autowired
//    private SecurityService securityService;
//
//    public UserResponse login(String code) throws UnauthorizedException, ServerTechnicalException {
//        String accessToken = exchangeAccessToken(code);
//        FbResponse fbResponse = getUserInfo(accessToken, -1);
//        User user = userService.updateFb(
//                fbResponse.getName(),
//                fbResponse.getBirthday(),
//                fbResponse.getEmail(),
//                fbResponse.getGender(),
//                fbResponse.getUpdatedTime(),
//                accessToken,
//                fbResponse.getId(),
//                fbResponse.getPicture().getData().getUrl());
//
//        securityService.signOutCurrentUser();
//        securityService.enSession(user);
//
//        UserResponse userResponse = new UserResponse(user);
//        return userResponse;
//    }
//
//    protected String exchangeAccessToken(String code) throws UnauthorizedException, ServerTechnicalException {
//        try (CloseableHttpClient client = HttpClients.createDefault()) {
//
//            List<NameValuePair> nameValuePairs = new ArrayList<>();
//            nameValuePairs.add(new BasicNameValuePair("client_id", vllsProperties.getProperty("fb.id")));
//            nameValuePairs.add(new BasicNameValuePair("client_secret", vllsProperties.getProperty("fb.secret")));
//            nameValuePairs.add(new BasicNameValuePair("redirect_uri", vllsProperties.getProperty("fb.redirect_uri")));
//            nameValuePairs.add(new BasicNameValuePair("code", code));
//            URI uri = new URIBuilder(vllsProperties.getProperty("fb.access_token_uri"))
//                    .addParameters(nameValuePairs)
//                    .build();
//            HttpGet httpGet = new HttpGet(uri);
//            HttpResponse httpResponse = client.execute(httpGet);
//            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//                String responseBody = IOUtils.toString(httpResponse.getEntity().getContent());
//                String accessToken = StringUtils.substringBetween(responseBody, "access_token=", "&expires=");
//                return accessToken;
//            } else {
//                throw new UnauthorizedException(String.format(
//                        "Error while getting facebook access token with code: %s. Status: %d. Body: %s",
//                        code,
//                        httpResponse.getStatusLine().getStatusCode(),
//                        IOUtils.toString(httpResponse.getEntity().getContent())
//                ));
//            }
//        } catch (IOException | URISyntaxException e) {
//            LOGGER.error("Error while getting facebook access token with code: " + code, e);
//            throw new ServerTechnicalException("Có lỗi xảy ra. Xin thử lại.");
//        }
//    }
//
//    protected FbResponse getUserInfo(String accessToken, int pictureSize) throws UnauthorizedException, ServerTechnicalException {
//        try (CloseableHttpClient client = HttpClients.createDefault()) {
//
//            List<NameValuePair> nameValuePairs = new ArrayList<>();
//            nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
//            nameValuePairs.add(new BasicNameValuePair("format", "json"));
//            nameValuePairs.add(new BasicNameValuePair("fields",
//                    "name,email,gender,birthday,updated_time,picture.height(" +
//                        (pictureSize < 0 ? 32 : pictureSize) +
//                        ")"));
//            URI uri = new URIBuilder(vllsProperties.getProperty("fb.graph_uri"))
//                    .addParameters(nameValuePairs)
//                    .build();
//            HttpGet httpGet = new HttpGet(uri);
//            HttpResponse httpResponse = client.execute(httpGet);
//            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//                String responseBody = IOUtils.toString(httpResponse.getEntity().getContent());
//                FbResponse fbResponse = objectMapper.readValue(responseBody, FbResponse.class);
//                return fbResponse;
//            } else {
//                throw new UnauthorizedException(String.format(
//                        "Error while getting facebook access token with access token: %s. Status: %d. Body: %s",
//                        accessToken,
//                        httpResponse.getStatusLine().getStatusCode(),
//                        IOUtils.toString(httpResponse.getEntity().getContent())
//                ));
//            }
//        } catch (IOException | URISyntaxException e) {
//            LOGGER.error("Error while getting user info with access token: " + accessToken, e);
//            throw new ServerTechnicalException("Có lỗi xảy ra. Xin thử lại.");
//        }
//    }
//}
