package jungle.ovengers.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class LoggingHandler {
    private static final String LOG_MESSAGE_FORMAT = "{} '{}' - {}";
    private static final String UNCAUGHT_LOG_MESSAGE = "??";


    public void writeLog(final Exception ex, final HttpServletRequest request) {
        try {
            log.error(LOG_MESSAGE_FORMAT, request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
        } catch (Exception e) {
            log.error(LOG_MESSAGE_FORMAT, UNCAUGHT_LOG_MESSAGE, UNCAUGHT_LOG_MESSAGE, e.getMessage(), e);
        }
    }
}
