###BOOK Cloud Native Spring in Action - Catalog Service: Responsible for managing the catalog of books in the Polar Bookshop system 

#create project
curl https://start.spring.io/starter.zip -d groupId=com.cnsia.polarbookshop -d artifactId=catalog-service -d name=catalog-service -d packageName=com.cnsia.polarbookshop.catalogservice -d dependencies=web -d javaVersion=21 -d bootVersion=3.4.1 -d type=gradle-project -o catalog-service.zip

#start a local Kubernetes cluster
minikube start

#import local image
minikube image load catalog-service:0.0.1-SNAPSHOT

#list the available images
minikube image ls --format table

#check the application logs
kubectl logs deployment/catalog-service

#create a deployment resource from a container image
kubectl create deployment catalog-service --image=catalog-service:0.0.1-SNAPSHOT

#verify the creation of the Deployment object
kubectl get deployment

#verify the creation of the Pod object 
kubectl get pod

#command to expose a Deployment as a Service - make applications accessible
kubectl expose deployment catalog-service --name=catalog-service --port=8080

#verify that the service was create correctly
kubectl get service catalog-service

#forwarding a port from localhost to a Service inside the cluster
kubectl port-forward service/catalog-service 8000:8080

#Terminate
kubectl delete service catalog-service
kubectl delete deployment catalog-service
minikube stop


