insert into cinema (contact_no, cinema_name, opening_hours, closing_hours, street, city, district) values
('+20123456789', 'citystars cinema', '10:00:00', '00:00:00', 'omar ibn elkhattab st', 'cairo', 'nasr city'),
('+20223456789', 'cfc cinemas', '11:00:00', '23:30:00', 'el tesseen st','cairo', 'fifth district'),
('+20323456789', 'point 90 cinema', '09:30:00', '23:00:00', 'el tesseen st', '6th of october', 'point 90 mall'),
('+20423456789', 'mall of egypt cinema', '10:00:00', '00:00:00', 'al wahat rd', 'giza', 'sheikh zayed'),
('+20523456789', 'genena city cinema', '12:00:00', '22:00:00', 'suez canal rd', 'ismailia', 'genena city'),
('+20623456789', 'asmall cinema', '09:00:00', '23:00:00', 'corniche el nile', 'aswan', 'elephantine island');

insert into screen values
('imax', 250.00, '4k laser'),
('4dx', 300.00, '2k'),
('vip', 200.00, '2k'),
('standard', 100.00, '2k'),
('3d', 150.00, '2k'),
('premium', 180.00, '4k'),
('gold class', 350.00, '4k'),
('dolby cinema', 280.00, '4k hdr');

insert into hall values
('dolby atmos', 250, 1, 'imax'),
('dts:x', 180, 1, '4dx'),
('dolby digital', 200, 1, 'vip'),
('dts', 300, 1, 'standard'),
('dolby atmos', 150, 1, 'premium'),
('auro 3d', 120, 1, 'gold class'),
('dolby digital', 220, 2, 'standard'),
('dts:x', 160, 2, '3d'),
('dolby atmos', 180, 2, 'premium'),
('dts', 280, 2, 'standard'),
('dolby digital', 130, 2, 'vip'),
('dolby atmos', 110, 2, 'dolby cinema'),
('dts:x', 240, 3, 'imax'),
('dolby digital', 170, 3, '4dx'),
('dts', 190, 3, 'standard'),
('dolby atmos', 310, 3, 'standard'),
('auro 3d', 140, 3, 'premium'),
('dolby digital', 100, 3, 'gold class'),
('dolby atmos', 230, 4, 'imax'),
('dts:x', 150, 4, '3d'),
('dolby digital', 210, 4, 'vip'),
('dts', 290, 4, 'standard'),
('dolby atmos', 160, 4, 'premium'),
('auro 3d', 90, 4, 'dolby cinema'),
('dolby digital', 200, 5, 'standard'),
('dts:x', 140, 5, '3d'),
('dolby atmos', 170, 5, 'premium'),
('dts', 270, 5, 'standard'),
('dolby digital', 120, 5, 'vip'),
('dolby atmos', 80, 5, 'gold class'),
('dts:x', 210, 6, 'standard'),
('dolby digital', 130, 6, '3d'),
('dts', 180, 6, 'standard'),
('dolby atmos', 260, 6, 'standard'),
('auro 3d', 110, 6, 'premium'),
('dolby digital', 70, 6, 'vip');

--insert seats
DECLARE @currentId INT = 1;
WHILE @currentId <= 20
BEGIN
    exec add_seats @hallId = @currentId;
    SET @currentId = @currentId + 1;
END

insert into movie values
('The Pharaoh''s Curse', 'Archaeologists uncover an ancient tomb with deadly secrets', 'English', 128, 10, 4, 2025, 1, 'PG-13', 7.8, 'Amr Waked', 'Marwan Hamed'),
('Cairo Confidential', 'A detective uncovers corruption in Egypt''s financial sector', 'Arabic', 115, 28, 3, 2025, 1, 'PG', 8.1, 'Ahmed Ezz', 'Mohamed Khan'),
('Dune: Part Three', 'The epic conclusion to the desert planet saga', 'English', 156, 15, 3, 2025, 1, 'PG-13', 8.9, 'Timoth e Chalamet', 'Denis Villeneuve'),
('Alexandria Nights', 'Interwoven stories of love and betrayal in coastal Egypt', 'Arabic', 122, 5, 4, 2025, 1, 'PG', 7.5, 'Yousra', 'Sherif Arafa'),
('The Mummy Returns', 'Reboot of the classic adventure franchise', 'English', 134, 22, 3, 2025, 1, 'PG-13', 6.7, 'Rami Malek', 'Stephen Sommers'),
('Ramses the Great', 'Epic historical drama about Egypt''s most powerful pharaoh', 'Arabic', 180, 1, 5, 2025, 0, 'PG', NULL, 'Karim Abdel Aziz', 'Samer Seif'),
('Mission: Impossible - Legacy', 'Ethan Hunt''s final mission takes him to Egypt', 'English', 148, 15, 5, 2025, 0, 'PG-13', NULL, 'Tom Cruise', 'Christopher McQuarrie'),
('The Nile Conspiracy', 'Thriller about a plot to control Egypt''s water supply', 'English', 126, 30, 5, 2025, 0, 'R', NULL, 'Ahmed Dawood', 'Ali El Arabi'),
('Assal Eswed', 'Popular Egyptian comedy for Eid season', 'Arabic', 105, 10, 6, 2025, 0, 'PG', NULL, 'Ahmed Helmy', 'Khaled Marei'),
('Avatar: The Deep Desert', 'James Cameron''s new chapter with Egyptian desert setting', 'English', 162, 20, 6, 2025, 0, 'PG-13', NULL, 'Sam Worthington', 'James Cameron'),
('Oppenheimer', 'It follows the life of J. Robert Oppenheimer, the American theoretical physicist who helped develop the first nuclear weapons during World War II.', 'English', 138, 5, 7, 2025, 0, 'PG-13', NULL, 'Cillian Murphy', 'Christopher Nolan'),
('The Pyramid Code', 'Archaeological mystery based on new Giza discoveries', 'English', 132, 18, 7, 2025, 0, 'PG', NULL, 'Khaled El Nabawy', 'Tarek Al Eryan'),
('Sons of the Sun', 'Action film about protecting Egypt''s antiquities', 'Arabic', 118, 1, 8, 2025, 0, 'PG-13', NULL, 'Mohamed Ramadan', 'Peter Mimi'),
('Desert Storm', 'War drama set during Gulf War with Egyptian forces', 'Arabic', 142, 14, 8, 2025, 0, 'R', NULL, 'Asser Yassin', 'Marwan Hamed'),
('The Book of the Dead', 'Horror film based on ancient Egyptian rituals', 'English', 96, 30, 9, 2025, 0, 'R', NULL, 'Ahmed Malek', 'Amr Salama');

insert into show values
(1, 1, '2025-04-19', '14:00:00'),
(2, 2, '2025-04-19', '16:30:00'),
(3, 3, '2025-04-19', '19:00:00'),
(4, 4, '2025-05-19', '20:30:00'),
(4, 5, '2025-05-20', '15:00:00'),
(5, 1, '2025-05-21', '18:00:00'),
(6, 2, '2025-05-22', '17:30:00'),
(7, 3, '2025-05-23', '20:00:00'),
(8, 4, '2025-05-24', '19:30:00'),
(9, 5, '2025-05-25', '21:00:00'),
(10, 1, '2025-05-26', '12:00:00'),
(11, 2, '2025-05-26', '14:30:00'),
(12, 3, '2025-05-26', '17:00:00'),
(1, 4, '2025-05-27', '13:30:00'),
(1, 5, '2025-05-27', '16:00:00'),
(11, 1, '2025-05-25', '22:30:00'),
(1, 2, '2025-05-26', '21:30:00'),
(2, 3, '2025-05-27', '20:30:00'),
(3, 4, '2025-05-28', '18:30:00'),
(2, 5, '2025-05-29', '19:00:00');

insert into customer values
('+201012345678', 'Ahmed', 'M', 'Elsayed', 28, 'ahmed.elsayed@mail.com'),
('+201112345678', 'Mohamed', 'A', 'Ali', 35, 'mohamed.ali@mail.com'),
('+201212345678', 'Mahmoud', 'K', 'Hassan', 22, 'mahmoud.hassan@mail.com'),
('+201512345678', 'Youssef', 'S', 'Ibrahim', 40, 'youssef.ibrahim@mail.com'),
('+201012345679', 'Fatma', 'H', 'Mohamed', 25, 'fatma.mohamed@mail.com'),
('+201112345679', 'Mariam', 'O', 'Ahmed', 30, 'mariam.ahmed@mail.com'),
('+201212345679', 'Aya', 'E', 'Mahmoud', 19, 'aya.mahmoud@mail.com'),
('+201512345679', 'Nour', 'Y', 'Khalid', 27, 'nour.khalid@mail.com'),
('+201012345680', 'Omar', 'F', 'Samir', 33, 'omar.samir@mail.com'),
('+201112345680', 'Khaled', 'W', 'Adel', 45, 'khaled.adel@mail.com'),
('+201212345680', 'Ali', 'R', 'Hussein', 21, 'ali.hussein@mail.com'),
('+201512345680', 'Hassan', 'T', 'Mostafa', 29, 'hassan.mostafa@mail.com'),
('+201012345681', 'Amira', 'N', 'Osman', 31, 'amira.osman@mail.com'),
('+201112345681', 'Dina', 'L', 'Waleed', 24, 'dina.waleed@mail.com'),
('+201212345681', 'Hana', 'B', 'Karim', 26, 'hana.karim@mail.com'),
('+201512345681', 'Rania', 'G', 'Fouad', 38, 'rania.fouad@mail.com'),
('+201012345682', 'Karim', 'D', 'Nasser', 42, 'karim.nasser@mail.com'),
('+201112345682', 'Tarek', 'J', 'Hamdy', 23, 'tarek.hamdy@mail.com'),
('+201212345682', 'Wael', 'P', 'Ashraf', 36, 'wael.ashraf@mail.com'),
('+201512345682', 'Sherif', 'Q', 'Zaki', 32, 'sherif.zaki@mail.com'),
('+201012345683', 'Nada', 'V', 'Saad', 20, 'nada.saad@mail.com'),
('+201112345683', 'Salma', 'X', 'Tamer', 28, 'salma.tamer@mail.com'),
('+201212345683', 'Farida', 'Z', 'Adham', 34, 'farida.adham@mail.com'),
('+201512345683', 'Hany', 'C', 'Raouf', 39, 'hany.raouf@mail.com'),
('+201012345684', 'Samir', 'U', 'Fathi', 41, 'samir.fathi@mail.com'),
('+201112345684', 'Heba', 'I', 'Samy', 27, 'heba.samy@mail.com'),
('+201212345684', 'Mona', 'E', 'Lotfy', 29, 'mona.lotfy@mail.com'),
('+201512345684', 'Nadia', 'O', 'Hatem', 37, 'nadia.hatem@mail.com'),
('+201012345685', 'Ibrahim', 'F', 'Gamal', 44, 'ibrahim.gamal@mail.com'),
('+201012345686', 'Sara', 'M', 'Hisham', 22, 'sara.hisham@mail.com');

insert into payment values

('2025-04-15 18:30:22', 250.00, 'Visa Credit Card', 'completed', '+201012345678'),
('2025-04-18 20:15:10', 300.00, 'Vodafone Cash', 'completed', '+201012345678'),

('2025-04-10 15:45:33', 100.00, 'Fawry', 'completed', '+201112345678'),

('2025-04-17 19:20:18', 150.00, 'InstaPay', 'completed', '+201212345678'),
('2025-04-19 14:05:42', 200.00, 'Mastercard', 'completed', '+201212345678'),

('2025-04-14 21:10:55', 350.00, 'Visa Debit Card', 'completed', '+201512345678'),

('2025-04-16 17:25:30', 180.00, 'Fawry', 'completed', '+201012345679'),

('2025-04-12 16:40:15', 280.00, 'Vodafone Cash', 'completed', '+201112345679'),
('2025-04-18 22:05:28', 100.00, 'Cash', 'completed', '+201112345679'),

('2025-04-19 13:15:37', 150.00, 'InstaPay', 'completed', '+201212345679'),

('2025-04-11 20:30:44', 200.00, 'Mastercard', 'completed', '+201512345679'),
('2025-04-17 18:45:12', 250.00, 'Visa Credit Card', 'completed', '+201512345679'),

('2025-04-13 19:55:03', 300.00, 'Fawry', 'completed', '+201012345680'),

('2025-04-15 14:20:49', 100.00, 'Cash', 'completed', '+201112345680'),
('2025-04-19 21:35:17', 180.00, 'Vodafone Cash', 'completed', '+201112345680'),

('2025-04-16 20:10:25', 280.00, 'InstaPay', 'completed', '+201212345680'),

('2025-04-14 15:40:38', 350.00, 'Mastercard', 'completed', '+201512345680'),

('2025-04-18 16:50:21', 200.00, 'Visa Debit Card', 'completed', '+201012345681'),

('2025-04-12 18:05:54', 250.00, 'Fawry', 'completed', '+201112345681'),
('2025-04-19 17:30:09', 300.00, 'Cash', 'completed', '+201112345681'),

('2025-04-17 14:45:33', 100.00, 'Vodafone Cash', 'completed', '+201212345681'),

('2025-04-15 19:20:47', 180.00, 'InstaPay', 'completed', '+201512345681'),
('2025-04-18 21:35:15', 280.00, 'Mastercard', 'completed', '+201512345681'),

('2025-04-13 20:25:42', 350.00, 'Visa Credit Card', 'completed', '+201012345682'),

('2025-04-16 15:50:28', 200.00, 'Fawry', 'completed', '+201112345682'),

('2025-04-14 17:15:39', 250.00, 'Cash', 'completed', '+201212345682'),
('2025-04-19 18:40:05', 300.00, 'Vodafone Cash', 'completed', '+201212345682'),

('2025-04-11 19:55:21', 100.00, 'InstaPay', 'completed', '+201512345682'),

('2025-04-17 16:30:48', 180.00, 'Mastercard', 'completed', '+201012345683'),

('2025-04-15 18:45:13', 280.00, 'Visa Debit Card', 'completed', '+201112345683'),
('2025-04-18 20:10:36', 350.00, 'Fawry', 'completed', '+201112345683'),

('2025-04-12 21:25:59', 200.00, 'Cash', 'completed', '+201212345683'),

('2025-04-14 14:50:44', 250.00, 'Vodafone Cash', 'completed', '+201512345683'),

('2025-04-16 17:05:17', 300.00, 'InstaPay', 'completed', '+201012345684'),
('2025-04-19 19:30:22', 100.00, 'Mastercard', 'completed', '+201012345684'),

('2025-04-13 18:55:35', 180.00, 'Visa Credit Card', 'completed', '+201112345684'),

('2025-04-15 20:20:08', 280.00, 'Fawry', 'completed', '+201212345684'),

('2025-04-11 15:45:51', 350.00, 'Cash', 'completed', '+201512345684'),
('2025-04-17 17:10:24', 200.00, 'Vodafone Cash', 'completed', '+201512345684'),

('2025-04-14 19:35:47', 250.00, 'InstaPay', 'completed', '+201012345685'),

('2025-04-16 14:00:12', 300.00, 'Mastercard', 'completed', '+201012345686'),
('2025-04-18 21:15:39', 100.00, 'Visa Debit Card', 'completed', '+201012345686');

insert into ticket values
('confirmed', 1, 1, 1, 1),
('confirmed', 1, 2, 1, 1),
('confirmed', 2, 1, 2, 2),
('confirmed', 3, 1, 3, 3),
('confirmed', 3, 2, 3, 3),
('confirmed', 4, 1, 4, 4);

insert into menu_item values
('Popcorn Classic', 45.00, 'Regular', 'Snacks'),
('Popcorn Classic', 65.00, 'Large', 'Snacks'),
('Popcorn Caramel', 55.00, 'Regular', 'Snacks'),
('Popcorn Caramel', 75.00, 'Large', 'Snacks'),
('Nachos with Cheese', 60.00, 'Regular', 'Snacks'),
('Nachos Supreme', 85.00, 'Large', 'Snacks'),
('Soft Drink', 25.00, 'Small', 'Beverages'),
('Soft Drink', 35.00, 'Medium', 'Beverages'),
('Soft Drink', 45.00, 'Large', 'Beverages'),
('Mineral Water', 15.00, '500ml', 'Beverages'),
('Hot Dog', 50.00, 'Regular', 'Meals'),
('Cheese Burger', 65.00, 'Regular', 'Meals'),
('Chicken Tenders', 70.00, '4 pieces', 'Meals'),
('Chocolate Brownie', 40.00, 'Regular', 'Desserts'),
('Ice Cream Cup', 30.00, 'Regular', 'Desserts');

insert into offer values
('Weekend Snack Special', 20.00, '2025-04-19', '2025-04-20', 'menu item'),
('Summer Movie Discount', 15.00, '2025-06-01', '2025-08-31', 'ticket'),
('Combo Meal Deal', 25.00, '2025-04-15', '2025-05-15', 'menu item'),
('Eid Festival Offer', 10.00, '2025-06-05', '2025-06-10', 'ticket'),
('Student Discount', 30.00, '2025-04-01', '2025-12-31', 'ticket'),
('Happy Hour Drinks', 15.00, '2025-04-01', '2025-04-30', 'menu item'),
('Family Package', 20.00, '2025-05-01', '2025-05-31', 'ticket'),
('Premium Snack Bundle', 15.00, '2025-04-10', '2025-05-10', 'menu item'),
('Early Bird Special', 10.00, '2025-04-01', '2025-06-30', 'ticket'),
('Dessert Combo', 25.00, '2025-04-15', '2025-05-15', 'menu item');

insert into purchased_with values
(1, 2), (1, 8),
(2, 3), (2, 9),
(3, 5), (3, 7),
(4, 1), (4, 10),
(5, 6), (5, 8),
(6, 4), (6, 9),
(7, 11), (7, 7),
(8, 12), (8, 8),
(9, 13), (9, 9),
(10, 14), (10, 10),
(11, 15), (11, 7),
(12, 2), (12, 8),
(13, 3), (13, 9),
(14, 5), (14, 10),
(15, 1), (15, 7);

insert into applied_to values
(1, 2), (2, 5), (3, 2), (4, 5), (5, 4),
(6, 5);

insert into eligible_for values
(1, 1), (1, 3),
(2, 1), (2, 3),
(3, 1), (3, 8),
(4, 1), (4, 8),
(5, 3), (5, 8),
(6, 3), (6, 8),
(7, 6), (8, 6),
(9, 6), (10, 6),
(3, 3), (4, 10),
(5, 10);

insert into genre values
('Adventure', 1), ('Horror', 1),
('Thriller', 2), ('Crime', 2),
('Sci-Fi', 3), ('Adventure', 3),
('Drama', 4), ('Romance', 4),
('Adventure', 5), ('Fantasy', 5),
('Historical', 6), ('Drama', 6),
('Action', 7), ('Thriller', 7),
('Thriller', 8), ('Drama', 8),
('Comedy', 9), ('Romance', 9),
('Sci-Fi', 10), ('Adventure', 10),
('Historical', 11), ('Drama', 11),
('Mystery', 12), ('Adventure', 12),
('Action', 13), ('Thriller', 13),
('War', 14), ('Drama', 14),
('Horror', 15), ('Thriller', 15);