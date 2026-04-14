# AWS Lambda Java 25 with Aurora DSQL as GraalVM Native Image

A serverless application demonstrating AWS Lambda functions built with Java 25, compiled to GraalVM native images, and integrated with Amazon Aurora DSQL database using JDBC and HikariCP connection pooling.

## Overview

This project showcases a modern serverless architecture using:
- **Java 25** with Records and modern language features
- **GraalVM Native Image** for fast cold starts and reduced memory footprint
- **Amazon Aurora DSQL** - a serverless, distributed SQL database
- **HikariCP** - high-performance JDBC connection pool
- **AWS Lambda Custom Runtime** (provided.al2023)
- **API Gateway** with API key authentication
- **AWS SAM** for infrastructure as code

## Architecture

The application exposes a REST API for managing products with the following endpoints:
- `POST /products` - Create a new product
- `GET /products/{id}` - Retrieve a product by ID

### Components

- **Lambda Functions**:
  - `CreateProductHandler` - Handles product creation
  - `GetProductByIdHandler` - Retrieves product by ID
- **Database**: Aurora DSQL cluster with `products` table
- **API Gateway**: REST API with usage plans and API key authentication
- **Connection Pooling**: HikariCP for efficient database connections

## Prerequisites

- AWS Account with appropriate permissions
- AWS CLI configured
- AWS SAM CLI installed
- SDKMAN for Java version management
- GraalVM 25 with Native Image support
- Maven 3.x
- Git

## Installation

### 1. Install GraalVM and Native Image

```bash
# Install SDKMAN
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Install GraalVM 25
sdk install java 25.0.2-graal

# Set JAVA_HOME
export JAVA_HOME=$HOME/.sdkman/candidates/java/25.0.2-graal/

# Install Native Image dependencies (Amazon Linux 2023/RHEL)
sudo dnf install gcc glibc-devel zlib-devel libstdc++-static
```

### 2. Install Maven

```bash
sudo dnf install maven
```

### 3. Clone the Repository

```bash
git clone https://github.com/Vadym79/aws-lambda-java-25.git
cd aws-lambda-java-25/aws-lambda-java-25-aurora-dsql-as-graalvm-native-image
```

## Build

Compile and package the application with Maven:

```bash
mvn clean package
```

This command will:
1. Compile Java 25 source code
2. Create a shaded JAR with all dependencies
3. Build a GraalVM native image
4. Package everything into `function.zip` for Lambda deployment

## Database Setup

### 1. Create Aurora DSQL Cluster

The SAM template automatically creates an Aurora DSQL cluster. After deployment, connect to it using one of these methods:
- AWS CloudShell
- psql client
- Aurora DSQL Query Editor in AWS Console

See [Aurora DSQL Getting Started Guide](https://docs.aws.amazon.com/aurora-dsql/latest/userguide/getting-started.html)

### 2. Initialize Database Schema

Connect to your DSQL cluster and execute:

```sql
-- Create products table
CREATE TABLE products (
    id int PRIMARY KEY,
    name varchar(256) NOT NULL,
    price int NOT NULL
);

-- Create sequence for auto-generating IDs
CREATE SEQUENCE product_id CACHE 1;
```

### 3. (Optional) Insert Sample Data

```sql
INSERT INTO products VALUES (1, 'Print 10x13', 15);
INSERT INTO products VALUES (2, 'A5 Book', 5000);
```

## Deployment

Deploy the application using AWS SAM:

```bash
sam deploy -g --region us-east-1
```

Follow the prompts to configure:
- Stack name
- AWS Region
- Confirm changes before deploy
- Allow SAM CLI IAM role creation
- Save arguments to configuration file

The deployment creates:
- Aurora DSQL cluster
- Two Lambda functions (native images)
- API Gateway with REST endpoints
- CloudWatch Log Groups
- IAM roles and policies

## Usage

### API Authentication

The API requires an API key. The default key is defined in `template.yaml`:
```
x-api-key: a6ZbcDefQW12BN56WEDQGVNI25
```

### Create a Product

```bash
curl -X POST https://{api-id}.execute-api.us-east-1.amazonaws.com/prod/products \
  -H "x-api-key: a6ZbcDefQW12BN56WEDQGVNI25" \
  -H "Content-Type: application/json" \
  -d '{"name": "Print 10x13", "price": 15}'
```

With auto-generated ID (using sequence):
```bash
curl -X POST https://{api-id}.execute-api.us-east-1.amazonaws.com/prod/products \
  -H "x-api-key: a6ZbcDefQW12BN56WEDQGVNI25" \
  -H "Content-Type: application/json" \
  -d '{"name": "A5 Book", "price": 5000}'
```

### Get Product by ID

```bash
curl -X GET https://{api-id}.execute-api.us-east-1.amazonaws.com/prod/products/1 \
  -H "x-api-key: a6ZbcDefQW12BN56WEDQGVNI25"
```

## Project Structure

```
.
├── src/
│   ├── main/
│   │   ├── java/software/amazonaws/example/product/
│   │   │   ├── dao/
│   │   │   │   ├── DsqlDataSourceConfig.java    # HikariCP configuration
│   │   │   │   └── ProductDao.java               # Data access layer
│   │   │   ├── entity/
│   │   │   │   └── Product.java                  # Product record
│   │   │   └── handler/
│   │   │       ├── CreateProductHandler.java     # POST handler
│   │   │       └── GetProductByIdHandler.java    # GET handler
│   │   ├── resources/
│   │   │   └── META-INF/native-image/            # GraalVM config
│   │   └── reflect-config.json                   # Reflection config
│   ├── assembly/
│   │   └── native.xml                            # Assembly descriptor
│   └── shell/native/
│       └── bootstrap                             # Lambda custom runtime bootstrap
├── pom.xml                                       # Maven configuration
├── template.yaml                                 # SAM template
└── samconfig.toml                                # SAM configuration
```

## Key Dependencies

- **AWS SDK for Java 2.x** - DSQL client
- **Aurora DSQL JDBC Connector** (1.4.0)
- **HikariCP** (7.0.2) - Connection pooling
- **AWS Lambda Java Core** (1.4.0)
- **AWS Lambda Java Events** (3.16.1)
- **FormKiQ Lambda Runtime GraalVM** (2.6.0)
- **Jackson** - JSON/XML processing
- **SLF4J** - Logging

## Configuration

### Environment Variables

The Lambda functions use the following environment variable (set automatically by SAM):
- `AURORA_DSQL_CLUSTER_ENDPOINT` - DSQL cluster endpoint

### Lambda Configuration

- **Runtime**: provided.al2023 (custom runtime)
- **Architecture**: x86_64
- **Memory**: 1024 MB
- **Timeout**: 30 seconds

## Performance Benefits

GraalVM Native Image provides:
- **Fast cold starts** (~100-200ms vs 1-2s for JVM)
- **Lower memory usage** (~50-70% reduction)
- **Predictable performance** - no JIT warmup
- **Smaller deployment packages**

## Cleanup

To delete all resources:

```bash
sam delete
```

## Troubleshooting

### Build Issues

- Ensure `JAVA_HOME` points to GraalVM 25
- Verify native-image dependencies are installed
- Check Maven version compatibility

### Database Connection Issues

- Verify Lambda has `dsql:DbConnectAdmin` permission
- Check DSQL cluster endpoint in environment variables
- Review CloudWatch Logs for connection errors

### API Gateway Issues

- Verify API key is included in request headers
- Check API Gateway logs in CloudWatch
- Ensure Lambda functions have proper permissions

## References

- [Aurora DSQL Documentation](https://docs.aws.amazon.com/aurora-dsql/latest/userguide/)
- [GraalVM Native Image](https://www.graalvm.org/latest/reference-manual/native-image/)
- [AWS SAM Documentation](https://docs.aws.amazon.com/serverless-application-model/)
- [HikariCP Documentation](https://github.com/brettwooldridge/HikariCP)

## License

This project is provided as-is for demonstration purposes.
