package com.armandorvila.poc.transactions.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Document(collection = "transactions")
public class Transaction {

	@Id
	private String id;
	
	@NotEmpty
	private String accountId;

	@NotEmpty
	private String description;
	
	@NotNull
	private BigDecimal value;
	
	private BigDecimal balance;

	@CreatedDate
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private LocalDateTime timestamp;
	
	public Transaction(String accountId, String description, BigDecimal value, BigDecimal balance) {
		this.accountId = accountId;
		this.description = description;
		this.value = value;
		this.balance = balance;
	}
	
	public Transaction(String accountId, String description, BigDecimal value, BigDecimal balance, LocalDateTime timestamp) {
		this.accountId = accountId;
		this.description = description;
		this.value = value;
		this.balance = balance;
		this.timestamp = timestamp;
	}
	
	public void updateBalance(BigDecimal lastBalnace) {
		this.balance = lastBalnace.add(this.value);
	}
}
