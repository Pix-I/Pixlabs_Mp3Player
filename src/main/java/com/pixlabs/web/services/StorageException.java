package com.pixlabs.web.services;

/**
 * Created by pix-i on 25/11/2016.
 */


class StorageException extends RuntimeException {

    StorageException(String message) {
        super(message);
    }

    StorageException(String message, Throwable cause) {
        super(message, cause);
    }

}
