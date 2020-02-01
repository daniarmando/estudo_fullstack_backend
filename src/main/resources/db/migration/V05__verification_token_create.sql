create table verification_token (
    id bigint not null auto_increment,
    token varchar(256),
    primary key (id)
) engine=MyISAM;