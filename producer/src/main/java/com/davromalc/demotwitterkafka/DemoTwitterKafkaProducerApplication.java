package com.davromalc.demotwitterkafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

@SpringBootApplication
public class DemoTwitterKafkaProducerApplication {

	public static void main(String[] args) {
		// Kafka config
		Properties properties = new Properties();
		properties.put("bootstrap.servers", "localhost:9092");
		properties.put("serializer.class", "kafka.serializer.StringEncoder");
		properties.put("client.id", "demo-twitter-kafka-application-producer");
		properties.put("key.serializer", StringSerializer.class.getName());
		properties.put("value.serializer", StringSerializer.class.getName());
		Producer<String, String> producer = new KafkaProducer<>(properties);

		// Twitter Stream
		final TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
		final StatusListener listener = new StatusListener() {
			public void onStatus(Status status) {
				
			}

			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
				
			}

			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				
			}

			public void onException(Exception ex) {
				
			}

			@Override
			public void onScrubGeo(long userId, long upToStatusId) {
				
			}

			@Override
			public void onStallWarning(StallWarning warning) {
				
			}
		};
		twitterStream.addListener(listener);
		final FilterQuery tweetFilterQuery = new FilterQuery();
		tweetFilterQuery.track(new String[] { "Java" });
		twitterStream.filter(tweetFilterQuery);
		SpringApplication.run(DemoTwitterKafkaProducerApplication.class, args);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> producer.close()));
	}
}
