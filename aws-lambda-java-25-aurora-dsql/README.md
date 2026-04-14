# AWS Lambda Java 25 with Amazon Aurora DSQL

A serverless application demonstrating AWS Lambda functions using Java 25 runtime with JDBC, HikariCP connection pooling, and Amazon Aurora DSQL database.

## Architecture

<p align="center">
  <img src="src/main/resources/img/app_arch.png" alt="Application Architecture"/>
</p>

The application consists of:
- **API Gateway**: REST API with API key authentication
- **Lambda Functions**: Four handlers for product operations
- **Aurora DSQL**: Serverless distributed SQL database
- **HikariCP**: Connection pooling for optimal database performance

## Features

- **Java 25 Runtime**: Latest Java features including records
- **SnapStart**: Enabled for faster cold starts
- **Connection Pooling**: HikariCP for efficient database connections
- **Multiple Priming Strategies**: Demonstrates different SnapStart priming approaches
- **REST API**: Full CRUD operations for products

## Lambda Functions

| Function | Handler | Endpoint | Description |
|----------|---------|----------|-------------|
| PostProductJava25WithDSQL | CreateProductHandler | POST /products | Create a new product |
| GetProductByIdJava25WithDSQL | GetProductByIdHandler | GET /products/{id} | Get product by ID |
| GetProductByIdJava25WithDSQLAndDSQLPriming | GetProductByIdWithDSQLPrimingHandler | GET /productsWithAuroraPriming/{id} | Get product with DSQL priming |
| GetProductByIdJava25WithDSQLAndFullPriming | GetProductByIdWithFullPrimingHandler | GET /productsWithFullPriming/{id} | Get product with full priming |

## Prerequisites

- Java 25 JDK
- Maven 3.x
- AWS CLI configured
- AWS SAM CLI
- AWS Account with permissions for Lambda, API Gateway, and Aurora DSQL

## Build

```bash
mvn clean package
```

This creates a shaded JAR at `target/aws-lambda-java-25-with-aurora-dsql-1.0.0-SNAPSHOT.jar`

## Deploy

```bash
sam deploy -g --region us-east-1
```

Follow the prompts to configure:
- Stack name
- AWS Region
- Confirm changes before deploy
- Allow SAM CLI IAM role creation
- Save arguments to configuration file

## Database Setup

After deployment, initialize the Aurora DSQL database:

### 1. Connect to Aurora DSQL

Choose one of these methods:
- **AWS CloudShell**: [Instructions](https://docs.aws.amazon.com/aurora-dsql/latest/userguide/getting-started.html#connect-dsql-cluster)
- **psql client**: [Instructions](https://docs.aws.amazon.com/aurora-dsql/latest/userguide/getting-started.html#accessing-sql-clients-psql)
- **Query Editor**: [Instructions](https://docs.aws.amazon.com/aurora-dsql/latest/userguide/getting-started-query-editor.html)

### 2. Create Schema

```sql
CREATE TABLE products (
    id int PRIMARY KEY,
    name varchar(256) NOT NULL,
    price int NOT NULL
);

CREATE SEQUENCE product_id CACHE 1;
```

### 3. Insert Sample Data

```sql
INSERT INTO products VALUES (1, 'Print 10x13', 15);
INSERT INTO products VALUES (2, 'A5 Book', 5000);
```

## Usage

### Get API Key and Endpoint

After deployment, note the API Gateway endpoint from outputs and use the API key: `a6ZbcDefQW12BN56WEDQ25`

### Create Product

```bash
curl -X POST https://{api-id}.execute-api.us-east-1.amazonaws.com/prod/products \
  -H "x-api-key: a6ZbcDefQW12BN56WEDQ25" \
  -H "Content-Type: application/json" \
  -d '{"name": "Print 10x13", "price": 15}'
```

With auto-generated ID:
```json
{"name": "Print 10x13", "price": 15}
```

With specific ID:
```json
{"id": 1, "name": "Print 10x13", "price": 15}
```

### Get Product by ID

```bash
curl https://{api-id}.execute-api.us-east-1.amazonaws.com/prod/products/1 \
  -H "x-api-key: a6ZbcDefQW12BN56WEDQ25"
```

## Project Structure

```
.
├── src/main/java/software/amazonaws/example/product/
│   ├── dao/
│   │   ├── DsqlDataSourceConfig.java    # HikariCP configuration
│   │   └── ProductDao.java               # Data access layer
│   ├── entity/
│   │   └── Product.java                  # Product record
│   └── handler/
│       ├── CreateProductHandler.java
│       ├── GetProductByIdHandler.java
│       ├── GetProductByIdWithDSQLPrimingHandler.java
│       └── GetProductByIdWithFullPrimingHandler.java
├── pom.xml                               # Maven configuration
├── template.yaml                         # SAM template
└── samconfig.toml                        # SAM configuration
```

## Key Dependencies

- **AWS SDK for Java v2**: Aurora DSQL client
- **Aurora DSQL JDBC Connector**: 1.4.0
- **HikariCP**: 7.0.2 - Connection pooling
- **AWS Lambda Java Core**: 1.4.0
- **AWS Lambda Java Events**: 3.16.1
- **CRaC**: 0.1.3 - Coordinated Restore at Checkpoint

## Configuration

### Environment Variables

- `AURORA_DSQL_CLUSTER_ENDPOINT`: Auto-configured by SAM template
- `JAVA_TOOL_OPTIONS`: `-XX:+TieredCompilation -XX:TieredStopAtLevel=1`

### Lambda Settings

- **Runtime**: java25
- **Memory**: 1024 MB
- **Timeout**: 30 seconds
- **Architecture**: x86_64
- **SnapStart**: Enabled on published versions

## API Gateway Configuration

- **API Key Required**: Yes
- **Rate Limit**: 10,000 requests/second
- **Burst Limit**: 5,000 requests
- **Daily Quota**: 2,000,000 requests
- **Access Logging**: Enabled with 7-day retention

## Clean Up

```bash
sam delete
```

## License

This project is provided as-is for demonstration purposes.
