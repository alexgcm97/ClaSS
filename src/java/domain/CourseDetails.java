/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

/**
 *
 * @author REPUBLIC
 */
public class CourseDetails {

    private String courseCode, courseName;
    double lecHours, tutHours, pracHours;


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

    public double getLecHours() {
        return lecHours;
    }

    public void setLecHours(double lecHours) {
        this.lecHours = lecHours;
    }

    public double getTutHours() {
        return tutHours;
    }

    public void setTutHours(double tutHours) {
        this.tutHours = tutHours;
    }

    public double getPracHours() {
        return pracHours;
    }

    public void setPracHours(double pracHours) {
        this.pracHours = pracHours;
    }

    public CourseDetails() {
    }

    public CourseDetails(String courseCode, String courseName, double lecHours, double tutHours, double pracHours) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.lecHours = lecHours;
        this.tutHours = tutHours;
        this.pracHours = pracHours;
    }

}
