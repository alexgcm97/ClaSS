/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package da;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import domain.Class;
import domain.Venue;
import java.util.List;

/**
 *
 * @author Alex
 */
public class VenueDA {

    private Connection connect;

    public ArrayList<Class> getClassList(String venueID) throws SQLException {
        ArrayList<Class> classList = new ArrayList();

        try {
            connect = DBConnection.getConnection();

            PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM class WHERE venueID = ?");
            pstmt.setString(1, venueID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Class c = new domain.Class(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getDouble(6), rs.getDouble(7));
                classList.add(c);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        connect.close();
        return classList;
    }

    public List<Venue> getVenueIdViaCourseCode(String courseCode) throws SQLException {

        Connection connect = null;

        List<Venue> output = new ArrayList<Venue>();
        try {
            connect = DBConnection.getConnection();
            PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM venue WHERE courseCodeList LIKE ?");
            pstmt.setString(1, '%' + courseCode + '%');

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Venue v = new Venue(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getString(5));
                output.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }

        DBConnection.close(connect);
        return output;

    }

    public List<Venue> getAllVenueRecords() throws SQLException {

        Connection connect = null;

        List<Venue> output = new ArrayList<Venue>();
        try {
            connect = DBConnection.getConnection();
            PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM venue");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Venue v = new Venue(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getString(5));
                output.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }

        DBConnection.close(connect);
        return output;

    }

    public List<Venue> getSelectedRecords(String venueID) throws SQLException {

        Connection connect = null;
        List<Venue> output = new ArrayList<Venue>();

        try {
            connect = DBConnection.getConnection();
            PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM venue WHERE venueID=? ");
            pstmt.setString(1, venueID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Venue v = new Venue();
                v.setVenueID(rs.getString(1));
                v.setBlock(rs.getString(2));
                v.setVenueType(rs.getString(3));
                v.setCapacity(rs.getInt(4));
                if (rs.getString(5) == null  || rs.getString(5)=="") {
                    v.setCourseCodeList("");
                } else {
                    v.setCourseCodeList(rs.getString(5));
                }
                output.add(v);

            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        DBConnection.close(connect);
        return output;

    }
}
