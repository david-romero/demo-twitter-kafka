package com.davromalc.demotwitterkafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.support.serializer.JsonSerde;

import com.davromalc.demotwitterkafka.model.InfluencerJsonSchema;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class DemoTwitterKafkaConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoTwitterKafkaConsumerApplication.class, args);
	}

	@Configuration
	@EnableKafkaStreams
	static class KafkaConsumerConfiguration {
		
		final Serde<Influencer> jsonSerde = new JsonSerde<>(Influencer.class);
		final Materialized<String, Influencer, KeyValueStore<Bytes, byte[]>> materialized = Materialized.<String, Influencer, KeyValueStore<Bytes, byte[]>>as("aggreation-tweets-by-likes").withValueSerde(jsonSerde);
		
		@Bean
		KStream<String, String> stream(StreamsBuilder streamBuilder){
			final KStream<String, String> stream = streamBuilder.stream("tweets");
			stream
				.selectKey(( key , value ) -> String.valueOf(value.split("::::")[0]))
				.groupByKey()
				.aggregate(Influencer::init, this::aggregateInfoToInfluencer, materialized)
				.mapValues(InfluencerJsonSchema::new)
				.toStream()
				.peek( (username, jsonSchema) -> log.info("Sending a new tweet from user: {}", username))
				.to("influencers", Produced.with(Serdes.String(), new JsonSerde<>(InfluencerJsonSchema.class)));
			return stream;
		}
		
		private Influencer aggregateInfoToInfluencer(String username, String tweet, Influencer influencer) {
			final long likes = Long.valueOf(tweet.split("::::")[2]);
			if ( likes >= influencer.getLikes() ) {
				return new Influencer(influencer.getTweets()+1, username, String.valueOf(tweet.split("::::")[1]), likes);
			} else {
				return new Influencer(influencer.getTweets()+1, username, influencer.getContent(), influencer.getLikes());
			}
		}
		
		@Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
		public StreamsConfig kStreamsConfigs(KafkaProperties kafkaProperties) {
			Map<String, Object> props = new HashMap<>();
			props.put(StreamsConfig.APPLICATION_ID_CONFIG, "demo-twitter-kafka-application");
			props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
			props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
			props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
			return new StreamsConfig(props);
		}
		
	}
	
	@RequiredArgsConstructor
	@Getter
	public static class Influencer {
		
		final long tweets;
		
		final String username;
		
		final String content;
		
		final long likes;
		
		
		static Influencer init() {
			return new Influencer(0, "","", 0);
		}
		
		@JsonCreator
		static Influencer fromJson(@JsonProperty("tweets") long tweetCounts, @JsonProperty("username") String username, @JsonProperty("content") String content, @JsonProperty("likes") long likes) {
			return new Influencer(tweetCounts, username, content, likes);
		}
		
	}
}
