-- =========================================================
-- UVICAR - Diagnóstico de errores tipo:
--   "Invalid column name 'X'" al INSERT/UPDATE
-- Usualmente proviene de TRIGGERS, VISTAS o SPs desalineados.
-- =========================================================

-- 1) Columnas reales de tTransmisiones
SELECT
  c.ORDINAL_POSITION,
  c.COLUMN_NAME,
  c.DATA_TYPE,
  c.CHARACTER_MAXIMUM_LENGTH,
  c.NUMERIC_PRECISION,
  c.NUMERIC_SCALE,
  c.IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS c
WHERE c.TABLE_NAME = 'tTransmisiones'
ORDER BY c.ORDINAL_POSITION;

-- 2) Triggers asociados a tTransmisiones
SELECT
  tr.name AS trigger_name,
  tr.is_disabled,
  OBJECT_SCHEMA_NAME(tr.parent_id) AS table_schema,
  OBJECT_NAME(tr.parent_id) AS table_name
FROM sys.triggers tr
WHERE tr.parent_id = OBJECT_ID('tTransmisiones');

-- 3) Buscar referencia a una columna problemática dentro de triggers/vistas/SPs
-- Reemplaza 'cFlagQuellaveco' por la columna que te salga en el error.
DECLARE @needle NVARCHAR(200) = N'cFlagQuellaveco';

SELECT
  o.type_desc,
  OBJECT_SCHEMA_NAME(m.object_id) AS schema_name,
  o.name AS object_name
FROM sys.sql_modules m
JOIN sys.objects o ON o.object_id = m.object_id
WHERE m.definition LIKE '%' + @needle + '%'
ORDER BY o.type_desc, schema_name, object_name;

-- 4) Ver el texto completo del trigger (si lo encontraste en el punto 2)
-- Ejemplo:
-- EXEC sp_helptext 'NombreDelTrigger';
