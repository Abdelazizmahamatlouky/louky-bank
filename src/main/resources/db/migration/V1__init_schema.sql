CREATE TABLE agencies (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL,
    name VARCHAR(120) NOT NULL,
    city VARCHAR(80) NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(30),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_agency_code UNIQUE (code)
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(150) NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    role VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    agency_id BIGINT,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_user_email UNIQUE (email),
    CONSTRAINT fk_user_agency FOREIGN KEY (agency_id) REFERENCES agencies (id)
);

CREATE TABLE credit_requests (
    id BIGSERIAL PRIMARY KEY,
    reference VARCHAR(30) NOT NULL,
    applicant_first_name VARCHAR(80) NOT NULL,
    applicant_last_name VARCHAR(80) NOT NULL,
    applicant_email VARCHAR(150),
    applicant_phone VARCHAR(30),
    amount NUMERIC(19,2) NOT NULL,
    duration_months INTEGER NOT NULL,
    monthly_income NUMERIC(19,2) NOT NULL,
    monthly_charges NUMERIC(19,2) NOT NULL,
    credit_type VARCHAR(30) NOT NULL,
    purpose VARCHAR(500) NOT NULL,
    status VARCHAR(20) NOT NULL,
    agency_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_credit_reference UNIQUE (reference),
    CONSTRAINT fk_credit_agency FOREIGN KEY (agency_id) REFERENCES agencies (id),
    CONSTRAINT fk_credit_creator FOREIGN KEY (created_by) REFERENCES users (id)
);

CREATE INDEX idx_credit_status ON credit_requests (status);
CREATE INDEX idx_credit_agency ON credit_requests (agency_id);
CREATE INDEX idx_credit_created_at ON credit_requests (created_at);

CREATE TABLE credit_assessments (
    id BIGSERIAL PRIMARY KEY,
    credit_request_id BIGINT NOT NULL,
    debt_ratio NUMERIC(7,2) NOT NULL,
    monthly_payment NUMERIC(19,2) NOT NULL,
    total_cost NUMERIC(19,2) NOT NULL,
    risk_score INTEGER NOT NULL,
    risk_category VARCHAR(20) NOT NULL,
    applied_rate NUMERIC(5,2) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_assessment_request UNIQUE (credit_request_id),
    CONSTRAINT fk_assessment_request FOREIGN KEY (credit_request_id) REFERENCES credit_requests (id)
);

CREATE TABLE credit_decisions (
    id BIGSERIAL PRIMARY KEY,
    credit_request_id BIGINT NOT NULL,
    decision_maker_id BIGINT NOT NULL,
    decision VARCHAR(20) NOT NULL,
    comment VARCHAR(1000),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_decision_request FOREIGN KEY (credit_request_id) REFERENCES credit_requests (id),
    CONSTRAINT fk_decision_maker FOREIGN KEY (decision_maker_id) REFERENCES users (id)
);

CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    type VARCHAR(30) NOT NULL,
    read BOOLEAN NOT NULL DEFAULT FALSE,
    reference_id BIGINT,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_notification_user ON notifications (user_id);
CREATE INDEX idx_notification_read ON notifications (read);

CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    user_email VARCHAR(150),
    action VARCHAR(80) NOT NULL,
    entity_type VARCHAR(80),
    entity_id BIGINT,
    details VARCHAR(2000),
    ip_address VARCHAR(45),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_audit_user ON audit_logs (user_id);
CREATE INDEX idx_audit_action ON audit_logs (action);

CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(500) NOT NULL,
    user_id BIGINT NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_refresh_token UNIQUE (token),
    CONSTRAINT fk_refresh_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_refresh_user ON refresh_tokens (user_id);