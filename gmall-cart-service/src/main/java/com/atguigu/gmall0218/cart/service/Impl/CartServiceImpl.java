package com.atguigu.gmall0218.cart.service.Impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall0218.bean.CartInfo;
import com.atguigu.gmall0218.bean.SkuInfo;
import com.atguigu.gmall0218.cart.constant.CartConst;
import com.atguigu.gmall0218.cart.mapper.CartInfoMapper;
import com.atguigu.gmall0218.config.RedisUtil;
import com.atguigu.gmall0218.service.CartService;
import com.atguigu.gmall0218.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * @author 任青成
 * @date 2020/8/16 14:58
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartInfoMapper cartInfoMapper;
    @Reference
    private ManageService manageService;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void addToCart(String skuId, String userId, int skuNum) {
        Jedis jedis = redisUtil.getJedis();
        String cartKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CART_KEY_SUFFIX;

        // 采用哪种数据类型来存储！hash
        // key = user:userId:cart field = skuId value=CartInfo的字符串

        // 先通过skuId，userId 查询一下 是否有该商品！
        CartInfo cartInfo = new CartInfo();
        cartInfo.setUserId(userId);
        cartInfo.setSkuId(skuId);
        CartInfo cartInfoExist = cartInfoMapper.selectOne(cartInfo);
        // 有相同的商品
        if (cartInfoExist != null) {
            cartInfoExist.setSkuNum(cartInfoExist.getSkuNum() + skuNum);// 数量相加 skuNum = 1
            cartInfoExist.setSkuPrice(cartInfoExist.getCartPrice());// 给skuPrice 初始化操作！ skuPrice = cartPrice
            cartInfoMapper.updateByPrimaryKeySelective(cartInfoExist);
        } else { // 没有相同的商品！
            SkuInfo skuInfo = manageService.getSkuInfo(skuId);
            CartInfo cartInfo1 = new CartInfo();
            // 属性赋值
            cartInfo1.setSkuId(skuId);
            cartInfo1.setCartPrice(skuInfo.getPrice());
            cartInfo1.setSkuPrice(skuInfo.getPrice());
            cartInfo1.setSkuName(skuInfo.getSkuName());
            cartInfo1.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo1.setUserId(userId);
            cartInfo1.setSkuNum(skuNum);
            cartInfoMapper.insertSelective(cartInfo1);// 添加到数据库
            cartInfoExist = cartInfo1;
        }
        // 将数据放入缓存！
        jedis.hset(cartKey, skuId, JSON.toJSONString(cartInfoExist));
        // 给整个购物车设置过期时间，过期时间设置为该用户的过期时间
        String userKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USERINFOKEY_SUFFIX;
        Long ttl = jedis.ttl(userKey);
        jedis.expire(cartKey, ttl.intValue());

        jedis.close();
    }

    @Override//先看redis，再看数据库
    public List<CartInfo> getCartList(String userId) {
        List<CartInfo> cartInfoList = new ArrayList<>();

        Jedis jedis = redisUtil.getJedis();
        String cartKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CART_KEY_SUFFIX;
        // 从缓存中获取数据
        List<String> stringList = jedis.hvals(cartKey);
        if (stringList != null && stringList.size() > 0) {
            for (String cartInfoStr : stringList) {
                cartInfoList.add(JSON.parseObject(cartInfoStr, CartInfo.class));
            }
            cartInfoList.sort((Comparator.comparing(CartInfo::getId)));//物品排序按更新时间，这里按id
            return cartInfoList;
        } else {
            // 从数据库获取数据 order by ,并添加到缓存！
            cartInfoList = loadCartCache(userId);
            return cartInfoList;
        }
    }

    // 根据userId 查询购物车，并放入redis
    private List<CartInfo> loadCartCache(String userId) {
        // cartInfo , skuInfo 从这两张表中查询！查询不到实时价格！
        List<CartInfo> cartInfoList = cartInfoMapper.selectCartListWithCurPrice(userId);
        if (cartInfoList == null || cartInfoList.size() == 0) {
            return null;
        }

        // cartInfoList 存入redis
        Jedis jedis = redisUtil.getJedis();
        String cartKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CART_KEY_SUFFIX;
        HashMap<String, String> map = new HashMap<>();
        for (CartInfo cartInfo : cartInfoList) {
            map.put(cartInfo.getSkuId(), JSON.toJSONString(cartInfo));
        }
        // 一次放入多条数据
        jedis.hmset(cartKey, map);
        jedis.close();

        return cartInfoList;
    }

    @Override//将cookie中的数据加入到DB中，并且从DB中查出合并数据，并放入redis
    public List<CartInfo> mergeToCartList(List<CartInfo> cartListCK, String userId) {

        // 根据userId 获取购物车数据
        List<CartInfo> cartInfoListDB = cartInfoMapper.selectCartListWithCurPrice(userId);
        // 开始合并 合并条件：skuId 相同
        for (CartInfo cartInfoCK : cartListCK) {
            // 定义一个boolean 类型变量 默认值给false
            boolean isMatch = false;
            for (CartInfo cartInfoDB : cartInfoListDB) {
                if (cartInfoCK.getSkuId().equals(cartInfoDB.getSkuId())) {
                    // 将数量进行相加
                    cartInfoDB.setSkuNum(cartInfoCK.getSkuNum() + cartInfoDB.getSkuNum());
                    // 修改数据库
                    cartInfoMapper.updateByPrimaryKeySelective(cartInfoDB);
                    // 给true
                    isMatch = true;
                }
            }
            // 没有匹配上！
            if (!isMatch) {
                // 未登录的对象添加到数据库
                // 将用户Id 赋值给未登录对象
                cartInfoCK.setUserId(userId);
                cartInfoMapper.insertSelective(cartInfoCK);
            }
        }
        // 最终将合并之后的数据返回！
        List<CartInfo> cartInfoList = loadCartCache(userId);
        return cartInfoList;
    }
}
