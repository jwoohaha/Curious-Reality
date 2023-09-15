package com.ssafy.curious.global.exception;

import lombok.Getter;

@Getter public abstract class CustomException extends RuntimeException{
    private final ErrorCode errorCode;
    protected CustomException(ErrorCode errorCode) {this.errorCode=errorCode;}
}
