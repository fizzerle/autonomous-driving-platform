package tuwien.dse.notificationstorageservice.exception;

import java.util.function.Supplier;

public class CrashNotFoundException extends Exception {

    public CrashNotFoundException(String message) {
        super(message);
    }
}
