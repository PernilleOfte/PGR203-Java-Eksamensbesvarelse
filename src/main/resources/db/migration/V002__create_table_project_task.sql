create table project_task (
id serial primary key,
name varchar(100),
status varchar(30) default 'To do'
);