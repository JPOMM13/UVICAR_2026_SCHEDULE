-- ShedLock table (SQL Server) - compatible con ejecución desde Spring Boot
IF OBJECT_ID('dbo.shedlock', 'U') IS NULL
BEGIN
EXEC('
        CREATE TABLE dbo.shedlock (
            name       VARCHAR(64)   NOT NULL PRIMARY KEY,
            lock_until DATETIME2(3)  NOT NULL,
            locked_at  DATETIME2(3)  NOT NULL,
            locked_by  VARCHAR(255)  NOT NULL
        )
    ');
END
