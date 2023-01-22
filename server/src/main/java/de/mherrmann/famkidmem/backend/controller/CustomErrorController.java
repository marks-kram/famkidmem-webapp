package de.mherrmann.famkidmem.backend.controller;

import de.mherrmann.famkidmem.backend.body.ResponseBodyError;
import de.mherrmann.famkidmem.backend.utils.ErrorResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class CustomErrorController implements ErrorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomErrorController.class);

    @RequestMapping("/error")
    public ResponseEntity<ResponseBodyError> handleError(HttpServletRequest request) {
        HttpStatus httpStatus = ErrorResponseUtil.getErrorStatus(request);
        ResponseBodyError errorResponse = ErrorResponseUtil.getErrorResponse(httpStatus);
        LOGGER.error("Request error: status:{}; message:{}", httpStatus.value(), errorResponse.getMessage());
        if("head".equalsIgnoreCase(request.getMethod())){
            return ResponseEntity.status(httpStatus).build();
        }
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    @Override
    @Deprecated
    public String getErrorPath() {
        return "/error";
    }
}
