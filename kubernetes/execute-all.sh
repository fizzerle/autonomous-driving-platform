#!/bin/bash
kubectl replace --force -f mongo-secret.yaml
kubectl replace --force -f mongo-configmap.yaml
kubectl replace --force -f mongo-deployment.yaml
kubectl replace --force -f entitystorageservice-deployment.yaml
kubectl replace --force -f eventstorageservice-deployment.yaml
kubectl replace --force -f notificationstorageservice-deployment.yaml
kubectl replace --force -f ingress.yaml
kubectl replace --force -f backendui-deployment.yaml
kubectl replace --force -f swaggergateway-deployment.yaml
kubectl replace --force -f apigateway-deployment.yaml