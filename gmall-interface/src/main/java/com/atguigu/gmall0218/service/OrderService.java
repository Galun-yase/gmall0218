package com.atguigu.gmall0218.service;

import com.atguigu.gmall0218.bean.OrderInfo;
import com.atguigu.gmall0218.bean.enums.ProcessStatus;

import java.util.List;

/**
 * @author 任青成
 * @date 2020/8/17 16:06
 */
public interface OrderService {

    /**
     * description: 保存订单信息 orderinfo /orderdetail
     */
    String saveOrder(OrderInfo orderInfo);

    /**
     * description: 生成流水号
     */
    String getTradeNo(String userId);

    /**
     * description: 比较流水号
     *
     * @param userId      获取缓存的流水号
     * @param tradeCodeNo 页面的流水号
     */
    boolean checkTradeCode(String userId, String tradeCodeNo);

    /**
     * description: 删除缓存中的流水号
     */
    void delTradeCode(String userId);

    /**
     * description: 验证库存
     */
    boolean checkStock(String skuId, Integer skuNum);

    /**
     * description: 根据订单id获取订单信息
     */
    OrderInfo getOrderInfo(String orderId);

    /**
     * description: 更新订单状态
     */
    void updateOrderStatus(String orderId, ProcessStatus processStatus);

    /**
     * description: 给仓储发送消息
     */
    void sendOrderStatus(String orderId);

    /**
     * description: 查询过期订单列表
     */
    List<OrderInfo> getExpiredOrderList();

    /**
     * description: 处理过期订单
     */
    void execExpiredOrder(OrderInfo orderInfo);
}
