package com.atguigu.gmall0218.config;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall0218.util.HttpClientUtil;
import io.jsonwebtoken.impl.Base64UrlCodec;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author 任青成
 * @date 2020/8/15 21:15
 */
@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {
    // 用户进入控制器之前！
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getParameter("newToken");
        //将token放入到cookie中
        if (token != null) {//放token
            CookieUtil.setCookie(request, response, "token", token, WebConst.COOKIE_MAXAGE, false);
        }
        // 当用户访问非登录之后的页面，登录之后，继续访问其他业务模块时，url 并没有newToken，但是后台可能将token 放入了cookie 中！
        if (token == null) {//取token
            token = CookieUtil.getCookieValue(request, "token", false);
        }
        // 从cookie 中获取token，解密token！
        if (token != null) {
            // 开始解密token 获取nickName
            Map map = getUserMapByToken(token);

            // 取出用户昵称
            String nickName = (String) map.get("nickName");
            // 保存到作用域
            request.setAttribute("nickName", nickName);
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        LoginRequire methodAnnotation = handlerMethod.getMethodAnnotation(LoginRequire.class);
        if (methodAnnotation != null) {
            String salt = "47.99.222";
            String result = HttpClientUtil.doGet(WebConst.VERIFY_ADDRESS + "?token=" + token + "&salt=" + salt);
            if ("success".equals(result)) {//认证成功
                Map map = getUserMapByToken(token);
                String userId = (String) map.get("userId");
                request.setAttribute("userId", userId);
                return true;
            } else {//认证失败，且必须登录，则跳转到登录界面
                if (methodAnnotation.autoRedirect()) {
                    String requestURL = request.getRequestURL().toString();
                    System.out.println("requestURL:" + requestURL); // http://item.gmall.com/36.html
                    String encodeURL = URLEncoder.encode(requestURL, "UTF-8");
                    System.out.println("encodeURL：" + encodeURL); //  http%3A%2F%2Fitem.gmall.com%2F36.html
                    response.sendRedirect(WebConst.LOGIN_ADDRESS + "?originUrl=" + encodeURL);
                    return false;
                }
            }
        }
        return true;
    }

    // 解密token获取map数据
    private Map getUserMapByToken(String token) {
        // token=eyJhbGciOiJIUzI1NiJ9.eyJuaWNrTmFtZSI6IkF0Z3VpZ3UiLCJ1c2VySWQiOiIxIn0.XzRrXwDhYywUAFn-ICLJ9t3Xwz7RHo1VVwZZGNdKaaQ
        // 获取中间部分
        String tokenUserInfo = StringUtils.substringBetween(token, ".");
        Base64UrlCodec base64UrlCodec = new Base64UrlCodec();
        byte[] decode = base64UrlCodec.decode(tokenUserInfo);
        String mapJson = null;
        mapJson = new String(decode, StandardCharsets.UTF_8);
        return JSON.parseObject(mapJson, Map.class);

    }

    // 进入控制器之后，视图渲染之前！
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    // 视图渲染之后！
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }
}
