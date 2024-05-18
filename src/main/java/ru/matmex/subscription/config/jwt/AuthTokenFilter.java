package ru.matmex.subscription.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.matmex.subscription.services.UserService;
import ru.matmex.subscription.services.utils.JwtUtils;

import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {
    /**
     * Класс представляющий инструменты для работы с JWT
     */
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    /**
     * Фильтрирует запрос на авторизацию
     *
     * @param request     - запрос на авторизацию
     * @param response    ответ
     * @param filterChain цепочка фильтров
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = userService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new AuthenticationServiceException("Что-то пошло не так");
        }
        filterChain.doFilter(request, response);
        logger.info("Успешная авторизация пользователя");
    }

    /**
     * Извлечь JWT
     *
     * @param request запрос на авторизацию
     * @return JWT
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
