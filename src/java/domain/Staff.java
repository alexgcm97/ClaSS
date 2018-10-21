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

    private String staffID, staffName, courseCodeList;
    private int blockDay;
    private double blockStart, blockDuration;
    private ArrayList<Class> classList;
    private ArrayList<String> tutGroupList;

    public Staff() {

    }

    public Staff(String staffID, String staffName, String courseCodeList) {
        this.staffID = staffID;
        this.staffName = staffName;
        this.blockDay = 99;
        this.blockStart = 0;
        this.blockDuration = 0;
        this.courseCodeList = courseCodeList;
        this.tutGroupList = new ArrayList();
        this.classList = new ArrayList();
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

    public String getCourseCodeList() {
        return courseCodeList;
    }

    public void setCourseCodeList(String courseCodeList) {
        this.courseCodeList = courseCodeList;
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
