/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import da.CourseDA;
import da.ProgrammeCohortDA;
import domain.Course;
import domain.ProgrammeCohort;
import java.io.IOException;
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
public class programmeCohortManage {

    public ProgrammeCohortDA pcda = new ProgrammeCohortDA();
    public ProgrammeCohort pc = new ProgrammeCohort();
    private CourseDA cda = new CourseDA();
    private List<String> courseCodeList = new ArrayList();
    private String[] courseCodeStr;

    public ProgrammeCohortDA getPcda() {
        return pcda;
    }

    public void setPcda(ProgrammeCohortDA pcda) {
        this.pcda = pcda;
    }

    public ProgrammeCohort getPc() {
        return pc;
    }

    public void setPc(ProgrammeCohort pc) {
        this.pc = pc;
    }

    public List<String> getCourseCodeList() throws SQLException {
        List<Course> courseList = cda.getAllRecords();
        for (Course c : courseList) {
            this.courseCodeList.add(c.getCourseCode());
        }
        return this.courseCodeList;
    }

    public void setCourseCodeList(List<String> courseCodeList) {
        this.courseCodeList = courseCodeList;
    }

    public String[] getCourseCodeStr() {
        return courseCodeStr;
    }

    public void setCourseCodeStr(String[] courseCodeStr) {
        this.courseCodeStr = courseCodeStr;
    }

    public void goToNew() throws IOException {
        pc.setCourseCodeList("");
        pc.setEntryYear("");
        pc.setCourseCodeList("");
        pc.setIntakeYear("");
        pc.setStudyYear(1);
        pc.setCourseCodeList("");
        FacesContext.getCurrentInstance().getExternalContext().redirect("newCohort.xhtml");
    }

    public void insertProgrammeCohort() throws SQLException, IOException {
        String tempStr = "";
        if (courseCodeStr != null) {
            for (String s : courseCodeStr) {
                if (tempStr.length() == 0) {
                    tempStr = s;
                } else {
                    tempStr += "," + s;
                }
            }
        }
        pc.setCourseCodeList(tempStr);
        pcda.insertProgrammeCohort(pc);
        FacesContext.getCurrentInstance().getExternalContext().redirect("selectProgrammeCohort.xhtml");
    }

    public void updateProgrammeCohort() throws SQLException, IOException {
        String tempStr = "-";
        if (courseCodeStr != null  && courseCodeStr.length > 0) {
            tempStr = "";
            for (String s : courseCodeStr) {
                if (tempStr.length() == 0) {
                    tempStr = s;
                } else {
                    tempStr += "," + s;
                }
            }
        }
        pc.setCourseCodeList(tempStr);
        pcda.updateProgrammeCohort(pc);
        FacesContext.getCurrentInstance().getExternalContext().redirect("selectProgrammeCohort.xhtml");
    }

    public void retrieveProgrammeCohort(String cohortID) throws SQLException, IOException {
        pc = pcda.get(cohortID);
        courseCodeStr = pc.getCourseCodeList().split(",");
        FacesContext.getCurrentInstance().getExternalContext().redirect("editCohort.xhtml");

    }

    public void deleteProgrammeCohort(String cohortID) throws SQLException, IOException {
        pcda.deleteProgrammeCohort(cohortID);
        FacesContext.getCurrentInstance().getExternalContext().redirect("selectProgrammeCohort.xhtml");
    }

    public void goBackSelectProgramme() throws IOException {
        pcda.reset();
        FacesContext.getCurrentInstance().getExternalContext().redirect("selectProgrammeCohort.xhtml");

    }

}
