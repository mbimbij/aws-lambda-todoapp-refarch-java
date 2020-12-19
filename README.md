# Déploiement

## via `aws CLI` - depuis target (taille < 50MB)

`aws lambda update-function-code --function-name hello-java --zip-file fileb://target/webapp-refarch-todo-java-0.0.1-SNAPSHOT.jar`

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

# Todo-App

Ici, le but est de partir d'un hello-world, que l'on sait déployer avec sam ou aws cli, et
de le faire évoluer vers quelque chose de proche de l'architecture de référence d'une webapp serverless:

https://github.com/aws-samples/lambda-refarch-webapp

On va procéder en "TDD" (avec tout de même un design cible).

EDIT: On effectue nos vérifications "à la main" dans un premier temps, en mode "tracer-bullet". Les tests automatisés viendront dans un second temps:
- À ce stade de l'implémentation, on est encore dans une phase de découverte de l'outil
- On ne connaît pas d'outils de test
- Plutôt que de perdre du temps et de la motivation à avoir tout l'outillage de test AVANT de pousser le moindre morceau de code,
on met deja en place quelques use case de la todo-app, que l'on vérifie "à la main"
- Quand on aura suffisamment découvert l'API, on mettra en place l'outillage de test 
    - tests automatisés en local
    - tests automatisés en remote
        - trouver un moyen d'introduire un environnement de test, ou bien utiliser API-Gateway pour faire une sorte de canary 
          et ne pas rediriger de traffic de prod vers la lambda en cours de test (ou la nouvelle version)
        - une fois les tests ok, pousser la nouvelle lambda en prod, ou bien utiliser API gateway par exemple, pour rediriger le traffic vers notre lambda validée, ou vers la nouvelle version de la lambda

tests:

- retourner une liste de tâches en dur
- retourner une liste de tâches en dur, mais récupérée depuis DynamoDB cette fois-ci
- créer un item
- supprimer un item (par id)
- retourner la liste des tâches, depuis l'api gateway
- créer une tâche, depuis l'api gateway
- supprimer une tâche par son id, depuis l'api gateway
- passer à "DONE" le status d'une tâche par son id, depuis l'api gateway