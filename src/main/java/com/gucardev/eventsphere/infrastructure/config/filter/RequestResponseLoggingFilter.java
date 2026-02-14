package com.gucardev.eventsphere.infrastructure.config.filter;

import com.gucardev.eventsphere.infrastructure.config.properties.AppSpecificProperties;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);
    private static final int MAX_PAYLOAD_SIZE = 5120; // 5KB
    private final List<String> ignorePaths;

    public RequestResponseLoggingFilter(AppSpecificProperties appProperties) {
        this.ignorePaths = appProperties.getIgnoredLoggingPaths();
    }

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(@Nonnull HttpServletRequest request) {
        // Check if the request URI matches any of the ignore patterns
        return ignorePaths.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, request.getRequestURI()));
    }

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // This method will only be called if shouldNotFilter() returns false
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request,0);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long timeTaken = System.currentTimeMillis() - startTime;
            logRequest(requestWrapper);
            logResponse(responseWrapper, timeTaken);
            responseWrapper.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        String requestBody = getContentAsString(request.getContentAsByteArray(), request.getCharacterEncoding());
        String headers = Collections.list(request.getHeaderNames()).stream()
                .map(headerName -> headerName + ": " + Collections.list(request.getHeaders(headerName)))
                .collect(Collectors.joining("\n"));

        log.debug("""
                        
                        === Request ===
                        Client IP: {}
                        Method: {}
                        URI: {}
                        Query Params: {}
                        Headers: {}
                        Body: {}
                        """,
                request.getRemoteAddr(),
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                headers,
                requestBody);
    }

    private void logResponse(ContentCachingResponseWrapper response, long timeTaken) {
        String responseBody = getContentAsString(response.getContentAsByteArray(), response.getCharacterEncoding());
        String headers = response.getHeaderNames().stream()
                .map(headerName -> headerName + ": " + response.getHeaders(headerName))
                .collect(Collectors.joining("\n"));

        log.debug("""
                        
                        === Response ===
                        Status: {}
                        Body: {}
                        Headers: {}
                        Time Taken: {} ms
                        """,
                response.getStatus(),
                responseBody,
                headers,
                timeTaken);
    }

    private String getContentAsString(byte[] buf, String charsetName) {
        if (buf == null || buf.length == 0) {
            return "";
        }
        try {
            int length = Math.min(buf.length, MAX_PAYLOAD_SIZE);
            String content = new String(buf, 0, length, charsetName);
            if (buf.length > MAX_PAYLOAD_SIZE) {
                content += "... [TRUNCATED]";
            }
            return content;
        } catch (UnsupportedEncodingException ex) {
            return "Failed to parse content as string";
        }
    }
}