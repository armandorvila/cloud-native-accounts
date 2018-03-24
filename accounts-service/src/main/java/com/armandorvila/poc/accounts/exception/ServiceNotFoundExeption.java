package com.armandorvila.poc.accounts.exception;

public class ServiceNotFoundExeption extends RuntimeException {

	private static final long serialVersionUID = -839428030650385194L;

    public ServiceNotFoundExeption() {
        super();
    }

    public ServiceNotFoundExeption(String message) {
        super(message);
    }
}
