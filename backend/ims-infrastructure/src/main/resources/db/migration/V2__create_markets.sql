CREATE TABLE markets (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    place VARCHAR(255),
    open_date DATE,
    close_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    created_at TIMESTAMP NOT NULL
);
