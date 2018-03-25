package com.armandorvila.poc.accounts.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ServiceNotFoundExeption extends RuntimeException {

	private static final long serialVersionUID = -839428030650385194L;

    public ServiceNotFoundExeption(String message) {
        super(message);
    }
}
