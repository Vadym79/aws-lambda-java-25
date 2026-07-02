
package dev.vkazulkin.handler;

import dev.vkazulkin.entity.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import tools.jackson.databind.ObjectMapper;

import software.amazon.lambda.durable.DurableContext;
import software.amazon.lambda.durable.DurableFuture;
import software.amazon.lambda.durable.DurableHandler;
import software.amazon.lambda.durable.config.CompletionConfig;
import software.amazon.lambda.durable.config.ParallelConfig;
import software.amazon.lambda.durable.model.ParallelResult;


public class AuthorContentExtractor extends DurableHandler<Author, AuthorContent> {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private static final String WORKSPACE_MOUNT  = System.getenv("WORKSPACE_MOUNT");
	private static final String UPCOMING_TALK_CONTENT_EXTRACTOR_FUNCTION_ARN  = System.getenv("UpcomingTalkContentExtractorFunctionArn");
	private static final String YOUTUBE_CONTENT_EXTRACTOR_FUNCTION_ARN  = System.getenv("YouTubeContentExtractorFunctionArn");

	@Override
	public AuthorContent handleRequest(Author author, DurableContext ctx) {

		ctx.getLogger().info("author "+author);
		ctx.getLogger().info("UpcomingTalkContentExtractorFunctionArn "+UPCOMING_TALK_CONTENT_EXTRACTOR_FUNCTION_ARN);
		ctx.getLogger().info("YouTubeContentExtractorFunctionArn "+YOUTUBE_CONTENT_EXTRACTOR_FUNCTION_ARN);

		var config = ParallelConfig.builder()
				.maxConcurrency(5)
				.completionConfig(CompletionConfig.allCompleted())
				.build();

	    var parallel = ctx.parallel("parallel-search", config);

		DurableFuture<UpcomingTalkContentList> upcomingTalkContentListTask = parallel.branch("searchForUpcomingTalksContent",
				UpcomingTalkContentList.class, branchCtx -> {
			return branchCtx.invoke("searchForUpcomingTalksContent",
					UPCOMING_TALK_CONTENT_EXTRACTOR_FUNCTION_ARN, author, UpcomingTalkContentList.class);
		});

		DurableFuture<YouTubeContentList> youtubeContentListTask = parallel.branch("searchForYouTubeContent",
				YouTubeContentList.class, branchCtx -> {
			return branchCtx.invoke("searchForYouTubeContent",
					YOUTUBE_CONTENT_EXTRACTOR_FUNCTION_ARN, author, YouTubeContentList.class);
		});

		ParallelResult result = parallel.get();
		ctx.getLogger().info("result: "+result);
		var authorContent = new AuthorContent(author, upcomingTalkContentListTask.get(), youtubeContentListTask.get());

	    ctx.getLogger().info("author content: "+authorContent);
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

	    return authorContent;
	}
}