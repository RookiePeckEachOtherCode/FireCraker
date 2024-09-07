package com.rookie.aspect;

import com.rookie.annotation.AuthRequired;
import com.rookie.model.Token;
import com.rookie.model.result.BaseResult;
import com.rookie.model.result.ResultCode;
import com.rookie.utils.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class Auth {
    private static final ThreadLocal<Token> tokenThreadLocal = new ThreadLocal<>();
    @Value("${jwt.secret}")
    private String secret;

    public static Token getToken() {
        return tokenThreadLocal.get();
    }

    @Before("@annotation(authRequired) && args(..,token)")
    public BaseResult<String> beforeMethodWithAuthRequired(AuthRequired authRequired, String token) {
        return tokenHandler(token);
    }

    @Before("@annotation(authRequired) && args(..,req)")
    public BaseResult<String> beforeMethodWithAuthRequired(AuthRequired authRequired, HttpServletRequest req) {
        var jwtToken = req.getParameter("token");
        return tokenHandler(jwtToken);
    }


    @After("@annotation(authRequired) && args(..,token)")
    public void afterMethodWithAuthRequired(AuthRequired authRequired, String token) {
        tokenThreadLocal.remove();
    }

    @After("@annotation(authRequired) && args(..,request)")
    public void afterMethodWithAuthRequired(AuthRequired authRequired, HttpServletRequest request) {
        tokenThreadLocal.remove();
    }

    @NotNull
    private BaseResult<String> tokenHandler(String token) {
        if (token == null) {
            throw new RuntimeException(ResultCode.PARAM_IS_BLANK.getMsg());
        }

        try {
            Token _token = JWTUtils.tokenParse(token, secret);
            tokenThreadLocal.set(_token);
            return BaseResult.success(token);
        } catch (Exception e) {
            throw new RuntimeException(ResultCode.PERMISSION_TOKEN_INVALID.getMsg());
        }
    }

}
