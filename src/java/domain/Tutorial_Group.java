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

public class Tutorial_Group {

    private String groupID, programmeID, cohortID;
    private int studyYear, groupNumber, size;

    public Tutorial_Group(String groupID, int studyYear, int groupNumber, int size, String programmeID, String cohortID) {
        this.groupID = groupID;
        this.programmeID = programmeID;
        this.cohortID = cohortID;
        this.studyYear = studyYear;
        this.groupNumber = groupNumber;
        this.size = size;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getProgrammeID() {
        return programmeID;
    }

    public void setProgrammeID(String programmeID) {
        this.programmeID = programmeID;
    }

    public String getCohortID() {
        return cohortID;
    }

    public void setCohortID(String cohortID) {
        this.cohortID = cohortID;
    }

    public int getStudyYear() {
        return studyYear;
    }

    public void setStudyYear(int studyYear) {
        this.studyYear = studyYear;
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

}