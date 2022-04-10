package com.restful.web.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Global Exception Handling을 위한 Response 객체
 *
 * @author ymkim
 * @since 2022.04.10 Sun 15:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {
    private Date timestamp;
    private String message;
    private String details;
}
