//@author Kok Teck Siong
//This page is to get data and display the list of tutorial group, staff, and venue
package client;

import da.DBConnection;
import domain.CourseType;
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
import javax.faces.bean.RequestScoped;

@ManagedBean
@RequestScoped
public class ViewTimetable {

    private Connection connect;

    //Get the staff list
    public List<Staff> getAllStaff() throws ClassNotFoundException, SQLException {
        connect = DBConnection.getConnection();

        List<Staff> Staff = new ArrayList<Staff>();
        PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM staff ORDER BY staffID");
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            Staff stf = new Staff();
            stf.setStaffID(rs.getString("staffID"));
            stf.setStaffName(rs.getString("staffName"));

            Staff.add(stf);
        }
        rs.close();
        pstmt.close();
        connect.close();

        return Staff;
    }

    //Get the group list
    public List<TutorialGroup> getAllGroup() throws ClassNotFoundException, SQLException {
        connect = DBConnection.getConnection();

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
                    grp.setYear(rs3.getString("year"));

                    group.add(grp);
                }
            }
        }
        rs.close();
        pstmt.close();
        connect.close();

        return group;
    }

    //Get the venue list
    public List<Venue> getAllVenue() throws ClassNotFoundException, SQLException {
        connect = DBConnection.getConnection();

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
        rs.close();
        pstmt.close();
        connect.close();

        return venue;
    }

    //Get the course list use by modifySchedule
    public List<CourseType> getAllCourse() throws ClassNotFoundException, SQLException {
        connect = DBConnection.getConnection();

        List<CourseType> cType = new ArrayList<CourseType>();
        PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM coursetype");
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            CourseType ct = new CourseType();
            ct.setCourseID(rs.getString("courseID"));
            ct.setCourseCode(rs.getString("courseCode"));
            ct.setCourseType(rs.getString("courseType"));

            cType.add(ct);
        }
        rs.close();
        pstmt.close();
        connect.close();

        return cType;
    }
}
