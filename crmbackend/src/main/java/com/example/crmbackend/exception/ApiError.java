package com.example.crmbackend.exception;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ApiError {
    @Builder.Default
    boolean success = false;
    String errorCode;
    String message;
}


