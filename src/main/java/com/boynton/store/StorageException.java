package com.boynton.store;

public class StorageException extends RuntimeException {
    String message;
    public StorageException(String msg) {
        message = msg;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
