ALTER TABLE `patient` ADD COLUMN `blood_group` VARCHAR(15) NULL;
UPDATE `patient` SET `blood_group`='UNKNOWN' WHERE `blood_group` IS NULL; 