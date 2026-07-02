package dev.vkazulkin.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import dev.vkazulkin.entity.Author;
import dev.vkazulkin.entity.YouTubeContent;
import dev.vkazulkin.entity.YouTubeContentList;

import java.util.Set;


public class YouTubeContentExtractor
		implements RequestHandler<Author, YouTubeContentList> {

	@Override
	public YouTubeContentList handleRequest(Author author, Context context) {
		context.getLogger().log("invoked YouTubeContentExtractor Lambda function with author "+author);
		return this.searchForYouTubeContent();
	}

	private YouTubeContentList searchForYouTubeContent() {
		var youtubeContent1= new YouTubeContent("Building AI Agents with Spring AI and Amazon Bedrock AgentCore",
				"https://www.youtube.com/watch?v=JQXfSjMOa1g");
		return new YouTubeContentList(Set.of(youtubeContent1));
	}

}