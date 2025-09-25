create table alfa (
  uno int not null,
  due varchar(45) null,
  primary key (uno),
  constraint vincolo unique (due)
);

create table beta (
  uno int not null,
  due varchar(45) null,
  primary key (uno),
  constraint vincolo unique (due)
);
