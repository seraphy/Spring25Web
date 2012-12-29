
/*
 *  spring security 用
 */
create table users (
  loginid varchar(256) not null primary key
, password varchar(128) not null -- 512bit HexDecimal
, enabled integer not null default 0
, failcount integer not null default 0
, lastlogin timestamp
);

create table authorities (
  loginid varchar(256) not null
, authority varchar(30) not null
, constraint authorities_pk primary key (loginid, authority)
);

/*
 *  初期ログインユーザ
 *  admin/admin, user/user, guest/guest
 */
insert into users(loginid, password, enabled) values ('admin', 'e02a1a8809e830cf7b7c875e43c16e684ed02a818c7ac25aeadd515432f908ea041447720c194d6b0ec19a1c3dd97f7b378efaab4dd8efd46de568adf3f44c9a', 1);
insert into users(loginid, password, enabled) values ('user', 'c35c71570b3f45bb21a588107e7cb946b3c50bf2cd9e885d3876de669a73df1133aabe8b69d24db37837c6f26f9e7bc35dc34ee04c8f9a51d53ed7d82859f80e', 1);
insert into users(loginid, password, enabled) values ('guest', 'bb88c40ade177cda778b3345f625424fee4dbf5f750ade350b713fbfc977d4307453496e241977df09abbc603584631bc860531cfa048ba43c09f2c2311b2e2d', 0);

insert into authorities(loginid, authority) values ('admin', 'ROLE_USER');
insert into authorities(loginid, authority) values ('admin', 'ROLE_ADMINISTRATOR');
insert into authorities(loginid, authority) values ('user', 'ROLE_USER');
insert into authorities(loginid, authority) values ('guest', 'ROLE_GUEST');

commit;
