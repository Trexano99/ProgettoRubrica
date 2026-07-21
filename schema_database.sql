-- schema_database.sql
-- ---------------------------------------------------------------------------
-- Costruzione del database MySQL usato dal backend "mysql" della Rubrica.
--
-- Da lanciare una sola volta, come parte dell'installazione, sul MySQL su cui
-- girera' l'applicazione. Esempio dalla riga di comando:
--
--     mysql -u root -p < schema_database.sql
--
-- Il nome del database (rubrica) deve coincidere con db.database
-- in credenziali_database.properties.
-- ---------------------------------------------------------------------------

CREATE DATABASE IF NOT EXISTS rubrica
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE rubrica;

-- Contatti della rubrica. L'id e' la chiave surrogata gestita dallo strato di
-- persistenza (AUTO_INCREMENT), non un dato di dominio.
CREATE TABLE IF NOT EXISTS persone (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    nome      VARCHAR(255) NOT NULL,
    cognome   VARCHAR(255) NOT NULL,
    indirizzo VARCHAR(255),
    telefono  VARCHAR(255) NOT NULL,
    eta       INT NOT NULL
);

-- Utenti che accedono al software. La password non e' mai in chiaro:
-- password_hash contiene la forma testuale "salt:digest" (vedi PasswordHash).
CREATE TABLE IF NOT EXISTS utenti (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL
);
