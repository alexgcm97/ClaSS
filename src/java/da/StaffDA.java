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
import java.io.Serializable;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Alex
 */
@ManagedBean
@SessionScoped
public class StaffDA implements Serializable{

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
      boolean success, message;

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

    public void insertStaff(Staff s) throws SQLException {
        String staffID = getMaxID();
        try {
            connect = DBConnection.getConnection();
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT STAFFID FROM STAFF");

            ArrayList<Integer> ls = new ArrayList<>();

            while (rs.next()) {
                try {
                    ls.add(Integer.parseInt(rs.getString(1).split("S")[1]));
                } catch (Exception ex) {
                    System.out.println("Invalid Exception");
                }
            }

            if (ls.size() > 0) {
                int max = Collections.max(ls) + 1;
                staffID = "S" + max;
            } else {
                staffID = "S1001";
            }

            PreparedStatement pstmt = connect.prepareStatement("INSERT INTO STAFF VALUES (?,?,?,?,?,?,?,?,?)");

            pstmt.setString(1, staffID);
            pstmt.setString(2, s.getStaffName());
            pstmt.setInt(3, s.getBlockDay());
            pstmt.setDouble(4, s.getBlockStart());
            pstmt.setDouble(5, s.getBlockDuration());
            pstmt.setString(6, s.getCourseCodeListS());
            pstmt.setString(7, s.getLecGroupListS());
            pstmt.setString(8, s.getTutGroupListS());
            pstmt.setString(9, s.getPracGroupListS());
           
            pstmt.executeUpdate();

            
        } catch (SQLException e) {
            System.out.println(e);

        }

    }

  public Staff deleteStaff(String staffID) {
      
        Staff s = new Staff();
        try {
            connect = DBConnection.getConnection();
            PreparedStatement ps = connect.prepareStatement("delete from Staff where StaffID = ?");
            ps.setString(1, staffID);
            System.out.println(ps);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return s;
    }

    public Staff get(String staffID) {
       
        Staff s = new Staff();
        try {
            connect = DBConnection.getConnection();
            
            PreparedStatement pstmt = connect.prepareStatement("select * from Staff where staffID = ?");
            pstmt.setString(1, staffID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                s = new Staff(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getDouble(4), rs.getDouble(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9));             
                
            }   
           
        } catch (SQLException e) {
            System.out.println(e);
            
        }
        return s;
    }
        
    public void updateStaff(Staff s) {
  
        try {
            connect = DBConnection.getConnection();
            PreparedStatement ps = connect.prepareStatement("update Staff set staffName=?, blockday=?, blockstart=?, blockduration=?, coursecodelist=?, lecgrouplist=?, tutgrouplist=?, pracgrouplist=? where staffID=?");
            
            ps.setString(1, s.getStaffName());
            ps.setInt(2, s.getBlockDay());
            ps.setDouble(3, s.getBlockStart());
            ps.setDouble(4, s.getBlockDuration());
            ps.setString(5, s.getCourseCodeListS());
            ps.setString(6, s.getLecGroupListS());
            ps.setString(7, s.getTutGroupListS());
            ps.setString(8, s.getPracGroupListS());         
            ps.setString(9, s.getStaffID());
            
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
        }

    }

    public String getMaxID() {
     
        String staffID = "";
        try {
            connect = DBConnection.getConnection();
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT STAFFID FROM STAFF");

            ArrayList<Integer> ls = new ArrayList<>();

            while (rs.next()) {
                try {
                    ls.add(Integer.parseInt(rs.getString(1).split("S")[1]));
                } catch (Exception ex) {
                    System.out.println("Invalid Exception");
                }
            }

            if (ls.size() > 0) {
                int max = Collections.max(ls) + 1;
                staffID = "S" + max;
            } else {
                staffID = "S";
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
        }
        System.out.println(staffID);
        return staffID;
    }
}
