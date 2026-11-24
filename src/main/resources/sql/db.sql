
IF DB_ID('HotelRoomManagement') IS NULL
BEGIN
    CREATE DATABASE HotelRoomManagement;
END;
GO

USE HotelRoomManagement;
GO

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
GO

CREATE UNIQUE INDEX UX_rooms_active_number
ON dbo.rooms(number)
WHERE deleted_at IS NULL;
GO

CREATE INDEX IX_rooms_cleaning_status
ON dbo.rooms(cleaning_status);
GO

CREATE INDEX IX_rooms_deleted_at
ON dbo.rooms(deleted_at);
GO
