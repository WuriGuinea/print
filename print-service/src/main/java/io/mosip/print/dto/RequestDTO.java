package io.mosip.print.dto;

import java.io.Serializable;

import io.mosip.print.constant.IdType;
import lombok.Data;

/**
 * Instantiates a new request DTO.
 * 
 * @author M1048358 Alok
 */

/**
 * Instantiates a new request DTO.
 */
@Data
public class RequestDTO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The idtype. */
	private IdType idtype;

	/** The id value. */
	private String idValue;

	/** The card type. */
	private String cardType;
}
