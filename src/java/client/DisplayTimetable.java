//@author Kok Teck Siong
//This page is to get the schedule data follow by the day
package client;

import da.DBConnection;
import domain.scheduleDetail;
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

    private ArrayList<scheduleDetail> monList, tuesList, wedList, thursList, friList, satList, sunList;
    private Connection connect;

    // Get Monday schedule to display
    public void intializeSchedule() throws ClassNotFoundException, SQLException {
        monList = new ArrayList();
        tuesList = new ArrayList();
        wedList = new ArrayList();
        thursList = new ArrayList();
        friList = new ArrayList();
        satList = new ArrayList();
        sunList = new ArrayList();

        connect = DBConnection.getConnection();

        PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM schedule ORDER BY day, startTime ASC");
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            scheduleDetail sch = new scheduleDetail();
            sch.setDay(rs.getInt("day"));
            sch.setCourseID(rs.getString("courseID"));
            sch.setStartTime(rs.getDouble("startTime"));
            sch.setEndTime(rs.getDouble("endTime"));
            sch.setCourseCode(rs.getString("courseCode"));
            if (!rs.getString("courseType").equals("")) {
                sch.setCourseType("(" + rs.getString("courseType") + ")");
            }
            sch.setVenueID(rs.getString("venueID"));
            sch.setProgrammeCode(rs.getString("programmeCode"));
            sch.setGroupID(rs.getString("groupID"));
            if (!rs.getString("groupNumber").equals("")) {
                sch.setGroupNumber("G" + rs.getString("groupNumber"));
            }
            sch.setStudyYear(rs.getString("studyYear"));
            sch.setStaffName(rs.getString("staffName"));
            sch.setCohort(rs.getString("cohort"));
            sch.setsTime(rs.getString("sTime"));
            sch.seteTime(rs.getString("eTime"));

            switch (sch.getDay()) {
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
                case 7:
                    this.sunList.add(sch);
                    break;
            }
        }
        rs.close();
        pstmt.close();
        DBConnection.close(connect);;
    }

    public void mergeClass(ArrayList<scheduleDetail> schList) {
        int nextIndex;
        for (int startIndex = 0; startIndex < schList.size(); startIndex++) {
            //get schedule detail from index 0
            scheduleDetail startSlot = schList.get(startIndex);
            //if the column is not empty
            if (!startSlot.getCourseID().equals("")) {
                //go to next index
                nextIndex = startIndex + 1;
                //get schedule detail from next index
                scheduleDetail nextSlot = schList.get(nextIndex);
                //if the index is not over the total index && the next index is not empty && first slot schedule course detail is same with the next slot && first slot schedule group detail is same with the next slot
                while (nextIndex < schList.size() && !nextSlot.getCourseID().equals("") && startSlot.getCourseID().equals(nextSlot.getCourseID()) && startSlot.getGroupID().equals(nextSlot.getGroupID())) {
                    //8-9 ; 9-10 => 8-10
                    startSlot.setEndTime(nextSlot.getEndTime());
                    //colspan+1
                    startSlot.increaseColspan();
                    //delete extra slot (8-9;9-10 = 2, now become 1 because of merge)
                    schList.remove(nextSlot);
                    //if the index is not over the total index
                    if (nextIndex < schList.size()) {
                        //original third index now become second slot of schedule detail
                        nextSlot = schList.get(nextIndex);
                    }
                }
            }
        }

    }

    //pass array list to merge method parameter
    public ArrayList<scheduleDetail> getMonList() {
        mergeClass(monList);
        return monList;
    }

    public ArrayList<scheduleDetail> getTuesList() {
        mergeClass(tuesList);
        return tuesList;
    }

    public ArrayList<scheduleDetail> getWedList() {
        mergeClass(wedList);
        return wedList;
    }

    public ArrayList<scheduleDetail> getThursList() {
        mergeClass(thursList);
        return thursList;
    }

    public ArrayList<scheduleDetail> getFriList() {
        mergeClass(friList);
        return friList;
    }

    public ArrayList<scheduleDetail> getSatList() {
        mergeClass(satList);
        return satList;
    }

    public ArrayList<scheduleDetail> getSunList() {
        mergeClass(sunList);
        return sunList;
    }

// Get courseCode to display
    public List<scheduleDetail> getCourseDetail() throws ClassNotFoundException, SQLException {
        connect = DBConnection.getConnection();

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
        DBConnection.close(connect);;

        return schedule;
    }
}
