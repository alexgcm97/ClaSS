/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import da.CourseDA;
import da.VenueDA;
import domain.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

/**
 *
 * @author junki
 */
@ManagedBean
@SessionScoped
public class venueManage {

    public VenueDA vda = new VenueDA();
    private CourseDA cda = new CourseDA();
    public Venue v = new Venue();
    private List<String> selectedCourseCodeList = new ArrayList();
    private String[] courseCodeStr;

    public void venueTypeChangeListener(ValueChangeEvent event) {
        if (event.getNewValue().equals("Hall")) {
            v.setBlock("DK");
        }
    }

    public VenueDA getVda() {
        return vda;
    }

    public void setVda(VenueDA vda) {
        this.vda = vda;
    }

    public CourseDA getCda() {
        return cda;
    }

    public void setCda(CourseDA cda) {
        this.cda = cda;
    }

    public List<String> getSelectedCourseCodeList() {
        return selectedCourseCodeList;
    }

    public void setSelectedCourseCodeList(List<String> selectedCourseCodeList) {
        this.selectedCourseCodeList = selectedCourseCodeList;
    }

    public String[] getCourseCodeStr() {
        return courseCodeStr;
    }

    public void setCourseCodeStr(String[] courseCodeStr) {
        this.courseCodeStr = courseCodeStr;
    }

    public List<String> getCourseCodeList() throws SQLException {
        List<Course> courseList = cda.getAllRecords();
        for (Course c : courseList) {
            this.selectedCourseCodeList.add(c.getCourseCode());
        }
        return this.selectedCourseCodeList;
    }

    public Venue getV() {
        return v;
    }

    public void setV(Venue v) {
        this.v = v;
    }

    public List<String> getBlockList() {
        List<String> blockList = new ArrayList();
        for (Character c : "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()) {
            blockList.add(c + "");
        }
        return blockList;
    }

    public void venueInsert() throws SQLException, IOException {
        String tempStr = "-";
        if (courseCodeStr != null && courseCodeStr.length > 0) {
            tempStr = "";
            for (String s : courseCodeStr) {
                if (tempStr.length() == 0) {
                    tempStr = s;
                } else {
                    tempStr += "," + s;
                }
            }
        }
        v.setCourseCodeList(tempStr);
        if (v.getVenueType().equals("Hall")) {
            v.setBlock("DK");
            v.setVenueID("DK " + v.getVenueID());
        } else {
            v.setVenueID(v.getBlock() + v.getVenueID());
        }
        vda.insertVenue(v);
        FacesContext.getCurrentInstance().getExternalContext().redirect("selectVenue.xhtml");
    }

    public void retrieveVenue(String venueID) throws SQLException, IOException {
        v = vda.get(venueID);
        FacesContext.getCurrentInstance().getExternalContext().redirect("editVenue.xhtml");

    }

    public void updateVenue() throws SQLException, IOException {
        vda.updateVenue(v);

        FacesContext.getCurrentInstance().getExternalContext().redirect("selectVenue.xhtml");
    }

    public void deleteVenue(String venueID) throws SQLException, IOException {
        vda.deleteVenue(venueID);
        FacesContext.getCurrentInstance().getExternalContext().redirect("selectVenue.xhtml");
    }

    public void backToVenue() throws SQLException, IOException {

        FacesContext.getCurrentInstance().getExternalContext().redirect("selectVenue.xhtml");
    }

    public void goBackMenu() throws IOException {
        vda.reset();
        FacesContext.getCurrentInstance().getExternalContext().redirect("EditInfo.xhtml");
    }

    public void goToNew() throws IOException {
        v.setVenueID("");
        v.setBlock("");
        v.setVenueType("Hall");
        v.setCapacity(0);
        v.setCourseCodeList("");
        FacesContext.getCurrentInstance().getExternalContext().redirect("newVenue.xhtml");
    }
}
