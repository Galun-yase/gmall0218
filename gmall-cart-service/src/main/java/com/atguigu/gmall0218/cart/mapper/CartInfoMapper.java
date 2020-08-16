package com.atguigu.gmall0218.cart.mapper;

import com.atguigu.gmall0218.bean.CartInfo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author 任青成
 * @date 2020/8/16 14:49
 */
public interface CartInfoMapper extends Mapper<CartInfo> {
    /**
     * 根据userId 查询实时价格 到cartInfo 中
     *
     * @param userId
     * @return
     */
    List<CartInfo> selectCartListWithCurPrice(String userId);
}
