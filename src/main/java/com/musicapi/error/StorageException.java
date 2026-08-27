package com.musicapi.error;

/**
 * Thrown when an uploaded file cannot be written to disk.
 *
 * Unchecked on purpose: controllers should not have to wrap every upload in a
 * try/catch just to satisfy IOException. GlobalExceptionHandler turns it into a
 * 500 with a caller-safe message and logs the cause.
 */
public class StorageException extends RuntimeException {

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
