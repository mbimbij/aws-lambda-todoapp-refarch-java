# Déploiement

`deploy_with_sam.sh` :
```shell
mvn clean package
sam deploy --template-file template.yml --stack-name $PROJECT_NAME --capabilities CAPABILITY_IAM --no-confirm-changeset --resolve-s3
```

# Todo-App

Ici, le but est de partir d'un hello-world, que l'on sait déployer avec sam ou aws cli, et de le faire évoluer vers
quelque chose de proche de l'architecture de référence d'une webapp serverless:

https://github.com/aws-samples/lambda-refarch-webapp

On va procéder en "TDD" (avec tout de même un design cible).

EDIT: On effectue nos vérifications "à la main" dans un premier temps, en mode "tracer-bullet". Les tests automatisés
viendront dans un second temps:

- À ce stade de l'implémentation, on est encore dans une phase de découverte de l'outil
- On ne connaît pas d'outils de test
- Plutôt que de perdre du temps et de la motivation à avoir tout l'outillage de test AVANT de pousser le moindre morceau
  de code, on met deja en place quelques use case de la todo-app, que l'on vérifie "à la main"
- Quand on aura suffisamment découvert l'API, on mettra en place l'outillage de test
    - tests automatisés en local
    - tests automatisés en remote
        - trouver un moyen d'introduire un environnement de test, ou bien utiliser API-Gateway pour faire une sorte de
          canary et ne pas rediriger de traffic de prod vers la lambda en cours de test (ou la nouvelle version)
        - une fois les tests ok, pousser la nouvelle lambda en prod, ou bien utiliser API gateway par exemple, pour
          rediriger le traffic vers notre lambda validée, ou vers la nouvelle version de la lambda

Après avoir beaucoup souffert lors de tests manuels, en rétrospective, il aurait été peut-être été plus judicieux de
creuser des tests automatisés en local plus tôt. En même temps si l'on avait démarré des tests automatisés plus tôt, on
aurait facilement pu se sentir frustré de freiner si près du but et on aurait commenté qu'il aurait mieux fallu finir
une première implémentation avec la stratégie initiale de tests manuels avant de faire des tests autos ... it is what it
is ...

tests:

- Phase #1 - "make it work"
    - retourner une liste de tâches en dur
    - retourner une liste de tâches en dur, mais récupérée depuis DynamoDB cette fois-ci
    - créer un item
    - supprimer un item (par id)
    - retourner la liste des tâches, depuis l'api gateway
    - créer une tâche, depuis l'api gateway
    - supprimer une tâche par son id, depuis l'api gateway
    - passer à "DONE" le status d'une tâche par son id, depuis l'api gateway
- Phase #2 - ajout de tests automatisés en local
    - création d'un item
    - récupération de tous les items
    - définir l'état d'un item comme "terminé"
    - supprimer un item