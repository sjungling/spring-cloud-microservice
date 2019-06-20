package com.microweb.utils;

import com.microweb.enums.BrokerMessageLogStatusEnum;
import com.microweb.mq.entity.BrokerMessageLog;
import com.microweb.mq.repository.BrokerMessageLogRepository;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class BrokerMessageLogUtils {
    @Autowired
    MongoOperations mongoOperations; //implement MongoTemplate

    @Autowired
    private BrokerMessageLogRepository brokerMessageLogRepository;


    public void save(BrokerMessageLog brokerMessageLog) {
        brokerMessageLogRepository.save(brokerMessageLog);
    }

    public UpdateResult updateStatusByMessageId(String messageId, BrokerMessageLogStatusEnum brokerMessageLogStatusEnum, Date updateTime) {
        Query query = new Query();
        query.addCriteria(Criteria.where("messageId").is(messageId));

        Update update = new Update();
        update.set("status", brokerMessageLogStatusEnum.getCode());
        update.set("updateTime", updateTime);

        System.out.println(update);
        return mongoOperations.updateFirst(query, update, BrokerMessageLog.class);
    }

    /*
    public UpdateResult updateField(){

    }
    */
}