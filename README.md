# Autonomous Driving Platform
A highly scalable protoytpe of a autonomous driving platform. Millions of autonomous cars are simulated for evaluation
purposes to send their location to car manufactures and get notified about nearby crash events. Car manufactures can
view all their autonomous navigating cars on a map and are notified about crash events. Furthermore there are blue light
organistions that get notified about crash events and they can resolve them.

Key facts:
* Automtic CI/CD to deploy to a kubernetes cluster in the google cloud.
* Technologies used: Spring Cloud, Spring Data, Vue, Hystrix, MongoDB, Redis, GCP, kubernetes, docker
* Latency and Fault Tolerance: Implemented with Hystrix, fallback is a Redis Cache which returns previous results of the method called

#### Dashboard for Car manufacturers where they can view all their cars, also in a map and notfications about crash events
![image](https://user-images.githubusercontent.com/14179713/180953439-6cd67541-71dd-4b90-94e3-7293b6c7dff0.png)
#### Simulation UI to simulate car movement and crashes
![image](https://user-images.githubusercontent.com/14179713/180953655-86c5709b-bf80-4388-9f99-7e4d35059dcf.png)
#### Cluster with the deployed and scaled container
![image](https://user-images.githubusercontent.com/14179713/180954824-6277a7fc-ea4e-4445-827c-57ab5ef8a076.png)
#### Hystrix Cache Fallback when containers are forced stopped
![image](https://user-images.githubusercontent.com/14179713/180961122-aa2d95dc-f1e2-47ed-bd44-f72e642c4e0c.png)


## Development

To run BackendUI:
* go to backendui folder and run `npm install` and then `npm run serve`

To run mongodb and rabbitmq:
* execute `docker-compose up`

Then you can start all other applications by simply running them in IntelliJ

## Kubernetes

### Setup
* Start Minikube `minikube start`
* minikube dashboard
* The Kubernets Interface is reachable through http://192.168.99.100:30000
* The services are reachable through `192.168.99.100/eventstorage`
* Run `kubectl create clusterrolebinding admin --clusterrole=cluster-admin --serviceaccount=default:default` to add new role that spring cloud can access the information from kubernetes

* then build all docker images of projects in the root directory with command `install -DskipTests`. This command automatically builds the docker images and pushes them to dockerhub. don't forget to login first with `docker login` if not done before
* Then run `kubectl apply -f entitystorageservice-deployment.yaml` for all files in the kubernetes folder or run `execute-all.sh`
