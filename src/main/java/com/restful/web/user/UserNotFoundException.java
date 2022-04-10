package com.restful.web.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// HTTP Status code
// 2XX -> OK
// 4XX -> Client Error [Auth..]
// 5XX -> Server Error [Server program..]

/**
 * 존재하지 않는 유저의 id 값을 요청하면 예외 발생
 *
 * @author ymkim
 * @since 2022.04.10 Sun 15:35
 * @desc
 * ======================================================
 * HTTP Status code
 *      - 2XX -> OK
 *      - 4XX -> Client Error [Auth..]
 *      - 5XX -> Server Error [Server program..]
 * ======================================================
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
