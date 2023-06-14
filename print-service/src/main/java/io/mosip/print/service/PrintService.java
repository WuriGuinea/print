package io.mosip.print.service;

import io.mosip.print.dto.BaseResponseDTO;
import io.mosip.print.dto.PrintStatusRequestDto;
import io.mosip.print.model.EventModel;

public interface PrintService {
    
	/**
	 * Get the card
	 * 
	 * 
	 * @param eventModel
	 * @return
	 * @throws Exception
	 */
	public boolean generateCard(EventModel eventModel) throws Exception;

	public BaseResponseDTO updatePrintTransactionStatus(PrintStatusRequestDto request);

	// Map<String, byte[]> getDocuments(String credentialSubject, String sign,
	// String cardType,
	// boolean isPasswordProtected);
	
}