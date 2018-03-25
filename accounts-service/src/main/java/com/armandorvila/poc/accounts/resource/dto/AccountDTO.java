package com.armandorvila.poc.accounts.resource.dto;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {

	@NotEmpty
	private String customerId;
	
	@NotEmpty
	private String description;
	
	@DecimalMin("0.00")
	private BigDecimal initialCredit;
}
