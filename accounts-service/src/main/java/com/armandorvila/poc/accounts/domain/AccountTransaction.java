package com.armandorvila.poc.accounts.domain;

import java.math.BigDecimal;
import java.time.Instant;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AccountTransaction {

	@NotEmpty
	private String id;
	
	@NotEmpty
	private String accountId;
	
	@NotEmpty
	private String description;
	
	@NotNull
	private BigDecimal value;
	
	private BigDecimal balance;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Instant createdAt;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Instant lastModifiedAt;
	
	public AccountTransaction(String description) {
		this.description = description;
	}
}
