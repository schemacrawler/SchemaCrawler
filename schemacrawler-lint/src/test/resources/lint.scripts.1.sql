CREATE TABLE IF NOT EXISTS products (
  id TEXT NOT NULL,
  name TEXT NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS aliases (
  id TEXT NOT NULL,
  product_id TEXT NOT NULL,
  PRIMARY KEY (id, product_id),
  FOREIGN KEY (product_id) REFERENCES products (id)
);
    
CREATE TABLE IF NOT EXISTS product_identifiers (
  product_id TEXT NOT NULL,
  identifier_type TEXT NOT NULL,
  identifier_value TEXT NOT NULL,
  PRIMARY KEY (product_id, identifier_type, identifier_value),
  FOREIGN KEY (product_id) REFERENCES products (id)
);

CREATE TABLE IF NOT EXISTS tags (
  id TEXT,
  uri TEXT UNIQUE NOT NULL,
  www TEXT NOT NULL,
  PRIMARY KEY (id)
);
