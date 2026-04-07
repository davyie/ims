-- IMS: Create per-service databases on the shared PostgreSQL instance.
-- The default database ims_user is created via POSTGRES_DB env var.

CREATE DATABASE ims_warehouse;
CREATE DATABASE ims_market;
CREATE DATABASE ims_transfer;
CREATE DATABASE ims_scheduling;
CREATE DATABASE ims_transaction;

GRANT ALL PRIVILEGES ON DATABASE ims_user        TO ims;
GRANT ALL PRIVILEGES ON DATABASE ims_warehouse   TO ims;
GRANT ALL PRIVILEGES ON DATABASE ims_market      TO ims;
GRANT ALL PRIVILEGES ON DATABASE ims_transfer    TO ims;
GRANT ALL PRIVILEGES ON DATABASE ims_scheduling  TO ims;
GRANT ALL PRIVILEGES ON DATABASE ims_transaction TO ims;
