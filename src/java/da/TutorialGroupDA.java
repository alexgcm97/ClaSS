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
            PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM Tutorial_Group");
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

    public List<String> getGroupIdViaProgrammeID(String programmeID) throws SQLException {

        Connection connect = null;

        List<String> output = new ArrayList<String>();
        try {
            connect = DBConnection.getConnection();
            PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM Tutorial_Group where programmeID = ?");
            pstmt.setString(1, programmeID);
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
            PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM tutorial_group WHERE groupID=? ");
            pstmt.setString(1, groupID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                TutorialGroup TG = new TutorialGroup();
                TG.setGroupID(rs.getString(1));
                TG.setStudyYear(rs.getInt(2));
                TG.setGroupNumber(rs.getInt(3));
                TG.setSize(rs.getInt(4));
                TG.setProgrammeID(rs.getString(5));
                TG.setCohortID(rs.getString(6));
                if (rs.getString(7) == null || rs.getString(7) == "") {
                    TG.setCourseCodeList("");
                } else {
                    TG.setCourseCodeList(rs.getString(7));
                }
                output.add(TG);
            }
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

//    public void reset(){
//        this.groupID = "";
//        this.studyYear = 0;
//        this.groupNumber = 0;
//        this.size = 0 ;
//    }
  
    public void insertTutorialGroup(TutorialGroup tg) throws SQLException {
        String tgID = getMaxID();
        Connection connect = null;

        try {
            connect = DBConnection.getConnection();
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT GroupID FROM TUTORIAL_GROUP");

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

            PreparedStatement pstmt = connect.prepareStatement("INSERT INTO TUTORIAL_GROUP VALUES(?,?,?,?,?,?,?)");

            pstmt.setString(1, tgID);
            pstmt.setInt(2, tg.getStudyYear());
            pstmt.setInt(3, tg.getGroupNumber());
            pstmt.setInt(4, tg.getSize());
            pstmt.setString(5, tg.getProgrammeID());
            pstmt.setString(6, tg.getCohortID());
            pstmt.setString(7, tg.getCourseCodeList());

            pstmt.executeUpdate();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public TutorialGroup get(String groupID) {
        Connection connect;
        TutorialGroup tg = new TutorialGroup();
        try {
            connect = DBConnection.getConnection();
            PreparedStatement pstmt = connect.prepareStatement("select * from TUTORIAL_GROUP where GROUPID = ?");
            pstmt.setString(1, groupID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                tg = new TutorialGroup(rs.getString(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getString(5), rs.getString(6), rs.getString(7));
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
            PreparedStatement ps = connect.prepareStatement("delete from TUTORIAL_GROUP where GROUPID = ?");
            ps.setString(1, groupID);
            System.out.println(ps);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return tg;
    }

    public void updateTutorialGroup(TutorialGroup tg) {

        Connection connect;
        try {
            connect = DBConnection.getConnection();;
            PreparedStatement ps = connect.prepareStatement("update TUTORIAL_GROUP set studyYear=?, groupNumber=?, size=?, programmeID=?, cohortID=?, coursecodelist=? where groupID=?");

            ps.setInt(1, tg.getStudyYear());
            ps.setInt(2, tg.getGroupNumber());
            ps.setInt(3, tg.getSize());
            ps.setString(4, tg.getProgrammeID());
            ps.setString(5, tg.getCohortID());
            ps.setString(6, tg.getCourseCodeList());
            ps.setString(7, tg.getGroupID());
            ps.executeUpdate();

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
            ResultSet rs = stmt.executeQuery("SELECT GROUPID FROM TUTORIAL_GROUP");

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
}
