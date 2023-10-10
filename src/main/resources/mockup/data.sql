--insert into pc_user table
INSERT INTO `pc_local`.`pc_user` (`user_id`, `created_by`, `created_on`, `modified_by`, `modified_on`, `email`, `enabled`, `password`, `role`, `username`, `first_name`, `last_name`)VALUES (1, 'PC_BACKEND' ,NOW() ,'PC_BACKEND' ,NOW(), 'miroslav.milinkov@persida-bio.com', 1, '$2a$10$kCIA24uD1XdQd4/T.E6dsOJK9usnpHnNCv//ZW7WNkkuzaoNTU2Qq','USER', 'pctest', 'pc', 'test');
INSERT INTO `pc_local`.`pc_user` (`user_id`, `created_by`, `created_on`, `modified_by`, `modified_on`, `email`, `enabled`, `password`, `role`, `username`, `first_name`, `last_name`)VALUES (2, 'PC_BACKEND' ,NOW() ,'PC_BACKEND' ,NOW(), 'bosko.jeftic@persida-bio.com', 1, '$2a$10$kCIA24uD1XdQd4/T.E6dsOJK9usnpHnNCv//ZW7WNkkuzaoNTU2Qq','USER', 'pcbosko', 'bosko', 'jeftic');

--insert into final_call table
INSERT INTO `pc_local`.`final_call` (`term`) VALUE ('INSUFFICIENT'),('LIKELY_BENIGN'),('BENIGN'),('UNCERTAIN'),('LIKELY_PATHOGENIC'),('PATHOGENIC'),('CONFLICTING');

--insert into inheritance table
INSERT INTO `pc_local`.`inheritance` (`term`) VALUE ('AUTOSOMAL_DOMINANT'),('AUTOSOMAL_RECESSIVE'),('X_LINKED_DOMINANT'),('X_LINKED_RECESSIVE'),('MITOCHONDRIAL'),('MULTIFACTORIAL'),('OTHER'),('UNKNOWN');
