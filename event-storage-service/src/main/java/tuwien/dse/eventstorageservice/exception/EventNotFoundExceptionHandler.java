package tuwien.dse.eventstorageservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class EventNotFoundExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EventNotFoundException.class)
    protected ResponseEntity<ErrorDto> handleEventNotFoundException(EventNotFoundException e) {
        return ResponseEntity.badRequest().body(new ErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }
}
