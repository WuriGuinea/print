package io.mosip.print.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Instantiates a new prints the request.
 * 
 * @author M1048358 Alok
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PrintRequest extends BaseRestRequestDTO {

	private static final long serialVersionUID = 1L;

	/** The request. */
	private RequestDTO request;
}
