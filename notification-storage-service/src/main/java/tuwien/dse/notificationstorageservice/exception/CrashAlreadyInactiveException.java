package tuwien.dse.notificationstorageservice.exception;

public class CrashAlreadyInactiveException extends Exception {

    public CrashAlreadyInactiveException() {
    }

    public CrashAlreadyInactiveException(String message) {
        super(message);
    }

    public CrashAlreadyInactiveException(String message, Throwable cause) {
        super(message, cause);
    }

    public CrashAlreadyInactiveException(Throwable cause) {
        super(cause);
    }

    public CrashAlreadyInactiveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
