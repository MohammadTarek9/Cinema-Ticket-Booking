create or alter procedure test
	@hallID int
as
declare	@seatNO int;
select @seatNO=no_of_seats from hall where hall_no = @hallID;

declare @iterator int = 1;
declare @sofaNO int = FLOOR(0.2 * @seatNO);
declare @premiumNO int = FLOOR(0.1 * @seatNO);
declare @disabeledNO int = FLOOR(0.2 * @seatNO);
declare @regularNO int = @seatNO - @sofaNO - @premiumNO - @disabeledNO;

begin
	while (@iterator <= @regularNO) begin
		insert into seat values (@hallID, @iterator, 0, 'regular');
		set @iterator = @iterator + 1;
	end

	while (@iterator <= @regularNO + @sofaNO) begin
		insert into seat values (@hallID, @iterator, 0, 'sofa');
		set @iterator = @iterator + 1;
	end

	while (@iterator <= @regularNO + @sofaNO + @disabeledNO) begin
		insert into seat values (@hallID, @iterator, 0, 'disabled');
		set @iterator = @iterator + 1;
	end

	while (@iterator <= @seatNO) begin
		insert into seat values (@hallID, @iterator, 0, 'premium');
		set @iterator = @iterator + 1;
	end
end