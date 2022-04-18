package models

type Database struct {
	Name  string `json:"name"`
	Owner string `json:"owner"`
}

type DatabaseRequest struct {
	*Database
}
