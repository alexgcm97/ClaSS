/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import da.DB_connection;
import domain.Staff;
import domain.TutorialGroup;
import domain.Venue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Teck Siong
 */
@ManagedBean
@SessionScoped
public class ViewTimetable {

    public List<Staff> getAllStaff() throws ClassNotFoundException, SQLException {

        DB_connection dc = new DB_connection();
        Connection connect = dc.connection();

        List<Staff> Staff = new ArrayList<Staff>();
        PreparedStatement pstmt = connect
                .prepareStatement("SELECT * FROM staff");
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {

            Staff stf = new Staff();
            stf.setStaffID(rs.getString("staffID"));
            stf.setStaffName(rs.getString("staffName"));

            Staff.add(stf);
        }

        // close resources
        rs.close();
        pstmt.close();
        connect.close();

        return Staff;
    }

    public List<TutorialGroup> getAllGroup() throws ClassNotFoundException, SQLException {

        DB_connection dc = new DB_connection();
        Connection connect = dc.connection();

        List<TutorialGroup> group = new ArrayList<TutorialGroup>();
        PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM Tutorial_Group");
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            TutorialGroup grp = new TutorialGroup();
            grp.setGroupID(rs.getString("groupID"));
            grp.setProgrammeID(rs.getString("programmeID"));
            grp.setCohortID(rs.getString("cohortID"));
            grp.setStudyYear(rs.getInt("studyYear"));
            grp.setGroupNumber(rs.getInt("groupNumber"));

            pstmt = connect.prepareStatement("SELECT * FROM Programme WHERE programmeID = '" + grp.getProgrammeID() + "'");
            ResultSet rs2 = pstmt.executeQuery();
            while (rs2.next()) {
                grp.setProgrammeCode(rs2.getString("programmeCode"));

                pstmt = connect.prepareStatement("SELECT * FROM cohort WHERE cohortID = '" + grp.getCohortID() + "'");
                ResultSet rs3 = pstmt.executeQuery();
                while (rs3.next()) {
                    grp.setMonth(rs3.getString("month"));
                    grp.setYear(rs3.getString("years"));

                    group.add(grp);
                }
            }
        }

        // close resources
        rs.close();
        pstmt.close();
        connect.close();

        return group;
    }

    public List<Venue> getAllVenue() throws ClassNotFoundException, SQLException {

        DB_connection dc = new DB_connection();
        Connection connect = dc.connection();

        List<Venue> venue = new ArrayList<Venue>();
        PreparedStatement pstmt = connect
                .prepareStatement("SELECT * FROM venue ORDER BY venueID");
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {

            Venue vn = new Venue();
            vn.setVenueID(rs.getString("venueID"));
            vn.setVenueType(rs.getString("venueType"));

            venue.add(vn);
        }

        // close resources
        rs.close();
        pstmt.close();
        connect.close();

        return venue;
    }

    public String forward() {
        return "/displayTT?faces-redirect=true";
    }

}
