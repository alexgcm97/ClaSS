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
import javax.faces.bean.RequestScoped;

@ManagedBean
@RequestScoped
public class DisplayTimetable {

    private ArrayList<scheduleDetail> timeList, monList, tuesList, wedList, thursList, friList, satList, sunList;

    // Get Monday schedule to display
    public void intializeSchedule() throws ClassNotFoundException, SQLException {
        monList = new ArrayList();
        tuesList = new ArrayList();
        wedList = new ArrayList();
        thursList = new ArrayList();
        friList = new ArrayList();
        satList = new ArrayList();
        sunList = new ArrayList();

        DB_connection dc = new DB_connection();
        Connection connect = dc.connection();

        PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM schedule");
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            scheduleDetail sch = new scheduleDetail();
            sch.setDay(rs.getInt("day"));
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

            switch (sch.getDay()) {
                case 0:
                    this.sunList.add(sch);
                    break;
                case 1:
                    this.monList.add(sch);
                    break;
                case 2:
                    this.tuesList.add(sch);
                    break;
                case 3:
                    this.wedList.add(sch);
                    break;
                case 4:
                    this.thursList.add(sch);
                    break;
                case 5:
                    this.friList.add(sch);
                    break;
                case 6:
                    this.satList.add(sch);
                    break;
            }
        }
        rs.close();
        pstmt.close();
        connect.close();
    }

    public ArrayList<scheduleDetail> getMonList() {
        return monList;
    }

    public ArrayList<scheduleDetail> getTuesList() {
        return tuesList;
    }

    public ArrayList<scheduleDetail> getWedList() {
        return wedList;
    }

    public ArrayList<scheduleDetail> getThursList() {
        return thursList;
    }

    public ArrayList<scheduleDetail> getFriList() {
        return friList;
    }

    public ArrayList<scheduleDetail> getSatList() {
        return satList;
    }

    public ArrayList<scheduleDetail> getSunList() {
        return sunList;
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
