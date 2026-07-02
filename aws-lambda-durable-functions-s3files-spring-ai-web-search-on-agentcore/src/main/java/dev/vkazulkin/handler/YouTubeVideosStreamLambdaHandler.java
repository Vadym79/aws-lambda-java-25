package dev.vkazulkin.handler;

public class YouTubeVideosStreamLambdaHandler extends AuthorContentCustomStreamLambdaHandler {

	@Override
	protected String getPath() {
		return "/author/content/youtubeVideos";
	}
    
}
