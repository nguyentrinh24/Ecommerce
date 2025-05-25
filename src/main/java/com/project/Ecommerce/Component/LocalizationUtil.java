package com.project.Ecommerce.Component;


import com.project.Ecommerce.Util.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class LocalizationUtil {

    private final LocaleResolver localeResolver;
    private final MessageSource messageSource;

    public String getMessage(String messageKey, Object... params) { //spread operator
        HttpServletRequest request = WebUtils.getRequest();
        Locale locale = localeResolver.resolveLocale(request);
        return messageSource.getMessage(messageKey, params, locale);
    }
}
