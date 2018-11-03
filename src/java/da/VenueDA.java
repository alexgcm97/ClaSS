/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package da;

import domain.Class;
import domain.Venue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

    public List<String> getVenueType() throws SQLException {

        Connection connect = null;

        List<String> output = new ArrayList<String>();
        try {
            connect = DBConnection.getConnection();
            PreparedStatement pstmt = connect.prepareStatement("SELECT venueType FROM venue");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                if (!output.contains(rs.getString(1))) {
                    output.add(rs.getString(1));
                }
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
                if (rs.getString(5) == null || rs.getString(5) == "") {
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

    //Get the alert message
    boolean success, message, update, delete;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isMessage() {
        return message;
    }

    public void setMessage(boolean message) {
        this.message = message;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;;
    }

    public void insertVenue(Venue v) throws SQLException {
        Connection connect = null;

        try {
            connect = DBConnection.getConnection();

            PreparedStatement pstmt = connect.prepareStatement("INSERT INTO VENUE(venueID,block,venueType,capacity,courseCodeList) VALUES(?,?,?,?,?)");

            pstmt.setString(1, v.getVenueID());
            pstmt.setString(2, v.getBlock());
            pstmt.setString(3, v.getVenueType());
            pstmt.setInt(4, v.getCapacity());
            pstmt.setString(5, v.getCourseCodeList());
            pstmt.executeUpdate();
            this.success = true;
            this.message = false;

        } catch (SQLException ex) {

            this.success = false;
            this.message = true;
            System.out.println(ex.getMessage());
        }
        DBConnection.close(connect);

    }

    public Venue get(String venueID) {
        Connection connect;
        Venue v = new Venue();
        try {
            connect = DBConnection.getConnection();
            PreparedStatement pstmt = connect.prepareStatement("select * from VENUE where VENUEID = ?");
            pstmt.setString(1, venueID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                v = new Venue(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getString(5));
            }

        } catch (SQLException e) {
            System.out.println(e);
        }
        return v;
    }

    public void updateVenue(String oriVenueID, Venue v) {
        Connection connect;
        try {
            connect = DBConnection.getConnection();
            PreparedStatement ps = connect.prepareStatement("update VENUE set venueID = ?, block=?, venueType=?, capacity=?, courseCodeList=? where venueID=?");
            ps.setString(1, v.getVenueID());
            ps.setString(2, v.getBlock());
            ps.setString(3, v.getVenueType());
            ps.setInt(4, v.getCapacity());
            ps.setString(5, v.getCourseCodeList());
            ps.setString(6, oriVenueID);
            ps.executeUpdate();

            this.update = true;

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public Venue deleteVenue(String venueID) {
        Connection connect;
        Venue v = new Venue();
        try {
            connect = DBConnection.getConnection();
            PreparedStatement ps = connect.prepareStatement("delete from VENUE where VenueID = ?");
            ps.setString(1, venueID);
            System.out.println(ps);
            ps.executeUpdate();
            this.delete = true;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return v;
    }

    public void reset() {
        this.success = false;
        this.update = false;
        this.delete = false;
    }
}
