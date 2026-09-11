FROM public.ecr.aws/lambda/java:25

# Copy function code and runtime dependencies from Maven layout
COPY target/aws-lambda-java-25-with-hibernate-and-aurora-dsql-as-container-image-*.jar ${LAMBDA_TASK_ROOT}/lib/