package io.mosip.print.util;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import io.mosip.print.logger.PrintLogger;

/**
 * The Class RestApiClient.
 *
 * @author Rishabh Keshari
 */
@Component
public class RestApiClient {

	/** The logger. */
	private Logger logger = PrintLogger.getLogger(RestApiClient.class);

	/** The builder. */
	@Autowired
	@Qualifier("selfTokenRestTemplate")
	private RestTemplate restTemplate;

	@Autowired
	Environment environment;

	private static final String AUTHORIZATION = "Authorization=";


	/**
	 * Gets the api. *
	 * 
	 * @param              <T> the generic type
	 * @param responseType the response type
	 * @return the api
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public <T> T getApi(URI uri, Class<?> responseType) throws Exception {
		T result = null;
		try {
			logger.info("RestApiClient::getApi()::entry uri : {}",uri.toString());
			result = (T) restTemplate.exchange(uri, HttpMethod.GET, setRequestHeader(null, null), responseType)
					.getBody();
		} catch (Exception e) {
			logger.error("RestApiClient::getApi()::error {} {}",e.getMessage(),e);
		}
		return result;
	}

	public <T> T getApi(String url, Class<?> responseType) {
		T result = null;
		try {
			result = (T) restTemplate.getForObject(url, responseType);
		} catch (Exception e) {
			logger.error("RestApiClient::getApi()::error {} {}",e.getMessage() ,e);
		}
		return result;
	}

	/**
	 * Post api.
	 *
	 * @param <T>
	 *            the generic type
	 * @param uri
	 *            the uri
	 * @param requestType
	 *            the rfequest type
	 * @param responseClass
	 *            the response class
	 * @return the t
	 */
	@SuppressWarnings("unchecked")
	public <T> T postApi(String uri, MediaType mediaType, Object requestType, Class<?> responseClass) throws Exception {

		T result = null;
		try {
			logger.info("RestApiClient::postApi()::entry uri : {}",uri);
			result = (T) restTemplate.postForObject(uri, setRequestHeader(requestType, mediaType), responseClass);
		} catch (Exception e) {
			logger.error("RestApiClient::postApi()::error {} {}"+e.getMessage(), e);
		}
		return result;
	}

	/**
	 * this method sets token to header of the request
	 *
	 * @param requestType
	 * @param mediaType
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private HttpEntity<Object> setRequestHeader(Object requestType, MediaType mediaType) throws IOException {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		if (mediaType != null) {
			headers.add("Content-Type", mediaType.toString());
		}
		if (requestType != null) {
			try {
				HttpEntity<Object> httpEntity = (HttpEntity<Object>) requestType;
				HttpHeaders httpHeader = httpEntity.getHeaders();
				Iterator<String> iterator = httpHeader.keySet().iterator();
				while (iterator.hasNext()) {
					String key = iterator.next();
					if (!(headers.containsKey("Content-Type") && key.equals("Content-Type")))
						headers.add(key, httpHeader.get(key).get(0));
				}
				return new HttpEntity<Object>(httpEntity.getBody(), headers);
			} catch (ClassCastException | NullPointerException e) {
				return new HttpEntity<Object>(requestType, headers);
			}
		} else
			return new HttpEntity<Object>(headers);
	}

}
