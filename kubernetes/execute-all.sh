#!/bin/bash
kubectl apply -f mongo-secret.yaml
kubectl apply -f mongo-configmap.yaml
kubectl apply -f mongo-deployment.yaml
kubectl apply -f entitystorageservice-deployment.yaml
kubectl apply -f eventstorageservice-deployment.yaml
kubectl apply -f notificationstorageservice-deployment.yaml
kubectl apply -f ingress.yaml
kubectl apply -f backendui-deployment.yaml
kubectl apply -f swaggergateway-deployment.yaml