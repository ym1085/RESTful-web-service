package com.restful.web.international;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class InternationalController {
    private final Logger log = LoggerFactory.getLogger(InternationalController.class);
    private final MessageSource messageSource;

    /**
     * 다국어 처리를 위한 API
     *
     * @param acceptLanguage
     * @return
     */
    @GetMapping("/language")
    public String handleInternationalLanguage(
            @RequestHeader(name = "Accept-Language", required = false) Locale acceptLanguage) {
        log.info("acceptLanguage = {}", acceptLanguage);
        return messageSource.getMessage("greeting.message", null, acceptLanguage);
    }
}
