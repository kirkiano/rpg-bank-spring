package org.kirkiano.rpg.bank.controller.error;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.HttpStatus;

import org.kirkiano.rpg.bank.config.Constants;
import org.kirkiano.rpg.bank.exn.UnknownCharIdException;
import org.kirkiano.rpg.bank.exn.AccountAlreadyExistsException;
import org.kirkiano.rpg.bank.exn.NegativeBalanceException;
import org.kirkiano.rpg.bank.exn.NoSuchAccountIdException;


/*
 See:
 - https://spring.io/guides/tutorials/rest/
 - https://www.baeldung.com/spring-boot-bean-validation#the-exceptionhandler-annotation
*/

/**
 * Class for converting exceptions to error responses
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Default constructor
     */
    public GlobalExceptionHandler() {}

    /**
     * Convert a {@link HttpMessageConversionException} to an error response
     *
     * @param ex the exception
     * @return response
     */
    @ResponseBody
    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<ErrorWrap> handleHttpMessageConversionException(
        HttpMessageConversionException ex
    )
    {
        record ErrorAndStatus(Error error, HttpStatus status) {}
        Throwable cause = ex.getMostSpecificCause();
        var errStat = switch (cause) {
            case InvalidFormatException ife -> {
                var e = Error.Type.create(ife);
                yield new ErrorAndStatus(e, HttpStatus.UNPROCESSABLE_ENTITY);
            }

            // See the comment at https://javadoc.io/doc/com.fasterxml.jackson.core/jackson-databind/2.9.5/com/fasterxml/jackson/databind/exc/MismatchedInputException.html
            // about InvalidDefinitionException not being a typical client error
            // case InvalidDefinitionException ide -> toError(ide);
            case UnrecognizedPropertyException upe -> {
                var e = new Error.UnknownProperty(upe);
                yield new ErrorAndStatus(e, HttpStatus.BAD_REQUEST);
            }
            case JsonProcessingException jpe -> {
                var e = new Error.UnparseableJson(jpe.getLocation());
                yield new ErrorAndStatus(e, HttpStatus.BAD_REQUEST);
            }
            default -> {
                var e = new Error.General(cause.toString());
                yield new ErrorAndStatus(e, HttpStatus.BAD_REQUEST);
            }
        };

        return new ResponseEntity<>(wrap(errStat.error()),
                                    errStat.status());
    }


    /**
     * Convert a {@link MethodArgumentNotValidException} to an error response
     *
     * @param ex exception
     * @return response
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorWrap handleValidationException(
        MethodArgumentNotValidException ex
    )
    {
        var errors = new HashSet<Error>();
        ex.getBindingResult().getAllErrors().forEach(err ->
            errors.add(convertValidationError(err))
        );
        return wrap(errors);
    }


    /**
     * Convert a {@link MethodArgumentTypeMismatchException} to an error
     * response
     *
     * @param ex exception
     * @return response
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ErrorWrap handleMethodArgumentTypeMismatchException(
        MethodArgumentTypeMismatchException ex
    )
    {
        var e = Error.Type.create(ex.getName(),
                                  ex.getValue(),
                                  ex.getParameter().getParameterType());
        return wrap(e);
    }


    /**
     * Convert a {@link NoSuchAccountIdException} to an error response
     *
     * @param ex exception
     * @return response
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchAccountIdException.class)
    public ErrorWrap handleNoSuchAccountIdException(NoSuchAccountIdException ex) {
        var error = new Error.NoSuchAccountId(ex.id);
        return wrap(error);
    }


    /**
     * Convert a {@link AccountAlreadyExistsException} to an error response
     *
     * @param ex exception
     * @return response
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ErrorWrap handleAccountAlreadyExistsException(
        AccountAlreadyExistsException ex
    )
    {
        var error = new Error.AccountAlreadyExists(ex.charId);
        return wrap(error);
    }


    /**
     * Convert a {@link NegativeBalanceException} to an error response
     *
     * @param ex exception
     * @return response
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(NegativeBalanceException.class)
    public ErrorWrap handleNegativeBalanceException(
        NegativeBalanceException ex
    )
    {
        var error = new Error.InsufficientFunds();
        return wrap(error);
    }


    /**
     * Convert a {@link PropertyReferenceException} to an error response
     *
     * @param ex exception
     * @return response
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(PropertyReferenceException.class)
    public ErrorWrap handlePropertyReferenceException(
        PropertyReferenceException ex
    )
    {
        PropertyPath path = ex.getBaseProperty();
        var e = new Error.UnknownProperty(
            path == null ? null : path.toString(),
            ex.getPropertyName()
        );
        return wrap(e);
    }


    /**
     * Convert an {@link UnknownCharIdException} to an error response
     *
     * @param ex exception
     * @return response
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UnknownCharIdException.class)
    public ErrorWrap handleCharIdNotFoundException(UnknownCharIdException ex) {
        var error = new Error.CharIdNotFound(ex.charId);
        return wrap(error);
    }

    ///////////////////////////////////////////////////////
    // private helpers

    private static Error convertValidationError(ObjectError err) {
        return err instanceof FieldError fe ?
            convertFieldError(fe) :
            new Error.General(err.toString());
    }

    private static Error convertFieldError(FieldError ex) {
        if (isMissingCharField(ex)) {
            return new Error.CharIdRequired();
        }
        else if (isNegativeBalance(ex)) {
            return new Error.InsufficientFunds();
        }
        else return new Error.General(ex.toString());
    }

    private static boolean isMissingCharField(FieldError ex) {
        return ex.getField().equals(Constants.CHAR_ID_KEY) &&
            ex.getCode() != null && ex.getCode().equals("NotNull");
    }

    private static boolean isNegativeBalance(FieldError ex) {
        return ex.getField().equals(Constants.BALANCE_KEY) &&
            ex.getCode() != null && ex.getCode().equals("Min");
    }


    ///////////////////////////////////////////////////////
    // ErrorWrap (private)

    /**
     * A thin wrapper around a set of errors, assigning them
     * to key "errors"
     *
     * @param errors errors
     */
    private record ErrorWrap(Set<Error> errors) {}

    /**
     * Make the given {@link Error} into a singleton set
     * and wrap it with an {@link ErrorWrap}.
     *
     * @param error error
     * @return wrapper
     */
    private static ErrorWrap wrap(Error error) {
        return wrap(Set.of(error));
    }

    /**
     * Wrap a set of {@link Error}s in an {@link ErrorWrap}
     *
     * @param errors errors
     * @return wrapper
     */
    private static ErrorWrap wrap(Set<Error> errors) {
        return new ErrorWrap(errors);
    }

}