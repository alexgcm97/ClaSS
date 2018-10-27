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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author REPUBLIC
 */
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
}
