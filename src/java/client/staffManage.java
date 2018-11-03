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
    
    private int blockStart = 0;
    
    public int getBlockStart() {
        return blockStart;
    }
    
    public void setBlockStart(int blockStart) {
        this.blockStart = blockStart;
    }
    
    public Staff getS() {
        return s;
    }
    
    public void setS(Staff s) {
        this.s = s;
    }
    
    public StaffDA getSda() {
        return sda;
    }
    
    public void setSda(StaffDA sda) {
        this.sda = sda;
    }
    
    public void goTonew() throws IOException {
        
        s.setBlockStart(0);
        FacesContext.getCurrentInstance().getExternalContext().redirect("newStaff.xhtml");
        
    }
    
    public void staffInsert() throws SQLException, IOException {
        s.setBlockStart(blockStart);
        sda.insertStaff(s);
        FacesContext.getCurrentInstance().getExternalContext().redirect("selectStaff.xhtml");
    }
    
    public void retrieveStaff(String staffID) throws SQLException, IOException {
        s = sda.get(staffID);
        blockStart = (int) s.getBlockStart();
        FacesContext.getCurrentInstance().getExternalContext().redirect("editStaff.xhtml");
        
    }
    
    public void updateStaff() throws SQLException, IOException {
        s.setBlockStart(blockStart);
        sda.updateStaff(s);
        FacesContext.getCurrentInstance().getExternalContext().redirect("selectStaff.xhtml");
    }
    
    public void deleteStaff(String staffID) throws SQLException, IOException {
        sda.deleteStaff(staffID);
        FacesContext.getCurrentInstance().getExternalContext().redirect("selectStaff.xhtml");
    }
    
    public void goBackMenu() throws IOException {
        sda.reset();
        FacesContext.getCurrentInstance().getExternalContext().redirect("EditInfo.xhtml");
    }
    
    public void goBackStaff() throws IOException {
        sda.reset();
        FacesContext.getCurrentInstance().getExternalContext().redirect("selectStaff.xhtml");
    }
    
    public void goToNew() throws IOException {
        s.setStaffName("");
        s.setBlockDay(0);
        s.setBlockDuration(0);
        s.setBlockStart(0);
        s.setCourseCodeListS("");
        s.setLecGroupListS("");
        s.setTutGroupListS("");
        s.setPracGroupListS("");
        FacesContext.getCurrentInstance().getExternalContext().redirect("newStaff.xhtml");
    }
}
