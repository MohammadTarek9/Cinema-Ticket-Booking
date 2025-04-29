//package org.example;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Screen {
    String screenType;
    double price;
    String resolution;

    public Screen(String screenType, double price, String resolution) {
        this.screenType = screenType;
        this.price = price;
        this.resolution = resolution;
    }

    public String getScreenType() {
        return screenType;
    }

    public void setScreenType(String screenType) {
        this.screenType = screenType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    //sql
    public boolean addScreen() {
        String sql = "{CALL AddScreen(?, ?, ?)}"; // Calling AddScreen stored procedure
        try (PreparedStatement preparedStatement = DatabaseConnector.getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, screenType);
            preparedStatement.setDouble(2, price);
            preparedStatement.setString(3, resolution);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //sql
    public static boolean deleteScreen(String screenType) {
        String query = "{CALL DeleteScreen(?)}"; // Calling DeleteScreen stored procedure
        try (PreparedStatement preparedStatement = DatabaseConnector.getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, screenType);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //sql
    public boolean updateScreen(String screenType) {
        String query =  "{CALL UpdateScreen(?, ?, ?)}"; // Calling UpdateScreen stored procedure
        try (PreparedStatement preparedStatement = DatabaseConnector.getConnection().prepareStatement(query)) {
            preparedStatement.setDouble(1, price);
            preparedStatement.setString(2, resolution);
            preparedStatement.setString(3, screenType);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
