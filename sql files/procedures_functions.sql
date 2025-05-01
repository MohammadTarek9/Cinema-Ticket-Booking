

-- 1. Procedure to get all movies with their genres
CREATE OR ALTER PROCEDURE sp_GetAllMoviesWithGenres
AS
BEGIN
    SELECT 
        m.movieID, m.title, m.description, m.main_language, 
        m.duration, m.movie_day, m.movie_month, m.movie_year,
        m.now_showing, m.censorship, m.rating, m.lead_actor, m.director,
        STRING_AGG(g.movie_genre, ', ') AS genres
    FROM movie m
    LEFT JOIN genre g ON m.movieID = g.movieID
    GROUP BY 
        m.movieID, m.title, m.description, m.main_language, 
        m.duration, m.movie_day, m.movie_month, m.movie_year,
        m.now_showing, m.censorship, m.rating, m.lead_actor, m.director;
END
GO


-- 2. Procedure to get now showing movies
CREATE OR ALTER PROCEDURE sp_GetNowShowingMovies
AS
BEGIN
    SELECT 
        m.movieID, m.title, m.description, m.main_language, 
        m.duration, m.movie_day, m.movie_month, m.movie_year,
        m.now_showing, m.censorship, m.rating, m.lead_actor, m.director,
        STRING_AGG(g.movie_genre, ', ') AS genres
    FROM movie m
    LEFT JOIN genre g ON m.movieID = g.movieID
    WHERE m.now_showing = 1
    GROUP BY 
        m.movieID, m.title, m.description, m.main_language, 
        m.duration, m.movie_day, m.movie_month, m.movie_year,
        m.now_showing, m.censorship, m.rating, m.lead_actor, m.director;
END
GO

-- 3. Procedure to get movie show times
CREATE OR ALTER PROCEDURE sp_GetMovieShowTimes
    @movieID INT
AS
BEGIN
    SELECT 
        s.showID, 
        s.show_date, 
        s.show_time,
        h.hall_no,
        h.no_of_seats,
        sc.screen_type,
        sc.price,
        sc.resolution
    FROM show s
    JOIN hall h ON s.hall_no = h.hall_no
    JOIN screen sc ON h.screen_type = sc.screen_type
    WHERE s.movieID = @movieID
    AND s.show_date >= CAST(GETDATE() AS DATE)
    ORDER BY s.show_date, s.show_time;
END
GO

-- 4. Function to check seat availability
CREATE OR ALTER FUNCTION fn_IsSeatAvailable
(
    @showID INT,
    @seatNo INT
)
RETURNS BIT
AS
BEGIN
    DECLARE @isAvailable BIT = 1;
    
    IF EXISTS (
        SELECT 1 FROM ticket 
        WHERE showID = @showID 
        AND seat_no = @seatNo
        AND booking_status = 'CONFIRMED'
    )
    BEGIN
        SET @isAvailable = 0;
    END
    
    RETURN @isAvailable;
END
GO

-- 5. Procedure to get seat map for a show
CREATE OR ALTER PROCEDURE sp_GetSeatMapForShow
    @showID INT
AS
BEGIN
    -- Get hall info
    DECLARE @hallNo INT, @totalSeats INT;
    
    SELECT 
        @hallNo = s.hall_no, 
        @totalSeats = h.no_of_seats
    FROM show s
    JOIN hall h ON s.hall_no = h.hall_no
    WHERE s.showID = @showID;
    
    -- Return seat information with availability status
    SELECT 
        s.seat_no,
        s.seat_type,
        s.seat_status,
        CASE 
            WHEN EXISTS (
                SELECT 1 FROM ticket t 
                WHERE t.showID = @showID 
                AND t.seat_no = s.seat_no
                AND t.booking_status = 'CONFIRMED'
            ) THEN 0 -- Booked
            ELSE 1 -- Available
        END AS is_available
    FROM seat s
    WHERE s.hall_no = @hallNo
    ORDER BY s.seat_no;
END
GO

-- 6. Procedure to get menu items
CREATE OR ALTER PROCEDURE sp_GetMenuItems
AS
BEGIN
    SELECT 
        itemID, 
        item_name, 
        price, 
        size, 
        category
    FROM menu_item
    ORDER BY category, item_name;
END
GO


-- 8. Procedure to get all distinct genres
CREATE OR ALTER PROCEDURE sp_GetAllGenres
AS
BEGIN
    SELECT DISTINCT movie_genre FROM genre ORDER BY movie_genre;
END
GO

-- 9. Procedure to get all distinct directors
CREATE OR ALTER PROCEDURE sp_GetAllDirectors
AS
BEGIN
    SELECT DISTINCT director FROM movie ORDER BY director;
END
GO

-- 10. Procedure to search movies
CREATE OR ALTER PROCEDURE sp_SearchMovies
    @searchTerm VARCHAR(100)
AS
BEGIN
    SELECT 
        m.movieID, m.title, m.description, m.main_language, 
        m.duration, m.movie_day, m.movie_month, m.movie_year,
        m.now_showing, m.censorship, m.rating, m.lead_actor, m.director,
        STRING_AGG(g.movie_genre, ', ') AS genres
    FROM movie m
    LEFT JOIN genre g ON m.movieID = g.movieID
    WHERE m.title LIKE '%' + @searchTerm + '%'
       OR m.description LIKE '%' + @searchTerm + '%'
       OR m.lead_actor LIKE '%' + @searchTerm + '%'
       OR m.director LIKE '%' + @searchTerm + '%'
    GROUP BY 
        m.movieID, m.title, m.description, m.main_language, 
        m.duration, m.movie_day, m.movie_month, m.movie_year,
        m.now_showing, m.censorship, m.rating, m.lead_actor, m.director;
END
GO



