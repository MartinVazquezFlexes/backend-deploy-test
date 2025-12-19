package com.techforb.apiportalrecruiting.core.exceptions;

public class VacancyNotActiveException extends RuntimeException{

    public VacancyNotActiveException(String message){
        super(message);
    }
}
