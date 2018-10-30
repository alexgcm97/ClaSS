/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import da.ProgrammeDA;
import domain.Programme;
import java.io.IOException;
import java.sql.SQLException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author DEREK
 */
@ManagedBean
@SessionScoped
public class programmeManage {

    public ProgrammeDA pda = new ProgrammeDA();
    public Programme p = new Programme();

    public Programme getP() {
        return p;
    }

    public void setP(Programme p) {
        this.p = p;
    }
    
    

    public void goToNew() throws IOException{
        p.setProgrammeCode("");
        p.setProgrammeID("");
        p.setProgrammeName("");
    FacesContext.getCurrentInstance().getExternalContext().redirect("newProgramme.xhtml");
    }
    public void insertProgramme() throws SQLException, IOException {
        pda.insertProgramme(p);
        p.setProgrammeCode("");
        p.setProgrammeID("");
        p.setProgrammeName("");
        
        FacesContext.getCurrentInstance().getExternalContext().redirect("newProgramme.xhtml");
    }

    public void updateProgramme() throws SQLException, IOException {
        pda.updateProgramme(p);
        FacesContext.getCurrentInstance().getExternalContext().redirect("selectProgrammeCohort.xhtml");
    }

    public void retrieveProgramme(String programmeID) throws SQLException, IOException {
        p = pda.get(programmeID);
        FacesContext.getCurrentInstance().getExternalContext().redirect("editProgramme.xhtml");

    }
    public void deleteProgramme(String programmeID) throws SQLException, IOException {
        pda.deleteProgramme(programmeID);
        FacesContext.getCurrentInstance().getExternalContext().redirect("selectProgrammeCohort.xhtml");
    }

}
