
package dev.vkazulkin.handler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Set;

import tools.jackson.databind.ObjectMapper;
import dev.vkazulkin.entity.*;
import software.amazon.lambda.durable.DurableContext;
import software.amazon.lambda.durable.DurableFuture;
import software.amazon.lambda.durable.DurableHandler;
import software.amazon.lambda.durable.config.CompletionConfig;
import software.amazon.lambda.durable.config.ParallelConfig;
import software.amazon.lambda.durable.model.ParallelResult;


public class AuthorContentExtractor extends DurableHandler<Author, AuthorContent> {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private static final String WORKSPACE_MOUNT  = System.getenv("WORKSPACE_MOUNT");
	  
	@Override
	public AuthorContent handleRequest(Author author, DurableContext ctx) {

		ctx.getLogger().info("author "+author);
		var config = ParallelConfig.builder()
				.maxConcurrency(5)
				.completionConfig(CompletionConfig.allCompleted())
				.build();
		var parallel = ctx.parallel("parallel-search", config);

		DurableFuture<UpcomingTalkContentList> upcomingTalkContentListTask = parallel.branch("searchUpcomingTalksContent", UpcomingTalkContentList.class, branchCtx -> {
			return branchCtx.step("searchForUpcomingTalksContent-step", UpcomingTalkContentList.class, stepCtx -> searchForUpcomingTalkContent());
		});

		DurableFuture<YouTubeContentList> youtubeContentListTask = parallel.branch("searchForYouTubeContent", YouTubeContentList.class, branchCtx -> {
			return branchCtx.step("searchForYouTubeContent-step", YouTubeContentList.class, stepCtx -> searchForYouTubeContent());
		});

		ParallelResult result = parallel.get();
		ctx.getLogger().info("result: "+result);

	    var authorContent = new AuthorContent(author, upcomingTalkContentListTask.get(), youtubeContentListTask.get());
		var authorContentAsJson = objectMapper.writeValueAsString(authorContent);
        var fileName= author.firstName()+"-"+author.lastName()+".json";
		Path path = Paths.get(WORKSPACE_MOUNT, fileName);
		byte[] strToBytes = authorContentAsJson.getBytes();
		ctx.getLogger().info("saving result to: "+path);

		try {
			Files.write(path, strToBytes);
		} catch (IOException ex) {
			ctx.getLogger().error("error wrting to the file", ex);
		}

		ctx.getLogger().info("author content: "+authorContent);
	    return authorContent;
	}

	private YouTubeContentList searchForYouTubeContent() {
		var youtubeContent1= new YouTubeContent("Building AI Agents with Spring AI and Amazon Bedrock AgentCore", 
				"https://www.youtube.com/watch?v=JQXfSjMOa1g");
		return new YouTubeContentList(Set.of(youtubeContent1));
	}
	
	private UpcomingTalkContentList searchForUpcomingTalkContent() {
		var upcomingTalkContent1= new UpcomingTalkContent("Building AI Agents with Spring AI and Amazon Bedrock AgentCore",
			LocalDate.of(2026, 6, 13),"https://www.meetup.com/aws-user-group-dusseldorf/events/315327513");
		return new UpcomingTalkContentList(Set.of(upcomingTalkContent1));
	}
}