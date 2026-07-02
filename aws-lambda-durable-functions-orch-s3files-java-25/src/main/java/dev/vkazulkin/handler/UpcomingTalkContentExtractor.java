package dev.vkazulkin.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import dev.vkazulkin.entity.*;

import java.time.LocalDate;
import java.util.Set;


public class UpcomingTalkContentExtractor
		implements RequestHandler<Author, UpcomingTalkContentList> {

	@Override
	public UpcomingTalkContentList handleRequest(Author author, Context context) {
		context.getLogger().log("invoked UpcomingTalkContentExtractor Lambda function with author "+author);
		return this.searchForUpcomingTalkContent();
	}

	private UpcomingTalkContentList searchForUpcomingTalkContent() {
		var upcomingTalkContent1= new UpcomingTalkContent("Building AI Agents with Spring AI and Amazon Bedrock AgentCore",
				LocalDate.of(2026, 6, 13),"https://www.meetup.com/aws-user-group-dusseldorf/events/315327513");
		return new UpcomingTalkContentList(Set.of(upcomingTalkContent1));
	}
}