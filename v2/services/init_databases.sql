ALTER SYSTEM SET wal_level = logical;
CREATE DATABASE "orders-db" OWNER postgres;
CREATE DATABASE "warehouse-db" OWNER postgres;
CREATE DATABASE "payments-db" OWNER postgres;
CREATE DATABASE "user-db" OWNER postgres;