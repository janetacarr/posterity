CREATE TABLE CUSTOMERS (
       customer_id serial primary key,
       ts timestamp default current_timestamp
);

CREATE TYPE product AS ENUM ('jira', 'confluence');

CREATE TABLE installs (
       install_id serial primary key,
       customer_id integer references customers(customer_id),
       key text not null,
       client_key text unique not null,
       account_id text,
       shared_secret text not null,
       base_url text not null,
       display_url text,
       display_url_service_help_center text,
       product_type product not null,
       description text,
       service_entitlement_number text not null,
       oauth_client_id text,
       enabled boolean default true,
       ts timestamp default current_timestamp,
       UNIQUE(base_url, product_type)
);

CREATE INDEX installs_cp_idx ON installs(client_key, product_type);

CREATE TABLE installkeys (
       installkey_id serial primary key,
       install_id integer references installs(install_id),
       client_key text unique not null,
       shared_secret text not null
);
