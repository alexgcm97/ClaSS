/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Teck Siong
 */
@ManagedBean
@SessionScoped
public class ProgrammeCohort {

    String cohortID, entryYear, programmeCode, programmeName, intakeYear, courseCodeList;
    int studyYear;

    public ProgrammeCohort(String cohortID, String entryYear, String programmeCode, String programmeName, String intakeYear, int studyYear, String courseCodeList) {
        this.cohortID = cohortID;
        this.entryYear = entryYear;
        this.programmeName = programmeName;
        this.programmeCode = programmeCode;
        this.intakeYear = intakeYear;
        this.courseCodeList = courseCodeList;
        this.studyYear = studyYear;
    }

    public ProgrammeCohort() {
    }

    public String getCohortID() {
        return cohortID;
    }

    public void setCohortID(String cohortID) {
        this.cohortID = cohortID;
    }

    public String getEntryYear() {
        return entryYear;
    }

    public void setEntryYear(String entryYear) {
        this.entryYear = entryYear;
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

    public String getProgrammeName() {
        return programmeName;
    }

    public void setProgrammeName(String programmeName) {
        this.programmeName = programmeName;
    }
    
    

}
