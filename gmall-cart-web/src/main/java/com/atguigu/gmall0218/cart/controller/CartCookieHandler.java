package com.atguigu.gmall0218.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall0218.bean.CartInfo;
import com.atguigu.gmall0218.bean.SkuInfo;
import com.atguigu.gmall0218.config.CookieUtil;
import com.atguigu.gmall0218.service.ManageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 任青成
 * @date 2020/8/16 16:06
 */
@Component
public class CartCookieHandler {
    private final String cookieCartName = "CART";// 定义购物车名称
    private final int COOKIE_CART_MAXAGE = 7 * 24 * 3600;    // 设置cookie 过期时间

    @Reference
    private ManageService manageService;


    public void addToCart(HttpServletRequest request, HttpServletResponse response, String skuId, String userId, int skuNum) {
        String cookieValue = CookieUtil.getCookieValue(request, cookieCartName, true);// 从cookie 中获取购物车数据
        List<CartInfo> cartInfoList = new ArrayList<>();

        boolean ifExist = false;// 如果没有，则直接添加到集合！记住一个boolean 类型变量处理！
        if (StringUtils.isNotEmpty(cookieValue)) {
            cartInfoList = JSON.parseArray(cookieValue, CartInfo.class);
            for (CartInfo cartInfo : cartInfoList) {
                // 比较添加商品的Id
                if (cartInfo.getSkuId().equals(skuId)) {
                    cartInfo.setSkuNum(cartInfo.getSkuNum() + skuNum);
                    cartInfo.setSkuPrice(cartInfo.getCartPrice());
                    ifExist = true;
                }
            }
        }
        // 在购物车中没有该商品
        if (!ifExist) {
            SkuInfo skuInfo = manageService.getSkuInfo(skuId);
            // 将商品添加到集合中
            CartInfo cartInfo = new CartInfo();
            cartInfo.setSkuId(skuId);
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setSkuPrice(skuInfo.getPrice());
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo.setUserId(userId);
            cartInfo.setSkuNum(skuNum);

            cartInfoList.add(cartInfo);
        }
        // 将最终的集合放入cookie 中
        CookieUtil.setCookie(request, response, cookieCartName, JSON.toJSONString(cartInfoList), COOKIE_CART_MAXAGE, true);
    }

    /**
     * description: 查询cookie中的购物车列表
     */
    public List<CartInfo> getCartList(HttpServletRequest request) {
        String cartJson = CookieUtil.getCookieValue(request, cookieCartName, true);
        if (StringUtils.isNotEmpty(cartJson)) {
            List<CartInfo> cartInfoList = JSON.parseArray(cartJson, CartInfo.class);
            return cartInfoList;
        }
        return null;
    }

    /**
     * description: 删除cookie中的购物车
     */
    public void deleteCartCookie(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.deleteCookie(request, response, cookieCartName);
    }

    /**
     * description: 未登录状态下，记录选中状态
     */
    public void checkCart(HttpServletRequest request, HttpServletResponse response, String skuId, String isChecked) {
        List<CartInfo> cartList = getCartList(request);
        for (CartInfo cartInfo : cartList) {
            if (cartInfo.getSkuId().equals(skuId)) {
                cartInfo.setIsChecked(isChecked);
            }
        }
        String newCartJson = JSON.toJSONString(cartList);
        CookieUtil.setCookie(request, response, cookieCartName, newCartJson, COOKIE_CART_MAXAGE, true);
    }
}
