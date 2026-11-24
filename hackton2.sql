
/* ============================================
   CASA NOBLE - HOTEL ROOM MANAGEMENT
   CREACION DE BASE DE DATOS, TABLA E INDICES
   ============================================ */

-- 1. CREAR BASE DE DATOS
IF DB_ID('HotelRoomManagement') IS NULL
BEGIN
    CREATE DATABASE HotelRoomManagement;
END;
GO

-- 2. SELECCIONAR BASE DE DATOS
USE HotelRoomManagement;
GO

-- 3. CREAR TABLA DE HABITACIONES
IF OBJECT_ID('dbo.rooms', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.rooms (
        room_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        number VARCHAR(20) NOT NULL,
        type VARCHAR(30) NOT NULL,
        price_per_night DECIMAL(10,2) NOT NULL,
        capacity INT NOT NULL,
        cleaning_status VARCHAR(30) NOT NULL,

        created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        updated_at DATETIME2 NULL,
        deleted_at DATETIME2 NULL,
        restored_at DATETIME2 NULL,

        CONSTRAINT CK_rooms_type
            CHECK (type IN ('Simple', 'Double', 'Suite')),

        CONSTRAINT CK_rooms_cleaning_status
            CHECK (cleaning_status IN ('Clean', 'Occupied', 'Maintenance')),

        CONSTRAINT CK_rooms_price
            CHECK (price_per_night > 0),

        CONSTRAINT CK_rooms_capacity
            CHECK (capacity > 0)
    );
END;
GO

-- 4. INDICE UNICO PARA NUMEROS DE HABITACION ACTIVOS
IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'UX_rooms_active_number'
      AND object_id = OBJECT_ID('dbo.rooms')
)
BEGIN
    CREATE UNIQUE INDEX UX_rooms_active_number
    ON dbo.rooms(number)
    WHERE deleted_at IS NULL;
END;
GO

-- 5. INDICE PARA ESTADO DE LIMPIEZA
IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'IX_rooms_cleaning_status'
      AND object_id = OBJECT_ID('dbo.rooms')
)
BEGIN
    CREATE INDEX IX_rooms_cleaning_status
    ON dbo.rooms(cleaning_status);
END;
GO

-- 6. INDICE PARA HABITACIONES ELIMINADAS
IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'IX_rooms_deleted_at'
      AND object_id = OBJECT_ID('dbo.rooms')
)
BEGIN
    CREATE INDEX IX_rooms_deleted_at
    ON dbo.rooms(deleted_at);
END;
GO

-- 7. VERIFICAR BASE DE DATOS ACTUAL
SELECT DB_NAME() AS database_name;
GO

-- 8. VERIFICAR TABLA CREADA
SELECT *
FROM dbo.rooms;
GO

-- 9. VERIFICAR INDICES CREADOS
SELECT
    name AS index_name,
    type_desc AS index_type
FROM sys.indexes
WHERE object_id = OBJECT_ID('dbo.rooms')
  AND name IS NOT NULL;
GO

SELECT
    DB_NAME() AS database_name,
    OBJECT_ID('dbo.rooms') AS rooms_object_id;

-- 10. AGREGAR HABITACIONES

USE HotelRoomManagement;
GO

INSERT INTO dbo.rooms
(number, type, price_per_night, capacity, cleaning_status)
VALUES
('101', 'Simple', 120.00, 1, 'Clean'),
('102', 'Double', 180.00, 2, 'Occupied'),
('103', 'Suite', 300.00, 4, 'Clean'),
('104', 'Simple', 120.00, 1, 'Maintenance'),
('105', 'Double', 180.00, 2, 'Clean');
GO

-- 11. VERIFICAR TODAS LAS HABITACIONES

SELECT * FROM dbo.rooms;
GO

-- 12. BUSCAR LA HABITACION
SELECT *
FROM dbo.rooms
WHERE number = '109';

-- 13. AGREGAR HABITACION

USE HotelRoomManagement;
GO

INSERT INTO dbo.rooms
(
    number,
    type,
    price_per_night,
    capacity,
    cleaning_status
)
VALUES
(
    '109',
    'Suite',
    350.00,
    4,
    'Clean'
);
GO

















