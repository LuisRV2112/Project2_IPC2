-- Tabla de Roles
CREATE TABLE roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE -- admin, marketing, servicio, empleado, cliente
);

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

SELECT id,email,password,dpi,phone,address,role_id FROM users;

-- Tabla de perfiles de clientes
CREATE TABLE client_profiles (
    user_id INT PRIMARY KEY,
    photo_url VARCHAR(255),
    description TEXT,
    hobbies TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
)

-- Eliminar tabla antigua de horarios de empleados (si existe)
DROP TABLE IF EXISTS employee_schedules;

-- Crear tabla de horario general
CREATE TABLE salon_hours (
    id INT PRIMARY KEY AUTO_INCREMENT,
    day_of_week INT NOT NULL UNIQUE, -- 1 (Lunes) a 7 (Domingo)
    opening_time TIME NOT NULL,
    closing_time TIME NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

-- Insertar días base
INSERT INTO salon_hours (day_of_week, opening_time, closing_time) VALUES
(1, '08:00:00', '18:00:00'),
(2, '08:00:00', '18:00:00'),
(3, '08:00:00', '18:00:00'),
(4, '08:00:00', '18:00:00'),
(5, '08:00:00', '18:00:00'),
(6, '09:00:00', '14:00:00'),
(7, '09:00:00', '12:00:00');

-- Tabla de Facturas
CREATE TABLE invoices (
    id INT PRIMARY KEY AUTO_INCREMENT,
    appointment_id INT NOT NULL UNIQUE,
    total DECIMAL(10,2) NOT NULL,
    generated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    payment_status ENUM('pendiente', 'pagado') DEFAULT 'pendiente',
    FOREIGN KEY (appointment_id) REFERENCES appointments(id)
);

-- Actualizar tabla de citas
ALTER TABLE appointments
ADD COLUMN no_show BOOLEAN DEFAULT FALSE,
ADD COLUMN invoice_id INT,
ADD CONSTRAINT fk_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id);

-- Tabla de Lista Negra
CREATE TABLE blacklist (
    client_id INT PRIMARY KEY,
    reason TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES users(id)
);

insert into services (name, description, image_url, duration_min, price) values
('Corte de Cabello', 'Corte de cabello para hombres y mujeres.', 'https://example.com/corte.jpg', 30, 150.00),
('Manicura', 'Servicio de manicura con esmalte.', 'https://example.com/manicura.jpg', 45, 200.00),
('Pedicura', 'Servicio de pedicura con esmalte.', 'https://example.com/pedicura.jpg', 45, 200.00),
('Masaje Relajante', 'Masaje relajante de cuerpo completo.', 'https://example.com//masaje.jpg', 60, 300.00);

---trabajadores
insert into users (email, password, dpi, phone, address, role_id) values
('t@test.com', '$2a$10$dVXf63kVZPO0N7urHY99Z.qTb2wNVcW6GI5yrb6OXnNT0pJi39xY2', '1234567890123', '1234567890', 'Calle 1, Ciudad', 4),
('t2@test.com', '$2a$10$dVXf63kVZPO0N7urHY99Z.qTb2wNVcW6GI5yrb6OXnNT0pJi39xY2', '1234567890123', '1234567890', 'Calle 2, Ciudad', 4),
('t3@test.com', '$2a$10$dVXf63kVZPO0N7urHY99Z.qTb2wNVcW6GI5yrb6OXnNT0pJi39xY2', '1234567890123', '1234567890', 'Calle 3, Ciudad', 4),
('t4@test.com', '$2a$10$dVXf63kVZPO0N7urHY99Z.qTb2wNVcW6GI5yrb6OXnNT0pJi39xY2', '1234567890123', '1234567890', 'Calle 4, Ciudad', 4);

-- Servicios de los trabajadores
insert into employee_services (employee_id, service_id) values
(4, 1),
(4, 2),
(5, 1),
(6, 1),
(7, 1),
(7, 2);


-- Usuario administrador
insert into users (email, password, dpi, phone, address, role_id) values
('admin@gmail.com', '$2a$10$dVXf63kVZPO0N7urHY99Z.qTb2wNVcW6GI5yrb6OXnNT0pJi39xY2', '1234567890123', '1234567890', 'Calle 1, Ciudad', 1);

alter table users add active boolean default true;