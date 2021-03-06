package com.rockyzhu.kafka;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;

/**
 * Created by hozhu on 11/30/16.
 */
public class ASyncProducerDemo extends Thread {

  private final String _topic;
  private final KafkaProducer<Integer, String> _producer;
  private final int _step;
  private final int _start;

  public ASyncProducerDemo(String topic, int start, int step) {
    _start = start;
    _step = step;
    _topic = topic;
    Properties properties = new Properties();
    properties.put("bootstrap.servers",  "localhost:9092");
    properties.put("client.id", "DemoProducer");
    properties.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
    properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    _producer = new KafkaProducer<>(properties);
  }

  public void run() {
    int messageNum = _start;
    while (true) {
      String message = "Message_" + messageNum;
      _producer.send(new ProducerRecord<>(_topic, messageNum, message), new ASyncProducerCallback(messageNum, message));
      messageNum += _step;
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
      }
    }
  }

  private class ASyncProducerCallback implements Callback {

    private final int _messageNum;
    private final String _message;

    public ASyncProducerCallback(int messageNum, String message) {
      _messageNum = messageNum;
      _message = message;
    }

    @Override
    public void onCompletion(RecordMetadata metadata, Exception exception) {
      if (metadata != null) {
        System.out.println("topic:" + _topic + "key: " + _messageNum + ", value: " + _message + " sent to partition " + metadata.partition() + ", offset: " + metadata.offset());
      } else {
        System.out.println("Failed to send " + _message);
      }
    }
  }
}
