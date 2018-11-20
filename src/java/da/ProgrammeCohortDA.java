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

    public List<ProgrammeCohort> getAllProgrammeCohortRecords() throws SQLException {

        Connection connect = null;

        List<ProgrammeCohort> output = new ArrayList<>();
        try {
            connect = DBConnection.getConnection();
            PreparedStatement pstmt = connect.prepareStatement("SELECT pc.programmeCode, pc.cohortID, pc.studyYear, pc.intakeYear, pc.entryYear, p.programmeName FROM programmeCohort pc, programme p where p.programmeCode = pc.programmeCode");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ProgrammeCohort pc = new ProgrammeCohort();
                pc.setProgrammeCode(rs.getString(1));
                pc.setCohortID(rs.getString(2));
                pc.setStudyYear(rs.getInt(3));
                pc.setIntakeYear(rs.getString(4));
                pc.setEntryYear(rs.getString(5));
                pc.setProgrammeName(rs.getString(6));
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

    public void insertProgrammeCohort(ProgrammeCohort pc) throws SQLException {

        String cohortID = getMaxID();
        Connection connect = null;

        try {
            connect = DBConnection.getConnection();
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT cohortID FROM PROGRAMMECOHORT");

            ArrayList<Integer> ls = new ArrayList<>();

            while (rs.next()) {
                try {
                    ls.add(Integer.parseInt(rs.getString(1).split("PC")[1]));
                } catch (Exception ex) {
                    System.out.println("Invalid Exception");
                }
            }

            if (ls.size() > 0) {
                int max = Collections.max(ls) + 1;
                cohortID = "PC" + max;
            } else {
                cohortID = "PC1001";
            }

            PreparedStatement pstmt = connect.prepareStatement("INSERT INTO PROGRAMMECOHORT VALUES(?,?,?,?,?,?)");

            pstmt.setString(1, cohortID);
            pstmt.setString(2, pc.getEntryYear());
            pstmt.setString(3, pc.getProgrammeCode());
            pstmt.setInt(4, pc.getStudyYear());
            pstmt.setString(5, pc.getIntakeYear());
            pstmt.setString(6, pc.getCourseCodeList());
            this.success = true;
            this.delete = false;
            this.update = false;

            pstmt.executeUpdate();

        } catch (SQLException ex) {

            System.out.println(ex.getMessage());
        }

    }

    public ProgrammeCohort deleteProgrammeCohort(String cohortID) {

        Connection connect;
        ProgrammeCohort p = new ProgrammeCohort();
        try {
            connect = DBConnection.getConnection();
            PreparedStatement ps = connect.prepareStatement("delete from PROGRAMMECOHORT where cohortID = ?");
            ps.setString(1, cohortID);
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

    public List<ProgrammeCohort> getCourseCodePC(String courseCode) {
        Connection connect;
        ProgrammeCohort pc = new ProgrammeCohort();
        List<ProgrammeCohort> output = new ArrayList<ProgrammeCohort>();
            try {
                connect = DBConnection.getConnection();
                PreparedStatement pstmt = connect.prepareStatement("select * from PROGRAMMECOHORT where COURSECODELIST LIKE ?");
                pstmt.setString(1, '%' + courseCode + '%');
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    pc = new ProgrammeCohort(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getString(5), rs.getString(6));
                    output.add(pc);
                }

            } catch (Exception e) {
                System.out.println(e);
            }
        
        return output;
    }

    public ProgrammeCohort get(String cohortID) {
        Connection connect;
        ProgrammeCohort pc = new ProgrammeCohort();
        try {
            connect = DBConnection.getConnection();
            PreparedStatement pstmt = connect.prepareStatement("select * from PROGRAMMECOHORT where cohortID = ?");
            pstmt.setString(1, cohortID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                pc = new ProgrammeCohort(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getString(5), rs.getString(6));
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return pc;

    }

    public void updateProgrammeCohort(ProgrammeCohort pc) {

        Connection connect = null;

        try {
            connect = DBConnection.getConnection();
            PreparedStatement ps = connect.prepareStatement("update PROGRAMMECOHORT set EntryYear=?, PROGRAMMECODE=?, STUDYYEAR = ?, INTAKEYEAR =?, courseCodeList = ? where COHORTID=?");
            ps.setString(1, pc.getEntryYear());
            ps.setString(2, pc.getProgrammeCode());
            ps.setInt(3, pc.getStudyYear());
            ps.setString(4, pc.getIntakeYear());
            ps.setString(5, pc.getCourseCodeList());
            ps.setString(6, pc.getCohortID());
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
        String cohortID = "";

        try {

            connect = DriverManager.getConnection(url, username, password);
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COHORTID FROM PROGRAMMECOHORT");

            ArrayList<Integer> ls = new ArrayList<>();

            while (rs.next()) {
                try {
                    ls.add(Integer.parseInt(rs.getString(1).split("PC")[1]));
                } catch (Exception ex) {
                    System.out.println("Invalid Exception");
                }
            }

            if (ls.size() > 0) {
                int max = Collections.max(ls) + 1;
                cohortID = "PC" + max;
            } else {
                cohortID = "PC";
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(cohortID);
        return cohortID;
    }

    public void reset() {
        this.success = false;
        this.update = false;
        this.delete = false;
    }
}
