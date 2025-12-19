package com.techforb.apiportalrecruiting.core.exceptions;

public class UnauthorizedActionException extends  RuntimeException{
    public UnauthorizedActionException(String message) {
        super(message);
    }
}
