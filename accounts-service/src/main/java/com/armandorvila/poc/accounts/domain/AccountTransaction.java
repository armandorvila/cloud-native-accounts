package com.armandorvila.poc.accounts.domain;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

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

	@Id
	private String id;
	
	private String description;
	
	@CreatedDate
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Instant createdAt;

	@LastModifiedDate
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Instant lastModifiedAt;
	
	public AccountTransaction(String description) {
		this.description = description;
	}
}
