package br.com.xfrontier.sgetea.core.filters;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.xfrontier.sgetea.api.v1.dtos.responses.ErrorResponse;
import br.com.xfrontier.sgetea.core.exceptions.TokenServiceException;
import br.com.xfrontier.sgetea.core.services.token.adapters.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AccessTokenRequestFilter extends OncePerRequestFilter {

    private static final String TOKEN_TYPE= "Bearer ";

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        try {
            tryDoFilterInternal(request, response, filterChain);
        } catch (TokenServiceException exception) {
            var status= HttpStatus.UNAUTHORIZED;

            var errorResponse= ErrorResponse.builder()
                .status(status.value())
                .timestamp(LocalDateTime.now())
                .message(exception.getLocalizedMessage())
                .path(request.getRequestURI())
                .build();

            var json= objectMapper.writeValueAsString(errorResponse);

            response.setStatus(status.value());
            response.setHeader("Content-Type", "application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write(json);
        }
    }

    private void tryDoFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain)
        throws IOException, ServletException {
        var token= "";
        var email= "";

        var authorizationHeader= request.getHeader("Authorization");

        if (isTokenPresent(authorizationHeader)) {
            token= authorizationHeader.substring(TOKEN_TYPE.length());
            email= tokenService.getSubjetDoAccessToken(token);
        }

        if (isEmailNotInContext(email)) {
            addEmailInContext(request, email);
        }

        filterChain.doFilter(request, response);
    }

    private Boolean isTokenPresent(String authorizationHeader) {
        return authorizationHeader != null && authorizationHeader.startsWith(TOKEN_TYPE);
    }

    private Boolean isEmailNotInContext(String email) {
        return !email.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private void addEmailInContext(HttpServletRequest request, String email) {
        var user= userDetailsService.loadUserByUsername(email);

        var autentication= new UsernamePasswordAuthenticationToken(user, null,
            user.getAuthorities());
        autentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(autentication);
    }

}
