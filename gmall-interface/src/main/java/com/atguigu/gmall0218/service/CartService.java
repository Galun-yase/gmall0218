package com.atguigu.gmall0218.service;

import com.atguigu.gmall0218.bean.CartInfo;

import java.util.List;

/**
 * @author 任青成
 * @date 2020/8/16 14:53
 */
public interface CartService {

    void addToCart(String skuId, String userId, int skuNum);

    /**
     * description:根据用户id，查询购物车，先看缓存，再看数据
     */
    List<CartInfo> getCartList(String userId);

    /**
     * description:把cookie中的购物车合并到数据库中
     */
    List<CartInfo> mergeToCartList(List<CartInfo> cartListCK, String userId);
}
