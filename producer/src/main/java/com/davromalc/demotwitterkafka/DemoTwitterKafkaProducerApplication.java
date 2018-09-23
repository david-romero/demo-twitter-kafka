package com.davromalc.demotwitterkafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoTwitterKafkaProducerApplication {

	public static void main(String[] args) {
		// Kafka config
		Properties properties = new Properties();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); //Kafka cluster hosts.
		properties.put(ProducerConfig.CLIENT_ID_CONFIG, "demo-twitter-kafka-application-producer"); // Group id
		properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()); 
		properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		Producer<String, String> producer = new KafkaProducer<>(properties);

		SpringApplication.run(DemoTwitterKafkaProducerApplication.class, args);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> producer.close())); //Kafka producer should close when application finishes.
	}
}
