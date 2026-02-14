// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.amazonaws.example.product.dao;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazonaws.example.product.entity.Product;
import software.amazonaws.example.product.entity.Products;

public class ProductDao  {
  private static final Logger logger = LoggerFactory.getLogger(ProductDao.class);
  private static final String PRODUCT_TABLE_NAME = System.getenv("PRODUCT_TABLE_NAME");
  private static final String REGION = System.getenv("REGION");

  private static final DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
    .credentialsProvider(DefaultCredentialsProvider.builder().build())
    .region(Region.of(REGION.toLowerCase()))
    //.httpClient(UrlConnectionHttpClient.create())
    .overrideConfiguration(ClientOverrideConfiguration.builder()
      .build())
    .build();


  public Optional<Product> getProduct(String id) {
    var getItemResponse = dynamoDbClient.getItem(GetItemRequest.builder()
      .key(Map.of("PK", AttributeValue.builder().s(id).build()))
      .tableName(PRODUCT_TABLE_NAME)
      .build());
    if (getItemResponse.hasItem()) {
      return Optional.of(ProductMapper.productFromDynamoDB(getItemResponse.item()));
    } else {
      return Optional.empty();
    }
  }

  public void putProduct(Product product) {
    dynamoDbClient.putItem(PutItemRequest.builder()
      .tableName(PRODUCT_TABLE_NAME)
      .item(ProductMapper.productToDynamoDb(product))
      .build());
  }

  public void deleteProduct(String id) {
    dynamoDbClient.deleteItem(DeleteItemRequest.builder()
      .tableName(PRODUCT_TABLE_NAME)
      .key(Map.of("PK", AttributeValue.builder().s(id).build()))
      .build());
  }

  public Products getAllProduct() {
    var scanResponse = dynamoDbClient.scan(ScanRequest.builder()
      .tableName(PRODUCT_TABLE_NAME)
      .limit(20)
      .build());

    logger.info("Scan returned: {} item(s)", scanResponse.count());

    List<Product> productList = new ArrayList<>();

    for (var item : scanResponse.items()) {
      productList.add(ProductMapper.productFromDynamoDB(item));
    }
    return new Products(productList);
  }
}
