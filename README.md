# Gamll商城
#### 商城架构
![项目架构](propertites\项目架构.png)
#### 项目模块依赖关系
![项目模块依赖关系](propertites\项目模块依赖.png)
#### sku与spu数据表关系
![sku与spu数据表关系](propertites\sku_spu.png)
#### 端口

- 8081 user-manage
- 8082 manage-web
- 8083 manage-service
- 8084 item-web
- 8085 order-web
- 8086 order-service
- 8087 passport-web
- 8088 cart-web
- 8089 cart-service
- 8090 gware-manage
- 8091 payment

### 项目描述
Gmall商城是一个综合的电商购物平台，采用分布式架构设计。平台主要销售电子产品，能够提供给消费者一个电子产品购买平台。
该项目实现了商品首页及详情,分类查询搜索,SSO单点登录,购物车,订单,支付等电商平台场景,完成商品从浏览到购买的基本功能。

### 涉及技术：SpringBoot、MyBatis、Dubbo、Zookeeper、MySQL、Redis、ActiveMQ、fastDFS

### 功能描述
- 使用fastDFS分布式文件系统存储商城平台使用的静态资源,实现静态资源的异步加载,提高用户体验
- 商品详情页面使用Redis做缓存优化，通过jedis,redisson两种方式实现分布式锁
- 使用Redis+JWT+Interceptor完成SSO单点登录，解决跨域登录session失效
- 通过ActiveMQ消息队列,延迟队列完成分布式事务,解决分布式环境下一致性问题
- 购物车模块使用cookie、redis两种方式实现,解决登录和未登录状态下的添加需求
