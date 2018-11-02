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
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author DEREK
 */
@ManagedBean
@SessionScoped
public class ProgrammeDA {

    String year = "";
    String month = "";

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

    public void setMessage(boolean message) {
        this.message = message;
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

    public void insertProgramme(Programme p) throws SQLException {
        Connection connect;

        try {
            connect = DBConnection.getConnection();

            PreparedStatement pstmt = connect.prepareStatement("INSERT INTO PROGRAMME VALUES(?,?)");

            pstmt.setString(1, p.getProgrammeCode());
            pstmt.setString(2, p.getProgrammeName());

            pstmt.executeUpdate();
            this.success = true;

        } catch (SQLException ex) {
            this.message = true;
            System.out.println(ex.getMessage());
        }

    }

    public Programme deleteProgramme(String programmeCode) {
        Connection connect;
        Programme p = new Programme();
        try {
            connect = DBConnection.getConnection();
            PreparedStatement ps = connect.prepareStatement("delete from PROGRAMME where programmeCode = ?");
            ps.setString(1, programmeCode);
            System.out.println(ps);
            ps.executeUpdate();
            this.delete = true;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return p;
    }

    public Programme get(String programmeCode) {
        Connection connect;
        Programme p = new Programme();
        try {
            connect = DBConnection.getConnection();
            PreparedStatement pstmt = connect.prepareStatement("select * from PROGRAMME where PROGRAMMECODE = ?");
            pstmt.setString(1, programmeCode);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                p = new Programme(rs.getString(1), rs.getString(2));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return p;

    }

    public void updateProgramme(Programme p) {
        Connection connect;

        try {
            connect = DBConnection.getConnection();
            PreparedStatement ps = connect.prepareStatement("update Programme set PROGRAMMENAME = ?  where PROGRAMMECODE=?");

            ps.setString(1, p.getProgrammeName());
            ps.setString(2, p.getProgrammeCode());

            ps.executeUpdate();
            this.update = true;
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void reset() {
        this.success = false;
        this.update = false;
        this.delete = false;
    }
}
