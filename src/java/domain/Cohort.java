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
public class Cohort {

    String cohortID;
    String year, month, studyYear;

    public Cohort(String cohortID, String year, String month, String studyYear) {
        this.cohortID = cohortID;
        this.year = year;
        this.month = month;
        this.studyYear = studyYear;
    }

    public Cohort() {
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

    public String getStudyYear() {
        return studyYear;
    }

    public void setStudyYear(String studyYear) {
        this.studyYear = studyYear;
    }

    
   

   
    
    
    
}
