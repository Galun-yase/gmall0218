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
}
