/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package da;

import domain.TutorialGroup;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author REPUBLIC
 */
@ManagedBean
@SessionScoped
public class TutorialGroupDA {

    public List<TutorialGroup> getAllTutorialGroupRecords() throws SQLException {

        Connection connect = null;

        List<TutorialGroup> output = new ArrayList<TutorialGroup>();
        try {
            connect = DBConnection.getConnection();
            PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM TutorialGroup");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                TutorialGroup tg = new TutorialGroup();
                tg.setGroupID(rs.getString(1));
                tg.setGroupNumber(rs.getInt(2));
                output.add(tg);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }

        DBConnection.close(connect);
        return output;

    }

    public List<String> getCourseCodeList(String groupID) throws SQLException {

        Connection connect = null;

        List<String> output = new ArrayList();
        try {
            connect = DBConnection.getConnection();
            PreparedStatement pstmt = connect.prepareStatement("SELECT pc.courseCodeList FROM ProgrammeCohort pc, TutorialGroup tg WHERE groupID = ? AND tg.cohortID = pc.cohortID");
            pstmt.setString(1, groupID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String strArr[] = rs.getString("courseCodeList").split(",");
                for (String s : strArr) {
                    output.add(s);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();

        }

        DBConnection.close(connect);
        return output;

    }

    public List<String> getGroupIdViaCohortID(String cohortID) throws SQLException {

        Connection connect = null;

        List<String> output = new ArrayList<String>();
        try {
            connect = DBConnection.getConnection();
            PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM TutorialGroup where cohortID = ?");
            pstmt.setString(1, cohortID);
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

    public List<TutorialGroup> getSelectedRecords(String groupID) throws SQLException {

        Connection connect = null;
        List<TutorialGroup> output = new ArrayList<TutorialGroup>();

        try {
            connect = DBConnection.getConnection();
            PreparedStatement pstmt = connect.prepareStatement("SELECT tg.groupID, tg.groupNumber, tg.size, tg.cohortID, pc.courseCodeList, pc.programmeCode, pc.studyYear ,pc.entryYear,pc.intakeYear FROM tutorialgroup tg, programmeCohort pc WHERE groupID=? AND tg.cohortID = pc.cohortID");
            pstmt.setString(1, groupID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                TutorialGroup TG = new TutorialGroup();
                TG.setGroupID(rs.getString(1));
                TG.setGroupNumber(rs.getInt(2));
                TG.setSize(rs.getInt(3));
                TG.setCohortID(rs.getString(4));
                TG.setCourseCodeList(rs.getString(5));
                TG.setProgrammeCode(rs.getString(6));
                TG.setStudyYear(rs.getInt(7));
                TG.setEntryYear(rs.getString(8));
                TG.setIntakeYear(rs.getString(9));
                output.add(TG);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
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

    public void insertTutorialGroup(TutorialGroup tg) throws SQLException {
        String tgID = getMaxID();
        Connection connect = null;

        try {
            connect = DBConnection.getConnection();
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT GroupID FROM TUTORIALGROUP");

            ArrayList<Integer> ls = new ArrayList<>();

            while (rs.next()) {
                try {
                    ls.add(Integer.parseInt(rs.getString(1).split("G")[1]));
                } catch (Exception ex) {
                    System.out.println("Invalid Exception");
                }
            }

            if (ls.size() > 0) {
                int max = Collections.max(ls) + 1;
                tgID = "G" + max;
            } else {
                tgID = "G1001";
            }

            PreparedStatement pstmt = connect.prepareStatement("INSERT INTO TUTORIALGROUP VALUES(?,?,?,?)");

            pstmt.setString(1, tgID);
            pstmt.setInt(2, tg.getGroupNumber());
            pstmt.setInt(3, tg.getSize());
            pstmt.setString(4, tg.getCohortID());

            pstmt.executeUpdate();
            this.success = true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public List<TutorialGroup> getTGviaCohortID(String cohortID) {
        Connection connect = null;
        List<TutorialGroup> output = new ArrayList<TutorialGroup>();
        try {
            connect = DBConnection.getConnection();
            PreparedStatement pstmt = connect.prepareStatement("select * from TUTORIALGROUP where COHORTID = ?");
            pstmt.setString(1, cohortID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                TutorialGroup tg = new TutorialGroup(rs.getString(1), rs.getInt(2), rs.getInt(3), rs.getString(4));
                output.add(tg);
            }

        } catch (SQLException e) {
            System.out.println(e);
        }
        return output;
    }

    public ArrayList<TutorialGroup> getAll() {
        Connection connect = null;
        TutorialGroup tg = new TutorialGroup();
        ArrayList<TutorialGroup> tgList = new ArrayList();
        try {
            connect = DBConnection.getConnection();
            PreparedStatement pstmt = connect.prepareStatement("select tg.groupID, tg.groupNumber, tg.size, tg.cohortID, pc.courseCodeList from TUTORIALGROUP tg, programmeCohort pc where pc.cohortID = tg.cohortID");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tg = new TutorialGroup(rs.getString(1), rs.getInt(2), rs.getInt(3), rs.getString(4), rs.getString(5));
                tgList.add(tg);
            }

        } catch (SQLException e) {
            System.out.println(e);
        }
        DBConnection.close(connect);
        return tgList;
    }

    public TutorialGroup get(String groupID) {
        Connection connect;
        TutorialGroup tg = new TutorialGroup();
        try {
            connect = DBConnection.getConnection();
            PreparedStatement pstmt = connect.prepareStatement("select * from TUTORIALGROUP where GROUPID = ?");
            pstmt.setString(1, groupID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                tg = new TutorialGroup(rs.getString(1), rs.getInt(2), rs.getInt(3), rs.getString(4));
            }

        } catch (SQLException e) {
            System.out.println(e);
        }
        return tg;
    }

    public TutorialGroup deleteGroup(String groupID) {
        Connection connect;
        TutorialGroup tg = new TutorialGroup();
        try {
            connect = DBConnection.getConnection();
            PreparedStatement ps = connect.prepareStatement("delete from TUTORIALGROUP where GROUPID = ?");
            ps.setString(1, groupID);
            System.out.println(ps);
            ps.executeUpdate();
            this.delete = true;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return tg;
    }

    public void updateTutorialGroup(TutorialGroup tg) {

        Connection connect;
        try {
            connect = DBConnection.getConnection();;
            PreparedStatement ps = connect.prepareStatement("update TUTORIALGROUP set groupNumber=?, size=?, cohortID=? where groupID=?");

            ps.setInt(1, tg.getGroupNumber());
            ps.setInt(2, tg.getSize());
            ps.setString(3, tg.getCohortID());
            ps.setString(4, tg.getGroupID());
            ps.executeUpdate();
            this.update = true;
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String getMaxID() {

        Connection connect;

        String groupID = "";

        try {

            connect = DBConnection.getConnection();;
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT GROUPID FROM TUTORIALGROUP");

            ArrayList<Integer> ls = new ArrayList<>();

            while (rs.next()) {
                try {
                    ls.add(Integer.parseInt(rs.getString(1).split("G")[1]));
                } catch (Exception ex) {
                    System.out.println("Invalid Exception");
                }
            }

            if (ls.size() > 0) {
                int max = Collections.max(ls) + 1;
                groupID = "G" + max;
            } else {
                groupID = "G1001";
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block

        }
        return groupID;
    }

    public void reset() {
        this.success = false;
        this.update = false;
        this.delete = false;
    }
}
