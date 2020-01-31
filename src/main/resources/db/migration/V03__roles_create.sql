CREATE TABLE `rel_user_role` (
  `user_id` bigint NOT NULL,
  `roles_id` bigint NOT NULL,
  KEY `FKj05w3jg2eatjunvigr91wnu75` (`roles_id`),
  KEY `FKnexcjsrtbmnhyyj4v8rrbhe6i` (`user_id`),
  CONSTRAINT `FKj05w3jg2eatjunvigr91wnu75` FOREIGN KEY (`roles_id`) REFERENCES `role` (`id`),
  CONSTRAINT `FKnexcjsrtbmnhyyj4v8rrbhe6i` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) engine=MyISAM;