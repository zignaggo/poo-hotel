DROP TYPE IF EXISTS reservation_status;
DROP TYPE IF EXISTS movement_type;

CREATE TYPE reservation_status AS ENUM (
  'OPENED',
  'IN_PROGRESS',
  'CANCELLED',
  'FINISHED'
);
CREATE TYPE movement_type AS ENUM (
  'CHECK_IN',
  'CHECK_OUT'
);

CREATE TABLE IF NOT EXISTS guests (
  id SERIAL PRIMARY KEY,
  cpf CHAR(14) NOT NULL UNIQUE,
  full_name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  phone CHAR(14) NOT NULL,
  birth_date DATE NOT NULL,
  address VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS rooms (
  id SERIAL PRIMARY KEY,
  number INTEGER NOT NULL UNIQUE,
  capacity INTEGER NOT NULL,
  price_per_night DECIMAL(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS reservations (
  id SERIAL PRIMARY KEY,
  guest_cpf CHAR(14) NOT NULL,
  status reservation_status NOT NULL,
  amount DECIMAL(10,2) DEFAULT 0,
  lunch BOOLEAN DEFAULT FALSE,
  number_of_guests INTEGER DEFAULT 0,
  payment_method VARCHAR(255) NOT NULL,
  check_in_date TIMESTAMP NOT NULL,
  check_out_date TIMESTAMP NOT NULL,
  FOREIGN KEY (guest_cpf) REFERENCES guests (cpf)
);

CREATE TABLE IF NOT EXISTS reservation_room (
  reservation_id INTEGER NOT NULL,
  room_id INTEGER NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  FOREIGN KEY (room_id) REFERENCES rooms (id),
  FOREIGN KEY (reservation_id) REFERENCES reservations (id),
  PRIMARY KEY (reservation_id, room_id)
);

CREATE TABLE IF NOT EXISTS movements (
  id SERIAL PRIMARY KEY,
  reservation_id INTEGER,
  type movement_type NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  date TIMESTAMP NOT NULL,
  FOREIGN KEY (reservation_id) REFERENCES reservations (id)
);

CREATE TABLE IF NOT EXISTS items (
  id SERIAL PRIMARY KEY,
  type VARCHAR(255) NOT NULL,
  available_quantity INTEGER NOT NULL,
  price DECIMAL(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS consumptions (
  id SERIAL PRIMARY KEY,
  reservation_id INTEGER,
  item_id INTEGER,
  date TIMESTAMP NOT NULL,
  FOREIGN KEY (reservation_id) REFERENCES reservations (id),
  FOREIGN KEY (item_id) REFERENCES items (id)
);

CREATE TABLE IF NOT EXISTS consumptions_item (
  consumption_id SERIAL,
  item_id INTEGER,
  date TIMESTAMP NOT NULL,
  FOREIGN KEY (consumption_id) REFERENCES consumptions (id),
  FOREIGN KEY (item_id) REFERENCES items (id),
  PRIMARY KEY(consumption_id, item_id)
);