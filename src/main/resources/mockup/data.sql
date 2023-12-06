--insert into pc_user table
INSERT INTO `pc_local`.`pc_user` (`user_id`, `created_by`, `created_on`, `modified_by`, `modified_on`, `email`, `enabled`, `password`, `role`, `username`, `first_name`, `last_name`)VALUES (1, 'PC_BACKEND' ,NOW() ,'PC_BACKEND' ,NOW(), 'miroslav.milinkov@persida-bio.com', 1, '$2a$10$kCIA24uD1XdQd4/T.E6dsOJK9usnpHnNCv//ZW7WNkkuzaoNTU2Qq','USER', 'pctest', 'pc', 'test');
INSERT INTO `pc_local`.`pc_user` (`user_id`, `created_by`, `created_on`, `modified_by`, `modified_on`, `email`, `enabled`, `password`, `role`, `username`, `first_name`, `last_name`)VALUES (2, 'PC_BACKEND' ,NOW() ,'PC_BACKEND' ,NOW(), 'bosko.jeftic@persida-bio.com', 1, '$2a$10$kCIA24uD1XdQd4/T.E6dsOJK9usnpHnNCv//ZW7WNkkuzaoNTU2Qq','USER', 'pcbosko', 'bosko', 'jeftic');

--insert into final_call table
INSERT INTO `pc_local`.`final_call` (`term`) VALUE ('Uncertain Significance - Insufficient Evidence'),('Likely Benign'),('Benign'),('Uncertain Significance'),('Likely Pathogenic'),('Pathogenic'),('Uncertain Significance - Conflicting Evidence');

--insert into inheritance table
INSERT INTO `pc_local`.`inheritance` (`term`) VALUE ('Autosomal Dominant'),('Autosomal Recessive'),('X-linked Dominant'),('X-linked Recessive'),('Mitochondrial'),('Multifactoral'),('Other'),('Unknown');

--insert into gene table
INSERT INTO `pc_local`.`gene` (`gene_id`) VALUES ('NDUFS8');
INSERT INTO `pc_local`.`gene` (`gene_id`) VALUES ('BRCA2');

--insert into variant table
INSERT INTO `pc_local`.`variant` (`created_by`, `created_on`, `modified_by`, `modified_on`, `caid`, `gene_id`) VALUES ('PC_BACKEND',NOW(),'PC_BACKEND',NOW(),'CA321211','NDUFS8');
INSERT INTO `pc_local`.`variant` (`created_by`, `created_on`, `modified_by`, `modified_on`, `caid`, `gene_id`) VALUES ('PC_BACKEND',NOW(),'PC_BACKEND',NOW(),'CA12345','BRCA2');

--insert into condition table
INSERT INTO `pc_local`.`condition` (`condition_id`,`term`) VALUE ('MONDO:0000769','chicken egg allergy');
INSERT INTO `pc_local`.`condition` (`condition_id`,`term`) VALUE ('MONDO:0000770','shellfish allergy');
INSERT INTO `pc_local`.`condition` (`condition_id`,`term`) VALUE ('MONDO:0000771','allergic respiratory disease');
INSERT INTO `pc_local`.`condition` (`condition_id`,`term`) VALUE ('MONDO:0000772','obsolete pollen allergy');

--insert into scpec_ruleset table
INSERT INTO `pc_local`.`scpec_ruleset` (`engine_id`,`engine_summary`,`organization`,`ruleset_id`,`ruleset_url`) VALUE ('GN001','Standards and guidelines for the interpretation of sequence variants: a joint consensus recommendation of the American College of Medical Genetics and Genomics and the Association for Molecular Pathology','American College of Medical Genetics and Genomics',135641113,'https://cspec.genome.network/cspec/api/RuleSet/id/135641113');

--insert into variant_interpretation table
INSERT INTO `pc_local`.`variant_interpretation` (`created_by`,`created_on`,`modified_by`,`modified_on`,`condition_id`,`finalcall_id`,`inheritance_id`,`user_id`,`variant_id`,`vi_description`,`cspecengine_id`) VALUES ('PC_BACKEND',NOW(),'PC_BACKEND',NOW(),'MONDO:0000769',1,1,1,1,'Description text for variant, To be EDITED!','GN001');
INSERT INTO `pc_local`.`variant_interpretation` (`created_by`,`created_on`,`modified_by`,`modified_on`,`condition_id`,`finalcall_id`,`inheritance_id`,`user_id`,`variant_id`,`vi_description`,`cspecengine_id`) VALUES ('PC_BACKEND',NOW(),'PC_BACKEND',NOW(),'MONDO:0000770',3,3,1,1,null,'GN001');
INSERT INTO `pc_local`.`variant_interpretation` (`created_by`,`created_on`,`modified_by`,`modified_on`,`condition_id`,`finalcall_id`,`inheritance_id`,`user_id`,`variant_id`,`vi_description`,`cspecengine_id`) VALUES ('PC_BACKEND',NOW(),'PC_BACKEND',NOW(),'MONDO:0000771',2,2,1,2,null,'GN001');
INSERT INTO `pc_local`.`variant_interpretation` (`created_by`,`created_on`,`modified_by`,`modified_on`,`condition_id`,`finalcall_id`,`inheritance_id`,`user_id`,`variant_id`,`vi_description`,`cspecengine_id`) VALUES ('PC_BACKEND',NOW(),'PC_BACKEND',NOW(),'MONDO:0000772',4,5,1,2,'Description text for variant, To be EDITED!','GN001');

--insert into evidence_summary table
INSERT INTO `pc_local`.`evidence_summary` (`created_by`,`created_on`,`modified_by`,`modified_on`,`summary`) VALUES ('PC_BACKEND',NOW(),'PC_BACKEND',NOW(),'bp1 evidence description.');
INSERT INTO `pc_local`.`evidence_summary` (`created_by`,`created_on`,`modified_by`,`modified_on`,`summary`) VALUES ('PC_BACKEND',NOW(),'PC_BACKEND',NOW(),'pp1 Moderate evidence description.');
INSERT INTO `pc_local`.`evidence_summary` (`created_by`,`created_on`,`modified_by`,`modified_on`,`summary`) VALUES ('PC_BACKEND',NOW(),'PC_BACKEND',NOW(),'bs2 evidence description.');
INSERT INTO `pc_local`.`evidence_summary` (`created_by`,`created_on`,`modified_by`,`modified_on`,`summary`) VALUES ('PC_BACKEND',NOW(),'PC_BACKEND',NOW(),'bs1 evidence description.');
INSERT INTO `pc_local`.`evidence_summary` (`created_by`,`created_on`,`modified_by`,`modified_on`,`summary`) VALUES ('PC_BACKEND',NOW(),'PC_BACKEND',NOW(),'ps2 Moderate evidence description.');
INSERT INTO `pc_local`.`evidence_summary` (`created_by`,`created_on`,`modified_by`,`modified_on`,`summary`) VALUES ('PC_BACKEND',NOW(),'PC_BACKEND',NOW(),'pm3 Very Strong evidence description.');

--insert into evidence table
INSERT INTO `pc_local`.`evidence` (`created_by`,`created_on`,`modified_by`,`modified_on`,`type`,`value`,`evd_summary_id`,`interpretation_id`) VALUES ('PC_BACKEND',NOW(),'PC_BACKEND',NOW(),'bp1','1',1,1);
INSERT INTO `pc_local`.`evidence` (`created_by`,`created_on`,`modified_by`,`modified_on`,`type`,`value`,`evd_summary_id`,`interpretation_id`) VALUES ('PC_BACKEND',NOW(),'PC_BACKEND',NOW(),'pp1','M',2,1);
INSERT INTO `pc_local`.`evidence` (`created_by`,`created_on`,`modified_by`,`modified_on`,`type`,`value`,`evd_summary_id`,`interpretation_id`) VALUES ('PC_BACKEND',NOW(),'PC_BACKEND',NOW(),'bs2','1',3,2);
INSERT INTO `pc_local`.`evidence` (`created_by`,`created_on`,`modified_by`,`modified_on`,`type`,`value`,`evd_summary_id`,`interpretation_id`) VALUES ('PC_BACKEND',NOW(),'PC_BACKEND',NOW(),'bs1','1',4,3);
INSERT INTO `pc_local`.`evidence` (`created_by`,`created_on`,`modified_by`,`modified_on`,`type`,`value`,`evd_summary_id`,`interpretation_id`) VALUES ('PC_BACKEND',NOW(),'PC_BACKEND',NOW(),'ps2','M',5,3);
INSERT INTO `pc_local`.`evidence` (`created_by`,`created_on`,`modified_by`,`modified_on`,`type`,`value`,`evd_summary_id`,`interpretation_id`) VALUES ('PC_BACKEND',NOW(),'PC_BACKEND',NOW(),'pm3','V',6,4);

--insert into evidence_link table
INSERT INTO `pc_local`.`evidence_link` (`created_by`,`created_on`,`modified_by`,`modified_on`,`link`,`comment`,`evidence_id`) VALUES ('PC_BACKEND',NOW(),'PC_BACKEND',NOW(),'www.bp1_link1.com','comment_bp1_1',1);
INSERT INTO `pc_local`.`evidence_link` (`created_by`,`created_on`,`modified_by`,`modified_on`,`link`,`comment`,`evidence_id`) VALUES ('PC_BACKEND',NOW(),'PC_BACKEND',NOW(),'www.bp1_link2.com','comment_bp1_2',1);
INSERT INTO `pc_local`.`evidence_link` (`created_by`,`created_on`,`modified_by`,`modified_on`,`link`,`comment`,`evidence_id`) VALUES ('PC_BACKEND',NOW(),'PC_BACKEND',NOW(),'www.bs2_link1.com','comment_bs2_1',3);
INSERT INTO `pc_local`.`evidence_link` (`created_by`,`created_on`,`modified_by`,`modified_on`,`link`,`comment`,`evidence_id`) VALUES ('PC_BACKEND',NOW(),'PC_BACKEND',NOW(),'www.bs1_link1.com','comment_bs1_1',4);
