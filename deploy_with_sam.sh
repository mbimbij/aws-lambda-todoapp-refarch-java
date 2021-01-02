#! /bin/bash

PROJECT_NAME=$(mvn -q -Dexec.executable="echo" -Dexec.args='${project.name}' --non-recursive exec:exec)
mvn clean package
#sam package --template-file template.yml --s3-bucket $1 --output-template-file out-template.yml
sam deploy --template-file template.yml --stack-name $PROJECT_NAME --capabilities CAPABILITY_IAM --no-confirm-changeset --resolve-s3

