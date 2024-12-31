-- Create the database
CREATE DATABASE Policy_Management;
USE Policy_Management;

-- Table for Users (for login and authentication)
CREATE TABLE Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    email_id VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('Customer', 'Admin') NOT NULL
);

-- Table for Customers (contains personal details of customers)
CREATE TABLE Customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    address TEXT,
    status ENUM('Active', 'Inactive') DEFAULT 'Active',
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- Table for Admins (contains personal details of admins)
CREATE TABLE Admins (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    admin_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    role ENUM('Manager', 'Claims Handler') NOT NULL,
    email_id VARCHAR(255) NOT NULL UNIQUE, -- Added email_id for uniqueness
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- Table for Schemes (details about the insurance schemes)
CREATE TABLE Schemes (
    scheme_id INT AUTO_INCREMENT PRIMARY KEY,
    scheme_name VARCHAR(255) NOT NULL,
    scheme_description TEXT,
    eligibility_criteria TEXT,
    scheme_benefits TEXT,
    terms_and_conditions TEXT,
    status ENUM('Active', 'Inactive') DEFAULT 'Active'
);

-- Table for Policies (specific policies linked to schemes and customers)
CREATE TABLE Policies (
    policy_id INT AUTO_INCREMENT PRIMARY KEY,
    start_date DATE NOT NULL,
    total_premium_amount DECIMAL(10, 2) NOT NULL,
    maturity_amount DECIMAL(10, 2) NOT NULL,
    number_of_years INT NOT NULL,
    policy_status ENUM('Active', 'Inactive', 'Closed') DEFAULT 'Active',
    annuity_term ENUM('Quarterly', 'Half-Yearly', 'Annual', 'One-Time') NOT NULL,
    customer_id INT NOT NULL,
    scheme_id INT NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id),
    FOREIGN KEY (scheme_id) REFERENCES Schemes(scheme_id)
);

-- Table for Claims (claims linked to policies)
CREATE TABLE Claims (
    claim_id INT AUTO_INCREMENT PRIMARY KEY,
    date_of_claim DATE NOT NULL,
    claim_description TEXT,
    claim_amount DECIMAL(10, 2) NOT NULL,
    claim_status ENUM('Pending', 'Paid', 'Rejected') DEFAULT 'Pending',
    policy_id INT NOT NULL,
    FOREIGN KEY (policy_id) REFERENCES Policies(policy_id)
);

-- Table for Feedback (feedback related to schemes, provided by customers)
CREATE TABLE Feedbacks (
    feedback_id INT AUTO_INCREMENT PRIMARY KEY,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comments TEXT,
    feedback_status ENUM('Reviewed', 'In Progress', 'Resolved') DEFAULT 'In Progress',
    customer_id INT NOT NULL,
    scheme_id INT NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id),
    FOREIGN KEY (scheme_id) REFERENCES Schemes(scheme_id)
);

-- Table for Payments (payments related to policies)
CREATE TABLE Payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    payment_date DATE NOT NULL,
    payment_amount DECIMAL(10, 2) NOT NULL,
    payment_method ENUM('Card', 'Bank Transfer', 'UPI') NOT NULL,
    payment_status ENUM('Paid', 'Unpaid') DEFAULT 'Unpaid',
    policy_id INT NOT NULL,
    FOREIGN KEY (policy_id) REFERENCES Policies(policy_id)
);
