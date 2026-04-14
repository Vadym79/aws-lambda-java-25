# AWS Lambda Java 25 with Hibernate and Amazon Aurora DSQL

A serverless REST API demonstrating AWS Lambda with the managed Java 25 runtime, using Hibernate ORM, HikariCP connection pool, and Amazon Aurora DSQL as the database.

## Architecture

<p align="center">
  <img src="src/main/resources/img/app_arch.png" alt="Application Architecture"/>
</p>

- **API Gateway** — REST API with API key authentication
- **AWS Lambda (Java 25)** — Four Lambda functions with SnapStart enabled
- **Amazon Aurora DSQL** — Serverless distributed SQL database (PostgreSQL-compatible)
- **Hibernate 7 + HikariCP** — ORM and connection pooling

## Lambda Functions

| Function | Handler | Endpoint |
|---|---|---|
| PostProductFunction | `CreateProductHandler` | `POST /products` |
| GetProductByIdFunction | `GetProductByIdHandler` | `GET /products/{id}` |
| GetProductByIdWithAuroraPrimingFunction | `GetProductByIdWithDSQLPrimingHandler` | `GET /productsWithAuroraPriming/{id}` |
| GetProductByIdWithFullPrimingFunction | `GetProductByIdWithFullPrimingHandler` | `GET /productsWithFullPriming/{id}` |

The two priming variants use CRaC (`org-crac`) to warm up the database connection before the SnapStart checkpoint, reducing cold start latency.

## Prerequisites

- Java 25
- Maven 3.x
- AWS SAM CLI
- AWS account with permissions to create Lambda, API Gateway, Aurora DSQL, and IAM resources

## Build

```bash
mvn clean package
```

This produces `target/aws-lambda-java-25-with-hibernate-and-aurora-dsql-1.0.0-SNAPSHOT.jar` via the Maven Shade plugin.

## Deploy

```bash
sam deploy -g --region eu-central-1
```

SAM will create the Aurora DSQL cluster, Lambda functions, API Gateway, and required IAM policies. The cluster endpoint is automatically injected as the `AURORA_DSQL_CLUSTER_ENDPOINT` environment variable.

## Database Setup

After deployment, connect to the Aurora DSQL cluster (via CloudShell, psql, or the Aurora DSQL query editor) and run:

```sql
CREATE TABLE products (id int PRIMARY KEY, name varchar(256) NOT NULL, price int NOT NULL);
CREATE SEQUENCE product_id CACHE 1;
```

Optionally seed some data:

```sql
INSERT INTO products VALUES (1, 'Print 10x13', 15);
INSERT INTO products VALUES (2, 'A5 Book', 5000);
```

## API Usage

All requests require the API key header: `x-api-key: a6ZbcDefQW12BN56WEHADQ25`

**Create a product**
```bash
curl -X POST https://<api-id>.execute-api.<region>.amazonaws.com/prod/products \
  -H "x-api-key: a6ZbcDefQW12BN56WEHADQ25" \
  -H "Content-Type: application/json" \
  -d '{"name": "Print 10x13", "price": 15}'
```

**Get a product by ID**
```bash
curl https://<api-id>.execute-api.<region>.amazonaws.com/prod/products/1 \
  -H "x-api-key: a6ZbcDefQW12BN56WEHADQ25"
```

The product `id` is auto-generated using the `product_id` sequence.

## References

- [Aurora DSQL Getting Started](https://docs.aws.amazon.com/aurora-dsql/latest/userguide/getting-started.html)
- [Lambda SnapStart](https://docs.aws.amazon.com/lambda/latest/dg/snapstart.html)
- [CRaC (Coordinated Restore at Checkpoint)](https://github.com/CRaC/org.crac)
