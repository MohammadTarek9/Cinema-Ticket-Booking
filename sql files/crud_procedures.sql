USE Cinema
GO

--getter queries
-- 1 --
CREATE OR ALTER PROCEDURE GetGenresForMovie
    @MovieID INT
AS
BEGIN
    SET NOCOUNT ON;

    SELECT movie_genre
    FROM genre
    WHERE movieID = @MovieID;
END
GO

-- 2 --
CREATE OR ALTER PROCEDURE GetAllScreens
AS
BEGIN
    SET NOCOUNT ON;

    SELECT * FROM screen;
END
GO

-- 3 --
CREATE OR ALTER PROCEDURE GetAllHalls
AS
BEGIN
    SET NOCOUNT ON;

    SELECT * FROM hall;
END
GO

-- 4 --
CREATE OR ALTER PROCEDURE GetAllCinemas
AS
BEGIN
    SET NOCOUNT ON;

    SELECT * FROM cinema;
END
GO

-- CRUD --
-- Add Screen
CREATE OR ALTER PROCEDURE AddScreen
    @screen_type NVARCHAR(50),
    @price DECIMAL(10,2),
    @resolution NVARCHAR(50)
AS
BEGIN
    INSERT INTO screen (screen_type, price, resolution)
    VALUES (@screen_type, @price, @resolution);
END;
GO

-- Delete Screen
CREATE OR ALTER PROCEDURE DeleteScreen
    @screen_type NVARCHAR(50)
AS
BEGIN
    DELETE FROM screen WHERE screen_type = @screen_type;
END;
GO

-- Update Screen
CREATE OR ALTER PROCEDURE UpdateScreen
    @price DECIMAL(10,2),
    @resolution VARCHAR(30),
    @screen_type VARCHAR(30)
AS
BEGIN
    UPDATE screen
    SET price = @price,
        resolution = @resolution
    WHERE screen_type = @screen_type;
END;
GO



-- Add Movie
CREATE OR ALTER PROCEDURE AddMovie
    @title NVARCHAR(255),
    @description NVARCHAR(255),
    @main_language NVARCHAR(50),
    @duration INT,
    @movie_day INT,
    @movie_month INT,
    @movie_year INT,
    @now_showing BIT,
    @censorship NVARCHAR(50),
    @rating DECIMAL(3,1),
    @lead_actor NVARCHAR(100),
    @director NVARCHAR(100)
AS
BEGIN
    INSERT INTO movie
    (title, description, main_language, duration, movie_day, movie_month, movie_year,
     now_showing, censorship, rating, lead_actor, director)
    VALUES
    (@title, @description, @main_language, @duration, @movie_day, @movie_month, @movie_year,
     @now_showing, @censorship, @rating, @lead_actor, @director);
    
END;
GO

-- Add Category
CREATE OR ALTER PROCEDURE AddCategory
	@genre_name VARCHAR(50),
    @movieID INT
AS
BEGIN
    -- Example of adding categories, assuming 'genre' is a related table
    -- Insert genres or categories based on your application's logic
    -- Example: 
    INSERT INTO genre VALUES (@genre_name, @movieID);
END;
GO

-- Update Movie
CREATE OR ALTER PROCEDURE UpdateMovie
    @title NVARCHAR(255),
    @description NVARCHAR(255),
    @main_language NVARCHAR(50),
    @duration INT,
    @movie_day INT,
    @movie_month INT,
    @movie_year INT,
    @now_showing BIT,
    @censorship NVARCHAR(50),
    @rating DECIMAL(3,1),
    @lead_actor NVARCHAR(100),
    @director NVARCHAR(100),
	@movieID INT
AS
BEGIN
    UPDATE movie
    SET title = @title,
        description = @description,
        main_language = @main_language,
        duration = @duration,
        movie_day = @movie_day,
        movie_month = @movie_month,
        movie_year = @movie_year,
        now_showing = @now_showing,
        censorship = @censorship,
        rating = @rating,
        lead_actor = @lead_actor,
        director = @director
    WHERE movieID = @movieID;

END;
GO

-- Drop Category
CREATE OR ALTER PROCEDURE DropCategory
    @movieID INT
AS
BEGIN
    DELETE FROM genre WHERE movieID = @movieID;
END;
GO

-- Delete Movie
CREATE OR ALTER PROCEDURE DeleteMovie
    @movieID INT
AS
BEGIN
    DELETE FROM movie WHERE movieID = @movieID;
END;
GO


-- Add Cinema
CREATE OR ALTER PROCEDURE AddCinema
    @cinema_name NVARCHAR(255),
    @contact_no NVARCHAR(50),
    @opening_hours NVARCHAR(50),
    @closing_hours NVARCHAR(50),
    @street NVARCHAR(255),
    @city NVARCHAR(100),
    @district NVARCHAR(100)
AS
BEGIN
    INSERT INTO cinema
    (cinema_name, contact_no, opening_hours, closing_hours, street, city, district)
    VALUES
    (@cinema_name, @contact_no, @opening_hours, @closing_hours, @street, @city, @district);
    
	DECLARE @cinemaID INT;
    SET @cinemaID = SCOPE_IDENTITY();

END;
GO

-- Delete Cinema
CREATE OR ALTER PROCEDURE DeleteCinema
    @cinemaID INT
AS
BEGIN
    DELETE FROM cinema WHERE cinemaID = @cinemaID;
END;
GO

-- Update Cinema
CREATE OR ALTER PROCEDURE UpdateCinema
    @cinema_name NVARCHAR(255),
    @contact_no NVARCHAR(50),
    @opening_hours NVARCHAR(50),
    @closing_hours NVARCHAR(50),
    @street NVARCHAR(255),
    @city NVARCHAR(100),
    @district NVARCHAR(100),
	@cinemaID INT
AS
BEGIN
    UPDATE cinema 
	SET cinema_name = @cinema_name, 
		contact_no = @contact_no, 
		opening_hours = @opening_hours, 
		closing_hours = @closing_hours, 
		street = @street, 
		city = @city, 
		district = @district 
	WHERE cinemaID = @cinemaID;
END;
GO

-- Get Specific Cinema--
CREATE OR ALTER PROCEDURE GetCinemaByID
    @cinemaID INT
AS
BEGIN
    SELECT * FROM cinema WHERE cinemaID =  @cinemaID;
END;
GO


-- Add Hall
CREATE OR ALTER PROCEDURE AddHall
    @sound_sys NVARCHAR(50),
    @screen_type NVARCHAR(50),
    @no_of_seats INT,
    @cinemaID INT
AS
BEGIN
    INSERT INTO hall (sound_sys, screen_type, no_of_seats, cinemaID)
    VALUES (@sound_sys, @screen_type, @no_of_seats, @cinemaID);
END;
GO

-- Delete Hall
CREATE OR ALTER PROCEDURE DeleteHall
    @hall_no INT
AS
BEGIN
    DELETE FROM hall WHERE hall_no = @hall_no;
END;
GO

-- Update Hall
CREATE OR ALTER PROCEDURE UpdateHall
    @sound_sys VARCHAR(30),
    @screen_type VARCHAR(30),
    @no_of_seats INT,
    @cinemaID INT,
	@hall_no INT
AS
BEGIN
    UPDATE hall
    SET sound_sys = @sound_sys,
        screen_type = @screen_type,
        no_of_seats = @no_of_seats,
        cinemaID = @cinemaID
    WHERE hall_no = @hall_no;
END;
GO


