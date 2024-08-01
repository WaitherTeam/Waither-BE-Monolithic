package com.waither.global.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class SeverHttpResponseUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> Mono<Void> sendSuccessResponse(ServerHttpResponse response, T result) {
        Map<String, Object> responseBody = new HashMap<>();

        response.setStatusCode(HttpStatus.OK);
        responseBody.put("code", String.valueOf(HttpStatus.OK.value()));
        responseBody.put("message", HttpStatus.OK.getReasonPhrase());


        if (result != null) {
            responseBody.put("result", result);
        }

        return sendResponse(response, responseBody);
    }

    public static Mono<Void> sendErrorResponse(ServerHttpResponse response, HttpStatus httpStatus, Object body) {
        response.setStatusCode(httpStatus);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("code", String.valueOf(httpStatus.value()));
        responseBody.put("message", body);

        return sendResponse(response, responseBody);
    }

    public static Mono<Void> sendResponse(ServerHttpResponse response, Object body) {
        HttpHeaders headers = response.getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String responseBodyString;
        try {
            responseBodyString = objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            return Mono.error(e);
        }

        return response.writeWith(Mono.just(response.bufferFactory().
                wrap(responseBodyString.getBytes(StandardCharsets.UTF_8))));
    }
}