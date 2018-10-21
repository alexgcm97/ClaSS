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

    private String staffID, staffName, courseCodeList, tutGroupList;
    private int blockDay;
    private double blockStart, blockDuration;
    private ArrayList<Class> classList;

    public Staff() {

    }

    public Staff(String staffID, String staffName, String courseCodeList, String tutGroupList) {
        this.staffID = staffID;
        this.staffName = staffName;
        this.blockDay = 99;
        this.blockStart = 0;
        this.blockDuration = 0;
        this.courseCodeList = courseCodeList;
        this.tutGroupList = tutGroupList;
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

    public String getTutGroupList() {
        return tutGroupList;
    }

    public void setTutGroupList(String tutGroupList) {
        this.tutGroupList = tutGroupList;
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
