package tuwien.dse.entitystorageservice.exception;

public class CarAlreadyExistsException extends Exception {

    public CarAlreadyExistsException() {
        super();
    }

    public CarAlreadyExistsException(String s) {
        super(s);
    }

    public CarAlreadyExistsException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public CarAlreadyExistsException(Throwable throwable) {
        super(throwable);
    }

    protected CarAlreadyExistsException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
