-- Tabla de Roles
CREATE TABLE roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE -- admin, marketing, servicio, empleado, cliente
);

insert into roles (name) values ('admin'), ('marketing'), ('servicio'), ('empleado'), ('cliente');

-- Tabla de Usuarios (clientes, empleados, administradores, etc.)
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- Almacenar hash
    dpi VARCHAR(20),
    phone VARCHAR(15),
    address VARCHAR(255),
    role_id INT NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Tabla de Servicios
CREATE TABLE services (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    image_url VARCHAR(255),
    duration_min INT NOT NULL, -- Duración en minutos
    price DECIMAL(10, 2) NOT NULL
);

-- Tabla de Relación Empleados-Servicios (qué empleados ofrecen qué servicios)
CREATE TABLE employee_services (
    employee_id INT,
    service_id INT,
    PRIMARY KEY (employee_id, service_id),
    FOREIGN KEY (employee_id) REFERENCES users(id),
    FOREIGN KEY (service_id) REFERENCES services(id)
);

-- Tabla de Horarios de Empleados
CREATE TABLE employee_schedules (
    id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    day_of_week INT NOT NULL, -- 1 (Lunes) a 7 (Domingo)
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES users(id)
);

-- Tabla de Citas
CREATE TABLE appointments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    client_id INT NOT NULL,
    employee_id INT NOT NULL,
    service_id INT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    status ENUM('pendiente', 'confirmado', 'cancelado') DEFAULT 'pendiente',
    FOREIGN KEY (client_id) REFERENCES users(id),
    FOREIGN KEY (employee_id) REFERENCES users(id),
    FOREIGN KEY (service_id) REFERENCES services(id)
);

-- Tabla de Anuncios
CREATE TABLE advertisements (
    id INT PRIMARY KEY AUTO_INCREMENT,
    type ENUM('video', 'imagen', 'texto') NOT NULL,
    content_url VARCHAR(255) NOT NULL, -- Enlace al recurso
    start_date DATE NOT NULL,
    end_date DATE NOT NULL
);

-- Tabla de Catálogos de Servicios (PDF)
CREATE TABLE service_catalogs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    service_id INT NOT NULL UNIQUE,
    pdf_url VARCHAR(255) NOT NULL,
    uploaded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (service_id) REFERENCES services(id)
);

ALTER TABLE services
ADD COLUMN is_active BOOLEAN DEFAULT TRUE;

-- Tabla de Precios de Anuncios (Admin)
CREATE TABLE ad_pricing (
    id INT PRIMARY KEY AUTO_INCREMENT,
    price_per_day DECIMAL(10,2) NOT NULL,
    effective_date DATE NOT NULL DEFAULT (CURRENT_DATE)
);

-- Tabla de Intereses de Usuarios
CREATE TABLE user_interests (
    user_id INT NOT NULL,
    interest VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, interest),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Actualización de la tabla de Anuncios
ALTER TABLE advertisements
ADD COLUMN category VARCHAR(50) NOT NULL,
ADD COLUMN price_per_day DECIMAL(10,2) NOT NULL,
ADD COLUMN total_cost DECIMAL(10,2) NOT NULL,
ADD COLUMN is_active BOOLEAN DEFAULT TRUE,
ADD COLUMN payment_status ENUM('pendiente', 'pagado_parcial', 'pagado_total') DEFAULT 'pendiente';

-- Tabla de Pagos de Anuncios
CREATE TABLE ad_payments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ad_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ad_id) REFERENCES advertisements(id)
);

-- Tabla para registrar impresiones de anuncios
CREATE TABLE ad_impressions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ad_id INT NOT NULL,
    shown_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    page_url VARCHAR(255) NOT NULL,
    FOREIGN KEY (ad_id) REFERENCES advertisements(id)
);

-- Actualizar tabla de anuncios para registrar compras
ALTER TABLE advertisements
ADD COLUMN purchase_count INT DEFAULT 0;

CREATE INDEX idx_impressions_ad ON ad_impressions(ad_id);
CREATE INDEX idx_impressions_time ON ad_impressions(shown_at);