CREATE TABLE IF NOT EXISTS "django_migrations"(
  "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  "app" varchar(255) NOT NULL,
  "name" varchar(255) NOT NULL,
  "applied" datetime NOT NULL
);
CREATE TABLE IF NOT EXISTS "django_admin_log"(
  "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  "action_time" datetime NOT NULL,
  "object_id" text NULL,
  "object_repr" varchar(200) NOT NULL,
  "change_message" text NOT NULL,
  "content_type_id" integer NULL REFERENCES "django_content_type"("id") DEFERRABLE INITIALLY DEFERRED,
  "user_id" integer NOT NULL REFERENCES "auth_user"("id") DEFERRABLE INITIALLY DEFERRED,
  "action_flag" smallint unsigned NOT NULL CHECK("action_flag" >= 0)
);
CREATE INDEX "django_admin_log_content_type_id_c4bce8eb" ON "django_admin_log"(
  "content_type_id"
);
CREATE INDEX "django_admin_log_user_id_c564eba6" ON "django_admin_log"(
  "user_id"
);
CREATE TABLE IF NOT EXISTS "django_content_type"(
  "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  "app_label" varchar(100) NOT NULL,
  "model" varchar(100) NOT NULL
);
CREATE UNIQUE INDEX "django_content_type_app_label_model_76bd3d3b_uniq" ON "django_content_type"(
  "app_label",
  "model"
);
CREATE TABLE IF NOT EXISTS "auth_permission"(
  "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  "content_type_id" integer NOT NULL REFERENCES "django_content_type"("id") DEFERRABLE INITIALLY DEFERRED,
  "codename" varchar(100) NOT NULL,
  "name" varchar(255) NOT NULL
);
CREATE UNIQUE INDEX "auth_permission_content_type_id_codename_01ab375a_uniq" ON "auth_permission"(
  "content_type_id",
  "codename"
);
CREATE INDEX "auth_permission_content_type_id_2f476e4b" ON "auth_permission"(
  "content_type_id"
);
CREATE TABLE IF NOT EXISTS "store_category"(
  "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  "slug" varchar(100) NOT NULL UNIQUE,
  "name" varchar(50) NOT NULL
);
CREATE TABLE IF NOT EXISTS "orders_order"(
  "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  "address" varchar(150) NOT NULL,
  "pin_code" varchar(10) NOT NULL,
  "city" varchar(50) NOT NULL,
  "paid" bool NOT NULL,
  "created" datetime NOT NULL,
  "updated" datetime NOT NULL,
  "user_id" integer NOT NULL REFERENCES "auth_user"("id") DEFERRABLE INITIALLY DEFERRED,
  "total_price" real NOT NULL,
  "status" varchar(10) NOT NULL
);
CREATE INDEX "orders_order_user_id_e9b59eb1" ON "orders_order"("user_id");
CREATE TABLE IF NOT EXISTS "orders_orderitem"(
  "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  "quantity" integer unsigned NOT NULL CHECK("quantity" >= 0),
  "order_id" integer NOT NULL REFERENCES "orders_order"("id") DEFERRABLE INITIALLY DEFERRED,
  "product_id" integer NOT NULL REFERENCES "store_product"("id") DEFERRABLE INITIALLY DEFERRED,
  "total" real NOT NULL
);
CREATE INDEX "orders_orderitem_order_id_fe61a34d" ON "orders_orderitem"(
  "order_id"
);
CREATE INDEX "orders_orderitem_product_id_afe4254a" ON "orders_orderitem"(
  "product_id"
);
CREATE TABLE IF NOT EXISTS "django_session"(
  "session_key" varchar(40) NOT NULL PRIMARY KEY,
  "session_data" text NOT NULL,
  "expire_date" datetime NOT NULL
);
CREATE INDEX "django_session_expire_date_a5c62663" ON "django_session"(
  "expire_date"
);
CREATE TABLE IF NOT EXISTS "store_product"(
  "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  "name" varchar(100) NOT NULL,
  "slug" varchar(50) NOT NULL,
  "description" text NOT NULL,
  "created" datetime NOT NULL,
  "updated" datetime NOT NULL,
  "image" varchar(100) NOT NULL,
  "availibility" bool NOT NULL,
  "category_id" integer NOT NULL REFERENCES "store_category"("id") DEFERRABLE INITIALLY DEFERRED,
  "price" integer NOT NULL
);
CREATE INDEX "store_product_slug_6de8ee4b" ON "store_product"("slug");
CREATE INDEX "store_product_category_id_574bae65" ON "store_product"(
  "category_id"
);
CREATE INDEX "store_product_id_slug_acb6888a_idx" ON "store_product"(
  "id",
  "slug"
);
CREATE TABLE IF NOT EXISTS "auth_user"(
  "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  "password" varchar(128) NOT NULL,
  "last_login" datetime NULL,
  "is_superuser" bool NOT NULL,
  "username" varchar(150) NOT NULL UNIQUE,
  "last_name" varchar(150) NOT NULL,
  "email" varchar(254) NOT NULL,
  "is_staff" bool NOT NULL,
  "is_active" bool NOT NULL,
  "date_joined" datetime NOT NULL,
  "first_name" varchar(150) NOT NULL
);
