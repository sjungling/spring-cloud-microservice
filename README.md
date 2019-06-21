#### 開發環境
1. java 1.8+
2. Gradle Wrapper (統一gradle版本且免在本地安裝)
3. Docker & docker-compose

#### 框架
* Spring Boot 2.x

* Spring Boot AMQP
    * RabbitMQ
        * 訊息機制 - 透過訊息機制的MQ可靠性，採取分散式交易(事務)處理資料，達成最終一致性
        * 延遲佇列 - 訂單超過三十分鐘未付款取消
* Spring Cloud Gateway
    * 統一服務轉發接口
    * 限流
* Spring Cloud OpenFeign
    * 服務調用
* Spring Cloud Eureka
    * Server: 註冊中心
    * Client: 服務發現
    * 註冊中心高可用(群集)
* Spring Data JPA 
    * MySQL (DBPool - HikariPool)
    * Redis (RedisPool - lettuce)
    * MongoDB

#### 第三方套件
1. Lombok
2. Swagger2
3. Redisson(Redis分散式鎖)
    優點:
    * 具有可重入鎖，防止死鎖
    *  key-value 讀寫快速
    *  解決高併發大量請求下超賣問題
    缺點:
    * 鎖超時問題，太短則業務來不及執行完畢，太長則等待時間過長
    解決方法:
    * 搭配其他分散式鎖如zookeeper等
#### 建置
./gradlew clean build

docker-compose up -d