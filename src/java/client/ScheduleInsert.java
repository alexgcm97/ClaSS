/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import da.DB_connection;
import domain.CourseType;
import domain.Programme;
import domain.Staff;
import domain.TutorialGroup;
import domain.Class;
import domain.scheduleDetail;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Teck Siong
 */
@ManagedBean
@SessionScoped
public class ScheduleInsert {

    String staffID;
    String sTime = "", eTime = "";

    public String view_Staff() throws SQLException {
        DB_connection dc = new DB_connection();
        Connection connect = dc.connection();

        PreparedStatement stmt = connect.prepareStatement(
                "DELETE FROM schedule");
        stmt.executeUpdate();

        stmt = connect.prepareStatement(
                "insert into schedule(day, startTime, EndTime, courseID, courseCode, courseType, venueID, groupID, groupNumber, staffID, staffName, programmeCode, studyYear, cohort, sTime, eTime) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        for (int d = 0; d < 7; d++) {
            double a = 8;
            for (double t = 8; t < 20; t += 0.5) {

                double chk = t - a;

                if (chk == 0.5) {
                    sTime = Integer.toString((int) t) + ":30";
                    eTime = Integer.toString((int) t + 1) + ":00";
                    a = t + 0.5;
                } else {
                    sTime = Integer.toString((int) t) + ":00";
                    eTime = Integer.toString((int) t) + ":30";
                }

                stmt.setInt(1, d);
                stmt.setDouble(2, t);
                stmt.setDouble(3, t + 0.5);
                stmt.setString(4, "");
                stmt.setString(5, "");
                stmt.setString(6, "");
                stmt.setString(7, "");
                stmt.setString(8, "");
                stmt.setString(9, "");
                stmt.setString(10, "");
                stmt.setString(11, "");
                stmt.setString(12, "");
                stmt.setString(13, "");
                stmt.setString(14, "");
                stmt.setString(15, sTime);
                stmt.setString(16, eTime);
                stmt.executeUpdate();

            }
        }

        FacesContext fc = FacesContext.getCurrentInstance();
        Map<String, String> params = fc.getExternalContext().getRequestParameterMap();
        staffID = params.get("action");

        stmt = connect.prepareStatement("SELECT * FROM class WHERE staffID = '" + staffID + "'");
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Class cd = new Class();
            cd.setCourseID(rs.getString("courseID"));
            cd.setVenueID(rs.getString("venueID"));
            cd.setGroupID(rs.getString("groupID"));
            cd.setStaffID(rs.getString("staffID"));
            cd.setDay(rs.getInt("day"));
            cd.setStartTime(rs.getDouble("startTime"));
            cd.setEndTime(rs.getDouble("endTime"));

            stmt = connect.prepareStatement("SELECT * FROM staff WHERE staffID = '" + staffID + "'");
            ResultSet rs1 = stmt.executeQuery();
            while (rs1.next()) {
                Staff sd = new Staff();
                sd.setStaffName(rs1.getString("staffName"));

                stmt = connect.prepareStatement("SELECT * FROM tutorial_group WHERE groupID = '" + cd.getGroupID() + "'");
                ResultSet rs2 = stmt.executeQuery();
                while (rs2.next()) {
                    TutorialGroup tg = new TutorialGroup();
                    tg.setStudyYear(rs2.getInt("studyYear"));
                    tg.setGroupNumber(rs2.getInt("groupNumber"));
                    tg.setProgrammeID(rs2.getString("programmeID"));
                    tg.setCohortID(rs2.getString("cohortID"));

                    stmt = connect.prepareStatement("SELECT * FROM Programme WHERE programmeID = '" + tg.getProgrammeID() + "'");
                    ResultSet rs3 = stmt.executeQuery();
                    while (rs3.next()) {
                        Programme pd = new Programme();
                        pd.setProgrammeCode(rs3.getString("programmeCode"));

                        stmt = connect.prepareStatement("SELECT * FROM CourseType WHERE courseID = '" + cd.getCourseID() + "'");
                        ResultSet rs4 = stmt.executeQuery();
                        while (rs4.next()) {
                            CourseType ct = new CourseType();
                            ct.setCourseCode(rs4.getString("courseCode"));
                            ct.setCourseType(rs4.getString("courseType"));

                            stmt = connect.prepareStatement("SELECT * FROM cohort WHERE cohortID = '" + tg.getCohortID() + "'");
                            ResultSet rs5 = stmt.executeQuery();
                            while (rs5.next()) {
                                tg.setYear(rs5.getString("years"));
                                tg.setMonth(rs5.getString("month"));

                                double st = cd.getStartTime();
                                double et = cd.getEndTime();
                                for (double i = 8; i <= 20; i += 0.5) {
                                    if (st <= i) {
                                        if (i < et) {
                                            stmt = connect.prepareStatement("SELECT * FROM schedule WHERE startTime = " + i);
                                            ResultSet rs6 = stmt.executeQuery();
                                            while (rs6.next()) {
                                                scheduleDetail sc = new scheduleDetail();
                                                sc.setsTime(rs6.getString("sTime"));
                                                sc.seteTime(rs6.getString("eTime"));

                                                stmt = connect.prepareStatement(
                                                        "update schedule SET endTime=?, courseID=?, courseCode=?, courseType=?, venueID=?, groupID=?, groupNumber=?, staffID=?, staffName=?, programmeCode=?, studyYear=?, cohort=?, sTime=?, eTime=? WHERE day=? AND startTime=?");

                                                if (ct.getCourseType().equals("L")) {
                                                    stmt.setInt(15, cd.getDay());
                                                    stmt.setDouble(16, i);
                                                    stmt.setDouble(1, i + 0.5);
                                                    stmt.setString(2, cd.getCourseID());
                                                    stmt.setString(3, ct.getCourseCode());
                                                    stmt.setString(4, ct.getCourseType());
                                                    stmt.setString(5, cd.getVenueID());
                                                    stmt.setString(6, cd.getGroupID());
                                                    stmt.setString(7, "");
                                                    stmt.setString(8, cd.getStaffID());
                                                    stmt.setString(9, sd.getStaffName());
                                                    stmt.setString(10, pd.getProgrammeCode());
                                                    stmt.setInt(11, tg.getStudyYear());
                                                    stmt.setString(12, tg.getYear() + tg.getMonth());
                                                    stmt.setString(13, sc.getsTime());
                                                    stmt.setString(14, sc.geteTime());
                                                } else {
                                                    stmt.setInt(15, cd.getDay());
                                                    stmt.setDouble(16, i);
                                                    stmt.setDouble(1, i + 0.5);
                                                    stmt.setString(2, cd.getCourseID());
                                                    stmt.setString(3, ct.getCourseCode());
                                                    stmt.setString(4, ct.getCourseType());
                                                    stmt.setString(5, cd.getVenueID());
                                                    stmt.setString(6, cd.getGroupID());
                                                    stmt.setInt(7, tg.getGroupNumber());
                                                    stmt.setString(8, cd.getStaffID());
                                                    stmt.setString(9, sd.getStaffName());
                                                    stmt.setString(10, pd.getProgrammeCode());
                                                    stmt.setInt(11, tg.getStudyYear());
                                                    stmt.setString(12, tg.getYear() + tg.getMonth());
                                                    stmt.setString(13, sc.getsTime());
                                                    stmt.setString(14, sc.geteTime());
                                                }
                                                stmt.executeUpdate();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return "/LecTimetable.xhtml?faces-redirect=true";
    }

    String groupID;

    public String view_Group() throws SQLException {
        DB_connection dc = new DB_connection();
        Connection connect = dc.connection();

        PreparedStatement stmt = connect.prepareStatement(
                "DELETE FROM schedule");
        stmt.executeUpdate();

        stmt = connect.prepareStatement(
                "insert into schedule(day, startTime, EndTime, courseID, courseCode, courseType, venueID, groupID, groupNumber, staffID, staffName, programmeCode, studyYear, cohort, sTime, eTime) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        for (int d = 0; d < 7; d++) {
            double a = 8;
            for (double t = 8; t < 20; t += 0.5) {

                double chk = t - a;

                if (chk == 0.5) {
                    sTime = Integer.toString((int) t) + ":30";
                    eTime = Integer.toString((int) t + 1) + ":00";
                    a = t + 0.5;
                } else {
                    sTime = Integer.toString((int) t) + ":00";
                    eTime = Integer.toString((int) t) + ":30";
                }

                stmt.setInt(1, d);
                stmt.setDouble(2, t);
                stmt.setDouble(3, t + 0.5);
                stmt.setString(4, "");
                stmt.setString(5, "");
                stmt.setString(6, "");
                stmt.setString(7, "");
                stmt.setString(8, "");
                stmt.setString(9, "");
                stmt.setString(10, "");
                stmt.setString(11, "");
                stmt.setString(12, "");
                stmt.setString(13, "");
                stmt.setString(14, "");
                stmt.setString(15, sTime);
                stmt.setString(16, eTime);
                stmt.executeUpdate();

            }
        }

        FacesContext fc = FacesContext.getCurrentInstance();
        Map<String, String> params = fc.getExternalContext().getRequestParameterMap();
        groupID = params.get("action");

        stmt = connect.prepareStatement("SELECT * FROM class WHERE groupID = '" + groupID + "'");
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Class cd = new Class();
            cd.setCourseID(rs.getString("courseID"));
            cd.setVenueID(rs.getString("venueID"));
            cd.setGroupID(rs.getString("groupID"));
            cd.setStaffID(rs.getString("staffID"));
            cd.setDay(rs.getInt("day"));
            cd.setStartTime(rs.getDouble("startTime"));
            cd.setEndTime(rs.getDouble("endTime"));

            stmt = connect.prepareStatement("SELECT * FROM staff WHERE staffID = '" + cd.getStaffID() + "'");
            ResultSet rs1 = stmt.executeQuery();
            while (rs1.next()) {
                Staff sd = new Staff();
                sd.setStaffName(rs1.getString("staffName"));

                stmt = connect.prepareStatement("SELECT * FROM tutorial_group WHERE groupID = '" + groupID + "'");
                ResultSet rs2 = stmt.executeQuery();
                while (rs2.next()) {
                    TutorialGroup tg = new TutorialGroup();
                    tg.setStudyYear(rs2.getInt("studyYear"));
                    tg.setGroupNumber(rs2.getInt("groupNumber"));
                    tg.setProgrammeID(rs2.getString("programmeID"));
                    tg.setCohortID(rs2.getString("cohortID"));

                    stmt = connect.prepareStatement("SELECT * FROM Programme WHERE programmeID = '" + tg.getProgrammeID() + "'");
                    ResultSet rs3 = stmt.executeQuery();
                    while (rs3.next()) {
                        Programme pd = new Programme();
                        pd.setProgrammeCode(rs3.getString("programmeCode"));

                        stmt = connect.prepareStatement("SELECT * FROM courseType WHERE courseID = '" + cd.getCourseID() + "'");
                        ResultSet rs4 = stmt.executeQuery();
                        while (rs4.next()) {
                            CourseType ct = new CourseType();
                            ct.setCourseCode(rs4.getString("courseCode"));
                            ct.setCourseType(rs4.getString("courseType"));

                            stmt = connect.prepareStatement("SELECT * FROM cohort WHERE cohortID = '" + tg.getCohortID() + "'");
                            ResultSet rs5 = stmt.executeQuery();
                            while (rs5.next()) {
                                tg.setYear(rs5.getString("years"));
                                tg.setMonth(rs5.getString("month"));

                                double st = cd.getStartTime();
                                double et = cd.getEndTime();
                                for (double i = 8; i <= 20; i += 0.5) {
                                    if (st <= i) {
                                        if (i < et) {
                                            stmt = connect.prepareStatement("SELECT * FROM schedule WHERE startTime = " + i);
                                            ResultSet rs6 = stmt.executeQuery();
                                            while (rs6.next()) {
                                                scheduleDetail sc = new scheduleDetail();
                                                sc.setsTime(rs6.getString("sTime"));
                                                sc.seteTime(rs6.getString("eTime"));

                                                stmt = connect.prepareStatement(
                                                        "update schedule SET endTime=?, courseID=?, courseCode=?, courseType=?, venueID=?, groupID=?, groupNumber=?, staffID=?, staffName=?, programmeCode=?, studyYear=?, cohort=?, sTime=?, eTime=? WHERE day=? AND startTime=?");

                                                stmt.setInt(15, cd.getDay());
                                                stmt.setDouble(16, i);
                                                stmt.setDouble(1, i + 0.5);
                                                stmt.setString(2, cd.getCourseID());
                                                stmt.setString(3, ct.getCourseCode());
                                                stmt.setString(4, ct.getCourseType());
                                                stmt.setString(5, cd.getVenueID());
                                                stmt.setString(6, cd.getGroupID());
                                                stmt.setInt(7, tg.getGroupNumber());
                                                stmt.setString(8, cd.getStaffID());
                                                stmt.setString(9, sd.getStaffName());
                                                stmt.setString(10, pd.getProgrammeCode());
                                                stmt.setInt(11, tg.getStudyYear());
                                                stmt.setString(12, tg.getYear() + tg.getMonth());
                                                stmt.setString(13, sc.getsTime());
                                                stmt.setString(14, sc.geteTime());

                                                stmt.executeUpdate();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return "/StdTimetable.xhtml?faces-redirect=true";
    }

    String venueID;

    public String view_Venue() throws SQLException {
        DB_connection dc = new DB_connection();
        Connection connect = dc.connection();

        PreparedStatement stmt = connect.prepareStatement(
                "DELETE FROM schedule");
        stmt.executeUpdate();

        stmt = connect.prepareStatement(
                "insert into schedule(day, startTime, EndTime, courseID, courseCode, courseType, venueID, groupID, groupNumber, staffID, staffName, programmeCode, studyYear, cohort, sTime, eTime) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        for (int d = 0; d < 7; d++) {
            double a = 8;
            for (double t = 8; t < 20; t += 0.5) {

                double chk = t - a;

                if (chk == 0.5) {
                    sTime = Integer.toString((int) t) + ":30";
                    eTime = Integer.toString((int) t + 1) + ":00";
                    a = t + 0.5;
                } else {
                    sTime = Integer.toString((int) t) + ":00";
                    eTime = Integer.toString((int) t) + ":30";
                }

                stmt.setInt(1, d);
                stmt.setDouble(2, t);
                stmt.setDouble(3, t + 0.5);
                stmt.setString(4, "");
                stmt.setString(5, "");
                stmt.setString(6, "");
                stmt.setString(7, "");
                stmt.setString(8, "");
                stmt.setString(9, "");
                stmt.setString(10, "");
                stmt.setString(11, "");
                stmt.setString(12, "");
                stmt.setString(13, "");
                stmt.setString(14, "");
                stmt.setString(15, sTime);
                stmt.setString(16, eTime);
                stmt.executeUpdate();

            }
        }

        FacesContext fc = FacesContext.getCurrentInstance();
        Map<String, String> params = fc.getExternalContext().getRequestParameterMap();
        venueID = params.get("action");

        stmt = connect.prepareStatement("SELECT * FROM class WHERE venueID = '" + venueID + "'");
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Class cd = new Class();
            cd.setCourseID(rs.getString("courseID"));
            cd.setVenueID(rs.getString("venueID"));
            cd.setGroupID(rs.getString("groupID"));
            cd.setStaffID(rs.getString("staffID"));
            cd.setDay(rs.getInt("day"));
            cd.setStartTime(rs.getDouble("startTime"));
            cd.setEndTime(rs.getDouble("endTime"));

            stmt = connect.prepareStatement("SELECT * FROM staff WHERE staffID = '" + cd.getStaffID() + "'");
            ResultSet rs1 = stmt.executeQuery();
            while (rs1.next()) {
                Staff sd = new Staff();
                sd.setStaffName(rs1.getString("staffName"));

                stmt = connect.prepareStatement("SELECT * FROM tutorial_group WHERE groupID = '" + cd.getGroupID() + "'");
                ResultSet rs2 = stmt.executeQuery();
                while (rs2.next()) {
                    TutorialGroup tg = new TutorialGroup();
                    tg.setStudyYear(rs2.getInt("studyYear"));
                    tg.setGroupNumber(rs2.getInt("groupNumber"));
                    tg.setProgrammeID(rs2.getString("programmeID"));
                    tg.setCohortID(rs2.getString("cohortID"));

                    stmt = connect.prepareStatement("SELECT * FROM Programme WHERE programmeID = '" + tg.getProgrammeID() + "'");
                    ResultSet rs3 = stmt.executeQuery();
                    while (rs3.next()) {
                        Programme pd = new Programme();
                        pd.setProgrammeCode(rs3.getString("programmeCode"));

                        stmt = connect.prepareStatement("SELECT * FROM CourseType WHERE courseID = '" + cd.getCourseID() + "'");
                        ResultSet rs4 = stmt.executeQuery();
                        while (rs4.next()) {
                            CourseType ct = new CourseType();
                            ct.setCourseCode(rs4.getString("courseCode"));
                            ct.setCourseType(rs4.getString("courseType"));

                            stmt = connect.prepareStatement("SELECT * FROM cohort WHERE cohortID = '" + tg.getCohortID() + "'");
                            ResultSet rs5 = stmt.executeQuery();
                            while (rs5.next()) {
                                tg.setYear(rs5.getString("years"));
                                tg.setMonth(rs5.getString("month"));

                                double st = cd.getStartTime();
                                double et = cd.getEndTime();
                                for (double i = 8; i <= 20; i += 0.5) {
                                    if (st <= i) {
                                        if (i < et) {
                                            stmt = connect.prepareStatement("SELECT * FROM schedule WHERE startTime = " + i);
                                            ResultSet rs6 = stmt.executeQuery();
                                            while (rs6.next()) {
                                                scheduleDetail sc = new scheduleDetail();
                                                sc.setsTime(rs6.getString("sTime"));
                                                sc.seteTime(rs6.getString("eTime"));

                                                stmt = connect.prepareStatement(
                                                        "update schedule SET endTime=?, courseID=?, courseCode=?, courseType=?, venueID=?, groupID=?, groupNumber=?, staffID=?, staffName=?, programmeCode=?, studyYear=?, cohort=?, sTime=?, eTime=? WHERE day=? AND startTime=?");

                                                if (ct.getCourseType().equals("L")) {
                                                    stmt.setInt(15, cd.getDay());
                                                    stmt.setDouble(16, i);
                                                    stmt.setDouble(1, i + 0.5);
                                                    stmt.setString(2, cd.getCourseID());
                                                    stmt.setString(3, ct.getCourseCode());
                                                    stmt.setString(4, ct.getCourseType());
                                                    stmt.setString(5, cd.getVenueID());
                                                    stmt.setString(6, cd.getGroupID());
                                                    stmt.setString(7, "");
                                                    stmt.setString(8, cd.getStaffID());
                                                    stmt.setString(9, sd.getStaffName());
                                                    stmt.setString(10, pd.getProgrammeCode());
                                                    stmt.setInt(11, tg.getStudyYear());
                                                    stmt.setString(12, tg.getYear() + tg.getMonth());
                                                    stmt.setString(13, sc.getsTime());
                                                    stmt.setString(14, sc.geteTime());
                                                } else {
                                                    stmt.setInt(15, cd.getDay());
                                                    stmt.setDouble(16, i);
                                                    stmt.setDouble(1, i + 0.5);
                                                    stmt.setString(2, cd.getCourseID());
                                                    stmt.setString(3, ct.getCourseCode());
                                                    stmt.setString(4, ct.getCourseType());
                                                    stmt.setString(5, cd.getVenueID());
                                                    stmt.setString(6, cd.getGroupID());
                                                    stmt.setInt(7, tg.getGroupNumber());
                                                    stmt.setString(8, cd.getStaffID());
                                                    stmt.setString(9, sd.getStaffName());
                                                    stmt.setString(10, pd.getProgrammeCode());
                                                    stmt.setInt(11, tg.getStudyYear());
                                                    stmt.setString(12, tg.getYear() + tg.getMonth());
                                                    stmt.setString(13, sc.getsTime());
                                                    stmt.setString(14, sc.geteTime());
                                                }
                                                stmt.executeUpdate();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return "/VenueTimetable.xhtml?faces-redirect=true";
    }

    public List<scheduleDetail> getStaff() throws ClassNotFoundException, SQLException {

        DB_connection dc = new DB_connection();
        Connection connect = dc.connection();

        List<scheduleDetail> schedule = new ArrayList<scheduleDetail>();

        PreparedStatement pstmt = connect.prepareStatement("SELECT staffName, cohort FROM schedule WHERE staffID = '" + staffID + "' GROUP BY staffName, cohort");
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            scheduleDetail sd = new scheduleDetail();
            sd.setStaffName(rs.getString("staffName"));
            sd.setCohort(rs.getString("cohort"));
            schedule.add(sd);
        }

        // close resources
        rs.close();
        pstmt.close();
        connect.close();

        return schedule;
    }

    public List<scheduleDetail> getGroup() throws ClassNotFoundException, SQLException {

        DB_connection dc = new DB_connection();
        Connection connect = dc.connection();

        List<scheduleDetail> schedule = new ArrayList<scheduleDetail>();

        PreparedStatement pstmt = connect.prepareStatement("SELECT programmeCode, studyYear, cohort, groupNumber FROM schedule WHERE groupID = '" + groupID + "' GROUP BY  programmeCode, studyYear, cohort, groupNumber");
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            scheduleDetail sd = new scheduleDetail();
            sd.setGroupNumber(rs.getString("groupNumber"));
            sd.setStudyYear(rs.getString("studyYear"));
            sd.setProgrammeCode(rs.getString("programmeCode"));
            sd.setGroupNumber(rs.getString("groupNumber"));
            sd.setCohort(rs.getString("cohort"));
            schedule.add(sd);
        }

        // close resources
        rs.close();
        pstmt.close();
        connect.close();

        return schedule;
    }

    public List<scheduleDetail> getVenue() throws ClassNotFoundException, SQLException {

        DB_connection dc = new DB_connection();
        Connection connect = dc.connection();

        List<scheduleDetail> schedule = new ArrayList<scheduleDetail>();

        PreparedStatement pstmt = connect.prepareStatement("SELECT venueID, cohort FROM schedule WHERE venueID = '" + venueID + "' GROUP BY venueID, cohort");
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            scheduleDetail sd = new scheduleDetail();
            sd.setVenueID(rs.getString("venueID"));
            sd.setCohort(rs.getString("cohort"));
            schedule.add(sd);
        }

        // close resources
        rs.close();
        pstmt.close();
        connect.close();

        return schedule;
    }
}
