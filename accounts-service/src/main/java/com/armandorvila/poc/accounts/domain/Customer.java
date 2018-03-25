package com.armandorvila.poc.accounts.domain;

import java.time.Instant;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
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
@Document(collection = "customers")
public class Customer {

	@Id
	private String id;
	
	@Email
	@Indexed(unique = true)
	private String email;
	
	@NotEmpty(message = "This field is required")
	private String firstName;
	
	@NotEmpty(message = "This field is required")
	private String lastName; 
	
	@CreatedDate
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Instant createdAt;

	@LastModifiedDate
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Instant lastModifiedAt;
}
