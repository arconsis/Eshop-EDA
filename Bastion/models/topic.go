package models

type Topic struct {
	Name              string            `json:"name"`
	Partitions        int               `json:"partitions"`
	ReplicationFactor int               `json:"replicationFactor"`
	Config            map[string]string `json:"config"`
}
