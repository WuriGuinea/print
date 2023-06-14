package io.mosip.print.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CredentialRequestDto {

	private String id;
	
	private String credentialType;
	
	private boolean encrypt;
	
	private String issuer;

	private String encryptionKey;
	
	private String recepiant;
	
	private String user;
	
    private List<String> sharableAttributes;
    
    private Map<String,Object> additionalData;
}
