
package software.amazonaws.example.product.handler;

import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.http.HttpStatusCode;
import software.amazonaws.example.product.dao.ProductDao;
import software.amazonaws.example.product.entity.Product;


public class CreateProductHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private final ObjectMapper objectMapper = new ObjectMapper();
	//private static final ProductDao productDao= new ProductDao();

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
		try {
			StandardServiceRegistryImpl  obj=StandardServiceRegistryImpl.create
					(new BootstrapServiceRegistryImpl() , List.of(new BytecodeProviderInitiator()), 
							List.of(), Map.of());
			
			obj.initiateService(new BytecodeProviderInitiator());
			var requestBody = requestEvent.getBody();
			var product = objectMapper.readValue(requestBody, Product.class);
	        //int id =productDao.createProduct(product);
			int id=0;
			return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatusCode.CREATED)
					.withBody("Product with id = " + id + " created");
		} catch (Exception e) {
			e.printStackTrace();
			return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR)
					.withBody("Internal Server Error :: " + e.getMessage());
		}
	}
}