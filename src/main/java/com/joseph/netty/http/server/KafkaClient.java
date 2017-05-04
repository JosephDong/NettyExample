package com.joseph.netty.http.server;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * Created by dys09435 on 2017/5/4.
 */
public class KafkaClient {
    private static Producer<String, String> producer = null;

    static {
        Properties props = new Properties();
        props.put("bootstrap.servers","kmaster:9092,kslave1:9092,kslave2:9092,kslave3:9092,kslave4:9092");
        props.put("acks", "1");
        props.put("retries", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(props);
    }

    public static void send(final String topic, final String value){
        producer.send(new ProducerRecord<>(topic, value));
    }

    public static void close(){
        if(producer != null){
            producer.close();
        }
    }
}
