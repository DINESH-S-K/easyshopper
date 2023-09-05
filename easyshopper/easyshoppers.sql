USE easyshopper;

CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    mobile_no VARCHAR(20) NOT NULL,
    password VARCHAR(100) NOT NULL,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by INT NOT NULL,
    updated_by INT NOT NULL,
    role_id INT NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE TABLE product (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    description TEXT,
    unit_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00 ,
    created_by INT NOT NULL,
    updated_by INT NOT NULL,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    quantity_in_stock INT NOT NULL DEFAULT 0,
    FOREIGN KEY (created_by) REFERENCES users (id),
    FOREIGN KEY (updated_by) REFERENCES users (id)
);

CREATE TABLE inventory (
    id INT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255),
    created_by INT NOT NULL,
    updated_by INT NOT NULL,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users (id),
    FOREIGN KEY (updated_by) REFERENCES users (id)
);

CREATE TABLE product_inventory (
    product_id INT AUTO_INCREMENT NOT NULL,
    inventory_id INT NOT NULL,
    PRIMARY KEY (product_id, inventory_id),
    FOREIGN KEY (product_id) REFERENCES product (id),
    FOREIGN KEY (inventory_id) REFERENCES inventory (id)
);

CREATE TABLE cart (
    id INT AUTO_INCREMENT PRIMARY KEY ,
    customer_id INT NOT NULL,
    quantity INT DEFAULT 0,
    total_amount DECIMAL(10, 2) DEFAULT 0.00,
    FOREIGN KEY (customer_id) REFERENCES users(id)
);

CREATE TABLE cart_product (
    cart_id INT AUTO_INCREMENT NOT NULL,
    product_id INT NOT NULL,
    quantity INT DEFAULT 0,
    unit_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    total_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (cart_id, product_id),
    FOREIGN KEY (product_id) REFERENCES product (id),
    FOREIGN KEY (cart_id) REFERENCES cart (id)
);

CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    quantity INT NOT NULL DEFAULT 0,
    purchased_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    order_status VARCHAR(20),
    FOREIGN KEY (customer_id) REFERENCES users(id)
);

CREATE TABLE order_product (
	order_id INT AUTO_INCREMENT NOT NULL,
    product_id INT NOT NULL,
    quantity INT DEFAULT 0,
    unit_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    total_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (order_id, product_id),
    FOREIGN KEY (product_id) REFERENCES product (id),
    FOREIGN KEY (order_id) REFERENCES orders (id)
);

CREATE TABLE customer_wallet (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    balance DECIMAL(10, 2) NOT NULL DEFAULT 0,
    reward_points INT NOT NULL DEFAULT 0,
    pin VARCHAR(4) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES users(id)
);

CREATE TABLE purchase_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    order_id INT NOT NULL,
    purchase_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    amount_debited DECIMAL(10, 2),
    wallet_balance VARCHAR(20),
    FOREIGN KEY (customer_id) REFERENCES users(id),
    FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE TABLE users_secret_key (
    id INT NOT NULL,
    role_id INT NOT NULL,
    secret_key VARCHAR(100) NOT NULL,
	PRIMARY KEY (id, role_id, secret_key),
    FOREIGN KEY (id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE TABLE aduitlog (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    username VARCHAR(100) NOT NULL,
    date_and_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    role VARCHAR(50) NOT NULL,
    action VARCHAR(150)
);


INSERT INTO `roles` VALUES (1,'ADMIN'),(2,'MANAGER'),(3,'CUSTOMER');


