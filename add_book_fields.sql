-- Add 4 new fields to the books table
-- pages: required Integer (minimum 1)
-- dewey_code: required String, max 20 chars (e.g. '001')
-- municipal_ref: optional String, max 20 chars (Negombo Municipal Council Library ref)
-- library_ref: optional String, max 20 chars (Talahena Public Library ref)

ALTER TABLE books
  ADD COLUMN pages INT NOT NULL DEFAULT 1,
  ADD COLUMN dewey_code VARCHAR(20) NOT NULL DEFAULT '000',
  ADD COLUMN municipal_ref VARCHAR(20) DEFAULT NULL,
  ADD COLUMN library_ref VARCHAR(20) DEFAULT NULL;
