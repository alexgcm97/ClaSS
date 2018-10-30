/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import da.StaffDA;
import domain.Staff;
import java.io.IOException;
import java.io.Serializable;
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
public class staffManage implements Serializable {

    public StaffDA sda = new StaffDA();

    public Staff s = new Staff();

    public Staff getS() {
        return s;
    }

    public void setS(Staff s) {
        this.s = s;
    }

    public void goTonew() throws IOException {
       
        s.setBlockStart(0);
        FacesContext.getCurrentInstance().getExternalContext().redirect("newStaff.xhtml");

    }

    public void staffInsert() throws SQLException, IOException {
        sda.insertStaff(s);
        FacesContext.getCurrentInstance().getExternalContext().redirect("selectStaff.xhtml");
    }

    public void retrieveStaff(String staffID) throws SQLException, IOException {
        s = sda.get(staffID);
        FacesContext.getCurrentInstance().getExternalContext().redirect("editStaff.xhtml");

    }

    public void updateStaff() throws SQLException, IOException {
        sda.updateStaff(s);
        FacesContext.getCurrentInstance().getExternalContext().redirect("step4Staff.xhtml");
    }

    public void deleteStaff(String staffID) throws SQLException, IOException {
        sda.deleteStaff(staffID);
        FacesContext.getCurrentInstance().getExternalContext().redirect("selectStaff.xhtml");
    }

}
