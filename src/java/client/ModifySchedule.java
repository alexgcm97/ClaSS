//@author Kok Teck Siong
//This page is to get data and update the modification done by user
package client;

import da.DBConnection;
import domain.Class;
import domain.CourseType;
import domain.Staff;
import domain.TutorialGroup;
import domain.Venue;
import domain.scheduleDetail;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped
public class ModifySchedule implements Serializable {

    // get data from previous page (modifySchedule.xhtml)
    private int day;
    private double startTime, endTime;
    private String venueType, groupID, courseID, staffID, venueID, oldStaffID, oldVenueID;
    private Connection connect;

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
        connect = DBConnection.getConnection();

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
        DBConnection.close(connect);;

        return scheduleDetail;
    }

    //Check the available venue and show in drop down list
    public List<Venue> getAvaVenue() throws ClassNotFoundException, SQLException {
        connect = DBConnection.getConnection();

        List<Venue> venue = new ArrayList<Venue>();
        List<Venue> venueNew = new ArrayList<Venue>();
        PreparedStatement pstmt = connect.prepareStatement("SELECT venueID, venueType FROM venue WHERE venueType=?");
        pstmt.setString(1, venueType);
        ResultSet rs2 = pstmt.executeQuery();
        while (rs2.next()) {
            Venue vn = new Venue();
            vn.setVenueID(rs2.getString("venueID"));
            vn.setVenueType(rs2.getString("venueType"));

            venue.add(vn);
        }

        pstmt = connect.prepareStatement("SELECT venueID FROM class WHERE day= ? AND ((startTime>= ? AND startTime< ?) OR (endTime>= ? AND endTime< ?))");
        pstmt.setInt(1, day);
        pstmt.setDouble(2, startTime);
        pstmt.setDouble(3, endTime);
        pstmt.setDouble(4, startTime);
        pstmt.setDouble(5, endTime);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            do {
                Class cl = new Class();
                cl.setVenueID(rs.getString("venueID"));
                pstmt = connect.prepareStatement("SELECT venueID, venueType FROM venue WHERE venueID= ?");
                pstmt.setString(1, cl.getVenueID());
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
        DBConnection.close(connect);;

        return venue;
    }

    //get the previous couseID and show in drop down list
    public List<CourseType> getOldCourse() throws ClassNotFoundException, SQLException {
        connect = DBConnection.getConnection();

        List<CourseType> cType = new ArrayList<CourseType>();

        CourseType ct = new CourseType();

        PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM Staff WHERE staffID = ?");
        pstmt.setString(1, oldStaffID);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            Staff stf = new Staff();
            stf.setCourseCodeListS(rs.getString("courseCodeList"));

            String stfList = stf.getCourseCodeListS();
            int chk = 0;

            if (stfList.contains("|")) {
                System.out.println("nfksgfksjdbfklasksbfkl");
                String[] splitF = stfList.split("\\|");
                List<String> FSplit = Arrays.asList(splitF);
                System.out.println("split | :" + FSplit);

                for (int al = 0; al < FSplit.size(); al++) {
                    chk = 0;
                    List<String> FSplit1 = new ArrayList<String>();
                    FSplit1.add(FSplit.get(al));

                    for (String stfList1 : FSplit1) {
                        if (stfList1.contains("-")) {
                            String[] splitFirst = stfList1.split("-");
                            List<String> firstSplit1 = Arrays.asList(splitFirst);
                            List<String> firstSplit = new ArrayList<String>();

                            if (firstSplit1.size() > 2) {
                                firstSplit.add(0, firstSplit1.get(0) + "-" + firstSplit1.get(1));
                                firstSplit.add(1, firstSplit1.get(2));
                            } else {
                                firstSplit.add(0, firstSplit1.get(0));
                                firstSplit.add(1, firstSplit1.get(1));
                            }

                            String a = firstSplit.get(0);
                            System.out.println("course=" + a);

                            for (String stfList2 : firstSplit) {

                                System.out.println("split - =" + stfList2);
                                if (stfList2.contains(",")) {

                                    String[] splitSec = stfList2.split(",");
                                    List<String> SecSplit = Arrays.asList(splitSec);
                                    System.out.println("split , =" + SecSplit);
                                    for (int i = 0; i < SecSplit.size(); i++) {
                                        System.out.println("size=" + SecSplit.size());
                                        if (SecSplit.get(i).equals("T")) {
                                            System.out.println("type=" + SecSplit.get(i));
                                            pstmt = connect.prepareStatement("SELECT * FROM courseType WHERE courseCode = ? AND courseType = ?");
                                            pstmt.setString(1, a);
                                            pstmt.setString(2, SecSplit.get(i));
                                            ResultSet rs1 = pstmt.executeQuery();

                                            while (rs1.next()) {
                                                CourseType ctt = new CourseType();
                                                ctt.setCourseID(rs1.getString("courseID"));

                                                System.out.println("course=" + ctt.getCourseID());

                                                pstmt = connect.prepareStatement("SELECT * FROM courseType WHERE courseID = ?");
                                                pstmt.setString(1, ctt.getCourseID());
                                                ResultSet rs2 = pstmt.executeQuery();

                                                while (rs2.next()) {
                                                    ctt.setCourseCode(rs2.getString("courseCode"));
                                                    ctt.setCourseType(rs2.getString("courseType"));

                                                    System.out.println("zzz" + ctt.getCourseCode());
                                                    System.out.println("zzz" + ctt.getCourseType());

                                                    cType.add(ctt);
                                                }
                                            }
                                        }

                                        if (SecSplit.get(i).equals("P")) {
                                            System.out.println("type=" + SecSplit.get(i));
                                            pstmt = connect.prepareStatement("SELECT * FROM courseType WHERE courseCode = ? AND courseType = ?");
                                            pstmt.setString(1, a);
                                            pstmt.setString(2, SecSplit.get(i));
                                            ResultSet rs1 = pstmt.executeQuery();

                                            while (rs1.next()) {
                                                CourseType ctp = new CourseType();

                                                ctp.setCourseID(rs1.getString("courseID"));

                                                System.out.println("course=" + ctp.getCourseID());

                                                pstmt = connect.prepareStatement("SELECT * FROM courseType WHERE courseID = ?");
                                                pstmt.setString(1, ctp.getCourseID());
                                                ResultSet rs2 = pstmt.executeQuery();

                                                while (rs2.next()) {
                                                    ctp.setCourseCode(rs2.getString("courseCode"));
                                                    ctp.setCourseType(rs2.getString("courseType"));

                                                    System.out.println("zzz" + ctp.getCourseCode());
                                                    System.out.println("zzz" + ctp.getCourseType());

                                                    cType.add(ctp);
                                                }
                                            }
                                        }

                                        if (SecSplit.get(i).equals("B")) {
                                            System.out.println("type=" + SecSplit.get(i));
                                            pstmt = connect.prepareStatement("SELECT * FROM courseType WHERE courseCode = ? AND courseType = ?");
                                            pstmt.setString(1, a);
                                            pstmt.setString(2, SecSplit.get(i));
                                            ResultSet rs1 = pstmt.executeQuery();

                                            while (rs1.next()) {
                                                CourseType ctb = new CourseType();

                                                ctb.setCourseID(rs1.getString("courseID"));

                                                System.out.println("course=" + ctb.getCourseID());

                                                pstmt = connect.prepareStatement("SELECT * FROM courseType WHERE courseID = ?");
                                                pstmt.setString(1, ctb.getCourseID());
                                                ResultSet rs2 = pstmt.executeQuery();

                                                while (rs2.next()) {
                                                    ctb.setCourseCode(rs2.getString("courseCode"));
                                                    ctb.setCourseType(rs2.getString("courseType"));

                                                    System.out.println("zzz" + ctb.getCourseCode());
                                                    System.out.println("zzz" + ctb.getCourseType());

                                                    cType.add(ctb);
                                                }
                                            }
                                        }
                                    }
                                } else {

                                    if (chk == 1) {
                                        for (int i = 0; i < firstSplit.size(); i++) {
                                            System.out.println("split:" + firstSplit);
                                            System.out.println("size:" + firstSplit.size());
                                            if (firstSplit.get(i).equals("T")) {
                                                System.out.println("type=" + firstSplit.get(i));
                                                pstmt = connect.prepareStatement("SELECT * FROM courseType WHERE courseCode = ? AND courseType = ?");
                                                pstmt.setString(1, a);
                                                pstmt.setString(2, "T");
                                                ResultSet rs3 = pstmt.executeQuery();

                                                while (rs3.next()) {
                                                    ct.setCourseID(rs3.getString("courseID"));
                                                    System.out.println("course=" + ct.getCourseID());

                                                    pstmt = connect.prepareStatement("SELECT * FROM courseType WHERE courseID = ?");
                                                    pstmt.setString(1, ct.getCourseID());
                                                    ResultSet rs4 = pstmt.executeQuery();

                                                    while (rs4.next()) {
                                                        ct.setCourseCode(rs4.getString("courseCode"));
                                                        ct.setCourseType(rs4.getString("courseType"));

                                                        System.out.println("sss" + ct.getCourseCode());
                                                        System.out.println("sss" + ct.getCourseType());

                                                        cType.add(ct);
                                                    }
                                                }
                                            } else if (firstSplit.get(i).equals("P")) {
                                                System.out.println("hlo");
                                                System.out.println("AAA" + a);
                                                System.out.println("aaa" + firstSplit.get(i));
                                                pstmt = connect.prepareStatement("SELECT * FROM courseType WHERE courseCode = ? AND courseType = ?");
                                                pstmt.setString(1, a);
                                                pstmt.setString(2, "P");
                                                ResultSet rs3 = pstmt.executeQuery();

                                                while (rs3.next()) {
                                                    ct.setCourseID(rs3.getString("courseID"));

                                                    pstmt = connect.prepareStatement("SELECT * FROM courseType WHERE courseID = ?");
                                                    pstmt.setString(1, ct.getCourseID());
                                                    ResultSet rs4 = pstmt.executeQuery();

                                                    while (rs4.next()) {
                                                        ct.setCourseCode(rs4.getString("courseCode"));
                                                        ct.setCourseType(rs4.getString("courseType"));

                                                        cType.add(ct);
                                                    }
                                                }
                                            } else if (firstSplit.get(i).equals("B")) {

                                                System.out.println("type=" + firstSplit.get(i));
                                                pstmt = connect.prepareStatement("SELECT * FROM courseType WHERE courseCode = ? AND courseType = ?");
                                                pstmt.setString(1, a);
                                                pstmt.setString(2, "B");
                                                ResultSet rs3 = pstmt.executeQuery();

                                                while (rs3.next()) {
                                                    ct.setCourseID(rs3.getString("courseID"));
                                                    System.out.println("course=" + ct.getCourseID());

                                                    pstmt = connect.prepareStatement("SELECT * FROM courseType WHERE courseID = ?");
                                                    pstmt.setString(1, ct.getCourseID());
                                                    ResultSet rs4 = pstmt.executeQuery();

                                                    while (rs4.next()) {
                                                        ct.setCourseCode(rs4.getString("courseCode"));
                                                        ct.setCourseType(rs4.getString("courseType"));

                                                        System.out.println("sss" + ct.getCourseCode());
                                                        System.out.println("sss" + ct.getCourseType());

                                                        cType.add(ct);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                chk = 1;
                            }
                        }
                    }
                }
            } else {
                if (stfList.contains("-")) {
                    String[] splitFirst = stfList.split("-");
                    List<String> firstSplit1 = Arrays.asList(splitFirst);
                    List<String> firstSplit = new ArrayList<String>();

                    if (firstSplit1.size() > 2) {
                        firstSplit.add(0, firstSplit1.get(0) + "-" + firstSplit1.get(1));
                        firstSplit.add(1, firstSplit1.get(2));
                    } else {
                        firstSplit.add(0, firstSplit1.get(0));
                        firstSplit.add(1, firstSplit1.get(1));
                    }

                    String a = firstSplit.get(0);
                    System.out.println("course=" + a);

                    for (String stfList2 : firstSplit) {

                        System.out.println("split - =" + stfList2);
                        if (stfList2.contains(",")) {

                            String[] splitSec = stfList2.split(",");
                            List<String> SecSplit = Arrays.asList(splitSec);
                            System.out.println("split , =" + SecSplit);
                            for (int i = 0; i < SecSplit.size(); i++) {
                                System.out.println("size=" + SecSplit.size());
                                if (SecSplit.get(i).equals("T")) {
                                    System.out.println("type=" + SecSplit.get(i));
                                    pstmt = connect.prepareStatement("SELECT * FROM courseType WHERE courseCode = ? AND courseType = ?");
                                    pstmt.setString(1, a);
                                    pstmt.setString(2, SecSplit.get(i));
                                    ResultSet rs1 = pstmt.executeQuery();

                                    while (rs1.next()) {
                                        CourseType ctt = new CourseType();
                                        ctt.setCourseID(rs1.getString("courseID"));

                                        System.out.println("course=" + ctt.getCourseID());

                                        pstmt = connect.prepareStatement("SELECT * FROM courseType WHERE courseID = ?");
                                        pstmt.setString(1, ctt.getCourseID());
                                        ResultSet rs2 = pstmt.executeQuery();

                                        while (rs2.next()) {
                                            ctt.setCourseCode(rs2.getString("courseCode"));
                                            ctt.setCourseType(rs2.getString("courseType"));

                                            System.out.println("zzz" + ctt.getCourseCode());
                                            System.out.println("zzz" + ctt.getCourseType());

                                            cType.add(ctt);
                                        }
                                    }
                                }

                                if (SecSplit.get(i).equals("P")) {
                                    System.out.println("type=" + SecSplit.get(i));
                                    pstmt = connect.prepareStatement("SELECT * FROM courseType WHERE courseCode = ? AND courseType = ?");
                                    pstmt.setString(1, a);
                                    pstmt.setString(2, SecSplit.get(i));
                                    ResultSet rs1 = pstmt.executeQuery();

                                    while (rs1.next()) {
                                        CourseType ctp = new CourseType();

                                        ctp.setCourseID(rs1.getString("courseID"));

                                        System.out.println("course=" + ctp.getCourseID());

                                        pstmt = connect.prepareStatement("SELECT * FROM courseType WHERE courseID = ?");
                                        pstmt.setString(1, ctp.getCourseID());
                                        ResultSet rs2 = pstmt.executeQuery();

                                        while (rs2.next()) {
                                            ctp.setCourseCode(rs2.getString("courseCode"));
                                            ctp.setCourseType(rs2.getString("courseType"));

                                            System.out.println("zzz" + ctp.getCourseCode());
                                            System.out.println("zzz" + ctp.getCourseType());

                                            cType.add(ctp);
                                        }
                                    }
                                }

                                if (SecSplit.get(i).equals("B")) {
                                    System.out.println("type=" + SecSplit.get(i));
                                    pstmt = connect.prepareStatement("SELECT * FROM courseType WHERE courseCode = ? AND courseType = ?");
                                    pstmt.setString(1, a);
                                    pstmt.setString(2, SecSplit.get(i));
                                    ResultSet rs1 = pstmt.executeQuery();

                                    while (rs1.next()) {
                                        CourseType ctb = new CourseType();
                                        
                                        ctb.setCourseID(rs1.getString("courseID"));

                                        System.out.println("course=" + ctb.getCourseID());

                                        pstmt = connect.prepareStatement("SELECT * FROM courseType WHERE courseID = ?");
                                        pstmt.setString(1, ctb.getCourseID());
                                        ResultSet rs2 = pstmt.executeQuery();

                                        while (rs2.next()) {
                                            ctb.setCourseCode(rs2.getString("courseCode"));
                                            ctb.setCourseType(rs2.getString("courseType"));

                                            System.out.println("zzz" + ctb.getCourseCode());
                                            System.out.println("zzz" + ctb.getCourseType());

                                            cType.add(ctb);
                                        }
                                    }
                                }
                            }
                        } else {

                            if (chk == 1) {
                                for (int i = 0; i < firstSplit.size(); i++) {
                                    System.out.println("split:" + firstSplit);
                                    System.out.println("size:" + firstSplit.size());
                                    if (firstSplit.get(i).equals("T")) {
                                        System.out.println("type=" + firstSplit.get(i));
                                        pstmt = connect.prepareStatement("SELECT * FROM courseType WHERE courseCode = ? AND courseType = ?");
                                        pstmt.setString(1, a);
                                        pstmt.setString(2, "T");
                                        ResultSet rs3 = pstmt.executeQuery();

                                        while (rs3.next()) {
                                            ct.setCourseID(rs3.getString("courseID"));
                                            System.out.println("course=" + ct.getCourseID());

                                            pstmt = connect.prepareStatement("SELECT * FROM courseType WHERE courseID = ?");
                                            pstmt.setString(1, ct.getCourseID());
                                            ResultSet rs4 = pstmt.executeQuery();

                                            while (rs4.next()) {
                                                ct.setCourseCode(rs4.getString("courseCode"));
                                                ct.setCourseType(rs4.getString("courseType"));

                                                System.out.println("sss" + ct.getCourseCode());
                                                System.out.println("sss" + ct.getCourseType());

                                                cType.add(ct);
                                            }
                                        }
                                    } else if (firstSplit.get(i).equals("P")) {
                                        System.out.println("hlo");
                                        System.out.println("AAA" + a);
                                        System.out.println("aaa" + firstSplit.get(i));
                                        pstmt = connect.prepareStatement("SELECT * FROM courseType WHERE courseCode = ? AND courseType = ?");
                                        pstmt.setString(1, a);
                                        pstmt.setString(2, "P");
                                        ResultSet rs3 = pstmt.executeQuery();

                                        while (rs3.next()) {
                                            ct.setCourseID(rs3.getString("courseID"));

                                            pstmt = connect.prepareStatement("SELECT * FROM courseType WHERE courseID = ?");
                                            pstmt.setString(1, ct.getCourseID());
                                            ResultSet rs4 = pstmt.executeQuery();

                                            while (rs4.next()) {
                                                ct.setCourseCode(rs4.getString("courseCode"));
                                                ct.setCourseType(rs4.getString("courseType"));

                                                cType.add(ct);
                                            }
                                        }
                                    } else if (firstSplit.get(i).equals("B")) {

                                        System.out.println("type=" + firstSplit.get(i));
                                        pstmt = connect.prepareStatement("SELECT * FROM courseType WHERE courseCode = ? AND courseType = ?");
                                        pstmt.setString(1, a);
                                        pstmt.setString(2, "B");
                                        ResultSet rs3 = pstmt.executeQuery();

                                        while (rs3.next()) {
                                            ct.setCourseID(rs3.getString("courseID"));
                                            System.out.println("course=" + ct.getCourseID());

                                            pstmt = connect.prepareStatement("SELECT * FROM courseType WHERE courseID = ?");
                                            pstmt.setString(1, ct.getCourseID());
                                            ResultSet rs4 = pstmt.executeQuery();

                                            while (rs4.next()) {
                                                ct.setCourseCode(rs4.getString("courseCode"));
                                                ct.setCourseType(rs4.getString("courseType"));

                                                System.out.println("sss" + ct.getCourseCode());
                                                System.out.println("sss" + ct.getCourseType());

                                                cType.add(ct);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        chk = 1;
                    }
                }
            }
        }

        rs.close();

        pstmt.close();

        DBConnection.close(connect);;

        return cType;
    }

    public List<TutorialGroup> getAllGroup() throws ClassNotFoundException, SQLException {
        connect = DBConnection.getConnection();

        List<TutorialGroup> group = new ArrayList<TutorialGroup>();
        PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM class WHERE staffID = ? AND courseID = ?");
        pstmt.setString(1, oldStaffID);
        pstmt.setString(2, courseID);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            TutorialGroup grp = new TutorialGroup();
            grp.setGroupID(rs.getString("groupID"));

            pstmt = connect.prepareStatement("SELECT * FROM tutorialGroup WHERE groupID = ? ORDER BY groupID");
            pstmt.setString(1, grp.getGroupID());
            ResultSet rs2 = pstmt.executeQuery();
            while (rs2.next()) {
                grp.setCohortID(rs2.getString("cohortID"));
                grp.setGroupNumber(rs2.getInt("groupNumber"));

                pstmt = connect.prepareStatement("SELECT * FROM programmeCohort WHERE cohortID = ?");
                pstmt.setString(1, grp.getCohortID());
                ResultSet rs3 = pstmt.executeQuery();
                while (rs3.next()) {
                    grp.setProgrammeCode(rs3.getString("programmeCode"));
                    grp.setStudyYear(rs3.getInt("studyYear"));
                    grp.setEntryYear(rs3.getString("entryYear"));
                }
            }
            group.add(grp);
        }
        rs.close();
        pstmt.close();
        DBConnection.close(connect);;

        return group;
    }

    //get the previous venueID and show in drop down list
    public List<Venue> getCurrentVenue() throws ClassNotFoundException, SQLException {
        connect = DBConnection.getConnection();

        Class cls = new Class();
        List<Venue> venue = new ArrayList<Venue>();

        PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM class WHERE staffID = ? AND courseID = ? AND groupID = ?");
        pstmt.setString(1, oldStaffID);
        pstmt.setString(2, courseID);
        pstmt.setString(3, groupID);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            cls.setVenueID(rs.getString("venueID"));

            pstmt = connect.prepareStatement("SELECT venueID, venueType FROM venue WHERE venueID = ? ORDER BY venueID");
            pstmt.setString(1, cls.getVenueID());
            ResultSet rs1 = pstmt.executeQuery();
            while (rs1.next()) {
                Venue vn = new Venue();
                vn.setVenueID(rs1.getString("venueID"));
                vn.setVenueType(rs1.getString("venueType"));

                venue.add(vn);
            }
        }
        rs.close();
        pstmt.close();
        DBConnection.close(connect);;

        return venue;
    }

    //Get the alert message
    boolean success, message;
    private String errorMssg;
    private int errorCode = 0;

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
    
    public String getErrorMssg() {
        return errorMssg;
    }

    public void setErrorMssg(String errorMssg) {
        this.errorMssg = errorMssg;
    }

    //Update the modification
    public String updateModify() throws SQLException, IOException {
        String stfID = "", grpID = "", venID = "";
        connect = DBConnection.getConnection();

        //Check validation
        PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM class WHERE staffID=?AND day=? AND ((startTime>=? AND startTime<?) OR (endTime>=? AND endTime<?)) OR ((?>=startTime AND ?<startTime) OR (?>=endTime AND ?<endTime))");
        pstmt.setString(1, staffID);
        pstmt.setInt(2, day);
        pstmt.setDouble(3, startTime);
        pstmt.setDouble(4, endTime);
        pstmt.setDouble(5, startTime);
        pstmt.setDouble(6, endTime);
        pstmt.setDouble(7, startTime);
        pstmt.setDouble(8, endTime);
        pstmt.setDouble(9, startTime);
        pstmt.setDouble(10, endTime);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            errorMssg = "Error! Staff time clash!!!";
            this.message = true;
            FacesContext.getCurrentInstance().getExternalContext().redirect("ViewTimetable.xhtml");
        } else {
            stfID = staffID;
        }

        pstmt = connect.prepareStatement("SELECT * FROM class WHERE groupID=?AND day=? AND ((startTime>=? AND startTime<?) OR (endTime>=? AND endTime<?)) OR ((?>=startTime AND ?<startTime) OR (?>=endTime AND ?<endTime))");
        pstmt.setString(1, groupID);
        pstmt.setInt(2, day);
        pstmt.setDouble(3, startTime);
        pstmt.setDouble(4, endTime);
        pstmt.setDouble(5, startTime);
        pstmt.setDouble(6, endTime);
        pstmt.setDouble(7, startTime);
        pstmt.setDouble(8, endTime);
        pstmt.setDouble(9, startTime);
        pstmt.setDouble(10, endTime);
        ResultSet rs2 = pstmt.executeQuery();

        if (rs2.next()) {
            errorMssg = "Error! Tutorial group time clash!!!";
            this.message = true;
            FacesContext.getCurrentInstance().getExternalContext().redirect("ViewTimetable.xhtml");
        } else {
            grpID = groupID;
        }

        pstmt = connect.prepareStatement("SELECT * FROM class WHERE venueID=?AND day=? AND ((startTime>=? AND startTime<?) OR (endTime>=? AND endTime<?)) OR ((?>=startTime AND ?<startTime) OR (?>=endTime AND ?<endTime))");
        pstmt.setString(1, venueID);
        pstmt.setInt(2, day);
        pstmt.setDouble(3, startTime);
        pstmt.setDouble(4, endTime);
        pstmt.setDouble(5, startTime);
        pstmt.setDouble(6, endTime);
        pstmt.setDouble(7, startTime);
        pstmt.setDouble(8, endTime);
        pstmt.setDouble(9, startTime);
        pstmt.setDouble(10, endTime);
        ResultSet rs3 = pstmt.executeQuery();

        if (rs3.next()) {
            errorMssg = "Error! Venue time clash!!!";
            this.message = true;
            FacesContext.getCurrentInstance().getExternalContext().redirect("ViewTimetable.xhtml");
        } else {
            venID = venueID;
        }

        if (!stfID.equals("") && !grpID.equals("") && !venID.equals("") && !venID.equals("null")) {

            pstmt = connect.prepareStatement("DELETE FROM class WHERE courseID= ? AND venueID= ? AND groupID= ? AND staffID= ?");
            pstmt.setString(1, courseID);
            pstmt.setString(2, oldVenueID);
            pstmt.setString(3, grpID);
            pstmt.setString(4, oldStaffID);

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
        }
        return "ViewTimetable.xhtml?faces-redirect=true";
    }

    //Call by ModifySchedule to check the available venue
    public void forward() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("modifySchedule.xhtml");
    }

    //Call by the ViewTimetable.xhtml (back)
    public String back() {
        this.message = false;
        this.success = false;
        return "ViewTimetable.xhtml?faces-redirect=true";
    }

    public void cancel() throws IOException {
        this.message = false;
        this.success = false;
        FacesContext.getCurrentInstance().getExternalContext().redirect("ViewTimetable.xhtml");
    }

    public String back1() {
        this.message = false;
        this.success = false;
        return "menu.xhtml?faces-redirect=true";
    }
}
