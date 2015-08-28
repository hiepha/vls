package com.vlls.service;

import com.vlls.exception.ClientTechnicalException;
import com.vlls.exception.ServerTechnicalException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.PropertyResolver;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by hiephn on 2014/07/23.
 */
public abstract class AbstractService extends StringUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractService.class);

    @Resource(name = "vllsPropertyResolver")
    protected PropertyResolver vllsProperties;

    protected String wildcardSearchKey(String key) {
        return isEmpty(key) ? "%" : "%" + key + "%";
    }

    protected RestTemplate restTemplate = new RestTemplate();

//    protected void validateEmailFormat(String email) throws InvalidEmailException {
//        try {
//            new InternetAddress(email);
//        } catch (AddressException e) {
//            throw new InvalidEmailException("Invalid email address: " + email);
//        }
//    }

    protected HttpResponse get(String url, List<NameValuePair> nameValuePairs)
            throws IOException, URISyntaxException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            URI uri = new URIBuilder(url).addParameters(nameValuePairs).build();
            HttpGet httpGet = new HttpGet(uri);
            HttpResponse httpResponse = client.execute(httpGet);
            return httpResponse;
        }
    }

    /**
     * Hash the string using simple hash algorithm: SHA-1
     *
     * @param str
     * @return
     */
    public String hashSha(String str) throws ServerTechnicalException {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] bytes = md.digest(str.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16)
                        .substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new ServerTechnicalException("Cannot hash string object.", e);
        }
        return generatedPassword;
    }

    /**
     * Persist content into filePath and return absolute path of the file.
     *
     * @param filePath filePath
     * @param content content
     * @return absolute path
     * @throws ServerTechnicalException
     */
    protected String persistFile(String filePath, byte[] content) throws ServerTechnicalException {
        try {
            File file = new File(filePath);
            FileUtils.writeByteArrayToFile(file, content);
            return file.getAbsolutePath();
        } catch (IOException e) {
            LOGGER.error("Cannot save content to file", e);
            throw new ServerTechnicalException("Cannot save content to file", e);
        }
    }

    public byte[] retrieveBytes(MultipartFile multipartFile) throws ClientTechnicalException {
        byte[] bytes;
        try {
            bytes = multipartFile.getBytes();
        } catch (IOException e) {
            throw new ClientTechnicalException("File upload error");
        }
        if (bytes.length == 0) {
            throw new ClientTechnicalException("File upload is empty");
        }
        return bytes;
    }
}
