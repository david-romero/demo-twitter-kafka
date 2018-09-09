package com.davromalc.demotwitterkafka.model;

import java.util.Arrays;
import java.util.List;

import com.davromalc.demotwitterkafka.DemoTwitterKafkaConsumerApplication.Influencer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * https://gist.github.com/rmoff/2b922fd1f9baf3ba1d66b98e9dd7b364
 * 
 *
 */
@Getter
public class InfluencerJsonSchema {

	Schema schema;
	Influencer payload;

	InfluencerJsonSchema(long tweetCounts, String username, String content, long likes) {
		this.payload = new Influencer(tweetCounts, username, content, likes);
		Field fieldTweetCounts = Field.builder().field("tweets").type("int64").build();
		Field fieldContent = Field.builder().field("content").type("string").build();
		Field fieldUsername = Field.builder().field("username").type("string").build();
		Field fieldLikes = Field.builder().field("likes").type("int64").build();
		this.schema = new Schema("struct", Arrays.asList(fieldUsername,fieldContent,fieldLikes,fieldTweetCounts));
	}
	
	public InfluencerJsonSchema(Influencer influencer) {
		this(influencer.getTweets(),influencer.getUsername(),influencer.getContent(),influencer.getLikes());
	}

	@Getter
	@Setter
	@AllArgsConstructor
	static class Schema {

		String type;

		List<Field> fields;

	}

	@Getter
	@Setter
	@Builder
	@AllArgsConstructor
	static class Field {

		String type;

		String field;

	}

}
