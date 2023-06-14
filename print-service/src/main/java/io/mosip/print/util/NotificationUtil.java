package io.mosip.print.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.print.core.http.ResponseWrapper;
import io.mosip.print.dto.ErrorDTO;
import io.mosip.print.dto.NotificationResponseDTO;
import io.mosip.print.exception.ApisResourceAccessException;
import io.mosip.print.exception.NotificationException;
import io.mosip.print.logger.PrintLogger;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class NotificationUtil {

    private Logger log = PrintLogger.getLogger(NotificationUtil.class);

    @Autowired
    private RestApiClient restApiClient;

    @Value("${emailResource.url}")
    private String emailResourceUrl;

    @Autowired
    private TemplateGenerator templateGenerator;

    @Autowired
    private ObjectMapper mapper;

    @Value("${mosip.utc-datetime-pattern}")
    private String dateTimeFormat;
    @Value("${mosip.primary-language}")
    private String primaryLang;

    private static final String UIN_CARD_EMAIL_SUB = "RPR_UIN_CARD_EMAIL_SUB";
    private static final String UIN_CARD_EMAIL = "RPR_UIN_CARD_EMAIL";
    private static final String UIN_CARD_EMAIL_SUB_DEFAULT = "UIN Card Attached!";
    private static final String UIN_CARD_EMAIL_DEFAULT = "Your UIN Card is attached.";

    public List<NotificationResponseDTO> emailNotification(List<String> emailIds, String fileName, Map<String, Object> attributes,
                                                           byte[] attachmentFile) throws Exception {
        log.info("sessionId", "idType", "id", "In emailNotification method of NotificationUtil service");
        HttpEntity<byte[]> doc = null;
        String fileText = null;
        //Map<String, Object> attributes = new LinkedHashMap<>();
        if (attachmentFile != null) {
            LinkedMultiValueMap<String, String> pdfHeaderMap = new LinkedMultiValueMap<>();
            pdfHeaderMap.add("Content-disposition",
                    "form-data; name=attachments; filename=" + fileName + ".pdf");
            pdfHeaderMap.add("Content-type", "application/pdf");
            doc = new HttpEntity<>(attachmentFile, pdfHeaderMap);
        }

        ResponseWrapper<?> responseWrapper = null;
        List<NotificationResponseDTO> notifierResponseList = new ArrayList<>();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<Object, Object> emailMap = new LinkedMultiValueMap<>();
        emailMap.add("attachments", doc);
        emailMap.add("mailContent", getEmailContent(attributes));
        emailMap.add("mailSubject", getEmailSubject(attributes));

        log.info("sessionId", "idType", "id",
                "In emailNotification method of NotificationUtil service emailResourceUrl: " + emailResourceUrl);
        emailIds.forEach(emailId -> {
            try {
                notifierResponseList.add(sendEmail(emailId, headers, emailMap));
            } catch (Exception e) {
                log.error("Failed to send notification via email.{}", emailId, e);
            }
        });
        return notifierResponseList;
    }

    private NotificationResponseDTO sendEmail(String emailId, HttpHeaders headers, MultiValueMap<Object, Object> emailMap) throws Exception {
        NotificationResponseDTO notifierResponse = new NotificationResponseDTO();
        try {
            if (!StringUtils.hasText(emailId)) {
                notifierResponse.setMessage("No Email-Id present.{}" + emailId);
                return notifierResponse;
            }
            emailMap.set("mailTo", emailId);
            HttpEntity<MultiValueMap<Object, Object>> httpEntity = new HttpEntity<>(emailMap, headers);
            ResponseWrapper<?> responseWrapper = (ResponseWrapper<?>) restApiClient.postApi(emailResourceUrl,
                    MediaType.MULTIPART_FORM_DATA, httpEntity, ResponseWrapper.class);
            if (responseWrapper != null) {
                if (responseWrapper.getErrors() != null && !responseWrapper.getErrors().isEmpty()) {
                    ErrorDTO error = responseWrapper.getErrors().get(0);
                    throw new NotificationException(error.getMessage());
                }
                notifierResponse = mapper.readValue(mapper.writeValueAsString(responseWrapper.getResponse()),
                        NotificationResponseDTO.class);
            }
        } catch (Exception e) {
            log.error("Error while sending pdf email.", e);
            throw e;
        }
        return notifierResponse;
    }

    private String getEmailContent(Map<String, Object> attributes) throws IOException, ApisResourceAccessException {
        InputStream in = templateGenerator.getTemplate(UIN_CARD_EMAIL, attributes, primaryLang);
        if (in == null) {
            return UIN_CARD_EMAIL_DEFAULT;
        }
        return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    }

    private String getEmailSubject(Map<String, Object> attributes) throws IOException, ApisResourceAccessException {
        InputStream in = templateGenerator.getTemplate(UIN_CARD_EMAIL_SUB, attributes, primaryLang);
        if (in == null) {
            return UIN_CARD_EMAIL_SUB_DEFAULT;
        }
        return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    }

    private String getCurrentResponseTime() {
        log.info("sessionId", "idType", "id", "In getCurrentResponseTime method of NotificationUtil service");
        return DateUtils.formatDate(new Date(System.currentTimeMillis()), dateTimeFormat);
    }
}
