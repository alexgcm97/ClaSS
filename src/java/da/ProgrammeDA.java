/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package da;

import domain.Programme;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author REPUBLIC
 */
@ManagedBean
@SessionScoped
public class ProgrammeDA {

    public List<Programme> getAllProgrammeRecords() throws SQLException {

        Connection connect = null;

        List<Programme> output = new ArrayList<Programme>();
        try {
            connect = DBConnection.getConnection();
            PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM programme");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Programme p = new Programme(rs.getString(1), rs.getString(2), rs.getString(3));
                output.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }

        DBConnection.close(connect);
        return output;

    }
    boolean success, message, delete, update;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isMessage() {
        return message;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public void setMessage(boolean message) {
        this.message = message;
    }

    public void insertProgramme(Programme p) throws SQLException {
      
        String programmeID = getMaxID();
        Connection connect = null;

        try {
            connect = DBConnection.getConnection();
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT PROGRAMMEID FROM PROGRAMME");

            ArrayList<Integer> ls = new ArrayList<>();

            while (rs.next()) {
                try {
                    ls.add(Integer.parseInt(rs.getString(1).split("P")[1]));
                } catch (Exception ex) {
                    System.out.println("Invalid Exception");
                }
            }

            if (ls.size() > 0) {
                int max = Collections.max(ls) + 1;
                programmeID = "P" + max;
            } else {
                programmeID = "P1001";
            }

            PreparedStatement pstmt = connect.prepareStatement("INSERT INTO PROGRAMME VALUES(?,?,?)");

            pstmt.setString(1, programmeID);
            pstmt.setString(2, p.getProgrammeCode());
            pstmt.setString(3, p.getProgrammeName());

            this.success = true;
            this.delete = false;
            this.update = false;

            pstmt.executeUpdate();

        } catch (SQLException ex) {

            System.out.println(ex.getMessage());
        }

    }

    public Programme deleteProgramme(String programmeID) {
  
        Connection connect;
        Programme p = new Programme();
        try {
            connect = DBConnection.getConnection();
            PreparedStatement ps = connect.prepareStatement("delete from PROGRAMME where PROGRAMMEID = ?");
            ps.setString(1, programmeID);
            System.out.println(ps);
            ps.executeUpdate();
            this.delete = true;
            this.update = false;
            this.success = false;

        } catch (SQLException e) {
            System.out.println(e);
        }
        return p;
    }

    public Programme get(String programmeID) {
        Connection connect;
        Programme p = new Programme();
        try {
            connect = DBConnection.getConnection();
            PreparedStatement pstmt = connect.prepareStatement("select * from PROGRAMME where PROGRAMMEID = ?");
            pstmt.setString(1, programmeID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                p = new Programme(rs.getString(1), rs.getString(2), rs.getString(3));
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return p;

    }

    public void updateProgramme(Programme p) {
   
        Connection connect = null;

        try {
            connect = DBConnection.getConnection();
            PreparedStatement ps = connect.prepareStatement("update PROGRAMME set PROGRAMMECODE=?, PROGEAMMENAME=? where PROGRAMMEID=?");
            ps.setString(1, p.getProgrammeCode());
            ps.setString(2, p.getProgrammeName());
            ps.setString(3, p.getProgrammeID());
            this.update = true;
            this.success = false;
            this.delete = false;
            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public String getMaxID() {

        FacesContext fc = FacesContext.getCurrentInstance();
        Map<String, String> params = fc.getExternalContext().getRequestParameterMap();

        Connection connect = null;

        String url = "jdbc:derby://localhost:1527/schedule";
        String username = "schedule";
        String password = "schedule";
        String programmeID = "";

        try {

            connect = DriverManager.getConnection(url, username, password);
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT PROGRAMMEID FROM PROGRAMME");

            ArrayList<Integer> ls = new ArrayList<>();

            while (rs.next()) {
                try {
                    ls.add(Integer.parseInt(rs.getString(1).split("P")[1]));
                } catch (Exception ex) {
                    System.out.println("Invalid Exception");
                }
            }

            if (ls.size() > 0) {
                int max = Collections.max(ls) + 1;
                programmeID = "P" + max;
            } else {
                programmeID = "P";
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(programmeID);
        return programmeID;
    }
      public void reset(){
        this.success = false;
        this.update = false;
        this.delete = false;
    }
}
