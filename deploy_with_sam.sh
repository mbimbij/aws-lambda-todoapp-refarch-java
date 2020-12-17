#! /bin/bash

if [ -z $1 ]; then 
  echo 'usage: ./deploy_with_sam.sh $BUCKET_NAME'
  exit 1
fi

PROJECT_NAME=$(mvn -q -Dexec.executable="echo" -Dexec.args='${project.name}' --non-recursive exec:exec)
mvn clean package
sam package --template-file template.yml --s3-bucket $1 --output-template-file out-template.yml
sam deploy --template-file out-template.yml --stack-name $PROJECT_NAME --capabilities CAPABILITY_IAM

