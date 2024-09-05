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

    @Before("@annotation(authRequired) && args(..,request) ")
    public BaseResult<String> beforeMethodWithAuthRequired(AuthRequired authRequired, HttpServletRequest request) {
        var jwtToken = request.getParameter("token");
        if (jwtToken == null) {
            throw new RuntimeException(ResultCode.PARAM_IS_BLANK.getMsg());
        }

        try {
            Token token = JWTUtils.tokenParse(jwtToken, secret);
            tokenThreadLocal.set(token);
            return BaseResult.success(jwtToken);
        } catch (Exception e) {
            throw new RuntimeException(ResultCode.PERMISSION_TOKEN_INVALID.getMsg());
        }
    }
    @After("@annotation(authRequired) && args(..,request)")
    public void afterMethodWithAuthRequired(AuthRequired authRequired, HttpServletRequest request) {
        tokenThreadLocal.remove();
    }
}
