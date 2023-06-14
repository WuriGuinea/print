package io.mosip.print.activemq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import io.mosip.print.constant.UinCardType;
import io.mosip.print.dto.MQResponseDto;
import io.mosip.print.dto.PrintMQDetails;
import io.mosip.print.service.impl.PrintServiceImpl;
import io.mosip.print.util.Helpers;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.jms.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class ActiveMQListener implements PrintMQListener {

	private static final Logger logger = LoggerFactory.getLogger(ActiveMQListener.class);

	@Value("${config.server.file.storage.uri}")
	private String configServerFileStorageURL;

	@Value("${print.activemq.listener.json}")
	private String printActiveMQListenerJson;
	
	@Value("${print.activemq.response.delay:0}")
	private int delayResponse;

	/** The Constant PRINTMQ. */
	private static final String PRINTMQ = "printMQ";

	/** The Constant USERNAME. */
	private static final String USERNAME = "userName";

	/** The Constant PASSWORD. */
	private static final String PASSWORD = "password";

	/** The Constant BROKERURL. */
	private static final String BROKERURL = "brokerUrl";

	/** The Constant FAIL_OVER. */
	private static final String FAIL_OVER = "failover:(";

	/** The Constant RANDOMIZE_FALSE. */
	private static final String RANDOMIZE_FALSE = ")?randomize=false";

	/** The Constant TYPEOFQUEUE. */
	private static final String TYPEOFQUEUE = "typeOfQueue";

	/** The Constant INBOUNDQUEUENAME. */
	private static final String INBOUNDQUEUENAME = "inboundQueueName";

	/** The Constant NAME. */
	private static final String NAME = "name";

	/** The Constant OUTBOUNDQUEUENAME. */
	private static final String OUTBOUNDQUEUENAME = "outboundQueueName";

	private ActiveMQConnectionFactory activeMQConnectionFactory;

	private static final String ID = "id";

	private Connection connection;
	private Session session;
	private Destination destination;

	/**
	 * This flag is added for development & debugging locally registration-processor-abis-sample.json
	 * If true then registration-processor-abis-sample.json will be picked from resources
	 */
	@Value("${local.development:false}")
	private boolean localDevelopment;

	public String outBoundQueue;

	private static final String PRINT_RESPONSE = "mosip.print.pdf.response";

	@Autowired
	PrintServiceImpl printServiceImpl;

	public void consumeLogic(javax.jms.Message message, String abismiddlewareaddress) {
		Integer textType = 0;
		String messageData = null;
		logger.info("Received message " + message);
		try {
			if (message instanceof TextMessage || message instanceof ActiveMQTextMessage) {
				textType = 1;
				TextMessage textMessage = (TextMessage) message;
				messageData = textMessage.getText();
			} else if (message instanceof ActiveMQBytesMessage) {
				textType = 2;
				messageData = new String(((ActiveMQBytesMessage) message).getContent().data);
			} else {
				logger.error("Received message is neither text nor byte");
				return ;
			}
			logger.info("Message Data " + messageData);
			MQResponseDto mqResponseDto = new Gson().fromJson(messageData, MQResponseDto.class);
			final ObjectMapper mapper = new ObjectMapper();
			mapper.findAndRegisterModules();
			mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

			ResponseEntity<Object> obj = null;

			logger.info("go on sleep {} ", delayResponse);
			TimeUnit.SECONDS.sleep(delayResponse);

			logger.info("Request type is " + mqResponseDto.getId());

			switch (mqResponseDto.getId().toString()) {
			case PRINT_RESPONSE:
				printServiceImpl.updatePrintTransactionStatus(mqResponseDto.getData());
				break;
			}
		} catch (Exception e) {
			logger.error("Issue while hitting mock abis API", e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void sendToQueue(ResponseEntity<Object> obj, Integer textType, UinCardType printType) throws JsonProcessingException, UnsupportedEncodingException {
		String outBoundQueueStr = outBoundQueue;
		final ObjectMapper mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		logger.info("Response: ", obj.getBody().toString());
		if (textType == 2) {
			send(mapper.writeValueAsString(obj.getBody()).getBytes("UTF-8"),
					outBoundQueueStr);
		} else if (textType == 1) {
			send(mapper.writeValueAsString(obj.getBody()), outBoundQueueStr);
		}
	}

	public static String getJson(String configServerFileStorageURL, String uri, boolean localQueueConf) throws IOException, URISyntaxException {
		if (localQueueConf) {
			return Helpers.readFileFromResources("print-activemq-listener.json");
		} else {
			RestTemplate restTemplate = new RestTemplate();
			logger.info("Json URL ",configServerFileStorageURL,uri);
			return restTemplate.getForObject(configServerFileStorageURL + uri, String.class);
		}
	}

	public List<PrintMQDetails> getQueueDetails() throws IOException, URISyntaxException {
		List<PrintMQDetails> queueDetailsList = new ArrayList<>();

		String printQueueJsonStringValue = getJson(configServerFileStorageURL, printActiveMQListenerJson, localDevelopment);

		logger.info(printQueueJsonStringValue);
		JSONObject printQueueJson;
		PrintMQDetails queueDetail = new PrintMQDetails();
		Gson g = new Gson();

		try {
			printQueueJson = g.fromJson(printQueueJsonStringValue, JSONObject.class);

			ArrayList<Map> printQueueJsonArray = (ArrayList<Map>) printQueueJson.get(PRINTMQ);

			for (int i = 0; i < printQueueJsonArray.size(); i++) {

				Map<String, String> json = printQueueJsonArray.get(i);
				String userName = validateQueueJsonAndReturnValue(json, USERNAME);
				String password = validateQueueJsonAndReturnValue(json, PASSWORD);
				String brokerUrl = validateQueueJsonAndReturnValue(json, BROKERURL);
				String failOverBrokerUrl = FAIL_OVER + brokerUrl + "," + brokerUrl + RANDOMIZE_FALSE;
				String typeOfQueue = validateQueueJsonAndReturnValue(json, TYPEOFQUEUE);
				String inboundQueueName = validateQueueJsonAndReturnValue(json, INBOUNDQUEUENAME);
				String outboundQueueName = validateQueueJsonAndReturnValue(json, OUTBOUNDQUEUENAME);
				String queueName = validateQueueJsonAndReturnValue(json, NAME);

				this.activeMQConnectionFactory = new ActiveMQConnectionFactory(userName, password, brokerUrl);

				queueDetail.setTypeOfQueue(typeOfQueue);
				queueDetail.setInboundQueueName(inboundQueueName);
				queueDetail.setOutboundQueueName(outboundQueueName);
				queueDetail.setName(queueName);
				queueDetailsList.add(queueDetail);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while fetching abis info", e.getMessage());
		}
		return queueDetailsList;
	}

	private String validateQueueJsonAndReturnValue(Map<String, String> jsonObject, String key) throws Exception {

		String value = (String) jsonObject.get(key);
		if (value == null) {
			throw new Exception("Value does not exists for key" + key);
		}
		return value;
	}

	public void setup() {
		try {
			if (connection == null || ((ActiveMQConnection) connection).isClosed()) {
				connection = activeMQConnectionFactory.createConnection();

				if (session == null) {
					connection.start();
					this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				}
			}
		} catch (JMSException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void runQueue() {
		try {
			List<PrintMQDetails> printQueueDetails = getQueueDetails();
			if (printQueueDetails != null && printQueueDetails.size() > 0) {

				for (int i = 0; i < printQueueDetails.size(); i++) {
					String outBoundAddress = printQueueDetails.get(i).getOutboundQueueName();
					outBoundQueue = outBoundAddress;
					QueueListener listener = new QueueListener() {

						@Override
						public void setListener(javax.jms.Message message) {
							consumeLogic(message, outBoundAddress);
						}
					};
					consume(printQueueDetails.get(i).getInboundQueueName(), listener,
							printQueueDetails.get(i).getTypeOfQueue());
				}

			} else {
				throw new Exception("Queue Connection Not Found");

			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

	}

	public byte[] consume(String address, QueueListener object, String queueName) throws Exception {

		ActiveMQConnectionFactory activeMQConnectionFactory = this.activeMQConnectionFactory;
		if (activeMQConnectionFactory == null) {
			throw new Exception("Invalid Connection Exception");
		}

		if (destination == null) {
			setup();
		}

		MessageConsumer consumer;
		try {
			destination = session.createQueue(address);
			consumer = session.createConsumer(destination);
			consumer.setMessageListener(getListener(queueName, object));

		} catch (JMSException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public static MessageListener getListener(String queueName, QueueListener object) {
		if (queueName.equals("ACTIVEMQ")) {

			return new MessageListener() {
				@Override
				public void onMessage(Message message) {
					object.setListener(message);
				}
			};

		}
		return null;
	}

	public Boolean send(byte[] message, String address) {
		boolean flag = false;

		try {
			initialSetup();
			destination = session.createQueue(address);
			MessageProducer messageProducer = session.createProducer(destination);
			BytesMessage byteMessage = session.createBytesMessage();
			byteMessage.writeObject(message);
			messageProducer.send(byteMessage);
			flag = true;
		} catch (JMSException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return flag;
	}

	public Boolean send(String message, String address) {
		boolean flag = false;

		try {
			initialSetup();
			destination = session.createQueue(address);
			MessageProducer messageProducer = session.createProducer(destination);
			messageProducer.send(session.createTextMessage(message));
			flag = true;
		} catch (JMSException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return flag;
	}

	private void initialSetup() throws Exception {
		if (this.activeMQConnectionFactory == null) {
			throw new Exception("Invalid Connection Exception");
		}
		setup();
	}

	public void connectActiveMQ() {
		try {
			List<PrintMQDetails> printQueueDetails = getQueueDetails();
			if (printQueueDetails != null && printQueueDetails.size() > 0) {

				for (int i = 0; i < printQueueDetails.size(); i++) {
					String outBoundAddress = printQueueDetails.get(i).getOutboundQueueName();
					outBoundQueue = outBoundAddress;
				}
			} else {
				throw new Exception("Queue Connection Not Found");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
}
