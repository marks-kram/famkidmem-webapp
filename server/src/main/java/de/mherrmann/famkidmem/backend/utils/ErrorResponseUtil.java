package de.mherrmann.famkidmem.backend.utils;

import de.mherrmann.famkidmem.backend.body.ResponseBodyError;
import org.springframework.http.HttpStatus;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

public class ErrorResponseUtil {

    public static HttpStatus getErrorStatus(HttpServletRequest request){
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Integer statusCode = 500;
        if(status != null) {
            statusCode = Integer.valueOf(status.toString());
        }
        return HttpStatus.resolve(statusCode);
    }

    public static ResponseBodyError getErrorResponse(HttpStatus httpStatus){
        String error;
        switch (httpStatus){
            case NOT_FOUND:
                error = "Bad URL. Wrong path or missing url arguments";
                break;
            case METHOD_NOT_ALLOWED:
                error = "Wrong request method";
                break;
            case UNSUPPORTED_MEDIA_TYPE:
                error = "Unsupported media type";
                break;
            default:
                error = "Unknown error";
                break;
        }
        return new ResponseBodyError(error);
    }
}
