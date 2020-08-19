package com.atguigu.gmall0218.bean;

import com.atguigu.gmall0218.bean.enums.OrderStatus;
import com.atguigu.gmall0218.bean.enums.PaymentWay;
import com.atguigu.gmall0218.bean.enums.ProcessStatus;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderInfo implements Serializable {

    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column
    private String consignee;

    @Column
    private String consigneeTel;


    @Column
    private BigDecimal totalAmount;

    @Column
    private OrderStatus orderStatus;

    @Column
    private ProcessStatus processStatus;


    @Column
    private String userId;

    @Column
    private PaymentWay paymentWay;

    @Column
    private Date expireTime;

    @Column
    private String deliveryAddress;

    @Column
    private String orderComment;

    @Column
    private Date createTime;

    ////////////////////////////////

    @Column//拆单时产生，默认为空
    private String parentOrderId;

    @Column//物流编号，初始为空，发货后补充
    private String trackingNo;


    @Transient
    private List<OrderDetail> orderDetailList;


    @Transient
    private String wareId;

    @Column//第三方支付编号 订单编号
    private String outTradeNo;

    /**
     * 计算总金额
     */
    public void sumTotalAmount() {
        BigDecimal totalAmount = new BigDecimal("0");
        for (OrderDetail orderDetail : orderDetailList) {
            totalAmount = totalAmount.add(orderDetail.getOrderPrice().multiply(new BigDecimal(orderDetail.getSkuNum())));
        }
        this.totalAmount = totalAmount;
    }

    /**
     * xxx等n件商品
     */
    public String getTradeBody() {
        OrderDetail orderDetail = orderDetailList.get(0);
        String tradeBody = orderDetail.getSkuName() + "等" + orderDetailList.size() + "件商品";
        return tradeBody;
    }

}
