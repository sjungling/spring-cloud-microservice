package com.microweb.mq.repository;

import com.microweb.mq.entity.BrokerMessageLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface BrokerMessageLogRepository extends MongoRepository<BrokerMessageLog, String> {
    //@Query(value = "{messageId: ?0}")
    @Query("{'messageId': {'$eq': ?0}}")
    BrokerMessageLog findByMessageId(Long messageId);

    //gte:大於等於, lte:小於等於
    @Query("{'UpdateTime':{$gte: ?0, $lte: ?1}}")
    Page<BrokerMessageLog> findByUpdateTimeBetween(Date beginTime, Date endTime, Pageable pageable);
}