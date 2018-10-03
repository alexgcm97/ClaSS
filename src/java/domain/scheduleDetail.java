/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

/**
 *
 * @author Teck Siong
 */
public class scheduleDetail {

    int day;
    double startTime;
    double endTime;
    String courseID;
    String courseCode;
    String courseType;
    String venueID;
    String groupID;
    String groupNumber;
    String staffID;
    String staffName;
    String programmeCode;
    String studyYear;
    String cohort;
    String courseName;
    String sTime;
    String eTime;

    public scheduleDetail() {
    }

    public scheduleDetail(int day, double startTime, double endTime, String courseID, String courseCode, String courseType, String venueID, String groupID, String groupNumber, String staffID, String staffName, String programmeCode, String studyYear, String cohort, String courseName, String sTime, String eTime) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.courseID = courseID;
        this.courseCode = courseCode;
        this.courseType = courseType;
        this.venueID = venueID;
        this.groupID = groupID;
        this.groupNumber = groupNumber;
        this.staffID = staffID;
        this.staffName = staffName;
        this.programmeCode = programmeCode;
        this.studyYear = studyYear;
        this.cohort = cohort;
        this.courseName = courseName;
        this.sTime = sTime;
        this.eTime = eTime;
    }

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

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public String getVenueID() {
        return venueID;
    }

    public void setVenueID(String venueID) {
        this.venueID = venueID;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }

    public String getStaffID() {
        return staffID;
    }

    public void setStaffID(String staffID) {
        this.staffID = staffID;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getProgrammeCode() {
        return programmeCode;
    }

    public void setProgrammeCode(String programmeCode) {
        this.programmeCode = programmeCode;
    }

    public String getStudyYear() {
        return studyYear;
    }

    public void setStudyYear(String studyYear) {
        this.studyYear = studyYear;
    }

    public String getCohort() {
        return cohort;
    }

    public void setCohort(String cohort) {
        this.cohort = cohort;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getsTime() {
        return sTime;
    }

    public void setsTime(String sTime) {
        this.sTime = sTime;
    }

    public String geteTime() {
        return eTime;
    }

    public void seteTime(String eTime) {
        this.eTime = eTime;
    }

}
