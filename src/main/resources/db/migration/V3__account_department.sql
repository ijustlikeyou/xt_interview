ALTER TABLE `sys_account`
    ADD COLUMN `department_id` BIGINT NULL,
    ADD INDEX `idx_account_department_id` (`department_id`),
    ADD CONSTRAINT `fk_account_department`
        FOREIGN KEY (`department_id`) REFERENCES `sys_department` (`id`);
