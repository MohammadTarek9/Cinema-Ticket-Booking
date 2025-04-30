CREATE OR ALTER PROCEDURE sp_InsertCustomer
    @phoneNum VARCHAR(20),
    @Fname VARCHAR(50),
    @Minit CHAR(1),
    @Lname VARCHAR(50),
    @age INT,
    @Email VARCHAR(100)
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (SELECT 1 FROM customer WHERE phoneNum = @phoneNum)
    BEGIN
        SELECT @phoneNum AS phoneNum;
        RETURN;
    END

    INSERT INTO customer (phoneNum, Fname, Minit, Lname, age, email)
    VALUES (@phoneNum, @Fname, @Minit, @Lname, @age, @Email);

    SELECT @phoneNum AS phoneNum;
END
GO

CREATE OR ALTER PROCEDURE sp_InsertPayment
    @payment_cost DECIMAL(5, 2),
    @method VARCHAR(50),
    @customer_phone_no VARCHAR(20),
    @payment_date DATETIME
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO payment (payment_cost, method, status, customer_phone_no, payment_date)
    VALUES (@payment_cost, @method, 'completed', @customer_phone_no, @payment_date);

    SELECT CAST(SCOPE_IDENTITY() AS INT) AS paymentID;
END;
GO


CREATE OR ALTER PROCEDURE sp_GetHallNumberByShowID
    @showID INT,
    @hall_no INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    SELECT @hall_no = hall_no
    FROM show
    WHERE showID = @showID;

    IF @hall_no IS NULL
        THROW 50000, 'Show not found with given ID.', 1;
END
GO

CREATE OR ALTER PROCEDURE sp_CreateTicket
    @showID INT,
    @hall_no INT,
    @seat_no INT,
    @paymentID INT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO ticket (showID, hall_no, seat_no, booking_status, paymentID)
    VALUES (@showID, @hall_no, @seat_no, 'CONFIRMED', @paymentID);
END
GO

CREATE OR ALTER PROCEDURE sp_UpdateSeatStatus
    @hall_no INT,
    @seat_no INT
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE seat
    SET seat_status = 1
    WHERE hall_no = @hall_no AND seat_no = @seat_no;
END
GO

CREATE OR ALTER PROCEDURE sp_AddOrderItem
    @paymentID INT,
    @itemID INT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO purchased_with (paymentID, itemID)
    VALUES (@paymentID, @itemID);
END
GO





