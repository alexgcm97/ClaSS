/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author DEREK
 */
@ManagedBean
@SessionScoped
public class Course implements Serializable {
    String courseCode, courseName;
    double creditHour;

    public Course() {
    }

    public Course(String courseCode, String courseName, double creditHour) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.creditHour = creditHour;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public double getCreditHour() {
        return creditHour;
    }

    public void setCreditHour(double creditHour) {
        this.creditHour = creditHour;
    }
    
    

   
    
}
