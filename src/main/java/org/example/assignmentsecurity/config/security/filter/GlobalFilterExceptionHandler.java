package org.example.assignmentsecurity.config.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.assignmentsecurity.common.error.BusinessException;
import org.example.assignmentsecurity.common.error.ErrorCode;
import org.example.assignmentsecurity.common.error.SecurityFilterChainException;
import org.example.assignmentsecurity.common.format.ApiResult;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class GlobalFilterExceptionHandler extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (SecurityFilterChainException ex) {
            if (!response.isCommitted()) {
                log.info("SecurityFilterChainException 오류 발생 : {}", ex.getMessage());
                errorResponse(response, ex.getErrorCode());
            }
        } catch (BusinessException ex) {
            if (!response.isCommitted()) {
                log.info("BusinessException 오류 발생 : {}", ex.getMessage());
                errorResponse(response, ex.getErrorCode());
            }
        } catch (Exception ex) {
            if (!response.isCommitted()) {
                log.info("Exception 발생 : {}", ex.getMessage());
                errorResponse(response, ErrorCode.SERVER_ERROR);
            }
        }
    }

    private void errorResponse(HttpServletResponse response, ErrorCode ex) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(ex.getStatus());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(ApiResult.error(ex)));
    }
}
