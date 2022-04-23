kubectl apply -f ./namespace.yaml

cd ./configs
sh deploy.sh
cd ../Bastion
sh deploy.sh
cd ../EmailService
sh deploy.sh
cd ../OrderService
sh deploy.sh
cd ../UserService
sh deploy.sh
cd ../PaymentService
sh deploy.sh
cd ../WarehouseService
sh deploy.sh
cd ../Debezium
sh deploy.sh
cd ../

kubectl apply -f ./ingress.yaml