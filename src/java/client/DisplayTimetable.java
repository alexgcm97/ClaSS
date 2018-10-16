//@author Kok Teck Siong
//This page is to get the schedule data follow by the day
package client;

import da.DB_connection;
import domain.scheduleDetail;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class DisplayTimetable implements Serializable {

    // Get Monday schedule to display
    public List<scheduleDetail> getScheduleMon() throws ClassNotFoundException, SQLException {

        DB_connection dc = new DB_connection();
        Connection connect = dc.connection();

        List<scheduleDetail> schedule = new ArrayList<scheduleDetail>();
        PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM schedule WHERE day = 1");
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            scheduleDetail sch = new scheduleDetail();
            sch.setStartTime(rs.getDouble("startTime"));
            sch.setEndTime(rs.getDouble("endTime"));
            sch.setCourseCode(rs.getString("courseCode"));
            sch.setCourseType(rs.getString("courseType"));
            sch.setVenueID(rs.getString("venueID"));
            sch.setProgrammeCode(rs.getString("programmeCode"));
            sch.setGroupNumber(rs.getString("groupNumber"));
            sch.setStudyYear(rs.getString("studyYear"));
            sch.setStaffName(rs.getString("staffName"));
            sch.setCohort(rs.getString("cohort"));
            sch.setsTime(rs.getString("sTime"));
            sch.seteTime(rs.getString("eTime"));

            schedule.add(sch);
        }
        rs.close();
        pstmt.close();
        connect.close();

        return schedule;
    }

    // Get Tuesday schedule to display
    public List<scheduleDetail> getScheduleTue() throws ClassNotFoundException, SQLException {

        DB_connection dc = new DB_connection();
        Connection connect = dc.connection();

        List<scheduleDetail> schedule2 = new ArrayList<scheduleDetail>();
        PreparedStatement pstmt2 = connect.prepareStatement("SELECT * FROM schedule WHERE day = 2");
        ResultSet rs2 = pstmt2.executeQuery();
        while (rs2.next()) {
            scheduleDetail sch2 = new scheduleDetail();
            sch2.setStartTime(rs2.getDouble("startTime"));
            sch2.setEndTime(rs2.getDouble("endTime"));
            sch2.setCourseCode(rs2.getString("courseCode"));
            sch2.setCourseType(rs2.getString("courseType"));
            sch2.setVenueID(rs2.getString("venueID"));
            sch2.setProgrammeCode(rs2.getString("programmeCode"));
            sch2.setGroupNumber(rs2.getString("groupNumber"));
            sch2.setStudyYear(rs2.getString("studyYear"));
            sch2.setStaffName(rs2.getString("staffName"));
            sch2.setCohort(rs2.getString("cohort"));
            sch2.setsTime(rs2.getString("sTime"));
            sch2.seteTime(rs2.getString("eTime"));

            schedule2.add(sch2);
        }
        rs2.close();
        pstmt2.close();
        connect.close();

        return schedule2;
    }

    // Get Wednesday schedule to display
    public List<scheduleDetail> getScheduleWed() throws ClassNotFoundException, SQLException {

        DB_connection dc = new DB_connection();
        Connection connect = dc.connection();

        List<scheduleDetail> schedule3 = new ArrayList<scheduleDetail>();
        PreparedStatement pstmt3 = connect.prepareStatement("SELECT * FROM schedule WHERE day = 3");
        ResultSet rs3 = pstmt3.executeQuery();
        while (rs3.next()) {
            scheduleDetail sch3 = new scheduleDetail();
            sch3.setStartTime(rs3.getDouble("startTime"));
            sch3.setEndTime(rs3.getDouble("endTime"));
            sch3.setCourseCode(rs3.getString("courseCode"));
            sch3.setCourseType(rs3.getString("courseType"));
            sch3.setVenueID(rs3.getString("venueID"));
            sch3.setProgrammeCode(rs3.getString("programmeCode"));
            sch3.setGroupNumber(rs3.getString("groupNumber"));
            sch3.setStudyYear(rs3.getString("studyYear"));
            sch3.setStaffName(rs3.getString("staffName"));
            sch3.setCohort(rs3.getString("cohort"));
            sch3.setsTime(rs3.getString("sTime"));
            sch3.seteTime(rs3.getString("eTime"));

            schedule3.add(sch3);
        }
        rs3.close();
        pstmt3.close();
        connect.close();

        return schedule3;
    }

    // Get Thursday schedule to display
    public List<scheduleDetail> getScheduleThu() throws ClassNotFoundException, SQLException {

        DB_connection dc = new DB_connection();
        Connection connect = dc.connection();

        List<scheduleDetail> schedule4 = new ArrayList<scheduleDetail>();
        PreparedStatement pstmt4 = connect.prepareStatement("SELECT * FROM schedule WHERE day = 4");
        ResultSet rs4 = pstmt4.executeQuery();
        while (rs4.next()) {
            scheduleDetail sch4 = new scheduleDetail();
            sch4.setStartTime(rs4.getDouble("startTime"));
            sch4.setEndTime(rs4.getDouble("endTime"));
            sch4.setCourseCode(rs4.getString("courseCode"));
            sch4.setCourseType(rs4.getString("courseType"));
            sch4.setVenueID(rs4.getString("venueID"));
            sch4.setProgrammeCode(rs4.getString("programmeCode"));
            sch4.setGroupNumber(rs4.getString("groupNumber"));
            sch4.setStudyYear(rs4.getString("studyYear"));
            sch4.setStaffName(rs4.getString("staffName"));
            sch4.setCohort(rs4.getString("cohort"));
            sch4.setsTime(rs4.getString("sTime"));
            sch4.seteTime(rs4.getString("eTime"));

            schedule4.add(sch4);
        }
        rs4.close();
        pstmt4.close();
        connect.close();

        return schedule4;
    }

    // Get Friday schedule to display
    public List<scheduleDetail> getScheduleFri() throws ClassNotFoundException, SQLException {

        DB_connection dc = new DB_connection();
        Connection connect = dc.connection();

        List<scheduleDetail> schedule5 = new ArrayList<scheduleDetail>();
        PreparedStatement pstmt5 = connect.prepareStatement("SELECT * FROM schedule WHERE day = 5");
        ResultSet rs5 = pstmt5.executeQuery();
        while (rs5.next()) {
            scheduleDetail sch5 = new scheduleDetail();
            sch5.setStartTime(rs5.getDouble("startTime"));
            sch5.setEndTime(rs5.getDouble("endTime"));
            sch5.setCourseCode(rs5.getString("courseCode"));
            sch5.setCourseType(rs5.getString("courseType"));
            sch5.setVenueID(rs5.getString("venueID"));
            sch5.setProgrammeCode(rs5.getString("programmeCode"));
            sch5.setGroupNumber(rs5.getString("groupNumber"));
            sch5.setStudyYear(rs5.getString("studyYear"));
            sch5.setStaffName(rs5.getString("staffName"));
            sch5.setCohort(rs5.getString("cohort"));
            sch5.setsTime(rs5.getString("sTime"));
            sch5.seteTime(rs5.getString("eTime"));

            schedule5.add(sch5);
        }
        rs5.close();
        pstmt5.close();
        connect.close();

        return schedule5;
    }

    // Get Saturday schedule to display
    public List<scheduleDetail> getScheduleSat() throws ClassNotFoundException, SQLException {

        DB_connection dc = new DB_connection();
        Connection connect = dc.connection();

        List<scheduleDetail> schedule6 = new ArrayList<scheduleDetail>();
        PreparedStatement pstmt6 = connect.prepareStatement("SELECT * FROM schedule WHERE day = 6");
        ResultSet rs6 = pstmt6.executeQuery();
        while (rs6.next()) {
            scheduleDetail sch6 = new scheduleDetail();
            sch6.setStartTime(rs6.getDouble("startTime"));
            sch6.setEndTime(rs6.getDouble("endTime"));
            sch6.setCourseCode(rs6.getString("courseCode"));
            sch6.setCourseType(rs6.getString("courseType"));
            sch6.setVenueID(rs6.getString("venueID"));
            sch6.setProgrammeCode(rs6.getString("programmeCode"));
            sch6.setGroupNumber(rs6.getString("groupNumber"));
            sch6.setStudyYear(rs6.getString("studyYear"));
            sch6.setStaffName(rs6.getString("staffName"));
            sch6.setCohort(rs6.getString("cohort"));
            sch6.setsTime(rs6.getString("sTime"));
            sch6.seteTime(rs6.getString("eTime"));

            schedule6.add(sch6);
        }
        rs6.close();
        pstmt6.close();
        connect.close();

        return schedule6;
    }

    // Get Sunday schedule to display
    public List<scheduleDetail> getScheduleSun() throws ClassNotFoundException, SQLException {

        DB_connection dc = new DB_connection();
        Connection connect = dc.connection();

        List<scheduleDetail> schedule7 = new ArrayList<scheduleDetail>();
        PreparedStatement pstmt7 = connect.prepareStatement("SELECT * FROM schedule WHERE day = 0");
        ResultSet rs7 = pstmt7.executeQuery();
        while (rs7.next()) {
            scheduleDetail sch7 = new scheduleDetail();
            sch7.setStartTime(rs7.getDouble("startTime"));
            sch7.setEndTime(rs7.getDouble("endTime"));
            sch7.setCourseCode(rs7.getString("courseCode"));
            sch7.setCourseType(rs7.getString("courseType"));
            sch7.setVenueID(rs7.getString("venueID"));
            sch7.setProgrammeCode(rs7.getString("programmeCode"));
            sch7.setGroupNumber(rs7.getString("groupNumber"));
            sch7.setStudyYear(rs7.getString("studyYear"));
            sch7.setStaffName(rs7.getString("staffName"));
            sch7.setCohort(rs7.getString("cohort"));
            sch7.setsTime(rs7.getString("sTime"));
            sch7.seteTime(rs7.getString("eTime"));

            schedule7.add(sch7);
        }
        rs7.close();
        pstmt7.close();
        connect.close();

        return schedule7;
    }

    // Get courseCode to display
    public List<scheduleDetail> getCourseDetail() throws ClassNotFoundException, SQLException {

        DB_connection dc = new DB_connection();
        Connection connect = dc.connection();

        List<scheduleDetail> schedule = new ArrayList<scheduleDetail>();
        PreparedStatement pstmt = connect.prepareStatement("SELECT courseCode FROM schedule GROUP BY courseCode");
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            scheduleDetail sch = new scheduleDetail();
            sch.setCourseCode(rs.getString("courseCode"));

            pstmt = connect.prepareStatement("SELECT * FROM course WHERE courseCode='" + sch.getCourseCode() + "'");
            ResultSet rs2 = pstmt.executeQuery();
            while (rs2.next()) {
                sch.setCourseName(rs2.getString("courseName"));

                schedule.add(sch);
            }
        }
        rs.close();
        pstmt.close();
        connect.close();

        return schedule;
    }
}
