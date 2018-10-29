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
import domain.Class;
import domain.Staff;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alex
 */
public class StaffDA {

    private Connection connect;

    public ArrayList<Class> getClassList(String staffID) throws SQLException {
        ArrayList<Class> classList = new ArrayList();

        try {
            connect = DBConnection.getConnection();

            PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM class WHERE staffID = ?");
            pstmt.setString(1, staffID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Class c = new Class(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getDouble(6), rs.getDouble(7));
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

    public List<String> getStaffIdViaGroupIdCourseCode(String courseCode, String groupId) throws SQLException {

        Connection connect = null;

        List<String> output = new ArrayList<String>();
        try {
            connect = DBConnection.getConnection();
            PreparedStatement pstmt = connect.prepareStatement("SELECT staffId FROM STAFF WHERE (lecGroupList LIKE ? and lecGroupList LIKE ?) or (tutGroupList LIKE ? and tutGroupList LIKE ?) or (pracGroupList LIKE ? and pracGroupList LIKE ?)");
            pstmt.setString(1, '%' + courseCode + '%');
            pstmt.setString(2, '%' + groupId + '%');
            pstmt.setString(3, '%' + courseCode + '%');
            pstmt.setString(4, '%' + groupId + '%');
            pstmt.setString(5, '%' + courseCode + '%');
            pstmt.setString(6, '%' + groupId + '%');

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                output.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }

        DBConnection.close(connect);
        return output;

    }

    public List<Staff> getSelectedStaff(String staffID) {

        List<Staff> output = new ArrayList<Staff>();

        try {
            connect = DBConnection.getConnection();

            PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM staff WHERE staffID = ?");
            pstmt.setString(1, staffID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Staff s = new Staff();
                s.setStaffID(rs.getString(1));
                s.setStaffName(rs.getString(2));
                s.setBlockDay(rs.getInt(3));
                s.setBlockStart(rs.getDouble(4));
                s.setBlockDuration(rs.getDouble(5));
                if (rs.getString(6) == null || rs.getString(6).equals("")) {
                    s.setCourseCodeListS("");
                } else {
                    s.setCourseCodeListS(rs.getString(6));
                }
                if (rs.getString(7) == null || rs.getString(7).equals("")) {
                    s.setLecGroupListS("");
                } else {
                    s.setLecGroupListS(rs.getString(7));
                }
                if (rs.getString(8) == null || rs.getString(8).equals("")) {
                    s.setTutGroupListS("");
                } else {
                    s.setTutGroupListS(rs.getString(8));
                }
                if (rs.getString(9) == null || rs.getString(9).equals("")) {
                    s.setPracGroupListS("");
                } else {
                    s.setPracGroupListS(rs.getString(9));
                }

                output.add(s);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        DBConnection.close(connect);
        return output;
    }
}
