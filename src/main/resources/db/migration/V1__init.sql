CREATE TABLE `cloudinary_upload_result` (
	`public_id` VARCHAR(255) NOT NULL,
	`image_url` VARCHAR(255),
	`format` VARCHAR(255),
	`file_size` INT,
	`height` INT,
	`width` INT,
	`created_at` VARCHAR(255),
	PRIMARY KEY (`public_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `user` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`email` VARCHAR(255),
	`password` VARCHAR(255),
	`role` VARCHAR(20),
	`profile_image_public_id` VARCHAR(255),
	PRIMARY KEY (`id`),
	UNIQUE KEY `uk_user_email` (`email`),
	UNIQUE KEY `uk_user_profile_image_public_id` (`profile_image_public_id`),
	CONSTRAINT `fk_user_profile_image` FOREIGN KEY (`profile_image_public_id`) REFERENCES `cloudinary_upload_result` (`public_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `patient` (
	`id` BIGINT NOT NULL,
	`first_name` VARCHAR(255),
	`last_name` VARCHAR(255),
	`phone_number` VARCHAR(255),
	`address` VARCHAR(255),
	`age` INT NOT NULL,
	`gender` VARCHAR(255),
	`credits` INT NOT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `fk_patient_user` FOREIGN KEY (`id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `doctor` (
	`id` BIGINT NOT NULL,
	`first_name` VARCHAR(255),
	`last_name` VARCHAR(255),
	`medical_license_number` VARCHAR(255),
	`clinic_address` VARCHAR(255),
	`years_of_experience` INT NOT NULL,
	`gender` VARCHAR(255),
	`credits` INT NOT NULL,
	`consultation_fee` INT NOT NULL,
	`city` VARCHAR(255),
	PRIMARY KEY (`id`),
	CONSTRAINT `fk_doctor_user` FOREIGN KEY (`id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `doctor_specializations` (
	`doctor_id` BIGINT NOT NULL,
	`specialization` VARCHAR(255) NOT NULL,
	PRIMARY KEY (`doctor_id`, `specialization`),
	CONSTRAINT `fk_doctor_specializations_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `doctor` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `doctor_languages` (
	`doctor_id` BIGINT NOT NULL,
	`language` VARCHAR(255) NOT NULL,
	PRIMARY KEY (`doctor_id`, `language`),
	CONSTRAINT `fk_doctor_languages_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `doctor` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `appointment_schedules` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`doctor_id` BIGINT NOT NULL,
	`date` DATE,
	`start_time` TIME,
	`end_time` TIME,
	`version` BIGINT,
	`status` VARCHAR(20) DEFAULT 'AVAILABLE',
	`created_at` DATETIME,
	`updated_at` DATETIME,
	`created_by` VARCHAR(255),
	`is_deleted` BOOLEAN DEFAULT FALSE,
	`patient_id` VARCHAR(255),
	`notes` VARCHAR(255),
	PRIMARY KEY (`id`),
	KEY `idx_doctor_date` (`doctor_id`, `date`),
	KEY `idx_status` (`status`),
	KEY `idx_date_time` (`date`, `start_time`),
	KEY `idx_doctor_status` (`doctor_id`, `status`),
	CONSTRAINT `fk_appointment_schedules_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `doctor_availability` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`doctor_id` BIGINT NOT NULL,
	`day` VARCHAR(20),
	`start_time` TIME NOT NULL,
	`end_time` TIME NOT NULL,
	`slot_duration_minutes` INT NOT NULL,
	`max_patients_per_slot` INT NOT NULL,
	`active` BOOLEAN NOT NULL DEFAULT FALSE,
	`version` BIGINT,
	`created_at` DATETIME,
	`updated_at` DATETIME,
	`is_deleted` BOOLEAN DEFAULT FALSE,
	PRIMARY KEY (`id`),
	KEY `idx_doctor_active` (`doctor_id`, `active`),
	KEY `idx_day_active` (`day`, `active`),
	KEY `idx_doctor_day` (`doctor_id`, `day`),
	CONSTRAINT `fk_doctor_availability_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `user` (`id`),
	CONSTRAINT `chk_doctor_availability_slot_duration` CHECK (`slot_duration_minutes` BETWEEN 5 AND 180),
	CONSTRAINT `chk_doctor_availability_max_patients` CHECK (`max_patients_per_slot` BETWEEN 1 AND 10),
	CONSTRAINT `chk_doctor_availability_time_range` CHECK (`end_time` > `start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `appointments` (
	`appointment_id` BIGINT NOT NULL AUTO_INCREMENT,
	`status` VARCHAR(20),
	`consultation_fee` INT NOT NULL,
	`version` BIGINT,
	`patient_id` BIGINT,
	`doctor_id` BIGINT,
	`date` DATE NOT NULL,
	`start_time` TIME NOT NULL,
	`type` VARCHAR(255),
	`notes` VARCHAR(255),
	`schedule_id` BIGINT,
	`created_at` DATETIME,
	`updated_at` DATETIME,
	`is_deleted` BOOLEAN NOT NULL DEFAULT FALSE,
	`meeting_room_id` VARCHAR(255),
	PRIMARY KEY (`appointment_id`),
	UNIQUE KEY `uk_appointments_schedule_id` (`schedule_id`),
	KEY `idx_patient_date` (`patient_id`, `date`),
	KEY `idx_doctor_date` (`doctor_id`, `date`),
	KEY `idx_status` (`status`),
	KEY `idx_date_status` (`date`, `status`),
	CONSTRAINT `fk_appointments_patient` FOREIGN KEY (`patient_id`) REFERENCES `user` (`id`),
	CONSTRAINT `fk_appointments_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `user` (`id`),
	CONSTRAINT `fk_appointments_schedule` FOREIGN KEY (`schedule_id`) REFERENCES `appointment_schedules` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `orders` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`order_id` VARCHAR(255),
	`amount` DOUBLE,
	`status` VARCHAR(255),
	`payment_id` VARCHAR(255),
	`customer_phone` VARCHAR(255),
	`created_at` DATETIME,
	`appointment_id` BIGINT NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY `uk_orders_order_id` (`order_id`),
	CONSTRAINT `fk_orders_appointment` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`appointment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `transaction` (
	`t_id` BIGINT NOT NULL AUTO_INCREMENT,
	`appointment_id` BIGINT NOT NULL,
	`status` VARCHAR(255),
	`amount` DOUBLE NOT NULL,
	`date` DATE,
	`payment_method` VARCHAR(255),
	PRIMARY KEY (`t_id`),
	UNIQUE KEY `uk_transaction_appointment_id` (`appointment_id`),
	CONSTRAINT `fk_transaction_appointment` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`appointment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
