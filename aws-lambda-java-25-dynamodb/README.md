# AWS Lambda Java 25 with DynamoDB

A serverless application demonstrating AWS Lambda functions using the managed Java 25 runtime with Amazon DynamoDB for product management.

## Architecture

<p align="center">
  <img src="src/main/resources/img/app_arch.png" alt="Application Architecture"/>
</p>

The application provides a REST API for managing products with the following components:

- **API Gateway**: REST API with API key authentication
- **Lambda Functions**: Four Lambda functions with SnapStart enabled
  - `PostProductFunction`: Creates new products
  - `GetProductByIdFunction`: Retrieves products by ID
  - `GetProductByIdWithDynamoDBPrimingFunction`: Retrieves products with DynamoDB priming optimization
  - `GetProductByIdWithFullPrimingFunction`: Retrieves products with full priming optimization
- **DynamoDB Table**: Stores product data with on-demand billing

## Prerequisites

- Java 25 JDK
- Apache Maven 3.x
- AWS CLI configured with appropriate credentials
- AWS SAM CLI
- Git

## Project Structure

```
aws-lambda-java-25-dynamodb/
├── src/main/java/software/amazonaws/example/product/
│   ├── handler/          # Lambda function handlers
│   ├── dao/              # Data access layer for DynamoDB
│   └── entity/           # Product domain models
├── pom.xml               # Maven configuration
├── template.yaml         # SAM template for infrastructure
└── samconfig.toml        # SAM deployment configuration
```

## Key Features

- **Java 25 Runtime**: Uses the latest managed Java runtime
- **SnapStart**: Enabled for improved cold start performance
- **DynamoDB Integration**: AWS SDK v2 for DynamoDB operations
- **API Gateway**: RESTful endpoints with API key authentication
- **Priming Strategies**: Multiple Lambda functions demonstrating different initialization approaches

## Build

Compile and package the application:

```bash
mvn clean package
```

This creates a shaded JAR at `target/aws-lambda-java-25-with-dynamodb-1.0.0-SNAPSHOT.jar`.

## Deploy

Deploy using AWS SAM:

```bash
sam deploy -g --region us-east-1
```

Follow the prompts to configure:
- Stack name
- AWS Region
- Confirm changes before deploy
- Allow SAM CLI IAM role creation

## API Endpoints

After deployment, the API Gateway endpoint will be displayed in the outputs.

### Create Product
```bash
POST /products
Content-Type: application/json
x-api-key: a6ZbcDefQW12BN56WEVDDB25

{
  "id": "1",
  "name": "Print 10x13",
  "price": 15
}
```

### Get Product by ID
```bash
GET /products/{id}
x-api-key: a6ZbcDefQW12BN56WEVDDB25
```

### Get Product with DynamoDB Priming
```bash
GET /productsWithDynamoDBPriming/{id}
x-api-key: a6ZbcDefQW12BN56WEVDDB25
```

### Get Product with Full Priming
```bash
GET /productsWithFullPriming/{id}
x-api-key: a6ZbcDefQW12BN56WEVDDB25
```

## Configuration

### Environment Variables
- `REGION`: AWS region (set automatically)
- `PRODUCT_TABLE_NAME`: DynamoDB table name (set automatically)
- `JAVA_TOOL_OPTIONS`: JVM optimization flags

### Lambda Configuration
- Runtime: Java 25
- Memory: 1024 MB
- Timeout: 30 seconds
- Architecture: x86_64
- SnapStart: Enabled on published versions

## Dependencies

- AWS SDK for Java v2 (DynamoDB)
- AWS Lambda Java Core 1.4.0
- AWS Lambda Java Events 3.16.1
- Jackson for JSON processing
- CRaC (Coordinated Restore at Checkpoint) support
- SLF4J for logging

## Local Testing

Run Lambda functions locally using SAM:

```bash
sam local start-api
```

## Clean Up

Delete the deployed stack:

```bash
sam delete
```

## License

This code is licensed under the MIT-0 License.
