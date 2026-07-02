package dev.vkazulkin.controller;

import dev.vkazulkin.entity.Author;
import dev.vkazulkin.entity.UpcomingTalk;
import dev.vkazulkin.entity.UpcomingTalks;
import dev.vkazulkin.entity.YouTubeVideo;
import dev.vkazulkin.entity.YouTubeVideos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.LocalDate;
import java.util.Set;


@RestController
@EnableWebMvc
public class AuthorContentExtractorController {

    private static final Logger logger = LoggerFactory.getLogger(AuthorContentExtractorController.class);

    @RequestMapping(path = "/author/content/youtubeVideos", method = RequestMethod.POST, 
    		consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public YouTubeVideos searchForYouTubeVideos(@RequestBody Author author) {
        logger.info("invoked searchForYouTubeVideos Lambda function with author "+author);
        return this.searchForYouTubeVideos();
    }

    
    @RequestMapping(path = "/author/content/upcomingTalks", method = RequestMethod.POST, 
    		consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public UpcomingTalks searchForUpcomingTalks(@RequestBody Author author) {
        logger.info("invoked searchForUpcomingTalks Lambda function with author "+author);
        return this.searchForUpcomingTalks();
    }

    
    private YouTubeVideos searchForYouTubeVideos() {
        var youtubeVideo1= new YouTubeVideo("Building AI Agents with Spring AI and Amazon Bedrock AgentCore",
                "https://www.youtube.com/watch?v=JQXfSjMOa1g");
        return new YouTubeVideos(Set.of(youtubeVideo1));
    }
    
	private UpcomingTalks searchForUpcomingTalks() {
		var upcomingTalk1= new UpcomingTalk("Building AI Agents with Spring AI and Amazon Bedrock AgentCore",
				LocalDate.of(2026, 6, 13),"https://www.meetup.com/aws-user-group-dusseldorf/events/315327513");
		return new UpcomingTalks(Set.of(upcomingTalk1));
	}
}
