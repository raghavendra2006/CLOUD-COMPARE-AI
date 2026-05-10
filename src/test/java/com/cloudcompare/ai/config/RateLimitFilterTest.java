package com.cloudcompare.ai.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;

class RateLimitFilterTest {

    private RateLimitFilter rateLimitFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rateLimitFilter = new RateLimitFilter();
    }

    @Test
    void testRateLimitPass() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/compare");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        rateLimitFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testNonApiPass() throws Exception {
        when(request.getRequestURI()).thenReturn("/index.html");

        rateLimitFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testXForwardedFor() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/compare");
        when(request.getHeader("X-Forwarded-For")).thenReturn("192.168.1.1, 10.0.0.1");

        rateLimitFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testRateLimitExceeded() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/compare");
        when(request.getRemoteAddr()).thenReturn("1.2.3.4");
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);

        for (int i = 0; i < 51; i++) {
            rateLimitFilter.doFilter(request, response, filterChain);
        }

        verify(response, atLeastOnce()).setStatus(429);
        verify(filterChain, times(50)).doFilter(request, response);
    }
}
