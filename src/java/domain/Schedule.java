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
public class Schedule {

    private String groupID, cohortID, courseCodeList;
    private int requiredNoOfClass;
    private ArrayList<Class> classList;

    public Schedule() {

    }

    public Schedule(String groupID, String cohortID, ArrayList<Class> classList, String courseCodeList) {
        this.groupID = groupID;
        this.cohortID = cohortID;
        this.classList = classList;
        this.courseCodeList = courseCodeList;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getCohortID() {
        return cohortID;
    }

    public void setCohortID(String cohortID) {
        this.cohortID = cohortID;
    }

    public ArrayList<Class> getClassList() {
        return classList;
    }

    public void setClassList(ArrayList<Class> classList) {
        this.classList = classList;
    }

    public void addClassToList(Class c) {
        this.classList.add(c);
    }

    public int getRequiredNoOfClass() {
        return requiredNoOfClass;
    }

    public void setRequiredNoOfClass(int requiredNoOfClass) {
        this.requiredNoOfClass = requiredNoOfClass;
    }

    public int getNoOfClass() {
        return this.classList.size();
    }

    public void resetClassList() {
        this.classList = new ArrayList();
    }

    public String getCourseCodeList() {
        return courseCodeList;
    }

    public void setCourseCodeList(String courseCodeList) {
        this.courseCodeList = courseCodeList;
    }

    public void moveRight(Class c, double moveDuration) {
        boolean isClash = false;
        for (Class d : this.classList) {
            if (c.getDay() == d.getDay() && !c.getCourseID().equals(d.getCourseID())) {
                double startTime1 = c.getStartTime() + moveDuration, endTime1 = c.getEndTime() + moveDuration, startTime2 = d.getStartTime(), endTime2 = d.getEndTime();
                if ((startTime1 >= startTime2 && startTime1 < endTime2) || (endTime1 > startTime2 && endTime1 <= endTime2) || (startTime2 >= startTime1 && startTime2 < endTime1) || (endTime2 > startTime1 && endTime2 <= endTime1)) {
                    isClash = true;
                }
            }
        }
        if (!isClash) {
            c.moveRight(moveDuration);
        }
    }

    public void moveLeft(Class c, double moveDuration) {
        boolean isClash = false;
        for (Class d : this.classList) {
            if (c.getDay() == d.getDay() && !c.getCourseID().equals(d.getCourseID())) {
                double startTime1 = c.getStartTime() - moveDuration, endTime1 = c.getEndTime() - moveDuration, startTime2 = d.getStartTime(), endTime2 = d.getEndTime();
                if ((startTime1 >= startTime2 && startTime1 < endTime2) || (endTime1 > startTime2 && endTime1 <= endTime2) || (startTime2 >= startTime1 && startTime2 < endTime1) || (endTime2 > startTime1 && endTime2 <= endTime1)) {
                    isClash = true;
                }
            }
        }
        if (!isClash) {
            c.moveLeft(moveDuration);
        }
    }
}
