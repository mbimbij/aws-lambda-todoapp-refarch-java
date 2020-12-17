# Déploiement

## via `aws CLI` - depuis target (taille < 50MB)

`aws lambda update-function-code --function-name hello-java --zip-file fileb://target/aws_lambda_hello_java-0.0.1-SNAPSHOT.jar`

## via `aws CLI` - depuis S3 (taille >= 50MB)

```
aws s3 cp target/aws_lambda_hello_java-0.0.1-SNAPSHOT.jar s3://myBucket
aws lambda update-function-code --function-name hello-java --s3-bucket myBucket --s3-key aws_lambda_hello_java-0.0.1-SNAPSHOT.jar
```