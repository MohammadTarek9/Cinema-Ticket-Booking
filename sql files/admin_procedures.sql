USE Cinema
GO

-- 1. Get Cinema Count
CREATE OR ALTER PROCEDURE sp_GetCinemaCount
AS
BEGIN
    SELECT COUNT(*) AS cinema_count FROM cinema;
END;
GO

-- 2. Get Hall Count
CREATE OR ALTER PROCEDURE sp_GetHallCount
AS
BEGIN
    SELECT COUNT(*) AS hall_count FROM hall;
END;
GO

-- 3. Get Now Showing Movies Count
CREATE OR ALTER PROCEDURE sp_GetNowShowingCount
AS
BEGIN
    SELECT COUNT(*) AS now_showing_count FROM movie WHERE now_showing = 1;
END;
GO

-- 4. Get Tickets Sold Count
CREATE OR ALTER PROCEDURE sp_GetTicketsSoldCount
AS
BEGIN
    SELECT COUNT(*) AS tickets_sold FROM ticket WHERE booking_status = 'CONFIRMED';
END;
GO

-- 5. Get Recent Activity
CREATE OR ALTER PROCEDURE sp_GetRecentActivity
AS
BEGIN
    SELECT TOP 10 
        'Booking' AS action_type,
        'Movie: ' + m.title + ', Seat: ' + CAST(t.seat_no AS VARCHAR) AS details,
        p.payment_date AS timestamp
    FROM ticket t
    JOIN payment p ON t.paymentID = p.paymentID
    JOIN show s ON t.showID = s.showID
    JOIN movie m ON s.movieID = m.movieID
    ORDER BY p.payment_date DESC;
END;
GO