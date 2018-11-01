/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.io.Serializable;

/**
 *
 * @author Teck Siong
 */
public class Programme  implements Serializable {

    String programmeID;
    String programmeCode;
    String programmeName;
    String cohortID,year,month,entryYear;
    public Programme() {
    }

    public Programme(String programmeID, String programmeCode, String programmeName, String cohortID, String year, String month, String entryYear) {
        this.programmeID = programmeID;
        this.programmeCode = programmeCode;
        this.programmeName = programmeName;
        this.cohortID = cohortID;
        this.year = year;
        this.month = month;
        this.entryYear = entryYear;
    }

    public Programme(String programmeID, String programmeCode, String programmeName) {
        this.programmeID = programmeID;
        this.programmeCode = programmeCode;
        this.programmeName = programmeName;
    }

    public String getCohortID() {
        return cohortID;
    }

    public void setCohortID(String cohortID) {
        this.cohortID = cohortID;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getEntryYear() {
        return entryYear;
    }

    public void setEntryYear(String entryYear) {
        this.entryYear = entryYear;
    }

    public String getProgrammeID() {
        return programmeID;
    }

    public void setProgrammeID(String programmeID) {
        this.programmeID = programmeID;
    }

    public String getProgrammeCode() {
        return programmeCode;
    }

    public void setProgrammeCode(String programmeCode) {
        this.programmeCode = programmeCode;
    }

    public String getProgrammeName() {
        return programmeName;
    }

    public void setProgrammeName(String programmeName) {
        this.programmeName = programmeName;
    }

}
