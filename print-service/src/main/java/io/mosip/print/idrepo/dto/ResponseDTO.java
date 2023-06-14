package io.mosip.print.idrepo.dto;

import java.util.List;

import io.mosip.print.dto.Documents;
import lombok.Data;

/**
 * The Class ResponseDTO.
 *
 * @author M1048358 Alok
 */
@Data
public class ResponseDTO {

	/** The entity. */
	private String entity;
	
	/** The identity. */
	private Object identity;
	
	private List<Documents> documents;
	
	/** The status. */
	private String status;

}
