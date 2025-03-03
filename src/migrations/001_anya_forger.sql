DROP TYPE IF EXISTS reservation_status;
DROP TYPE IF EXISTS movement_type;

CREATE TYPE reservation_status AS ENUM (
  'in_progress',
  'cancelled',
  'finished'
);
CREATE TYPE movement_type AS ENUM (
  'check_in',
  'check_out'
);

CREATE TABLE IF NOT EXISTS guests (
  cpf CHAR(11) PRIMARY KEY,
  full_name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  phone CHAR(14) NOT NULL,
  birth_date DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS rooms (
  number CHAR(10) PRIMARY KEY,
  type VARCHAR(255) NOT NULL,
  capacity INTEGER NOT NULL,
  price_per_night DECIMAL(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS reservations (
  id SERIAL PRIMARY KEY,
  guest_cpf CHAR(11) NOT NULL,
  status reservation_status NOT NULL,
  amount DECIMAL(10,2) DEFAULT 0,
  lunch BOOLEAN DEFAULT FALSE,
  number_of_guests INTEGER DEFAULT 0,
  payment_method VARCHAR(255) NOT NULL,
  check_in_date DATE NOT NULL,
  check_out_date DATE NOT NULL,
  FOREIGN KEY (guest_cpf) REFERENCES guests (cpf)
);

CREATE TABLE IF NOT EXISTS reservation_room (
  reservation_id INTEGER,
  room_number VARCHAR(255),
  FOREIGN KEY (room_number) REFERENCES rooms (number),
  FOREIGN KEY (reservation_id) REFERENCES reservations (id),
  PRIMARY KEY (reservation_id, room_number)
);

CREATE TABLE IF NOT EXISTS movements (
  id SERIAL PRIMARY KEY,
  reservation_id INTEGER,
  type movement_type NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  date DATE NOT NULL,
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
  date DATE NOT NULL,
  FOREIGN KEY (reservation_id) REFERENCES reservations (id),
  FOREIGN KEY (item_id) REFERENCES items (id)
);

CREATE TABLE IF NOT EXISTS consumptions_item (
  consumption_id SERIAL,
  item_id INTEGER,
  date DATE NOT NULL,
  FOREIGN KEY (consumption_id) REFERENCES consumptions (id),
  FOREIGN KEY (item_id) REFERENCES items (id),
  PRIMARY KEY(consumption_id, item_id)
);