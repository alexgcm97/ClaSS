/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.util.ArrayList;

/**
 *
 * @author Alex
 */
public class Staff {

    private String staffID, staffName;
    private int blockDay;
    private double blockStart, blockDuration;
    private ArrayList<Class> classList;
    private ArrayList<String> courseCodeList, tutGroupList, lecGroupList, pracGroupList;
    private String courseCodeListS, tutGroupListS, lecGroupListS, pracGroupListS;

    public Staff() {

    }

    public Staff(String staffID, String staffName, int blockDay, double blockStart, double blockDuration, String courseCodeListS, String tutGroupListS, String lecGroupListS, String pracGroupListS) {
        this.staffID = staffID;
        this.staffName = staffName;
        this.blockDay = blockDay;
        this.blockStart = blockStart;
        this.blockDuration = blockDuration;
        this.courseCodeListS = courseCodeListS;
        this.tutGroupListS = tutGroupListS;
        this.lecGroupListS = lecGroupListS;
        this.pracGroupListS = pracGroupListS;
    }

    public Staff(String staffID, String staffName) {
        this.staffID = staffID;
        this.staffName = staffName;
        this.blockDay = 99;
        this.blockStart = 0;
        this.blockDuration = 0;
        this.courseCodeList = new ArrayList();
        this.tutGroupList = new ArrayList();
        this.lecGroupList = new ArrayList();
        this.pracGroupList = new ArrayList();
        this.classList = new ArrayList();
    }

    public String getCourseCodeListS() {
        return courseCodeListS;
    }

    public void setCourseCodeListS(String courseCodeListS) {
        this.courseCodeListS = courseCodeListS;
    }

    public String getTutGroupListS() {
        return tutGroupListS;
    }

    public void setTutGroupListS(String tutGroupListS) {
        this.tutGroupListS = tutGroupListS;
    }

    public String getLecGroupListS() {
        return lecGroupListS;
    }

    public void setLecGroupListS(String lecGroupListS) {
        this.lecGroupListS = lecGroupListS;
    }

    public String getPracGroupListS() {
        return pracGroupListS;
    }

    public void setPracGroupListS(String pracGroupListS) {
        this.pracGroupListS = pracGroupListS;
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

    public ArrayList<String> getCourseCodeList() {
        return courseCodeList;
    }

    public void setCourseCodeList(ArrayList<String> courseCodeList) {
        this.courseCodeList = courseCodeList;
    }

    public void addCourseCodeToList(String courseCode) {
        this.courseCodeList.add(courseCode);
    }

    public ArrayList<String> getTutGroupList() {
        return tutGroupList;
    }

    public void setTutGroupList(ArrayList<String> tutGroupList) {
        this.tutGroupList = tutGroupList;
    }

    public void addTutGroupToList(String tutGroup) {
        this.tutGroupList.add(tutGroup);
    }

    public String searchTutGroupList(String courseCode) {
        String str = "";
        for (String tutGroupStr : this.tutGroupList) {
            if (tutGroupStr.contains(courseCode)) {
                str = tutGroupStr;
            }
        }
        return str;
    }

    public ArrayList<String> getLecGroupList() {
        return lecGroupList;
    }

    public void setLecGroupList(ArrayList<String> lecGroupList) {
        this.lecGroupList = lecGroupList;
    }

    public void addLecGroupToList(String lecGroup) {
        this.lecGroupList.add(lecGroup);
    }

    public ArrayList<String> getPracGroupList() {
        return pracGroupList;
    }

    public void setPracGroupList(ArrayList<String> pracGroupList) {
        this.pracGroupList = pracGroupList;
    }

    public void addPracGroupToList(String pracGroup) {
        this.pracGroupList.add(pracGroup);
    }

    public int getBlockDay() {
        return blockDay;
    }

    public void setBlockDay(int blockDay) {
        this.blockDay = blockDay;
    }

    public double getBlockStart() {
        return blockStart;
    }

    public void setBlockStart(double blockStart) {
        this.blockStart = blockStart;
    }

    public double getBlockDuration() {
        return blockDuration;
    }

    public void setBlockDuration(double blockDuration) {
        this.blockDuration = blockDuration;
    }

    public ArrayList<Class> getClassList() {
        return classList;
    }

    public void setClassList(ArrayList<Class> classList) {
        this.classList = classList;
    }
}
