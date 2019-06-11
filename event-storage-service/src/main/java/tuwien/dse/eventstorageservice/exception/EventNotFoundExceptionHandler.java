package tuwien.dse.eventstorageservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class EventNotFoundExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Maps the thrown Exception to a proper error response with Http-Error-Status
     * @param e the Exception
     * @return Error response
     */
    @ExceptionHandler(EventNotFoundException.class)
    protected ResponseEntity<ErrorDto> handleEventNotFoundException(EventNotFoundException e) {
        return ResponseEntity.badRequest().body(new ErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }
}
