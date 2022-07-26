# Autonomous Driving Platform

![image](https://user-images.githubusercontent.com/14179713/180953439-6cd67541-71dd-4b90-94e3-7293b6c7dff0.png)
![image](https://user-images.githubusercontent.com/14179713/180953655-86c5709b-bf80-4388-9f99-7e4d35059dcf.png)
![image](https://user-images.githubusercontent.com/14179713/180954824-6277a7fc-ea4e-4445-827c-57ab5ef8a076.png)
![image](https://user-images.githubusercontent.com/14179713/180955126-773f4145-8af0-438b-957d-d54fc8d393ad.png)
![image](https://user-images.githubusercontent.com/14179713/180959236-44e28cf2-5791-4b7e-b88c-b113d15360f3.png)
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


* first delete dev properties file and rename application-prod.yml to application.yml
* then build all docker images of projects in the root directory with command `install -DskipTests`. This command automatically builds the docker images and pushes them to dockerhub. don't forget to login first with `docker login` if not done before
* build backendui with `docker build -t e1426972/backendui .`
* push backendui docker push e1426972/backendui
* Then run `kubectl apply -f entitystorageservice-deployment.yaml` for all files in the kubernetes folder or run `execute-all.sh`

### Cheet Sheet Commands for Testing

kubectl port-forward event-storage-8466b48c9-pjp4r 8081:8080
docker run -it -p 8080:8080 --rm --name backendui e1426972/backendui

### Used libaries for gui which may be needed to installed:
* vue-google-maps `npm install --save vue2-google-maps`
* v-tooltip `npm install --save v-tooltip`
* icons `npm install material-design-icons-iconfont -D`
* geolocation-utils `npm install --save geolocation-utils`
