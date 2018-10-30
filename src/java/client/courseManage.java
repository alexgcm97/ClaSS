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
            ct.setCourseType("T");
            ct.setCourseDuration(Double.toString(tutHours));
            ct.setCourseID(cda.getMaxID());
            cda.insertCourseType(ct);
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

    public void updateCourseType(String courseCode) throws SQLException, IOException{
            cda.updateCourse(c);
            List<CourseType> ctList = new ArrayList<>();
            ctList= cda.getCourseType(courseCode);
            for(int i=0;i<ctList.size();i++){
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
            if (ctList.get(i).getCourseType().equals("L")) {
                lecHours = Double.parseDouble(ctList.get(i).getCourseDuration());
            }
            if (ctList.get(i).getCourseType().equals("T")) {
                tutHours = Double.parseDouble(ctList.get(i).getCourseDuration());
            }
            if (ctList.get(i).getCourseType().equals("P")) {
                pracHours = Double.parseDouble(ctList.get(i).getCourseDuration());
            }
        }

        FacesContext.getCurrentInstance().getExternalContext().redirect("editCourse.xhtml");
    }

    public void deleteVenue(String courseCode) throws SQLException, IOException {
        cda.deleteCourse(courseCode);
        FacesContext.getCurrentInstance().getExternalContext().redirect("selectCourse.xhtml");
    }
}
