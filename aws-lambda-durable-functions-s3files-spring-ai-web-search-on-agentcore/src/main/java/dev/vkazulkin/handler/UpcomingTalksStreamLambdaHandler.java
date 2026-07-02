package dev.vkazulkin.handler;

public class UpcomingTalksStreamLambdaHandler extends AuthorContentCustomStreamLambdaHandler {

	@Override
	protected String getPath() {
		return "/author/content/upcomingTalks";
	}
    
}
