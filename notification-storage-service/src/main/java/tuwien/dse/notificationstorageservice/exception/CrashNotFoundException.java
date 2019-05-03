package tuwien.dse.notificationstorageservice.exception;

import java.util.function.Supplier;

public class CrashNotFoundException extends Exception {

    public CrashNotFoundException() {
    }

    public CrashNotFoundException(String message) {
        super(message);
    }

    public CrashNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CrashNotFoundException(Throwable cause) {
        super(cause);
    }

    public CrashNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
