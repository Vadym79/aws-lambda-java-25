FROM public.ecr.aws/lambda/java:25

# Copy function code and runtime dependencies from Maven layout
#COPY target/classes ${LAMBDA_TASK_ROOT}
COPY target/aws-lambda-java-25-with-dynamodb-as-container-image-* ${LAMBDA_TASK_ROOT}/lib/