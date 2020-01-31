create table user (
    id bigint not null auto_increment,
    first_name varchar(30),
    last_name varchar(30),
    email varchar(80),
    password varchar(30),
    enabled bit,
    primary key (id)
) engine=MyISAM;