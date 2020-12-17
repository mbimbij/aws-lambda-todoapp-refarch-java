# Déploiement

## via `aws CLI` - depuis target (taille < 50MB)

`aws lambda update-function-code --function-name hello-java --zip-file fileb://target/aws_lambda_hello_java-0.0.1-SNAPSHOT.jar`

## via `aws CLI` - depuis S3 (taille >= 50MB)

```shell
aws s3 cp target/aws_lambda_hello_java-0.0.1-SNAPSHOT.jar s3://acg-devops-joseph
aws lambda update-function-code --function-name hello-java --s3-bucket acg-devops-joseph --s3-key aws_lambda_hello_java-0.0.1-SNAPSHOT.jar
```

## via `SAM`

```shell
mvn clean package
sam package --template-file template.yml --s3-bucket acg-devops-joseph --output-template-file out-template.yml
sam deploy --template-file out-template.yml --stack-name hello-java --capabilities CAPABILITY_IAM
```