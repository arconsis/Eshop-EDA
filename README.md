# EDA (Event Driven Architecture) e-shop 

This project acts as a playground to learn and develop *event driven system* using an **e-shop** example as a showcase.

We took multiple approaches of developing an event based systems:

1) Version 1 is build using events and asynchronous communication between the services. The code for v1 can be found [here](https://github.com/arconsis/Eshop-EDA/tree/main/v1)
2) Version 2 is build using Debezium to stream changes from the databases. The code for v2 can be found [here](https://github.com/arconsis/Eshop-EDA/tree/main/v2)
3) Version 3 is build using the Event Sourcing approach and KSQL and KStreams. The code for v3 can be found [here](https://github.com/arconsis/Eshop-EDA/tree/main/v3)

We will keep the readme updated providing also and info about the macro architecture of the different versions.

#### Kubernetes setup

To get the kubeconfig for the cluster run the following command after applying terraform
```
aws eks update-kubeconfig --region region-code --name cluster-name
```

## Show your support

Give a ⭐️ if this project helped you!
