package com.atguigu.gmall0218.bean;

import com.atguigu.gmall0218.bean.enums.PaymentStatus;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class PaymentInfo implements Serializable {

    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column//订单编号，对外交易编号
    private String outTradeNo;

    @Column
    private String orderId;

    @Column//支付宝回调时生成
    private String alipayTradeNo;

    @Column//订单金额
    private BigDecimal totalAmount;

    @Column//交易内容
    private String Subject;

    @Column//支付状态
    private PaymentStatus paymentStatus;

    @Column
    private Date createTime;

    @Column//回调时间
    private Date callbackTime;

    @Column//回调信息
    private String callbackContent;

}

