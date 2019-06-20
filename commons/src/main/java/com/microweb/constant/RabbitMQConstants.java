package com.microweb.constant;

//忽略編譯器的提示警告 unused:未用or停用
//@SuppressWarnings("unused")

public class RabbitMQConstants {
    //重新發送的時間(當訊息投遞發送後，超過此時間皆為回傳確認時，則使用定時任務(cronjob)進行重新發送)
    public static final int RETRY_TIMEOUT = 60 * 1000; //1min

    //訂單關閉時間
    public static final long ORDER_UNPAID_CLOSE_TIMEOUT = /*30 * */60 * 1000; //30min

    /*
    public static final String ORDER_DIRECT_EXCHANGE = "ORDER.DIRECT.EXCHANGE";
    public static final String ORDER_TOPIC_EXCHANGE = "ORDER.TOPIC.EXCHANGE";
    public static final String ORDER_FANOUT_EXCHANGE = "ORDER.FANOUT.EXCHANGE";

    public static final String ORDER_DIRECT_QUEUE = "ORDER.DIRECT.QUEUE";
    public static final String ORDER_TOPIC_QUEUE = "ORDER.TOPIC.QUEUE";
    public static final String ORDER_FANOUT_QUEUE = "ORDER.FANOUT.QUEUE";
    */

    public static final String ORDER_CREATE_QUEUE = "ORDER.CREATE.QUEUE";
    public static final String ORDER_CREATE_DIRECT_EXCHANGE = "ORDER.CREATE.DIRECT.EXCHANGE";
    public static final String ORDER_CREATE_DIRECT_RK = "ORDER.CREATE.DIRECT_RK";

    //延遲佇列
    public static final class DelayQueue {
        //延遲佇列交換機 延遲佇列超時後，會變為死信佇列(Dead Letter Queue)
        public static final String DEAD_LETTER_EXCHANGE = "DEAD.LETTER.EXCHANGE";

        public static final String DELAY_QUEUE = "DELAY.QUEUE";
        public static final String DELAY_ROUTING_KEY = "DELAY.ROUTING.KEY";

        public static final String DELAY_PROCESS_EXCHANGE = "DELAY.PROCESS.EXCHANGE";
        public static final String DELAY_PROCESS_ROUTING_KEY = "DELAY.PROCESS.ROUTING.KEY";

        //死信佇列轉發後，實際處理的佇列
        //關閉訂單
        public static final String ORDER_CLOSE_DELAY_PROCESS_QUEUE = "ORDER.CLOSE.DELAY.PROCESS.QUEUE";
    }
}