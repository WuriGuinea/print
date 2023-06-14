package io.mosip.print.activemq;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.mosip.print.constant.UinCardType;
import org.springframework.http.ResponseEntity;

import java.io.UnsupportedEncodingException;

public interface PrintMQListener {
    void sendToQueue(ResponseEntity<Object> obj, Integer textType, UinCardType printType) throws JsonProcessingException, UnsupportedEncodingException;
}
