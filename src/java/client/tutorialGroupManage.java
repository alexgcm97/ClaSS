/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import da.TutorialGroupDA;
import domain.TutorialGroup;
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
public class tutorialGroupManage {
    public TutorialGroupDA tgDA = new TutorialGroupDA();
    public TutorialGroup tg  = new TutorialGroup();

    public TutorialGroup getTg() {
        return tg;
    }

    public void setTg(TutorialGroup tg) {
        this.tg = tg;
    }

    public TutorialGroupDA getTgDA() {
        return tgDA;
    }

    public void setTgDA(TutorialGroupDA tgDA) {
        this.tgDA = tgDA;
    }

 
    
   
    
    public void GroupInsert() throws SQLException, IOException {
       
        tgDA.insertTutorialGroup(tg);
        FacesContext.getCurrentInstance().getExternalContext().redirect("selectGroups.xhtml");
    }
       public void retrieveGroup(String groupID) throws SQLException, IOException {
        tg = tgDA.get(groupID);
        FacesContext.getCurrentInstance().getExternalContext().redirect("editGroup.xhtml");

    }
    public void updateGroup() throws SQLException, IOException {
        tgDA.updateTutorialGroup(tg);
        FacesContext.getCurrentInstance().getExternalContext().redirect("selectGroups.xhtml");
    }
    public void deleteGroup(String groupID) throws SQLException, IOException {
        tgDA.deleteGroup(groupID);
        FacesContext.getCurrentInstance().getExternalContext().redirect("selectGroups.xhtml");
    }


}
