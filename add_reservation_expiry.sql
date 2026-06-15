-- =============================================================
-- Reservation Expiry Migration
-- Adds expiry_date column and back-fills existing reservations
-- 
-- Note: If spring.jpa.hibernate.ddl-auto=update is active,
-- Hibernate will auto-create the column. Run this script
-- ONLY if you want to back-fill expiry dates for existing rows.
-- =============================================================

-- Add the column if it doesn't already exist
ALTER TABLE reservations
    ADD COLUMN IF NOT EXISTS expiry_date TIMESTAMP NULL;

-- Back-fill: set expiry_date = reservation_date + 3 days for rows missing it
UPDATE reservations
SET expiry_date = DATE_ADD(reservation_date, INTERVAL 3 DAY)
WHERE expiry_date IS NULL AND reservation_date IS NOT NULL;

-- Add index for efficient expiry queries
CREATE INDEX IF NOT EXISTS idx_reservation_expiry ON reservations(expiry_date);
