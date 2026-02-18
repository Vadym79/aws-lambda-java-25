
package software.amazonaws.example.product.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.http.HttpStatusCode;
import software.amazonaws.example.product.dao.ProductDao;
import software.amazonaws.example.product.entity.Product;
import java.util.stream.StreamSupport;
import java.util.ServiceLoader;

public class CreateProductHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private final ObjectMapper objectMapper = new ObjectMapper();
	//private static final ProductDao productDao= new ProductDao();

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
		try {
			
			ServiceLoader<org.hibernate.bytecode.spi.BytecodeProvider> loader =
			    ServiceLoader.load(org.hibernate.bytecode.spi.BytecodeProvider.class);
		
			context.getLogger().log("SIZE: " + StreamSupport.stream(loader.spliterator(), false).count());
			for (org.hibernate.bytecode.spi.BytecodeProvider impl : loader) {
			    context.getLogger().log("impl found: "+impl.getClass());
			}


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