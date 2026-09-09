FROM public.ecr.aws/lambda/java:25

# Copy function code and runtime dependencies from Maven layout
#COPY target/classes ${LAMBDA_TASK_ROOT}
COPY target/aws-lambda-java-25-with-dynamodb-1.0.0-SNAPSHOT.jar ${LAMBDA_TASK_ROOT}/lib/