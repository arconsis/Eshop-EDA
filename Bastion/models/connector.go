package models

type Connector struct {
	Name   string `json:"name"`
	Config struct {
		ConnectorClass                        string `json:"connector.class"`
		PluginName                            string `json:"plugin.name"`
		DatabaseHostname                      string `json:"database.hostname"`
		DatabasePort                          string `json:"database.port"`
		DatabaseUser                          string `json:"database.user"`
		DatabasePassword                      string `json:"database.password"`
		DatabaseDbname                        string `json:"database.dbname"`
		DatabaseServerName                    string `json:"database.server.name"`
		TableIncludeList                      string `json:"table.include.list"`
		DatabaseHistoryKafkaBootstrapServers  string `json:"database.history.kafka.bootstrap.servers"`
		DatabaseHistoryKafkaTopic             string `json:"database.history.kafka.topic"`
		SlotName                              string `json:"slot.name"`
		TopicCreationDefaultReplicationFactor int    `json:"topic.creation.default.replication.factor"`
		TopicCreationDefaultPartitions        int    `json:"topic.creation.default.partitions"`
	} `json:"config"`
}
