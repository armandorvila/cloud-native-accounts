package com.armandorvila.poc.accounts.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"createdAt", "lastModifiedAt"})
@Document(collection = "accounts")
public class Account {
	
	@Id
	private String id;
	
	@NotEmpty(message = "This field is required")
	private String description;
	
	@DBRef
	private Customer customer;
	
	private BigDecimal balance;
	
	@CreatedDate
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private LocalDateTime lastModifiedAt;
	
	public Account(String description, Customer customer) {
		this.description = description;
		this.customer = customer;
	}
	
	public Account(String id, String description) {
		this.id = id;
		this.description = description;
	}
	
	public Account updateBalance(BigDecimal balance) {
		this.setBalance(balance);
		return this;
	}
}
