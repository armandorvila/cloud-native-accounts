package com.armandorvila.poc.edge;

import java.util.Map;

import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

@Component
public class GatewayErrorHanlder extends DefaultErrorAttributes {

	@Override
	public Map<String, Object> getErrorAttributes(ServerRequest request,
			boolean includeStackTrace) {
		Map<String, Object> errorAttributes = super.getErrorAttributes(request, includeStackTrace);
		Throwable error = super.getError(request);
		
		if(error instanceof NotFoundException) {
			errorAttributes.put("status", HttpStatus.BAD_GATEWAY.value());
			errorAttributes.put("error", HttpStatus.BAD_GATEWAY.getReasonPhrase());
		}
			
		return errorAttributes;
	}
}
