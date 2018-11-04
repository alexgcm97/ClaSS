/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Alex
 */
package domain;

import java.io.Serializable;

public class TutorialGroup implements Serializable {

    private String groupID, cohortID, programmeCode, intakeYear, courseCodeList, entryYear;
    private int studyYear, groupNumber, size;

    public TutorialGroup() {
    }

    public TutorialGroup(String groupID, String cohortID) {
        this.groupID = groupID;
        this.cohortID = cohortID;
    }

    public TutorialGroup(String groupID, int groupNumber, int size, String cohortID) {
        this.groupID = groupID;
        this.cohortID = cohortID;
        this.groupNumber = groupNumber;
        this.size = size;
    }

    public TutorialGroup(String groupID, int groupNumber, int size, String cohortID, String courseCodeList) {
        this.groupID = groupID;
        this.cohortID = cohortID;
        this.groupNumber = groupNumber;
        this.size = size;
        this.courseCodeList = courseCodeList;
    }

    public TutorialGroup(String groupID, int studyYear, int groupNumber, int size, String cohortID, String programmeCode, String month, String intakeYear) {
        this.groupID = groupID;
        this.cohortID = cohortID;
        this.groupNumber = groupNumber;
        this.studyYear = studyYear;
        this.size = size;
        this.programmeCode = programmeCode;
        this.intakeYear = intakeYear;
    }
    
    public TutorialGroup(String groupID, int studyYear, int groupNumber, String cohortID, String programmeCode, String intakeYear, String entryYear) {
        this.groupID = groupID;
        this.cohortID = cohortID;
        this.groupNumber = groupNumber;
        this.studyYear = studyYear;
        this.programmeCode = programmeCode;
        this.intakeYear = intakeYear;
        this.entryYear = entryYear;
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

    public int getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(int groupNumber) {
        this.groupNumber = groupNumber;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getProgrammeCode() {
        return programmeCode;
    }

    public void setProgrammeCode(String programmeCode) {
        this.programmeCode = programmeCode;
    }

    public String getIntakeYear() {
        return intakeYear;
    }

    public void setIntakeYear(String intakeYear) {
        this.intakeYear = intakeYear;
    }

    public String getCourseCodeList() {
        return courseCodeList;
    }

    public void setCourseCodeList(String courseCodeList) {
        this.courseCodeList = courseCodeList;
    }

    public int getStudyYear() {
        return studyYear;
    }

    public void setStudyYear(int studyYear) {
        this.studyYear = studyYear;
    }

    public String getEntryYear() {
        return entryYear;
    }

    public void setEntryYear(String entryYear) {
        this.entryYear = entryYear;
    }

}
