package com.lephuduy.jobhunter.util.error;

import jakarta.persistence.Id;

public class IdInvalidException extends Exception{
    public IdInvalidException(String message){
        super(message);
    }
}
