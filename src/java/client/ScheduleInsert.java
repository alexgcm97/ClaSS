//@author Kok Teck Siong
//This page is to get the class table data and insert to schedule table for display purpose
package client;

import da.DBConnection;
import domain.*;
import domain.Class;
import java.io.IOException;
import java.io.Serializable;
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@ManagedBean
@SessionScoped
public class ScheduleInsert implements Serializable {

    String staffID;
    String sTime = "", eTime = "";
    private Connection connect;

    private final String filePath = XMLPath.getXMLPath();

    //Generate staff timetable for view
    public String view_Staff() throws SQLException, ParserConfigurationException, SAXException, IOException {
        connect = DBConnection.getConnection();

        //Delete everytime generate new schedule for view
        PreparedStatement stmt = connect.prepareStatement("DELETE FROM schedule");
        stmt.executeUpdate();

        //Insert the day, startTime and endTime to schedule table
        stmt = connect.prepareStatement("insert into schedule(day, startTime, EndTime, courseID, courseCode, courseType, venueID, groupID, groupNumber, staffID, staffName, programmeCode, studyYear, cohort, sTime, eTime) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        //Get start time and end time from Configuration.xml
//        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//        Element e;

//        Document doc = dBuilder.parse(filePath + "Configuration.xml");
//        NodeList nodes = doc.getElementsByTagName("configuration");

//        e = (Element) nodes.item(0);

//        double studyStart = Double.parseDouble(e.getElementsByTagName("startTime").item(0).getTextContent());
//        double studyEnd = Double.parseDouble(e.getElementsByTagName("endTime").item(0).getTextContent());

        for (int d = 1; d <= 7; d++) {
            double a = 8;
            for (double t = 8; t < 20; t += 0.5) {

                double chk = t - a;

                //Time display format
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

        //Get data from the ViewTimetable.xhtml (view button)
        FacesContext fc = FacesContext.getCurrentInstance();
        Map<String, String> params = fc.getExternalContext().getRequestParameterMap();
        staffID = params.get("action");

        stmt = connect.prepareStatement("SELECT * FROM class WHERE staffID = ?");
        stmt.setString(1, staffID);
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

            stmt = connect.prepareStatement("SELECT * FROM staff WHERE staffID = ?");
            stmt.setString(1, staffID);
            ResultSet rs1 = stmt.executeQuery();
            while (rs1.next()) {
                Staff sd = new Staff();
                sd.setStaffName(rs1.getString("staffName"));
                String name[] = sd.getStaffName().split(" ");

                stmt = connect.prepareStatement("SELECT * FROM tutorialgroup WHERE groupID = ?");
                stmt.setString(1, cd.getGroupID());
                ResultSet rs2 = stmt.executeQuery();
                while (rs2.next()) {
                    TutorialGroup tg = new TutorialGroup();
                    tg.setGroupNumber(rs2.getInt("groupNumber"));
                    tg.setCohortID(rs2.getString("cohortID"));

                    stmt = connect.prepareStatement("SELECT * FROM ProgrammeCohort WHERE cohortID = ?");
                    stmt.setString(1, tg.getCohortID());
                    ResultSet rs3 = stmt.executeQuery();
                    while (rs3.next()) {
                        ProgrammeCohort pc = new ProgrammeCohort();
                        pc.setProgrammeCode(rs3.getString("programmeCode"));
                        pc.setStudyYear(rs3.getInt("studyYear"));
                        pc.setIntakeYear(rs3.getString("intakeYear"));
                        pc.setEntryYear(rs3.getString("entryYear"));

                        stmt = connect.prepareStatement("SELECT * FROM CourseType WHERE courseID = ?");
                        stmt.setString(1, cd.getCourseID());
                        ResultSet rs4 = stmt.executeQuery();
                        while (rs4.next()) {
                            CourseType ct = new CourseType();
                            ct.setCourseCode(rs4.getString("courseCode"));
                            ct.setCourseType(rs4.getString("courseType"));

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

                                            //Do not display the tutorial group number if detected courseType=L
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
                                                stmt.setString(10, pc.getProgrammeCode());
                                                stmt.setInt(11, pc.getStudyYear());
                                                stmt.setString(12, pc.getEntryYear());
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
                                                stmt.setString(10, pc.getProgrammeCode());
                                                stmt.setInt(11, pc.getStudyYear());
                                                stmt.setString(12, pc.getEntryYear());
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

        return "/LecTimetable.xhtml?faces-redirect=true";
    }

    String groupID;

    //Generate group timetable for view (step similar to View_Staff)
    public String view_Group() throws SQLException, ParserConfigurationException, SAXException, IOException {
        connect = DBConnection.getConnection();

        PreparedStatement stmt = connect.prepareStatement("DELETE FROM schedule");
        stmt.executeUpdate();

        stmt = connect.prepareStatement("insert into schedule(day, startTime, EndTime, courseID, courseCode, courseType, venueID, groupID, groupNumber, staffID, staffName, programmeCode, studyYear, cohort, sTime, eTime) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Element e;

        Document doc = dBuilder.parse(filePath + "Configuration.xml");
        NodeList nodes = doc.getElementsByTagName("configuration");

        e = (Element) nodes.item(0);

//        double studyStart = Double.parseDouble(e.getElementsByTagName("startTime").item(0).getTextContent());
//        double studyEnd = Double.parseDouble(e.getElementsByTagName("endTime").item(0).getTextContent());

        for (int d = 1; d <= 7; d++) {
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

        stmt = connect.prepareStatement("SELECT * FROM class WHERE groupID = ?");
        stmt.setString(1, groupID);
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

            stmt = connect.prepareStatement("SELECT * FROM staff WHERE staffID = ?");
            stmt.setString(1, cd.getStaffID());
            ResultSet rs1 = stmt.executeQuery();
            while (rs1.next()) {
                Staff sd = new Staff();
                sd.setStaffName(rs1.getString("staffName"));
                String name[] = sd.getStaffName().split(" ");
                if (name.length >= 2) {
                    name[0] += " ";
                    for (int i = 1; i < name.length; i++) {
                        name[i] = name[i].charAt(0) + "";
                        name[0] += name[i];
                    }
                }
                sd.setStaffName(name[0]);

                stmt = connect.prepareStatement("SELECT * FROM tutorialgroup WHERE groupID = ?");
                stmt.setString(1, groupID);
                ResultSet rs2 = stmt.executeQuery();
                while (rs2.next()) {
                    TutorialGroup tg = new TutorialGroup();
                    tg.setGroupNumber(rs2.getInt("groupNumber"));
                    tg.setCohortID(rs2.getString("cohortID"));

                    stmt = connect.prepareStatement("SELECT * FROM ProgrammeCohort WHERE cohortID = ?");
                    stmt.setString(1, tg.getCohortID());
                    ResultSet rs3 = stmt.executeQuery();
                    while (rs3.next()) {
                        ProgrammeCohort pc = new ProgrammeCohort();
                        pc.setProgrammeCode(rs3.getString("programmeCode"));
                        pc.setStudyYear(rs3.getInt("studyYear"));
                        pc.setIntakeYear(rs3.getString("intakeYear"));
                        pc.setEntryYear(rs3.getString("entryYear"));

                        stmt = connect.prepareStatement("SELECT * FROM courseType WHERE courseID = ?");
                        stmt.setString(1, cd.getCourseID());
                        ResultSet rs4 = stmt.executeQuery();
                        while (rs4.next()) {
                            CourseType ct = new CourseType();
                            ct.setCourseCode(rs4.getString("courseCode"));
                            ct.setCourseType(rs4.getString("courseType"));

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
                                            stmt.setString(10, pc.getProgrammeCode());
                                            stmt.setInt(11, pc.getStudyYear());
                                            stmt.setString(12, pc.getEntryYear());
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

        return "/StdTimetable.xhtml?faces-redirect=true";
    }

    String venueID;

    //Generate venue timetable for view (step similar to View_Staff)
    public String view_Venue() throws SQLException, ParserConfigurationException, SAXException, IOException {
        connect = DBConnection.getConnection();

        PreparedStatement stmt = connect.prepareStatement("DELETE FROM schedule");
        stmt.executeUpdate();

        stmt = connect.prepareStatement("insert into schedule(day, startTime, EndTime, courseID, courseCode, courseType, venueID, groupID, groupNumber, staffID, staffName, programmeCode, studyYear, cohort, sTime, eTime) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Element e;

        Document doc = dBuilder.parse(filePath + "Configuration.xml");
        NodeList nodes = doc.getElementsByTagName("configuration");

        e = (Element) nodes.item(0);

//        double studyStart = Double.parseDouble(e.getElementsByTagName("startTime").item(0).getTextContent());
//        double studyEnd = Double.parseDouble(e.getElementsByTagName("endTime").item(0).getTextContent());

        for (int d = 1; d <= 7; d++) {
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

        stmt = connect.prepareStatement("SELECT * FROM class WHERE venueID = ?");
        stmt.setString(1, venueID);
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

            stmt = connect.prepareStatement("SELECT * FROM staff WHERE staffID = ?");
            stmt.setString(1, cd.getStaffID());
            ResultSet rs1 = stmt.executeQuery();
            while (rs1.next()) {
                Staff sd = new Staff();
                sd.setStaffName(rs1.getString("staffName"));
                String name[] = sd.getStaffName().split(" ");
                if (name.length >= 2) {
                    name[0] += " ";
                    for (int i = 1; i < name.length; i++) {
                        name[i] = name[i].charAt(0) + "";
                        name[0] += name[i];
                    }
                }
                sd.setStaffName(name[0]);

                stmt = connect.prepareStatement("SELECT * FROM tutorialgroup WHERE groupID = ?");
                stmt.setString(1, cd.getGroupID());
                ResultSet rs2 = stmt.executeQuery();
                while (rs2.next()) {
                    TutorialGroup tg = new TutorialGroup();
                    tg.setGroupNumber(rs2.getInt("groupNumber"));
                    tg.setCohortID(rs2.getString("cohortID"));

                    stmt = connect.prepareStatement("SELECT * FROM ProgrammeCohort WHERE cohortID = ?");
                    stmt.setString(1, tg.getCohortID());
                    ResultSet rs3 = stmt.executeQuery();
                    while (rs3.next()) {
                        ProgrammeCohort pc = new ProgrammeCohort();
                        pc.setProgrammeCode(rs3.getString("programmeCode"));
                        pc.setStudyYear(rs3.getInt("studyYear"));
                        pc.setIntakeYear(rs3.getString("intakeYear"));
                        pc.setEntryYear(rs3.getString("entryYear"));

                        stmt = connect.prepareStatement("SELECT * FROM CourseType WHERE courseID = ?");
                        stmt.setString(1, cd.getCourseID());
                        ResultSet rs4 = stmt.executeQuery();
                        while (rs4.next()) {
                            CourseType ct = new CourseType();
                            ct.setCourseCode(rs4.getString("courseCode"));
                            ct.setCourseType(rs4.getString("courseType"));

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
                                                stmt.setString(10, pc.getProgrammeCode());
                                                stmt.setInt(11, pc.getStudyYear());
                                                stmt.setString(12, pc.getEntryYear());
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
                                                stmt.setString(10, pc.getProgrammeCode());
                                                stmt.setInt(11, pc.getStudyYear());
                                                stmt.setString(12, pc.getEntryYear());
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

        return "/VenueTimetable.xhtml?faces-redirect=true";
    }

    //Get staff timetable display title
    public List<scheduleDetail> getStaff() throws ClassNotFoundException, SQLException {
        connect = DBConnection.getConnection();

        List<scheduleDetail> schedule = new ArrayList<scheduleDetail>();

        PreparedStatement pstmt = connect.prepareStatement("SELECT staffName, cohort FROM schedule WHERE staffID = ? GROUP BY staffName, cohort");
        pstmt.setString(1, staffID);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            do {
                scheduleDetail sd = new scheduleDetail();
                if (schedule.isEmpty()) {
                    sd.setStaffName(rs.getString("staffName"));
                    sd.setCohort(rs.getString("cohort"));
                    schedule.add(sd);
                }
            } while (rs.next());
        } else {
            scheduleDetail sd = new scheduleDetail();
            sd.setCohort("No Classes Display");
            schedule.add(sd);
        }
        rs.close();
        pstmt.close();
        DBConnection.close(connect);;

        return schedule;
    }
    //Get group timetable display title

    public List<scheduleDetail> getGroup() throws ClassNotFoundException, SQLException {
        connect = DBConnection.getConnection();

        List<scheduleDetail> schedule = new ArrayList<scheduleDetail>();
        PreparedStatement pstmt = connect.prepareStatement("SELECT programmeCode, studyYear, cohort, groupNumber FROM schedule WHERE groupID = ? GROUP BY  programmeCode, studyYear, cohort, groupNumber");
        pstmt.setString(1, groupID);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            do {
                scheduleDetail sd = new scheduleDetail();
                sd.setGroupNumber(rs.getString("groupNumber"));
                sd.setStudyYear(rs.getString("studyYear"));
                sd.setProgrammeCode(rs.getString("programmeCode"));
                sd.setGroupNumber(rs.getString("groupNumber"));
                sd.setCohort(rs.getString("cohort"));
                schedule.add(sd);
            } while (rs.next());
        } else {
            scheduleDetail sd = new scheduleDetail();
            sd.setCohort("No Classes Display");
            schedule.add(sd);
        }
        rs.close();
        pstmt.close();
        DBConnection.close(connect);;

        return schedule;
    }

    //Get venue timetable display title
    public List<scheduleDetail> getVenue() throws ClassNotFoundException, SQLException {
        connect = DBConnection.getConnection();

        List<scheduleDetail> schedule = new ArrayList<scheduleDetail>();
        PreparedStatement pstmt = connect.prepareStatement("SELECT venueID, cohort FROM schedule WHERE venueID = ? GROUP BY venueID, cohort");
        pstmt.setString(1, venueID);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            do {
                scheduleDetail sd = new scheduleDetail();
                if (schedule.isEmpty()) {
                    sd.setVenueID(rs.getString("venueID"));
                    sd.setCohort(rs.getString("cohort"));
                    schedule.add(sd);
                }
            } while (rs.next());
        } else {
            scheduleDetail sd = new scheduleDetail();
            sd.setCohort("No Classes Display");
            schedule.add(sd);
        }
        rs.close();
        pstmt.close();
        DBConnection.close(connect);;

        return schedule;
    }
}
