package com.armandorvila.poc.accounts.resource;

import java.util.Map;

import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import com.armandorvila.poc.accounts.exception.ServiceNotFoundExeption;

@Component
public class AccountErrorHandler extends DefaultErrorAttributes {

	@Override
	public Map<String, Object> getErrorAttributes(ServerRequest request,
			boolean includeStackTrace) {
		
		Map<String, Object> errorAttributes = super.getErrorAttributes(request, includeStackTrace);
		Throwable error = super.getError(request);
		
		if(error instanceof ServiceNotFoundExeption) {
			errorAttributes.put("status", HttpStatus.BAD_GATEWAY.value());
			errorAttributes.put("error", HttpStatus.BAD_GATEWAY.getReasonPhrase());
		}
			
		return errorAttributes;
	}
}

