CREATE TABLE IF NOT EXISTS reservation (
  id           SERIAL PRIMARY KEY,
  client_id    BIGINT NOT NULL,
  event_id     BIGINT NOT NULL,
  ticket_count INT    NOT NULL,
  expiry_date  DATE  NOT NULL
);

CREATE TABLE IF NOT EXISTS reservation_counter (
  event_id   BIGINT  PRIMARY KEY,
  max_tickets INT NOT NULL,
  reserved_tickets INT NOT NULL,
  max_tickets_per_client INT NOT NULL
);

create unique index uidx_event_id_client_id on reservation(event_id, client_id);

create TABLE IF NOT EXISTS country_telephone_prefix (
  country    VARCHAR(5) NOT NULL PRIMARY KEY,
  prefix     VARCHAR(5) NOT NULL
);

create TABLE IF NOT EXISTS country_currency (
  country    VARCHAR(5) NOT NULL PRIMARY KEY,
  currency   VARCHAR(5) NOT NULL
);

-- test data
insert into reservation_counter(event_id, max_tickets, reserved_tickets, max_tickets_per_client) VALUES(1000, 500, 0, 5);
insert into reservation_counter(event_id, max_tickets, reserved_tickets, max_tickets_per_client) VALUES(1001, 100, 0, 10);
insert into reservation(id, client_id, event_id, ticket_count, expiry_date) values(10001, 100, 1000, 1, '2020-06-29 23:38:12');
insert into reservation(id, client_id, event_id, ticket_count, expiry_date) values(10002, 101, 1000, 1, '2020-06-29 23:38:12');
insert into reservation(id, client_id, event_id, ticket_count, expiry_date) values(10003, 101, 1001, 1, '2020-06-29 23:38:12');

insert into country_telephone_prefix(country, prefix) values('PL', '48');
insert into country_telephone_prefix(country, prefix) values('NZ', '64');
insert into country_telephone_prefix(country, prefix) values('GB', '44');

insert into country_currency(country, currency) values('PL', 'PLN');
insert into country_currency(country, currency) values('NZ', 'NZD');
insert into country_currency(country, currency) values('GB', 'GBP');
