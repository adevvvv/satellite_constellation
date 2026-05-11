-- Удаляем таблицы если существуют
DROP TABLE IF EXISTS satellites CASCADE;
DROP TABLE IF EXISTS satellite_constellations CASCADE;

-- Создание таблицы группировок спутников
CREATE TABLE satellite_constellations (
    id BIGSERIAL PRIMARY KEY,
    constellation_name VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_constellation_name ON satellite_constellations(constellation_name);

-- Создание таблицы спутников
CREATE TABLE satellites (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    satellite_type VARCHAR(50) NOT NULL,
    battery_level DOUBLE PRECISION NOT NULL DEFAULT 1.0,
    is_active BOOLEAN DEFAULT FALSE,
    status_message VARCHAR(255) DEFAULT 'Не активирован',
    internal_temperature DOUBLE PRECISION DEFAULT 25.0,
    external_temperature DOUBLE PRECISION DEFAULT -50.0,
    bandwidth DOUBLE PRECISION,
    resolution DOUBLE PRECISION,
    photos_taken INTEGER DEFAULT 0,
    constellation_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_constellation FOREIGN KEY (constellation_id)
        REFERENCES satellite_constellations(id) ON DELETE CASCADE,
    CONSTRAINT chk_satellite_type CHECK (satellite_type IN ('COMMUNICATION', 'IMAGING'))
);

CREATE INDEX idx_satellite_name ON satellites(name);
CREATE INDEX idx_satellite_type ON satellites(satellite_type);
CREATE INDEX idx_satellite_constellation ON satellites(constellation_id);
CREATE INDEX idx_satellite_internal_temp ON satellites(internal_temperature);