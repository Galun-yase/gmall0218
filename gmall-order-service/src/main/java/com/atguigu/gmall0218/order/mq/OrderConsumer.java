package com.atguigu.gmall0218.order.mq;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall0218.bean.enums.ProcessStatus;
import com.atguigu.gmall0218.service.OrderService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;

/**
 * @author 任青成
 * @date 2020/8/19 19:52
 */
@Component
public class OrderConsumer {

    @Reference
    private OrderService orderService;

    @JmsListener(destination = "PAYMENT_RESULT_QUEUE", containerFactory = "jmsQueueListener")
    public void consumerPaymentResult(MapMessage mapMessage) throws JMSException {

        String orderId = mapMessage.getString("orderId");
        String result = mapMessage.getString("result");

        if ("success".equals(result)) {
            //接收到支付成功消息 更新订单状态
            orderService.updateOrderStatus(orderId, ProcessStatus.PAID);
            //发送消息给仓储
            orderService.sendOrderStatus(orderId);
            //更新订单状态为 已通知仓储
            orderService.updateOrderStatus(orderId, ProcessStatus.NOTIFIED_WARE);
        } else {
            orderService.updateOrderStatus(orderId, ProcessStatus.UNPAID);
        }

    }

    @JmsListener(destination = "SKU_DEDUCT_QUEUE", containerFactory = "jmsQueueListener")
    public void consumeSkuDeduct(MapMessage mapMessage) throws JMSException {
        // 通过mapMessage获取
        String orderId = mapMessage.getString("orderId");
        String status = mapMessage.getString("status");
        if ("DEDUCTED".equals(status)) {
            orderService.updateOrderStatus(orderId, ProcessStatus.DELEVERED);
        } else {
            orderService.updateOrderStatus(orderId, ProcessStatus.STOCK_EXCEPTION);
        }
    }

}
