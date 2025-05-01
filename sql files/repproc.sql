

-- 1. Monthly Ticket Sales Report
CREATE OR ALTER PROCEDURE sp_MonthlyTicketSalesReport
    @Year INT,
    @Month INT
AS
BEGIN
    SELECT DAY(s.show_date) as day, 
           COUNT(t.ticketID) as tickets, 
           SUM(p.payment_cost) as revenue
    FROM ticket t
    JOIN show s ON t.showID = s.showID
    JOIN payment p ON t.paymentID = p.paymentID
    WHERE YEAR(s.show_date) = @Year 
      AND MONTH(s.show_date) = @Month
    GROUP BY DAY(s.show_date)
    ORDER BY day;
END;
GO

-- 2. Movie Performance Report
CREATE OR ALTER PROCEDURE sp_MoviePerformanceReport
    @MovieTitle NVARCHAR(100),
    @Year INT,
    @Month INT
AS
BEGIN
    SELECT m.title, 
           DAY(s.show_date) as day, 
           COUNT(t.ticketID) as tickets, 
           SUM(p.payment_cost) as revenue
    FROM ticket t
    JOIN show s ON t.showID = s.showID
    JOIN movie m ON s.movieID = m.movieID
    JOIN payment p ON t.paymentID = p.paymentID
    WHERE m.title = @MovieTitle 
      AND YEAR(s.show_date) = @Year 
      AND MONTH(s.show_date) = @Month
    GROUP BY m.title, DAY(s.show_date)
    ORDER BY day;
END;
GO

-- 3. Cinema Performance Report
CREATE OR ALTER PROCEDURE sp_CinemaPerformanceReport
    @CinemaName NVARCHAR(100),
    @Year INT,
    @Month INT
AS
BEGIN
    SELECT c.cinema_name, 
           DAY(s.show_date) as day, 
           COUNT(t.ticketID) as tickets, 
           SUM(p.payment_cost) as revenue
    FROM ticket t
    JOIN show s ON t.showID = s.showID
    JOIN hall h ON s.hall_no = h.hall_no
    JOIN cinema c ON h.cinemaid = c.cinemaid
    JOIN payment p ON t.paymentID = p.paymentID
    WHERE c.cinema_name = @CinemaName 
      AND YEAR(s.show_date) = @Year 
      AND MONTH(s.show_date) = @Month
    GROUP BY c.cinema_name, DAY(s.show_date)
    ORDER BY day;
END;
GO

-- 4. Menu Item Sales Report
CREATE OR ALTER PROCEDURE sp_MenuItemSalesReport
    @Year INT,
    @Month INT
AS
BEGIN
    SELECT mi.item_name, 
           COUNT(pw.itemID) as item_count, 
           SUM(mi.price) as revenue
    FROM purchased_with pw
    JOIN menu_item mi ON pw.itemID = mi.itemID
    JOIN payment p ON pw.paymentID = p.paymentID
    WHERE YEAR(p.payment_date) = @Year 
      AND MONTH(p.payment_date) = @Month
    GROUP BY mi.item_name
    ORDER BY revenue DESC;
END;
GO

-- 5. Popular Menu Items Report
CREATE OR ALTER PROCEDURE sp_PopularMenuItemsReport
    @Year INT
AS
BEGIN
    SELECT TOP 5 mi.item_name, 
           COUNT(pw.itemID) as purchase_count
    FROM purchased_with pw
    JOIN menu_item mi ON pw.itemID = mi.itemID
    JOIN payment p ON pw.paymentID = p.paymentID
    WHERE YEAR(p.payment_date) = @Year
    GROUP BY mi.item_name
    ORDER BY purchase_count DESC;
END;
GO

-- 6. Movie Comparisons Report
CREATE OR ALTER PROCEDURE sp_MovieComparisonsReport
    @Year INT
AS
BEGIN
    SELECT TOP 5 m.title, 
           COUNT(t.ticketID) as tickets, 
           SUM(p.payment_cost) as revenue
    FROM ticket t
    JOIN show s ON t.showID = s.showID
    JOIN movie m ON s.movieID = m.movieID
    JOIN payment p ON t.paymentID = p.paymentID
    WHERE YEAR(s.show_date) = @Year
    GROUP BY m.title
    ORDER BY revenue DESC;
END;
GO

-- 7. Revenue by Screen Type Report
CREATE OR ALTER PROCEDURE sp_RevenueByScreenTypeReport
    @Year INT
AS
BEGIN
    SELECT sc.screen_type, 
           SUM(p.payment_cost) as revenue
    FROM ticket t
    JOIN show s ON t.showID = s.showID
    JOIN hall h ON s.hall_no = h.hall_no
    JOIN screen sc ON h.screen_type = sc.screen_type
    JOIN payment p ON t.paymentID = p.paymentID
    WHERE YEAR(s.show_date) = @Year
    GROUP BY sc.screen_type
    ORDER BY revenue DESC;
END;
GO

-- 8. Get Seat Utilization
CREATE OR ALTER PROCEDURE sp_GetSeatUtilization
    @CinemaName NVARCHAR(100),
    @Year INT,
    @Month INT
AS
BEGIN
    SELECT SUM(h.no_of_seats) as total_seats, 
           SUM(CASE WHEN t.ticketID IS NOT NULL THEN 1 ELSE 0 END) as occupied_seats
    FROM hall h
    JOIN cinema c ON h.cinemaid = c.cinemaid
    LEFT JOIN show s ON h.hall_no = s.hall_no 
                     AND YEAR(s.show_date) = @Year 
                     AND MONTH(s.show_date) = @Month
    LEFT JOIN ticket t ON s.showID = t.showID
    WHERE c.cinema_name = @CinemaName;
END;
GO