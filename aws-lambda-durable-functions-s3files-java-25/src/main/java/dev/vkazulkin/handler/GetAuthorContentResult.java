package dev.vkazulkin.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import tools.jackson.databind.ObjectMapper;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.http.HttpStatusCode;


public class GetAuthorContentResult
		implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private static final String REGION = System.getenv("REGION");
	private static final String S3_BUCKET_NAME = System.getenv("S3_BUCKET_NAME");
	private static final String S3_FOLDER_NAME = System.getenv("S3_FOLDER_NAME");
	private static final S3Client S3_CLIENT = S3Client.builder()
			  .region(Region.of(REGION))
			  .build();


	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
		var firstName = requestEvent.getPathParameters().get("firstname");
		var lastName = requestEvent.getPathParameters().get("lastname");
		var key= S3_FOLDER_NAME+"/"+firstName+"-"+lastName+".json";
		context.getLogger().log("s3 bucket: "+S3_BUCKET_NAME+ " key: "+key);
		var authorContent= getAuthorContent(S3_BUCKET_NAME, key);
		context.getLogger().log("author content: "+authorContent);
		return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatusCode.OK)
					.withBody(authorContent);
	}

	private String getAuthorContent(String bucketName, String key) {
		var getObjectRequest = GetObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.build();

		return S3_CLIENT.getObject(getObjectRequest, ResponseTransformer.toBytes()).asUtf8String();
	}
}