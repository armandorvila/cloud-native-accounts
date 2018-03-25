package com.armandorvila.poc.accounts.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CustomerNotFoundExeption extends RuntimeException {

	private static final long serialVersionUID = -6844214687133718990L;

	public CustomerNotFoundExeption(String message) {
        super(message);
    }
}
