###BOOK Cloud Native Spring in Action - Catalog Service: Responsible for managing the catalog of books in the Polar Bookshop system ### 

#create project:
curl https://start.spring.io/starter.zip -d groupId=com.cnsia.polarbookshop -d artifactId=catalog-service -d name=catalog-service -d packageName=com.cnsia.polarbookshop.catalogservice -d dependencies=web -d javaVersion=21 -d bootVersion=3.4.1 -d type=gradle-project -o catalog-service.zip

## REST API
|:---------------:|:--------:|:----------:|:------:|:--------------:|:------------------------------------------|
| Endpoint	  | Method   | Req. body  | Status | Resp. body     | Description                               |
|:---------------:|:--------:|:----------:|:------:|:--------------:|:------------------------------------------|
| `/books`        | `GET`    |            | 200    | Book[]         | Get all the books in the catalog.         |
|_________________|__________|____________|________|________________|___________________________________________|
| `/books`        | `POST`   | Book       | 201    | Book           | Add a new book to the catalog.            |
|_________________|__________|____________|________|________________|___________________________________________|
|                 |          |            | 422    |                | A book with the same ISBN already exists. |
|_________________|__________|____________|________|________________|___________________________________________|
| `/books/{isbn}` | `GET`    |            | 200    | Book           | Get the book with the given ISBN.         |
|_________________|__________|____________|________|________________|___________________________________________|
|                 |          |            | 404    |                | No book with the given ISBN exists.       |
|_________________|__________|____________|________|________________|___________________________________________|
| `/books/{isbn}` | `PUT`    | Book       | 200    | Book           | Update the book with the given ISBN.      |
|_________________|__________|____________|________|________________|___________________________________________|
|                 |          |            | 200    | Book           | Create a book with the given ISBN.        |
|_________________|__________|____________|________|________________|___________________________________________|
| `/books/{isbn}` | `DELETE` |            | 204    |                | Delete the book with the given ISBN.      |
|:---------------:|:--------:|:----------:|:------:|:--------------:|:------------------------------------------|

#start a local Kubernetes cluster: 
minikube start

#import local image: 
minikube image load catalog-service:0.0.1-SNAPSHOT

#list the available images: 
minikube image ls --format table

#create a deployment resource from a container image: 
kubectl create deployment catalog-service --image=catalog-service:0.0.1-SNAPSHOT

#check the application logs
kubectl logs deployment/catalog-service

#verify the creation of the Deployment object: 
kubectl get deployment

#verify the creation of the Pod object: 
kubectl get pod

#command to expose a Deployment as a Service - make applications accessible: 
kubectl expose deployment catalog-service --name=catalog-service --port=9001

#verify that the service was create correctly
kubectl get service catalog-service

#forwarding a port from localhost to a Service inside the cluster: 
kubectl port-forward service/catalog-service 8000:9001

#Terminate: 
kubectl delete service catalog-service
kubectl delete deployment catalog-service
minikube image rm  catalog-service:0.0.1-SNAPSHOT
minikube stop

#combines the operations of the bootJar and test tasks: 
./gradlew build

#run the application: 
 ./gradlew bootRun

#compiles the code and packages the application as a JAR file: 
./gradlew bootJar

#Package the application as a container using Buildpack (build the image)
./gradlew bootBuildImage

#run the image / run the image connecting to the catalog-network, set the datasource URL, and set the param profile to load test data.
docker run --rm --name catalog-service -p 9001:9001 catalog-service:0.0.1-SNAPSHOT
docker run -d --name catalog-service --net catalog-network -p 9001:9001 -e SPRING_DATASOURCE_URL=jdbc:postgresql://polar-postgres:5432/polardb_catalog -e SPRING_PROFILES_ACTIVE=testdata catalog-service

#add a book to the catalog: 
http POST :9001/books author="Lyra Silverstar" title="Northern Lights" isbn="1234567891" price=9.90
curl -X POST http://localhost:9001/books -d '{"author": "Lyra Silverstart", "title":"Nothern Lights", "isbn":"1284567891", "price":9.90}' -H "content-type: application/json" -v

#request to fetch the book with the ISBN code: 
 http :9001/books/1234567891

#request to get all books
http :9001/books

#run tests: 
./gradlew test

#run a specific test: 
./gradlew test --tests BookValidationTests
./gradlew test --tests CatalogServiceApplicationTests
./gradlew test --tests BookControllerMvcTests
./gradlew test --tests BookJsonTests
./gradlew test --tests BookServiceTest

#running the application with the prod profile enabled
java -jar build/libs/catalog-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

#test the polar.greeting property calling the HomeController object
http :9001/

#trigger a RefreshScopeRefreshedEvent event
http POST :9001/actuator/refresh

#Create Docker DNS Server
docker network create catalog-network

#running PostgreSQL as a container / start a PSQL container specifiying the catalog-network 
docker run -d --name polar-postgres -e POSTGRES_USER=user -e POSTGRES_PASSWORD=password -e POSTGRES_DB=polardb_catalog -p 5432:5432 postgres:16.6
docker run -d --name polar-postgres --net catalog-network -e POSTGRES_USER=user -e POSTGRES_PASSWORD=password -e POSTGRES_DB=polardb_catalog -p 5432:5432 postgres:16.6

#stop, start, and remove the container
docker stop polar-postgres
docker start polar-postgres
docker rm -fv polar-postgres

#Start an interactive PSQL console:
docker exec -it polar-postgres psql -U user -d polardb_catalog

#List all databases
\list

#connect to polar database
\connect polardb_catalog

#list all tables
\dt

#show the table schema
\d book

#Test with Testcontainers
./gradlew test --tests BookRepositoryJdbcTests
./gradlew test --tests CatalogServiceApplicationTests


#Containerizing Spring Boot with Dockerfiles
1. Create  file called Dockerfile in the root folder
2. ./gradlew clean bootJar #build the JAR artifact
3. docker build -t catalog-service . #build the continer image

#GitHub container registry
docker login ghcr.io (PAT - Settings > Developer Settings > Personal access tokens)

#check if the newly created image contains any vulnerability
grype catalog-service

#delete Docker resources
docker rm -f catalog-service polar-postgres
docker network rm catalog-network

#build and publish the image
./gradlew bootBuildImage --imageName ghcr.io/fabiop35/catalog-service --publishImage -PregistryUrl=ghcr.io -PregistryUsername=fabiop35 -PregistryToken=<github_token>

#start the containers in detached mode
docker compose up -d

#stop and remove both containers
docker compose down

#CH07

#create a new Kubernetes cluster named polar on top of Docker
> minikube start --cpus 2 --memory 4g --driver docker --profile polar
> minikube start --profile polar
> minikube stop --profile polar
> kubectl get nodes
> kubectl apply -f services
$ kubectl delete -f services
> kubectl get pod
> kubectl logs deployment/polar-postgres
> minikube image load catalog-service --profile polar
> kubectl apply -f k8s/deployment.yml
$ kubectl delete -f k8s/deployment.yml
$ kubectl get all -l app=catalog-service
$ kubectl logs deployment/catalog-service
$ kubectl describe pod catalog-service
$ kubectl get svc -l app=catalog-service
$ kubectl -n default delete deploy catalog-service
$ kubectl get namespaces
$ kubectl delete svc catalog-service
$ kubectl delete pod <pod-name>

#cd catalog-service
> mkdir k8s
> touch deployment.yml

$ kubectl port-forward service/catalog-service 9001:80
$ netstat -an | grep LISTEN
 #tcp6       0      0 ::1:9001                :::*                    LISTEN
$ http :9001/books
$ cd catalog-service
$ touch Tiltfile
$ tilt up --host 0.0.0.0
  > Tilt started on http://localhost:10350/