drop table transactionrec;
drop table account;
drop table bankuser;

create table bankuser (
    userId number not null,
    userName varchar2(30) not null,
    password varchar2(60) not null,
    firstName varchar2(50) not null,
    lastName varchar2(50) not null,
    middleInitial varchar2(1),
    gender varchar2(1) not null,
    dateOfBirth date not null,
    street varchar2(100) not null,
    city varchar2(40) not null,
    state varchar2(40) not null,
    zip varchar2(10) not null,
    phone varchar2(20) not null,
    email varchar2(80) not null,
    constraint bankuser_pk primary key (userId),
    constraint bank_username_uk unique (userName)
);

create table account (
    accountId number not null,
    accountType varchar2(10) not null,
    accountBalance number not null,
    accountPin varchar2(60) not null,
    dateOfCreated timestamp not null, 
    userId number not null,
    constraint account_pk primary key (accountId),
    constraint fk_bankuser foreign key (userId) references bankuser (userId)
);

create table transactionrec (
    tranId number not null,
    accountId_from number not null,
    accountId_to number,
    transactionType varchar2(10) not null,
    transactionAmount number not null,
    transactionTime timestamp not null,   
    constraint transaction_pk primary key (tranId),
    constraint fk_account_from foreign key (accountId_from) references account (accountId),
    constraint fk_account_to foreign key (accountId_to) references account (accountId)
);

