package io.mosip.print.dto;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PrintResponse extends BaseRestResponseDTO {

	private static final long serialVersionUID = 1L;

	private List<ErrorDTO> errors;

    private ResponseDTO response;
}
