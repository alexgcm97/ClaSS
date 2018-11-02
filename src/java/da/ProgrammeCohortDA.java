/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package da;

import domain.ProgrammeCohort;
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
public class ProgrammeCohortDA {

    public List<ProgrammeCohort> getAllProgrammeRecords() throws SQLException {

        Connection connect = null;

        List<ProgrammeCohort> output = new ArrayList<>();
        try {
            connect = DBConnection.getConnection();
            PreparedStatement pstmt = connect.prepareStatement("SELECT p.programmeCode,p.programmeName, pc.cohortID, pc.studyYear, pc.intakeYear, pc.entryYear FROM programme p, programmeCohort pc WHERE p.programmeCode=pc.programmeCode");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ProgrammeCohort pc = new ProgrammeCohort();
                pc.setProgrammeCode(rs.getString(1));
                pc.setProgrammeName(rs.getString(2));
                pc.setCohortID(rs.getString(3));
                pc.setStudyYear(rs.getInt(4));
                pc.setIntakeYear(rs.getString(5));
                pc.setEntryYear(rs.getString(6));

                output.add(pc);
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
//
//    public void insertProgramme(ProgrammeCohort pc) throws SQLException {
//
//        String programmeID = getMaxID();
//        Connection connect = null;
//
//        try {
//            connect = DBConnection.getConnection();
//            Statement stmt = connect.createStatement();
//            ResultSet rs = stmt.executeQuery("SELECT PROGRAMMEID FROM PROGRAMME");
//
//            ArrayList<Integer> ls = new ArrayList<>();
//
//            while (rs.next()) {
//                try {
//                    ls.add(Integer.parseInt(rs.getString(1).split("P")[1]));
//                } catch (Exception ex) {
//                    System.out.println("Invalid Exception");
//                }
//            }
//
//            if (ls.size() > 0) {
//                int max = Collections.max(ls) + 1;
//                programmeID = "P" + max;
//            } else {
//                programmeID = "P1001";
//            }
//
//            PreparedStatement pstmt = connect.prepareStatement("INSERT INTO PROGRAMME VALUES(?,?,?,?)");
//
//            pstmt.setString(1, programmeID);
//            pstmt.setString(2, p.getProgrammeCode());
//            pstmt.setString(3, p.getProgrammeName());
//            pstmt.setString(4, p.getCohortID());
//
//            this.success = true;
//            this.delete = false;
//            this.update = false;
//
//            pstmt.executeUpdate();
//
//        } catch (SQLException ex) {
//
//            System.out.println(ex.getMessage());
//        }
//
//    }
//
//    public Programme deleteProgramme(String programmeID) {
//
//        Connection connect;
//        Programme p = new Programme();
//        try {
//            connect = DBConnection.getConnection();
//            PreparedStatement ps = connect.prepareStatement("delete from PROGRAMME where PROGRAMMEID = ?");
//            ps.setString(1, programmeID);
//            System.out.println(ps);
//            ps.executeUpdate();
//            this.delete = true;
//            this.update = false;
//            this.success = false;
//
//        } catch (SQLException e) {
//            System.out.println(e);
//        }
//        return p;
//    }
//
//    public ProgrammeCohort get(String programmeID) {
//        Connection connect;
//        ProgrammeCohort pc = new ProgrammeCohort();
//        try {
//            connect = DBConnection.getConnection();
//            PreparedStatement pstmt = connect.prepareStatement("select * from PROGRAMME where PROGRAMMEID = ?");
//            pstmt.setString(1, programmeID);
//            ResultSet rs = pstmt.executeQuery();
//            if (rs.next()) {
//                p = new Programme(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
//            }
//
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//        return p;
//
//    }
//
//    public void updateProgramme(Programme p) {
//
//        Connection connect = null;
//
//        try {
//            connect = DBConnection.getConnection();
//            PreparedStatement ps = connect.prepareStatement("update PROGRAMME set PROGRAMMECODE=?, PROGRAMMENAME=?, COHORTID = ? where PROGRAMMEID=?");
//            ps.setString(1, p.getProgrammeCode());
//            ps.setString(2, p.getProgrammeName());
//            ps.setString(3, p.getCohortID());
//            ps.setString(4, p.getProgrammeID());
//            this.update = true;
//            this.success = false;
//            this.delete = false;
//            ps.executeUpdate();
//
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//
//    }
//
//    public String getMaxID() {
//
//        FacesContext fc = FacesContext.getCurrentInstance();
//        Map<String, String> params = fc.getExternalContext().getRequestParameterMap();
//
//        Connection connect = null;
//
//        String url = "jdbc:derby://localhost:1527/schedule";
//        String username = "schedule";
//        String password = "schedule";
//        String programmeID = "";
//
//        try {
//
//            connect = DriverManager.getConnection(url, username, password);
//            Statement stmt = connect.createStatement();
//            ResultSet rs = stmt.executeQuery("SELECT PROGRAMMEID FROM PROGRAMME");
//
//            ArrayList<Integer> ls = new ArrayList<>();
//
//            while (rs.next()) {
//                try {
//                    ls.add(Integer.parseInt(rs.getString(1).split("P")[1]));
//                } catch (Exception ex) {
//                    System.out.println("Invalid Exception");
//                }
//            }
//
//            if (ls.size() > 0) {
//                int max = Collections.max(ls) + 1;
//                programmeID = "P" + max;
//            } else {
//                programmeID = "P";
//            }
//
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        System.out.println(programmeID);
//        return programmeID;
//    }
//
//    public void reset() {
//        this.success = false;
//        this.update = false;
//        this.delete = false;
//    }
}
