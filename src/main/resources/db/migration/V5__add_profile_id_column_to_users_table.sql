-- Agregar la columna profile_id
ALTER TABLE users
ADD COLUMN profile_id BIGINT;

-- Establecer la clave foránea
ALTER TABLE users
ADD CONSTRAINT fk_profile_id
FOREIGN KEY (profile_id)
REFERENCES profiles(id);

-- Establecer un valor por defecto para profile_id
UPDATE users
SET profile_id = 4
WHERE profile_id IS NULL;

-- Establecer  la columna profile_id no sea NULL
ALTER TABLE users
MODIFY COLUMN profile_id BIGINT NOT NULL DEFAULT 4;