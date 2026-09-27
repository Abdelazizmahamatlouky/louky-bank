-- =========================================================
-- LOUKY BANK - CreditFlow
-- Donnees initiales (agences + utilisateurs de demonstration)
-- Devise : FCFA (XAF)
-- =========================================================

-- ---------------------------------------------------------
-- 1. AGENCES
-- ---------------------------------------------------------
INSERT INTO agencies (code, name, city, address, phone, active, created_at, updated_at, version)
VALUES
    ('AG-ABH-001', 'Agence Abeche Centre', 'Abeche', 'Gouvernorat', '+235233400001', TRUE, NOW(), NOW(), 0),
    ('AG-NDJ-001', 'Agence NDjamena Centre', 'NDjamena', 'Avenue Joseph Brahim Seid', '+235222300001', TRUE, NOW(), NOW(), 0),
    ('AG-MND-001', 'Agence Moundou Centre', 'Moundou', 'Avenue du 15 Octobre', '+235233500001', TRUE, NOW(), NOW(), 0),
    ('AG-SAR-001', 'Agence Sarh Centre', 'Sarh', 'Avenue Charles de Gaulle', '+235233600001', TRUE, NOW(), NOW(), 0);

-- ---------------------------------------------------------
-- 2. UTILISATEURS DE DEMONSTRATION
-- ---------------------------------------------------------
-- Mot de passe commun (hash BCrypt) : LoukyBank@2025
-- Hash : $2a$12$rc5Po.qywUlBo/N5kWonAOW.5uevrZqyzKuhvIBSs4CbPH.edNJ6W
--
-- ATTENTION : a remplacer en production par des comptes
-- individuels crees via l'API /api/v1/users (role DIRECTEUR).
-- ---------------------------------------------------------

-- Directeur general (acces global, sans agence dediee)
INSERT INTO users (email, password, first_name, last_name, role, active, agency_id, created_at, updated_at, version)
VALUES (
    'directeur@loukybank.com',
    '$2a$12$rc5Po.qywUlBo/N5kWonAOW.5uevrZqyzKuhvIBSs4CbPH.edNJ6W',
    'Mahamat', 'Idriss',
    'DIRECTEUR', TRUE,
    (SELECT id FROM agencies WHERE code = 'AG-NDJ-001'),
    NOW(), NOW(), 0
);

-- Directeur d'agence Abeche
INSERT INTO users (email, password, first_name, last_name, role, active, agency_id, created_at, updated_at, version)
VALUES (
    'directeur.abeche@loukybank.com',
    '$2a$12$rc5Po.qywUlBo/N5kWonAOW.5uevrZqyzKuhvIBSs4CbPH.edNJ6W',
    'Abakar', 'Hassan',
    'DIRECTEUR', TRUE,
    (SELECT id FROM agencies WHERE code = 'AG-ABC-001'),
    NOW(), NOW(), 0
);

-- Agent d'agence NDjamena
INSERT INTO users (email, password, first_name, last_name, role, active, agency_id, created_at, updated_at, version)
VALUES (
    'agent.ndjamena@loukybank.com',
    '$2a$12$rc5Po.qywUlBo/N5kWonAOW.5uevrZqyzKuhvIBSs4CbPH.edNJ6W',
    'Fatime', 'Zara',
    'AGENT', TRUE,
    (SELECT id FROM agencies WHERE code = 'AG-NDJ-001'),
    NOW(), NOW(), 0
);

-- Agent d'agence Abeche
INSERT INTO users (email, password, first_name, last_name, role, active, agency_id, created_at, updated_at, version)
VALUES (
    'agent.abeche@loukybank.com',
    '$2a$12$rc5Po.qywUlBo/N5kWonAOW.5uevrZqyzKuhvIBSs4CbPH.edNJ6W',
    'Mariam', 'Brahim',
    'AGENT', TRUE,
    (SELECT id FROM agencies WHERE code = 'AG-ABC-001'),
    NOW(), NOW(), 0
);

-- Agent d'agence Moundou
INSERT INTO users (email, password, first_name, last_name, role, active, agency_id, created_at, updated_at, version)
VALUES (
    'agent.moundou@loukybank.com',
    '$2a$12$rc5Po.qywUlBo/N5kWonAOW.5uevrZqyzKuhvIBSs4CbPH.edNJ6W',
    'Ngarta', 'Tolingar',
    'AGENT', TRUE,
    (SELECT id FROM agencies WHERE code = 'AG-MND-001'),
    NOW(), NOW(), 0
);