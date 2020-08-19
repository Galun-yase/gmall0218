package com.atguigu.gmall0218.service;

import com.atguigu.gmall0218.bean.PaymentInfo;

/**
 * @author 任青成
 * @date 2020/8/18 15:59
 */
public interface PaymentService {

    /**
     * description: 保存支付信息
     */
    void savePaymentInfo(PaymentInfo paymentInfo);

    /**
     * description: 获取支付信息
     */
    PaymentInfo getPaymentInfo(PaymentInfo paymentInfoQuery);

    /**
     * description: 更新支付信息
     */
    void updatePaymentInfo(String out_trade_no, PaymentInfo paymentInfo);

    /**
     * description: 退款
     */
    boolean refund(String orderId);

    /**
     * description: 发送支付消息给订单模块
     */
    void sendPaymentResult(PaymentInfo paymentInfo, String result);

    /**
     * description: 根据orderId向支付宝查询订单状态
     */
    boolean checkPayment(PaymentInfo paymentInfoQuery);

    /**
     * description: 发送延迟队列
     */
    void sendDelayPaymentResult(String outTradeNo, int delaySec, int checkCount);

    /**
     * description: 关闭支付信息
     */
    void closePayment(String orderId);
}
