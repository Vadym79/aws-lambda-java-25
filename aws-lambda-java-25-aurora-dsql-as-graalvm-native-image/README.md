# Example of Lambda with managed Java 25 runtime using Amazon Aurora DSQL  


## Architecture


## Installation and deployment


```bash

Clone git repository locally
git clone https://github.com/Vadym79/aws-lambda-java-25.git

## Switch to the aws-lambda-java-25-aurora-dsql directory  
## Compile and package the Java application with Maven from the root (where pom.xml is located) of the project

mvn clean package

Deploy your application with AWS SAM
sam deploy -g --region us-east-1

```

## In oder to use it you're required to

1) Connect to the already created Aurora DSQL cluster using CloudShell, psql or integrated query browser in the Aurora DSQL console see the desciption here  
 https://docs.aws.amazon.com/aurora-dsql/latest/userguide/getting-started.html#connect-dsql-cluster and here  
 https://docs.aws.amazon.com/aurora-dsql/latest/userguide/getting-started.html#accessing-sql-clients-psql
 https://docs.aws.amazon.com/aurora-dsql/latest/userguide/getting-started-query-editor.html
 
2) Execute these sql statements to create table and sequences   

CREATE TABLE products (id int PRIMARY KEY,  name varchar (256) NOT NULL, price int NOT NULL); 

3) Populate some data

INSERT INTO products VALUES (1, 'Print 10x13', 15);
INSERT INTO products VALUES (2,  'A5 Book', 5000); 


Or you can use the deployed API Gateway and its REST endpoints like get for /product/{id} and post for /product and so on

Use this Http Body as Json to create a sample order with 2 items :

 { "id": 1, "name": "Print 10x13", "price": 15 }
 and
 { "id": 2, "name": "A5 Book", "price": 5000 }


