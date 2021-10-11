echo "Please enter the enviroment (stg OR prod): "
read enviroment

if [ $enviroment != "stg" ] && [ $enviroment != "prod" ]; then
  echo "Select a valid enviroment"
  exit 0
fi

if [ $enviroment == "stg" ];
then
  kubectl config use-context stg
else
  kubectl config use-context prod
fi

kubectl apply -f ../kubernetes/namespace.yaml

if [ $enviroment == "stg" ];
then
  kubectl create -f ../kubernetes/EmailService/stg-secret.yaml
else
  kubectl create -f ../kubernetes/EmailService/prod-secret.yaml
fi
kubectl apply -f ../kubernetes/EmailService/deployment.yaml
kubectl apply -f ../kubernetes/EmailService/service.yaml

if [ $enviroment == "stg" ];
then
  kubectl create -f ../kubernetes/OrderService/stg-secret.yaml
else
  kubectl create -f ../kubernetes/OrderService/prod-secret.yaml
fi
kubectl apply -f ../kubernetes/OrderService/deployment.yaml
kubectl apply -f ../kubernetes/OrderService/service.yaml

if [ $enviroment == "stg" ];
then
  kubectl create -f ../kubernetes/PaymentService/stg-secret.yaml
else
  kubectl create -f ../kubernetes/PaymentService/prod-secret.yaml
fi
kubectl apply -f ../kubernetes/PaymentService/deployment.yaml
kubectl apply -f ../kubernetes/PaymentService/service.yaml

if [ $enviroment == "stg" ];
then
  kubectl create -f ../kubernetes/ShippingService/stg-secret.yaml
else
  kubectl create -f ../kubernetes/ShippingService/prod-secret.yaml
fi
kubectl apply -f ../kubernetes/ShippingService/deployment.yaml
kubectl apply -f ../kubernetes/ShippingService/service.yaml

if [ $enviroment == "stg" ];
then
  kubectl create -f ../kubernetes/UserService/stg-secret.yaml
else
  kubectl create -f ../kubernetes/UserService/prod-secret.yaml
fi
kubectl apply -f ../kubernetes/UserService/deployment.yaml
kubectl apply -f ../kubernetes/UserService/service.yaml

if [ $enviroment == "stg" ];
then
  kubectl create -f ../kubernetes/WarehouseService/stg-secret.yaml
else
  kubectl create -f ../kubernetes/WarehouseService/prod-secret.yaml
fi
kubectl apply -f ../kubernetes/WarehouseService/deployment.yaml
kubectl apply -f ../kubernetes/WarehouseService/service.yaml

kubectl apply -f ../kubernetes/ingress.yaml