package jkml;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

@Component
public class RequestLoggingInterceptor implements AsyncHandlerInterceptor {

	private final Logger log = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    	log.info("Received request: {}", request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    	log.info("Sent response: {}", response.getStatus());
    }

}
