package dev.vkazulkin.controller;

import dev.vkazulkin.entity.Author;
import dev.vkazulkin.entity.UpcomingTalk;
import dev.vkazulkin.entity.UpcomingTalks;
import dev.vkazulkin.entity.YouTubeVideo;
import dev.vkazulkin.entity.YouTubeVideos;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpClientTransport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.DescribeUserPoolClientRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUserPoolClientsRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUserPoolsRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserPoolClientDescription;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserPoolClientType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserPoolDescriptionType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.core.type.TypeReference;
import software.amazon.awssdk.regions.Region;

import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.Set;


@RestController
public class AuthorContentExtractorController {

    @Value("${cognito.user.pool.name}")
    private String USER_POOL_NAME;

    @Value("${cognito.user.pool.client.name}")
    private String USER_POOL_CLIENT_NAME;

    @Value("${cognito.auth.token.resource.server.id}")
    private String RESOURCE_SERVER_ID;

    @Value("${amazon.bedrock.agentcore.gateway.base.url}")
    private String AGENTCORE_GATEWAY_BASE_URL;
    @Value("${amazon.bedrock.agentcore.gateway.endpoint}")
    private String AGENTCORE_GATEWAY_ENDPOINT;

    private final ChatClient chatClient;
    
    private final CognitoIdentityProviderClient cognitoClient;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private static final Logger logger = LoggerFactory.getLogger(AuthorContentExtractorController.class);
 
    public AuthorContentExtractorController(ChatClient.Builder builder) {
        var options = ToolCallingChatOptions.builder()
                //.model("us.anthropic.claude-sonnet-4-6")
        		.model("amazon.nova-pro-v1:0")
                .maxTokens(2000);

        this.chatClient = builder
                .defaultOptions(options)
                //.defaultSystem(SYSTEM_PROMPT)
                .build();
        cognitoClient = CognitoIdentityProviderClient.builder().region(Region.of(System.getenv("REGION"))).build();
    }
    

    @RequestMapping(path = "/author/content/youtubeVideos", method = RequestMethod.POST, 
    		consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public YouTubeVideos searchForYouTubeVideos(@RequestBody Author author) {
        logger.info("invoked searchForYouTubeVideos Lambda function with author "+author);
        return this.webSearch(author,"YouTube videos", 3 ,YouTubeVideos.class);
        // return this.searchForYouTubeVideos();
    }

    
    @RequestMapping(path = "/author/content/upcomingTalks", method = RequestMethod.POST, 
    		consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public UpcomingTalks searchForUpcomingTalks(@RequestBody Author author) {
        logger.info("invoked searchForUpcomingTalks Lambda function with author "+author);
        return webSearch(author, "upcoming talks", 3, UpcomingTalks.class);
        //return this.searchForUpcomingTalks();
    }

    private <T> T webSearch(Author author, String searchTopic, int maxNumberOfResults, Class<T> clazz) {
    	var token = getAuthTokenViaHttpClient();
    	try (var client = McpClient.sync(getMcpClientTransport(token)).build()) {
 			client.initialize();
            client.listTools().tools().forEach(tool -> logger.info("tool found: " + tool));

            var mcpToolCallbackProvider = SyncMcpToolCallbackProvider.builder().mcpClients(client)
                    .build();

            var prompt = """
                    Search for the %s given by %s %s. Provide maximum %d results.
                    Your response should be in JSON format.
                    Do not include any explanations, only provide a RFC8259 compliant JSON response following this format without deviation.
                    """.formatted(searchTopic, author.firstName(), author.lastName(), maxNumberOfResults);

            logger.info("prompt: " + prompt);
            var response = this.chatClient.prompt().user(prompt)
            		//.advisors(AdvisorParams.ENABLE_NATIVE_STRUCTURED_OUTPUT)
                    .tools(mcpToolCallbackProvider.getToolCallbacks())
                    .call()
                    .entity(clazz);
                    //.content();
            logger.info("response: " + response);
            return response;
        }
    }

    private McpClientTransport getMcpClientTransport(String token) {
        var headerValue = "Bearer " + token;
	    var httpRequestBuilder = HttpRequest.newBuilder().header("Authorization", headerValue);
	    
        return HttpClientStreamableHttpTransport.builder(AGENTCORE_GATEWAY_BASE_URL)
                .connectTimeout(Duration.ofMinutes(3))
                .endpoint(AGENTCORE_GATEWAY_ENDPOINT)
                .requestBuilder(httpRequestBuilder)
                .build();
    }



    /**
     * returns authorization token required by the mcp client
     * @return authorization token
     */
    private String getAuthTokenViaHttpClient() {
        var userPool = getUserPool();
        logger.info("user pool " + userPool);
        if(userPool == null) {
            throw new RuntimeException("cognito user pool with the name "+USER_POOL_NAME+ " is not found");
        }
        var userPoolClient = getUserPoolClient(userPool);
        logger.info("user pool " + userPoolClient);

        if(userPoolClient == null) {
            throw new RuntimeException("cognito user pool client with the name "+USER_POOL_CLIENT_NAME+ " is not found");
        }

        var userPoolClientType = describeUserPoolClient(userPoolClient);
        logger.info("user pool client type " + userPoolClientType);

        if(userPoolClientType == null) {
            throw new RuntimeException("cognito user client type for the client "+USER_POOL_CLIENT_NAME+ " is not found");
        }
        var userPoolId = userPool.id();
        userPoolId = userPoolId.replace("_", "").toLowerCase();
        var url = "https://" + userPoolId + ".auth." + Region.US_EAST_1.id() + ".amazoncognito.com/oauth2/token";
        logger.info("url: " + url);

        var SCOPE_STRING = RESOURCE_SERVER_ID + "/*";

        var entity = "grant_type=client_credentials&" + "client_id=" + userPoolClientType.clientId() + "&"
                + "client_secret=" + userPoolClientType.clientSecret() + "&" + "scope=" + SCOPE_STRING;

        logger.info("entity " + entity);
        try (var httpClient = HttpClients.createDefault()) {
            var httpPost = ClassicRequestBuilder.post(url)
                    .setHeader("Content-Type", "application/x-www-form-urlencoded").setEntity(entity).build();
            return httpClient.execute(httpPost, new AuthTokenResponseHandler());

        } catch (IOException e) {
            e.printStackTrace();
            logger.error("error occured with the message: ", e.getMessage());
        }
        return null;
    }


    /**
     * returns cognito user pool with specific user name
     *
     * @return cognito user pool with specific user name
     */
    private UserPoolDescriptionType getUserPool() {
        try {
            var request = ListUserPoolsRequest.builder().maxResults(10).build();
            var response = cognitoClient.listUserPools(request);
            for (var userPool : response.userPools()) {
                logger.info("User pool " + userPool.name() + ", User ID " + userPool.id());
                if (userPool.name().equals(USER_POOL_NAME)) {
                    return userPool;
                }
            }

        } catch (CognitoIdentityProviderException e) {
        	logger.error("error occured with the message: ", e.getMessage());
        }
        return null;
    }

    /**
     * returns cognito user pool client for the given cognito user pool
     *
     * @param userPool - cognito user pool
     * @return cognito user pool client for the given cognito user pool
     */
    private UserPoolClientDescription getUserPoolClient(UserPoolDescriptionType userPool) {
        try {
            var request = ListUserPoolClientsRequest.builder().userPoolId(userPool.id()).maxResults(10).build();

            var response = cognitoClient.listUserPoolClients(request);
            for (var userPoolClient : response.userPoolClients()) {
                logger.info("User Pool Client Name " + userPoolClient.clientName() + ", User Pool Client ID "
                        + userPoolClient.clientId());
                if (userPoolClient.clientName().equals(USER_POOL_CLIENT_NAME)) {
                    return userPoolClient;
                }
            }
        } catch (CognitoIdentityProviderException e) {
            logger.error("error occured with the message: ", e.getMessage());
        }
        return null;
    }



    /** returns cognito user pool client type for the given cognito user pool client
     *
     * @param userPoolClient- cognito user pool client
     * @return cognito user pool client type for the given cognito user pool client
     */
    private UserPoolClientType describeUserPoolClient(UserPoolClientDescription userPoolClient) {
        var request = DescribeUserPoolClientRequest.builder()
                .userPoolId(userPoolClient.userPoolId()).clientId(userPoolClient.clientId()).build();
        var response = cognitoClient.describeUserPoolClient(request);
        var optionalType = response.getValueForField("UserPoolClient",
                UserPoolClientType.class);
        if(optionalType.isEmpty()) {
            return null;
        }
        return optionalType.get();
    }

    private class AuthTokenResponseHandler implements HttpClientResponseHandler<String> {
        @Override
        public String handleResponse(ClassicHttpResponse response) throws HttpException, IOException {
            var inputStream = response.getEntity().getContent();
            var responseString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            logger.info("response: " + responseString);

            var responseMap = objectMapper.readValue(responseString, new TypeReference<Map<String, Object>>() {});
            var token = (String) responseMap.get("access_token");
            logger.info("token : " + token);

            var expiresInSeconds = (Integer) responseMap.get("expires_in");
            logger.info("token expires in seconds : " + expiresInSeconds);
            // add handling of the auth token expiration

            return token;
        }
    }
}