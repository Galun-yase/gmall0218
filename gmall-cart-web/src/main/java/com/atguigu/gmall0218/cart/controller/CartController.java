package com.atguigu.gmall0218.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall0218.bean.CartInfo;
import com.atguigu.gmall0218.bean.SkuInfo;
import com.atguigu.gmall0218.config.LoginRequire;
import com.atguigu.gmall0218.service.CartService;
import com.atguigu.gmall0218.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author 任青成
 * @date 2020/8/16 13:21
 */
@Controller
public class CartController {

    @Reference
    private CartService cartService;
    @Reference
    private ManageService manageService;
    @Autowired
    private CartCookieHandler cartCookieHandler;

    // 如何区分用户是否登录？只需要看userId
    @RequestMapping("addToCart")
    @LoginRequire(autoRedirect = false)
    public String addToCart(HttpServletRequest request, HttpServletResponse response) {
        // 获取商品数量
        String skuNum = request.getParameter("skuNum");
        String skuId = request.getParameter("skuId");

        // 获取userId
        String userId = (String) request.getAttribute("userId");
        if (userId != null) {
            // 调用登录添加购物车
            cartService.addToCart(skuId, userId, Integer.parseInt(skuNum));
        } else {
            // 调用未登录添加购物车
            cartCookieHandler.addToCart(request, response, skuId, userId, Integer.parseInt(skuNum));
        }
        // 根据skuId 查询skuInfo
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);

        request.setAttribute("skuNum", skuNum);
        request.setAttribute("skuInfo", skuInfo);
        return "success";
    }


    @RequestMapping("cartList")
    @LoginRequire(autoRedirect = false)
    public String cartList(HttpServletRequest request) {
        // 获取userId
        String userId = (String) request.getAttribute("userId");
        List<CartInfo> cartInfoList = null;
        if (userId != null) {
            // 合并购物车：
            List<CartInfo> cartListCK = cartCookieHandler.getCartList(request);
            if (cartListCK != null && cartListCK.size() > 0) {
                // 合并购物车
                cartInfoList = cartService.mergeToCartList(cartListCK, userId);
            } else {
                // 登录状态下查询购物车
                cartInfoList = cartService.getCartList(userId);
            }

        } else {
            // 调用未登录添加购物车
            cartInfoList = cartCookieHandler.getCartList(request);
        }
        // 保存购物车集合
        request.setAttribute("cartInfoList", cartInfoList);
        return "cartList";
    }


}
