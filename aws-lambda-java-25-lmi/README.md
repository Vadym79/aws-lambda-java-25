# AWS Lambda Java 25 with Lambda Managed Instances (LMI)

This project demonstrates a serverless Product API built with **Java 25** on **AWS Lambda**, using **Lambda Managed Instances (LMI)** for capacity management. It explores different cold-start optimization strategies via static initialization priming.

## Architecture

```
API Gateway → Lambda (Java 25) → DynamoDB
```

- API Gateway with API key authentication and usage plan throttling
- Four Lambda functions backed by a single DynamoDB table (`AWSLambdaJava25WithLMIProductsTable`)
- LMI capacity provider (`CapacityProviderForJava25LMI`) managing x86_64 EC2 instances (m7a.large / m6a.large)
- Deployed to `eu-central-1` via AWS SAM

## Lambda Functions

| Function | Handler | Endpoint | Description |
|---|---|---|---|
| `PostProductJava25WithLMI` | `CreateProductHandler` | `POST /products` | Creates a product |
| `GetProductByIdJava25WithLMI` | `GetProductByIdHandler` | `GET /products/{id}` | Gets a product — no priming |
| `GetProductByIdJava25WithDynamoDBPrimingAndLMI` | `GetProductByIdWithDynamoDBPrimingHandler` | `GET /productsWithDynamoDBPriming/{id}` | Gets a product — DynamoDB client primed in static initializer |
| `GetProductByIdJava25WithFullPrimingAndLMI` | `GetProductByIdWithFullPrimingHandler` | `GET /productsWithFullPriming/{id}` | Gets a product — full request path primed in static initializer |

## Prerequisites

- Java 25 JDK
- Apache Maven 3.x
- AWS SAM CLI
- AWS account with permissions to deploy Lambda, API Gateway, DynamoDB, and IAM resources

## Build

```bash
mvn clean package
```

This produces `target/aws-lambda-java-25-lmi-1.0.0-SNAPSHOT.jar` (fat JAR via maven-shade-plugin).

## Deploy

```bash
sam deploy
```

Uses `samconfig.toml` defaults: stack `AWSLambdaJava25WithLMI`, region `eu-central-1`, stage `prod`.

First-time deploy (guided):

```bash
sam deploy --guided
```

## API Usage

All requests require the API key header:

```
x-api-key: a6ZbcDefQW12BN56WEV7LMI
```

**Create a product:**
```bash
curl -X POST https://<api-id>.execute-api.eu-central-1.amazonaws.com/prod/products \
  -H "x-api-key: a6ZbcDefQW12BN56WEV7LMI" \
  -H "Content-Type: application/json" \
  -d '{"id":"1","name":"Widget","price":9.99}'
```

**Get a product:**
```bash
curl https://<api-id>.execute-api.eu-central-1.amazonaws.com/prod/products/1 \
  -H "x-api-key: a6ZbcDefQW12BN56WEV7LMI"
```

Replace `/products/` with `/productsWithDynamoDBPriming/` or `/productsWithFullPriming/` to test the primed variants.

## Project Structure

```
src/main/java/software/amazonaws/example/product/
├── entity/         # Product record (id, name, price)
├── dao/            # DynamoDB data access (DynamoProductDao)
└── handler/        # Lambda request handlers (4 variants)
```

## License

MIT-0 — See [LICENSE](https://github.com/aws/mit-0)
