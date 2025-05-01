create table cinema (
cinemaid int identity(1,1) primary key,
contact_no varchar(25) not null,
cinema_name varchar(100) not null,
opening_hours time not null,
closing_hours time not null,
street varchar(100) not null,
city varchar(50) not null,
district varchar(50) not null
);

create table screen (
screen_type varchar(30) primary key,
price decimal(5,2) not null,
resolution varchar(20) not null,
);

create table hall (
hall_no int identity(1,1) primary key,
sound_sys varchar(30),
no_of_seats int,
cinemaid int foreign key references cinema(cinemaid) on delete cascade,
screen_type varchar(30) foreign key references screen(screen_type)
);

create table seat (
hall_no int foreign key references hall(hall_no) on delete cascade not null,
seat_no int not null,
seat_status tinyint not null,
seat_type varchar(20) not null,
primary key (hall_no, seat_no)
);

create table movie (
movieID int identity(1,1) primary key,
title varchar(100) not null,
description varchar(500),
main_language varchar(30) not null,
duration int not null,
movie_day int not null,
movie_month int not null,
movie_year int not null,
now_showing tinyint not null,
censorship varchar(10) not null,
rating decimal(3,1),
lead_actor varchar(100) not null,
director varchar(100) not null
);

create table show (
showID int identity(1,1) primary key,
hall_no int foreign key references hall(hall_no) on delete cascade not null,
movieID int foreign key references movie(movieID) not null,
show_date date not null,
show_time time not null
);

create table customer (
phoneNum varchar(25) primary key,
Fname varchar(20) not null,
Minit varchar(2),
Lname varchar(20) not null,
age int not null,
email varchar(30) not null
);

create table payment (
paymentID int identity(1,1) primary key,
payment_date datetime not null,
payment_cost decimal(5,2) not null,
method varchar(30) not null,
status varchar(15),
customer_phone_no varchar(25) foreign key references customer(phoneNum)
);


create table ticket (
ticketID int identity(1,1) primary key,
booking_status varchar(20) not null,
hall_no int not null,
seat_no int not null,
showID int foreign key references show(showID),
foreign key (hall_no, seat_no) references seat(hall_no, seat_no),
paymentID int foreign key references payment(paymentID)
);

create table menu_item (
itemID int identity(1,1) primary key,
item_name varchar(50) not null,
price decimal(5,2) not null,
size varchar(20) not null,
category varchar(30) not null
);

create table offer (
offerID int identity(1,1) primary key,
offer_name varchar(50) not null,
discount_percentage decimal (5,2) not null,
offer_start_date date not null,
offer_end_date date not null,
applicable_to varchar(20) not null
);

create table purchased_with (
paymentID int references payment(paymentID),
itemID int references menu_item(itemID),
primary key(paymentID, itemID)
);

create table applied_to (
ticketID int references ticket(ticketID),
offerID int references offer(offerID),
primary key(ticketID, offerID)
);

create table eligible_for (
itemID int references menu_item(itemID),
offerID int references offer(offerID),
primary key(itemID, offerID)
);

create table genre (
movie_genre varchar(50) not null,
movieID int references movie(movieID) on delete cascade not null,
primary key(movie_genre, movieID)
);