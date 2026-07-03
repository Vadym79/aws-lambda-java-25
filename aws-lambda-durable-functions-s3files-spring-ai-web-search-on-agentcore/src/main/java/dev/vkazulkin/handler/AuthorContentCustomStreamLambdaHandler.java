package dev.vkazulkin.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import com.amazonaws.serverless.proxy.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import dev.vkazulkin.Application;
//import tools.jackson.databind.ObjectMapper;


abstract class AuthorContentCustomStreamLambdaHandler implements RequestStreamHandler {
	
	//private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final Logger logger = LoggerFactory.getLogger(AuthorContentCustomStreamLambdaHandler.class);
	
    private static SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;
    static {
        try {
            handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(Application.class);
        } catch (ContainerInitializationException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not initialize Spring Boot application", e);
        }
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
            throws IOException {
    	logger.info("entered generic stream lambda handler");
    	var author = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    	logger.info("input stream "+author );    
    	var proxyRequestEvent=getAwsProxyRequest(author);
    	//logger.info("request: "+ objectMapper.writeValueAsString(proxyRequestEvent));
	    var response = handler.proxy(proxyRequestEvent, context);
	    var body =response.getBody();
	    logger.info("body "+body );
	    try (var printStream = new PrintStream(outputStream, true, StandardCharsets.UTF_8)) {
	        printStream.print(body);
	    }
	    logger.info("finished handleRequest");    
    }

	
    private AwsProxyRequest getAwsProxyRequest (String author) {
    	var awsProxyRequest = new AwsProxyRequest ();
      	awsProxyRequest.setHttpMethod("POST");
    	awsProxyRequest.setPath(this.getPath());
    	awsProxyRequest.setBody(author);

    	var header= new SingleValueHeaders();
    	header.put("Content-Type", "application/json");
    	awsProxyRequest.setHeaders(header);

		var headers= new Headers();
		headers.add("Content-Type", "application/json");
		awsProxyRequest.setMultiValueHeaders(headers);
      	//awsProxyRequest.setPathParameters(Map.of());
      	//awsProxyRequest.setQueryStringParameters(Map.of());
    	
    	var awsProxyRequestContext = new AwsProxyRequestContext();
    	var apiGatewayRequestIdentity= new ApiGatewayRequestIdentity();
    	apiGatewayRequestIdentity.setApiKey("blabla");
    	awsProxyRequestContext.setIdentity(apiGatewayRequestIdentity);
    	
    	awsProxyRequest.setRequestContext(awsProxyRequestContext);
    	return awsProxyRequest;		   
}

 protected abstract String getPath();

}