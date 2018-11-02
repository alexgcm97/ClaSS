/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import da.CourseDA;
import da.CourseTypeDA;
import domain.Course;
import domain.CourseType;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author DEREK
 */
@ManagedBean
@SessionScoped
public class courseManage implements Serializable {

    public CourseDA cda = new CourseDA();
    public CourseTypeDA ctda = new CourseTypeDA();
    public Course c = new Course();
    public CourseType ct = new CourseType();
    private double lecHours, tutHours, pracHours;

    public CourseDA getCda() {
        return cda;
    }

    public void setCda(CourseDA cda) {
        this.cda = cda;
    }

    public CourseTypeDA getCtda() {
        return ctda;
    }

    public void setCtda(CourseTypeDA ctda) {
        this.ctda = ctda;
    }

    public CourseType getCt() {
        return ct;
    }

    public void setCt(CourseType ct) {
        this.ct = ct;
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

    public Course getC() {
        return c;
    }

    public void setC(Course c) {
        this.c = c;
    }

    public void InsertCourse(String cc) throws SQLException, IOException {
        cda.insertCourse(c);
        ct.setCourseCode(cc);
        if (lecHours > 0.0) {
            ct.setCourseType("L");
            ct.setCourseDuration(Double.toString(lecHours));
            ct.setCourseID(cda.getMaxID());
            cda.insertCourseType(ct);
        }

        if (tutHours > 0.0) {
            if (lecHours > 0.0) {
                ct.setCourseType("T");
                ct.setCourseDuration(Double.toString(tutHours));
                ct.setCourseID(cda.getMaxID());
                cda.insertCourseType(ct);
            } else {
                ct.setCourseType("B");
                ct.setCourseDuration(Double.toString(tutHours));
                ct.setCourseID(cda.getMaxID());
                cda.insertCourseType(ct);
            }
        }

        if (pracHours > 0.0) {
            ct.setCourseType("P");
            ct.setCourseDuration(Double.toString(lecHours));
            ct.setCourseID(cda.getMaxID());
            cda.insertCourseType(ct);
        }
        cda.setMessage(true);
        FacesContext.getCurrentInstance().getExternalContext().redirect("selectCourse.xhtml");
    }

    public void updateCourseType(String courseCode) throws SQLException, IOException {
        cda.updateCourse(c);
        List<CourseType> ctList = new ArrayList<>();
        ctList = cda.getCourseType(courseCode);
        for (int i = 0; i < ctList.size(); i++) {
            cda.deleteCourseType(ctList.get(i).getCourseID());
        }
        if (lecHours > 0.0) {
            ct.setCourseType("L");
            ct.setCourseDuration(Double.toString(lecHours));
            ct.setCourseID(cda.getMaxID());
            ct.setCourseCode(c.getCourseCode());
            cda.insertCourseType(ct);
        }

        if (tutHours > 0.0) {
            ct.setCourseType("T");
            ct.setCourseDuration(Double.toString(tutHours));
            ct.setCourseID(cda.getMaxID());
            ct.setCourseCode(c.getCourseCode());
            cda.insertCourseType(ct);
        }
        if (pracHours > 0.0) {
            ct.setCourseType("P");
            ct.setCourseDuration(Double.toString(pracHours));
            ct.setCourseID(cda.getMaxID());
            ct.setCourseCode(c.getCourseCode());
            cda.insertCourseType(ct);
        }
        FacesContext.getCurrentInstance().getExternalContext().redirect("selectCourse.xhtml");

    }

    public void retrieveCourse(String courseCode) throws SQLException, IOException {
        c = cda.getCourse(courseCode);
        List<CourseType> ctList = new ArrayList<>();
        ctList = cda.getCourseType(courseCode);
        System.out.println(ctList.size());
        for (int i = 0; i < ctList.size(); i++) {
            switch (ctList.get(i).getCourseType()) {
                case "L":
                    lecHours = Double.parseDouble(ctList.get(i).getCourseDuration());
                    break;
                case "P":
                    pracHours = Double.parseDouble(ctList.get(i).getCourseDuration());
                    break;
                default:
                    tutHours = Double.parseDouble(ctList.get(i).getCourseDuration());
                    break;
            }
        }

        FacesContext.getCurrentInstance().getExternalContext().redirect("editCourse.xhtml");
    }

    public void deleteCourse(String courseCode) throws SQLException, IOException {
        cda.deleteCourse(courseCode);
        cda.deleteCourseType(courseCode);
        FacesContext.getCurrentInstance().getExternalContext().redirect("selectCourse.xhtml");
    }

    public void backToCourse() throws SQLException, IOException {
        cda.reset();
        FacesContext.getCurrentInstance().getExternalContext().redirect("selectCourse.xhtml");
    }

    public void goBackMenu() throws IOException {
        cda.reset();
        FacesContext.getCurrentInstance().getExternalContext().redirect("EditInfo.xhtml");
    }

    public void goBackVenue() throws IOException {
        cda.reset();
        FacesContext.getCurrentInstance().getExternalContext().redirect("selectCourse.xhtml");
    }

    public void goToNew() throws IOException {
        c.setCourseCode("");
        c.setCourseName("");
        c.setCreditHour(0);
        pracHours = 0.0;
        lecHours = 0.0;
        tutHours = 0.0;

        FacesContext.getCurrentInstance().getExternalContext().redirect("newCourse.xhtml");
    }
}
