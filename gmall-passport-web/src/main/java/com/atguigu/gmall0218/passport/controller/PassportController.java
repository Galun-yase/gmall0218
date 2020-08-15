package com.atguigu.gmall0218.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall0218.bean.UserInfo;
import com.atguigu.gmall0218.passport.config.JwtUtil;
import com.atguigu.gmall0218.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 任青成
 * @date 2020/8/15 15:25
 */
@Controller
public class PassportController {

    @Reference
    UserService userService;
    @Value("${token.key}")
    String signKey;

    @RequestMapping("index")
    public String index(HttpServletRequest request) {
        String originUrl = request.getParameter("originUrl");
        request.setAttribute("originUrl", originUrl);
        return "index";
    }

    @RequestMapping("login")
    @ResponseBody
    public String login(UserInfo userInfo) {
        String salt = "47.99.222";
        UserInfo info = userService.login(userInfo);//比对信息，以及把信息存入redis,表示登录成功
        if (info != null) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("userId", info.getId());
            map.put("nickName", info.getNickName());
            String token = JwtUtil.encode(signKey, map, salt);//生成token
            return token;//生成的token返回给原页面的url+newToken=token
        } else {
            return "fail";
        }
    }

    @RequestMapping("verify")
    @ResponseBody
    public String verify(HttpServletRequest request) {
        String token = request.getParameter("token");
        String salt = request.getParameter("salt");
        Map<String, Object> map = JwtUtil.decode(token, signKey, salt);
        if (map != null && map.size() > 0) {
            String userId = (String) map.get("userId");
            UserInfo userInfo = userService.verify(userId);//从redis中是否存在key，value为userinfo
            if (userInfo != null) {
                return "success";
            } else {
                return "fail";
            }
        }
        return "fail";

    }

}
