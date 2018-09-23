package com.davromalc.demotwitterkafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

@SpringBootApplication
@Slf4j
public class DemoTwitterKafkaProducerApplication {

	public static void main(String[] args) {
		// Kafka config
		Properties properties = new Properties();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); //Kafka cluster hosts.
		properties.put(ProducerConfig.CLIENT_ID_CONFIG, "demo-twitter-kafka-application-producer"); // Group id
		properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()); 
		properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		Producer<String, String> producer = new KafkaProducer<>(properties);

		// Twitter Stream
		final TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
		final StatusListener listener = new StatusListener() {
			public void onStatus(Status status) {
				final long likes = getLikes(status);
				final String tweet = status.getText();
				final String content = status.getUser().getName() + "::::" + tweet + "::::" + likes;
				log.info(content);
				producer.send(new ProducerRecord<>("tweets", content));
			}

			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
			}

			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
			}

			public void onException(Exception ex) {
				log.error("Unable to get tweets", ex);
			}

			@Override
			public void onScrubGeo(long userId, long upToStatusId) {
			}

			@Override
			public void onStallWarning(StallWarning warning) {
			}

			private long getLikes(Status status) {
				return status.getRetweetedStatus() != null ? status.getRetweetedStatus().getFavoriteCount() : 0;
			}
		};
		twitterStream.addListener(listener);
		final FilterQuery tweetFilterQuery = new FilterQuery();
		tweetFilterQuery.track(new String[] { "Java" });
		twitterStream.filter(tweetFilterQuery);
		SpringApplication.run(DemoTwitterKafkaProducerApplication.class, args);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> producer.close())); //Kafka producer should close when application finishes.
	}
}
