-- Add uploader fields to bs_books table
USE books_shop;

-- Add uploader ID field
ALTER TABLE bs_books ADD COLUMN bs_uploaderId INT DEFAULT 1 COMMENT 'Uploader ID';

-- Add uploader username field
ALTER TABLE bs_books ADD COLUMN bs_uploaderName VARCHAR(50) DEFAULT 'Admin' COMMENT 'Uploader Username';

-- Set default values for existing data
UPDATE bs_books SET bs_uploaderId = 1, bs_uploaderName = 'Admin' WHERE bs_uploaderId IS NULL; 