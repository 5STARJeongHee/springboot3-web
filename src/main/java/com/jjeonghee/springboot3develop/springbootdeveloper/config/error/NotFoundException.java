package com.jjeonghee.springboot3develop.springbootdeveloper.config.error;

public class NotFoundException extends BusinessBaseException{

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }

    public NotFoundException() {
        super(ErrorCode.NOT_FOUND);
    }
}
