package com.davromalc.demotwitterkafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.common.base.Charsets;

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
		properties.put("bootstrap.servers", "localhost:9092");
		properties.put("serializer.class", "kafka.serializer.StringEncoder");
		properties.put("client.id", "demo-twitter-kafka-application-producer");
		properties.put("key.serializer", StringSerializer.class.getName());
		properties.put("value.serializer", StringSerializer.class.getName());
		Producer<String, String> producer = new KafkaProducer<>(properties);

		// Twitter Stream
		final TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
		final FilterQuery tweetFilterQuery = new FilterQuery();
		tweetFilterQuery.track(new String[] { "Java" });

		final StatusListener listener = new StatusListener() {
			public void onStatus(Status status) {
				final long likes = getLikes(status);
				final String tweet = getTweet(status);
				final String content = status.getUser().getName() + "::::" + tweet + "::::" + likes;
				log.info(content);
				producer.send(new ProducerRecord<>("tweets", content));
			}

			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
				log.debug("Status deletion: {}", statusDeletionNotice.getUserId());
			}

			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				log.debug("On notice: {}", numberOfLimitedStatuses);
			}

			public void onException(Exception ex) {
				log.error("Unable to get tweets", ex);
			}

			@Override
			public void onScrubGeo(long userId, long upToStatusId) {
				log.debug("Geo status: {} - {}", userId, upToStatusId);
			}

			@Override
			public void onStallWarning(StallWarning warning) {
				log.warn("Warning: {}", warning.getMessage());
			}

			private long getLikes(Status status) {
				return status.getRetweetedStatus() != null ? status.getRetweetedStatus().getFavoriteCount() : 0;
			}

			private String getTweet(Status status) {
				final byte[] tweetInUtf8 = status.getText().getBytes(Charsets.UTF_8);
				return new String(tweetInUtf8, Charsets.UTF_8);
			}
		};
		twitterStream.addListener(listener);
		twitterStream.filter(tweetFilterQuery);
		SpringApplication.run(DemoTwitterKafkaProducerApplication.class, args);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> producer.close()));
	}
}
