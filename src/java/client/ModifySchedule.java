//@author Kok Teck Siong
//This page is to get data and update the modification done by user

package client;

import da.DB_connection;
import domain.Class;
import domain.CourseType;
import domain.Venue;
import domain.scheduleDetail;
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
public class ModifySchedule {

    // get data from previous page (modifySchedule.xhtml)
    private int day;
    private double startTime, endTime;
    private String venueType, groupID, courseID, staffID, venueID, oldStaffID, oldVenueID;

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }

    public String getVenueType() {
        return venueType;
    }

    public void setVenueType(String venueType) {
        this.venueType = venueType;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getStaffID() {
        return staffID;
    }

    public void setStaffID(String staffID) {
        this.staffID = staffID;
    }

    public String getVenueID() {
        return venueID;
    }

    public void setVenueID(String venueID) {
        this.venueID = venueID;
    }

    public String getOldStaffID() {
        return oldStaffID;
    }

    public void setOldStaffID(String oldStaffID) {
        this.oldStaffID = oldStaffID;
    }

    public String getOldVenueID() {
        return oldVenueID;
    }

    public void setOldVenueID(String oldVenueID) {
        this.oldVenueID = oldVenueID;
    }

    //Display the selection start time and end time
    public List<scheduleDetail> getTime() throws ClassNotFoundException, SQLException {

        DB_connection dc = new DB_connection();
        Connection connect = dc.connection();

        List<scheduleDetail> scheduleDetail = new ArrayList<scheduleDetail>();
        PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM schedule WHERE day = 1");
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            scheduleDetail sd = new scheduleDetail();
            sd.setsTime(rs.getString("sTime"));
            sd.seteTime(rs.getString("eTime"));
            sd.setStartTime(rs.getDouble("startTime"));
            sd.setEndTime(rs.getDouble("endTime"));

            scheduleDetail.add(sd);
        }
        rs.close();
        pstmt.close();
        connect.close();

        return scheduleDetail;
    }

    //Check the available venue and show in drop down list
    public List<Venue> getAvaVenue() throws ClassNotFoundException, SQLException {

        DB_connection dc = new DB_connection();
        Connection connect = dc.connection();

        List<Venue> venue = new ArrayList<Venue>();
        List<Venue> venueNew = new ArrayList<Venue>();
        PreparedStatement pstmt = connect.prepareStatement("SELECT venueID, venueType FROM venue WHERE venueType='" + venueType + "'");
        ResultSet rs2 = pstmt.executeQuery();
        while (rs2.next()) {
            Venue vn = new Venue();
            vn.setVenueID(rs2.getString("venueID"));
            vn.setVenueType(rs2.getString("venueType"));

            venue.add(vn);
        }

        pstmt = connect.prepareStatement("SELECT venueID FROM class WHERE day=" + day + " AND ((startTime>=" + startTime + " AND startTime<" + endTime + ") OR (endTime>=" + startTime + " AND endTime<" + endTime + "))");
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            do {
                Class cl = new Class();
                cl.setVenueID(rs.getString("venueID"));
                pstmt = connect.prepareStatement("SELECT venueID, venueType FROM venue WHERE venueID='" + cl.getVenueID() + "'");
                ResultSet rs1 = pstmt.executeQuery();
                while (rs1.next()) {
                    Venue vn = new Venue();
                    vn.setVenueID(rs1.getString("venueID"));
                    vn.setVenueType(rs1.getString("venueType"));

                    venueNew.add(vn);
                }
            } while (rs.next());
        }

        for (int i = 0; i < venueNew.size(); i++) {
            Venue v1 = venueNew.get(i);
            for (int a = 0; a < venue.size(); a++) {
                Venue v2 = venue.get(a);
                if (v1.getVenueID().equals(v2.getVenueID())) {
                    venue.remove(a);
                }
            }
        }
        rs.close();
        pstmt.close();
        connect.close();

        return venue;
    }

    //get the previous venueID and show in drop down list
    public List<Venue> getOldVenue() throws ClassNotFoundException, SQLException {

        DB_connection dc = new DB_connection();
        Connection connect = dc.connection();

        List<Venue> venue = new ArrayList<Venue>();
        PreparedStatement pstmt = connect.prepareStatement("SELECT venueID, venueType FROM venue WHERE venueType!='Hall' ORDER BY venueID");
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

    //get the previous couseID and show in drop down list
    public List<CourseType> getOldCourse() throws ClassNotFoundException, SQLException {

        DB_connection dc = new DB_connection();
        Connection connect = dc.connection();

        List<CourseType> cType = new ArrayList<CourseType>();
        PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM coursetype WHERE courseType!='L' ORDER BY courseCode");
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

    //Get the alert message
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

    //Update the modification
    public String updateModify() throws SQLException {
        String stfID = "", grpID = "", venID = "";
        DB_connection dc = new DB_connection();
        Connection connect = dc.connection();

        //Check validation
        PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM class WHERE staffID='" + staffID + "' AND day=" + day + " AND ((startTime>=" + startTime + " AND startTime<" + endTime + ") OR (endTime>=" + startTime + " AND endTime<" + endTime + "))");
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            this.message = true;
        } else {
            stfID = staffID;
        }

        pstmt = connect.prepareStatement("SELECT * FROM class WHERE groupID='" + groupID + "' AND day=" + day + " AND ((startTime>=" + startTime + " AND startTime<" + endTime + ") OR (endTime>=" + startTime + " AND endTime<" + endTime + "))");
        ResultSet rs2 = pstmt.executeQuery();

        if (rs2.next()) {
            this.message = true;
        } else {
            grpID = groupID;
        }

        pstmt = connect.prepareStatement("SELECT * FROM class WHERE venueID='" + venueID + "' AND day=" + day + " AND ((startTime>=" + startTime + " AND startTime<" + endTime + ") OR (endTime>=" + startTime + " AND endTime<" + endTime + "))");
        ResultSet rs3 = pstmt.executeQuery();

        if (rs3.next()) {
            this.message = true;
        } else {
            venID = venueID;
        }

        if (!stfID.equals("") && !grpID.equals("") && !venID.equals("")) {

            pstmt = connect.prepareStatement("DELETE FROM class WHERE courseID='" + courseID + "' AND venueID='" + oldVenueID + "' AND groupID='" + grpID + "' AND staffID='" + oldStaffID + "'");
            pstmt.executeUpdate();

            pstmt = connect.prepareStatement("INSERT INTO class (courseID, venueID, groupID, staffID, day, startTime, endTime) VALUES (?,?,?,?,?,?,?)");
            pstmt.setString(1, courseID);
            pstmt.setString(2, venID);
            pstmt.setString(3, grpID);
            pstmt.setString(4, stfID);
            pstmt.setInt(5, day);
            pstmt.setDouble(6, startTime);
            pstmt.setDouble(7, endTime);
            pstmt.executeUpdate();
            this.success = true;
            this.message = false;
        } else {
            this.message = true;
            this.success = false;
        }
        return "ViewTimetable.xhtml?faces-redirect=true";
    }

    //Call by ModifySchedule to check the available venue
    public String forward() {
        return "setConstraints.xhtml?faces-redirect=true";
    }
    
    //Call by the ViewTimetable.xhtml (back)
    public String back() {
        this.message = false;
        this.success = false;
        return "ViewTimetable.xhtml?faces-redirect=true";
    }
}