///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package client;
//
//import da.ProgrammeDA;
//import domain.Cohort;
//import java.io.IOException;
//import java.sql.SQLException;
//import javax.faces.bean.ManagedBean;
//import javax.faces.bean.SessionScoped;
//import javax.faces.context.FacesContext;
//
///**
// *
// * @author DEREK
// */
//@ManagedBean
//@SessionScoped
//public class cohortManage {
//
//    public ProgrammeDA cda = new ProgrammeDA();
//    public Cohort c = new Cohort();
//
//    public ProgrammeDA getCda() {
//        return cda;
//    }
//
//    public void setCda(ProgrammeDA cda) {
//        this.cda = cda;
//    }
//
//    public Cohort getC() {
//        return c;
//    }
//
//    public void setC(Cohort c) {
//        this.c = c;
//    }
//    
//    public void goToNew() throws IOException {
//        c.setMonth("");
//        c.setYear("");
//        c.setStudyYear("");
//        FacesContext.getCurrentInstance().getExternalContext().redirect("newCohort.xhtml");
//    }
//
//    public void insertCohort() throws SQLException, IOException {
//        cda.insertCohort(c);
//        FacesContext.getCurrentInstance().getExternalContext().redirect("selectProgrammeCohort.xhtml");
//    }
//
//    public void updateCohort() throws SQLException, IOException {
//        cda.updateCohort(c);
//        FacesContext.getCurrentInstance().getExternalContext().redirect("selectProgrammeCohort.xhtml");
//    }
//
//    public void retrieveCohort(String cohortID) throws SQLException, IOException {
//        c = cda.get(cohortID);
//        FacesContext.getCurrentInstance().getExternalContext().redirect("editCohort.xhtml");
//
//    }
//
//    public void deleteCohort(String cohortID) throws SQLException, IOException {
//        cda.deleteCohort(cohortID);
//        FacesContext.getCurrentInstance().getExternalContext().redirect("selectProgrammeCohort.xhtml");
//    }
//
//    public void goBackSelectProgramme() throws IOException {
//        cda.reset();
//        FacesContext.getCurrentInstance().getExternalContext().redirect("selectProgrammeCohort.xhtml");
//
//    }
//
//}
