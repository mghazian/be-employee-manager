package com.ghazian.employee_manager.core.filter;

import com.ghazian.employee_manager.core.models.ApiCallHistory;
import com.ghazian.employee_manager.core.repositories.ApiCallHistoryRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ApiLoggingFilter extends OncePerRequestFilter {

    private final ApiCallHistoryRepository apiCallHistoryRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(request, response);

        ApiCallHistory entry = ApiCallHistory.builder()
                .httpMethod(request.getMethod())
                .endpoint(request.getRequestURI())
                .responseStatus(response.getStatus())
                .build();

        apiCallHistoryRepository.save(entry);
    }
}
