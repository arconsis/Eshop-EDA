echo "Please enter the environment (stg OR prod): "
read environment

if [ "$environment" != "stg" ] && [ "$environment" != "prod" ]; then
  echo "Select a valid environment"
  exit 0
fi

if [ "$environment" == "stg" ];
then
  kubectl config use-context eks_test-eks-cluster
else
  kubectl config use-context prod
fi

kubectl apply -f ./namespace.yaml

if [ "$environment" == "stg" ];
then
  kubectl apply -f ./EmailService/stg-secret.yaml
  kubectl apply -f ./OrderService/stg-secret.yaml
  kubectl apply -f ./PaymentService/stg-secret.yaml
  kubectl apply -f ./ShippingService/stg-secret.yaml
  kubectl apply -f ./UserService/stg-secret.yaml
  kubectl apply -f ./WarehouseService/stg-secret.yaml
else
  kubectl apply -f ./EmailService/prod-secret.yaml
  kubectl apply -f ./OrderService/prod-secret.yaml
  kubectl apply -f ./PaymentService/prod-secret.yaml
  kubectl apply -f ./ShippingService/prod-secret.yaml
  kubectl apply -f ./UserService/prod-secret.yaml
  kubectl apply -f ./WarehouseService/prod-secret.yaml
fi

kubectl apply -f ./EmailService/deployment.yaml
kubectl apply -f ./EmailService/service.yaml

kubectl apply -f ./OrderService/deployment.yaml
kubectl apply -f ./OrderService/service.yaml

kubectl apply -f ./PaymentService/deployment.yaml
kubectl apply -f ./PaymentService/service.yaml

kubectl apply -f ./ShippingService/deployment.yaml
kubectl apply -f ./ShippingService/service.yaml

kubectl apply -f ./UserService/deployment.yaml
kubectl apply -f ./UserService/service.yaml

kubectl apply -f ./WarehouseService/deployment.yaml
kubectl apply -f ./WarehouseService/service.yaml

kubectl apply -f ./ingress.yaml