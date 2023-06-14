package io.mosip.print.util;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.websub.spi.PublisherClient;
import io.mosip.kernel.core.websub.spi.SubscriptionClient;
import io.mosip.kernel.websub.api.exception.WebSubClientException;
import io.mosip.kernel.websub.api.model.SubscriptionChangeRequest;
import io.mosip.kernel.websub.api.model.SubscriptionChangeResponse;
import io.mosip.kernel.websub.api.model.UnsubscriptionRequest;
import io.mosip.print.logger.PrintLogger;
import io.mosip.print.model.CredentialStatusEvent;
import org.springframework.web.client.RestTemplate;

@Component
public class WebSubSubscriptionHelper {

	@Autowired
	SubscriptionClient<SubscriptionChangeRequest, UnsubscriptionRequest, SubscriptionChangeResponse> sb;

	@Value("${mosip.event.hubURL}")
	private String webSubHubUrl;

	@Value("${mosip.event.secret}")
	private String webSubSecret;

	@Value("${mosip.event.callBackUrl}")
	private String callBackUrl;

	@Value("${mosip.event.topic}")
	private String topic;

	@Autowired
	private PublisherClient<String, CredentialStatusEvent, HttpHeaders> pb;

	@Autowired
	private RestTemplate restTemplate;

	/** The Constant BIOMETRICS. */
	private static final String WEBSUBSUBSCRIPTIONHEPLER = "WebSubSubscriptionHelper";

	/** The Constant ID_REPO_SERVICE_IMPL. */
	private static final String INITSUBSCRIPTION = "initSubsriptions";

	private Logger LOGGER = PrintLogger.getLogger(WebSubSubscriptionHelper.class);


	@Scheduled(fixedDelayString = "${print-websub-resubscription-delay-millisecs}",
			initialDelayString = "${mosip.event.delay-millisecs}")
	public void initSubsriptions() {
		LOGGER.info("Initializing subscribptions... {} {}", WEBSUBSUBSCRIPTIONHEPLER, INITSUBSCRIPTION);
		subscribeForPrintServiceEvents();
	}

	private void subscribeForPrintServiceEvents() {
		try {
			SubscriptionChangeRequest subscriptionRequest = new SubscriptionChangeRequest();
			subscriptionRequest.setCallbackURL(callBackUrl);
			subscriptionRequest.setHubURL(webSubHubUrl);
			subscriptionRequest.setSecret(webSubSecret);
			subscriptionRequest.setTopic(topic);
			LOGGER.info("subscription request : {}", subscriptionRequest);
			sb.subscribe(subscriptionRequest);
		} catch (WebSubClientException e) {
			LOGGER.info("websub subscription error {} {}", WEBSUBSUBSCRIPTIONHEPLER, INITSUBSCRIPTION);
		}
	}

	public void printStatusUpdateEvent(String topic, CredentialStatusEvent credentialStatusEvent) {
		try {
		HttpHeaders headers = new HttpHeaders();
		pb.publishUpdate(topic, credentialStatusEvent, MediaType.APPLICATION_JSON_UTF8_VALUE, headers,
				webSubHubUrl);
		} catch (WebSubClientException e) {
			LOGGER.info("websub publish update error {} {}", WEBSUBSUBSCRIPTIONHEPLER, INITSUBSCRIPTION);
		}
	}


	/*@Cacheable(value = "topics", key = "{#topic}")
	public void registerTopic(String topic) {
		try {
			pb.registerTopic(topic, webSubHubUrl);
		} catch (WebSubClientException e) {
			LOGGER.info(LoggerFileConstant.SESSIONID.toString(), WEBSUBSUBSCRIPTIONHEPLER, INITSUBSCRIPTION,
					"topic already registered");
		}

	}*/
}
