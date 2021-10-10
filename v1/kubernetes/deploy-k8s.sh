kubectl config use-context stg

kubectl apply -f namespace.yaml

kubectl create -f EmailService/stg-secret.yaml
kubectl apply -f EmailService/deployment.yaml
kubectl apply -f EmailService/service.yaml

kubectl create -f OrderService/stg-secret.yaml
kubectl apply -f OrderService/deployment.yaml
kubectl apply -f OrderService/service.yaml

kubectl create -f PaymentService/stg-secret.yaml
kubectl apply -f PaymentService/deployment.yaml
kubectl apply -f PaymentService/service.yaml

kubectl create -f ShippingService/stg-secret.yaml
kubectl apply -f ShippingService/deployment.yaml
kubectl apply -f ShippingService/service.yaml

kubectl create -f UserService/stg-secret.yaml
kubectl apply -f UserService/deployment.yaml
kubectl apply -f UserService/service.yaml

kubectl create -f WarehouseService/stg-secret.yaml
kubectl apply -f WarehouseService/deployment.yaml
kubectl apply -f WarehouseService/service.yaml

kubectl apply -f ingress.yaml