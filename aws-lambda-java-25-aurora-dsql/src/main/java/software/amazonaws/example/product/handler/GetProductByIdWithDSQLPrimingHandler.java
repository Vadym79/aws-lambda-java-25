// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.amazonaws.example.product.handler;


import org.crac.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.crac.Core;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.http.HttpStatusCode;
import software.amazonaws.example.product.dao.ProductDao;


public class GetProductByIdWithDSQLPrimingHandler
		implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>, Resource {

	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final ProductDao productDao= new ProductDao();
	private static final Logger logger = LoggerFactory.getLogger(GetProductByIdWithDSQLPrimingHandler.class);
	
	public GetProductByIdWithDSQLPrimingHandler () {
		Core.getGlobalContext().register(this);
	}
	
	@Override
	public void beforeCheckpoint(org.crac.Context<? extends Resource> context) throws Exception {
		logger.info("before applying a custom priming");
		productDao.getProductById(0);
		logger.info("after applying a custom priming");
    }

	
	@Override
	public void afterRestore(org.crac.Context<? extends Resource> context) throws Exception {	
	
	}

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
		var id = requestEvent.getPathParameters().get("id");
		try {
			var optionalProduct = productDao.getProductById(Integer.valueOf(id));
			if (optionalProduct.isEmpty()) {
				context.getLogger().log(" product with id " + id + " not found ");
				return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatusCode.NOT_FOUND)
						.withBody("Product with id = " + id + " not found");
			}
			context.getLogger().log(" product " + optionalProduct.get() + " found ");
			return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatusCode.OK)
					.withBody(objectMapper.writeValueAsString(optionalProduct.get()));
		} catch (Exception je) {
			je.printStackTrace();
			return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR)
					.withBody("Internal Server Error :: " + je.getMessage());
		}
	}

}