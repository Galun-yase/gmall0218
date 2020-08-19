package com.atguigu.gmall0218.payment.mq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

/**
 * @author 任青成
 * @date 2020/8/18 23:45
 */
public class ProducerTest {
    public static void main(String[] args) throws JMSException {
                /*
        1.  创建连接工厂
        2.  创建连接
        3.  打开连接
        4.  创建session
        5.  创建队列
        6.  创建消息提供者
        7.  创建消息对象
        8.  发送消息
        9.  关闭
         */

//        Connection connection = new ProducerTest().activeMQUtil.getConnection();
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory("tcp://47.99.222.232:61616");
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        // 第一个参数：是否开启事务
        // 第二个参数：表示开启/关闭事务的相应配置参数，
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//        Session session = connection.createSession(true, Session.SESSION_TRANSACTED); // 必须提交

        Queue atguigu = session.createQueue("atguigu-true");

        MessageProducer producer = session.createProducer(atguigu);

        ActiveMQTextMessage activeMQTextMessage = new ActiveMQTextMessage();
        activeMQTextMessage.setText("困死了！特别想睡");
        producer.send(activeMQTextMessage);

        //session.commit();

        producer.close();
        session.close();
        connection.close();

    }
}
