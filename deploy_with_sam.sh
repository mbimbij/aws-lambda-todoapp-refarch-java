#! /bin/bash

if [ -z $1 ]; then 
  echo 'usage: ./deploy_with_sam.sh $BUCKET_NAME'
  exit 1
fi

mvn clean package
sam package --template-file template.yml --s3-bucket $1 --output-template-file out-template.yml
sam deploy --template-file out-template.yml --stack-name hello-java --capabilities CAPABILITY_IAM

