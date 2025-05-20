package com.amusementpark.config;

import com.amusementpark.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Filter to protect endpoints that require authentication
 */
@Component
@Order(1)
public class AuthenticationFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    
    // List of paths that don't require authentication
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/logout"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String path = httpRequest.getRequestURI();
        
        // Allow public paths without authentication
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }
        
        // Check if user is authenticated
        HttpSession session = httpRequest.getSession(false);
        User user = session != null ? (User) session.getAttribute("user") : null;
        
        if (user == null) {
            logger.warn("Unauthorized access attempt to {}", path);
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().write("Unauthorized: Please login to access this resource");
            return;
        }
        
        // User is authenticated, proceed with the request
        chain.doFilter(request, response);
    }
    
    private boolean isPublicPath(String path) {
        // Check if the path is in the public paths list
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith) ||
               // Also allow static resources and root path
               path.startsWith("/css/") ||
               path.startsWith("/js/") ||
               path.startsWith("/images/") ||
               path.equals("/");
    }

    @Override
    public void init(FilterConfig filterConfig) {
        logger.info("Initializing Authentication Filter");
    }

    @Override
    public void destroy() {
        logger.info("Destroying Authentication Filter");
    }
}