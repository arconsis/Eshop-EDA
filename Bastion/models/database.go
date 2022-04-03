package models

type Database struct {
	Name     string `json:"name"`
	UserName string `json:"userName"`
	Endpoint string `json:"endpoint"`
}

type DatabaseRequest struct {
	*Database
}
