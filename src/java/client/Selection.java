/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import da.CourseDA;
import da.ProgrammeDA;
import da.StaffDA;
import da.TutorialGroupDA;
import da.VenueDA;
import domain.CourseDetails;
import domain.Programme;
import domain.Staff;
import domain.TutorialGroup;
import domain.Venue;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author REPUBLIC
 */
@ManagedBean(name = "selection")
@SessionScoped
public class Selection {

    private generateXML xml = new generateXML();
    private VenueDA vda = new VenueDA();
    private Venue v = new Venue();
    private List<Venue> venueList = new ArrayList<Venue>();
    private List<Venue> selectedVenue = new ArrayList<Venue>();
    private List<Venue> mustVenue = new ArrayList<Venue>();
    private List<Venue> mustVenueList = new ArrayList<Venue>();
    private List<String> selectedVenueID = new ArrayList<String>();
    private List<String> allVenueTypes = new ArrayList<String>();
    private List<Venue> filteredVenues = new ArrayList<Venue>();

    private TutorialGroupDA tgda = new TutorialGroupDA();
    private TutorialGroup tg = new TutorialGroup();
    private List<TutorialGroup> tutorialGroupList = new ArrayList<TutorialGroup>();
    private List<String> selectedGroups = new ArrayList<String>();

    private CourseDA cda = new CourseDA();
    private CourseDetails c = new CourseDetails();
    private List<CourseDetails> courseDetailsList = new ArrayList<CourseDetails>();
    private List<String> courseCodeList = new ArrayList();

    private ProgrammeDA pda = new ProgrammeDA();
    private Programme p = new Programme();
    private List<Programme> programmeList = new ArrayList<Programme>();
    private List<Programme> selectedProgramme = new ArrayList<Programme>();

    private StaffDA sda = new StaffDA();
    private Staff s = new Staff();
    private List<Staff> staffList = new ArrayList<Staff>();
    private List<String> selectedStaff = new ArrayList<String>();

    private Map<String, Boolean> checked = new HashMap<String, Boolean>();

    private int errorCode = 0;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public List<CourseDetails> getAllCourseRecords() throws SQLException {
        this.courseDetailsList = cda.getRelatedCourseRecords(courseCodeList);
        return this.courseDetailsList;
    }

    public List<String> getAllVenueTypes() throws SQLException {
        allVenueTypes = vda.getVenueType();
        return allVenueTypes;
    }

    public List<Venue> getFilteredVenues() {
        return filteredVenues;
    }

    public void setFilteredVenues(List<Venue> filteredVenues) {
        this.filteredVenues = filteredVenues;
    }

    public void setAllVenueTypes(List<String> allVenueTypes) {
        this.allVenueTypes = allVenueTypes;
    }

    public List<String> getCourseCodeList() {
        return courseCodeList;
    }

    public void setCourseCodeList(List<String> courseCodeList) {
        this.courseCodeList = courseCodeList;
    }

    public List<Venue> getAllVenueRecords() throws SQLException {
        this.venueList = vda.getAllVenueRecords();
        return this.venueList;
    }

    public List<Programme> getAllProgrammeRecords() throws SQLException {
        this.programmeList = pda.getAllProgrammeRecords();
        return this.programmeList;
    }

    public String intakeMonth(String month) {
        String output = "";
        switch (month) {
            case "01":
                output = "January";
                break;
            case "02":
                output = "February";
                break;
            case "03":
                output = "March";
                break;
            case "04":
                output = "April";
                break;
            case "05":
                output = "May";
                break;
            case "06":
                output = "June";
                break;
            case "07":
                output = "July";
                break;
            case "08":
                output = "August";
                break;
            case "09":
                output = "September";
                break;
            case "10":
                output = "October";
                break;
            case "11":
                output = "November";
                break;
            case "12":
                output = "December";
                break;

        }
        return output;
    }

    public void programmeButton() throws SQLException, IOException {
        selectedGroups.clear();
        List<String> tempList = new ArrayList<String>();
        for (int i = 0; i < selectedProgramme.size(); i++) {
            tempList = tgda.getGroupIdViaProgrammeID(selectedProgramme.get(i).getProgrammeID());
            for (int j = 0; j < tempList.size(); j++) {
                selectedGroups.add(tempList.get(j));
            }
        }
        System.out.println("selectedProgramme size: " + selectedProgramme.size());
        for (int b = 0; b < selectedProgramme.size(); b++) {
            System.out.println(selectedProgramme.get(b).getProgrammeID());
        }
        System.out.println("selectedGroups size: " + selectedGroups.size());
        System.out.println(selectedGroups.size());
        for (int k = 0; k < selectedGroups.size(); k++) {
            System.out.println(selectedGroups.get(k));
            for (String s : tgda.getCourseCodeList(selectedGroups.get(k))) {
                if (!courseCodeList.contains(s)) {
                    courseCodeList.add(s);
                }
            }
        }
        System.out.println("courseCodeList size: " + courseCodeList.size());
        for (int m = 0; m < courseCodeList.size(); m++) {
            System.out.println(courseCodeList.get(m));
        }

        selectedStaff.clear();
        mustVenue.clear();
        selectedVenue.clear();
        List<String> tempList1 = new ArrayList<String>();

        for (int i = 0; i < courseCodeList.size(); i++) {
            for (int j = 0; j < selectedGroups.size(); j++) {
                tempList1 = sda.getStaffIdViaGroupIdCourseCode(courseCodeList.get(i), selectedGroups.get(j));
                for (int k = 0; k < tempList1.size(); k++) {
                    if (!selectedStaff.contains(tempList1.get(k))) {
                        selectedStaff.add(tempList1.get(k));
                    }
                }
            }

        }
        System.out.println("selectedCourse size: " + courseCodeList.size());
        for (int b = 0; b < courseCodeList.size(); b++) {
            System.out.println(courseCodeList.get(b));
            mustVenue = vda.getVenueIdViaCourseCode(courseCodeList.get(b));
            for (int c = 0; c < mustVenue.size(); c++) {
                selectedVenue.add(mustVenue.get(c));
                mustVenueList.add(mustVenue.get(c));
            }
        }
        System.out.println("mustVenueList size: " + selectedVenue.size());
        for (int d = 0; d < selectedVenue.size(); d++) {
            System.out.println(selectedVenue.get(d).getVenueID());

        }

        System.out.println("selectedStaff size: " + selectedStaff.size());
        for (int a = 0; a < selectedStaff.size(); a++) {
            System.out.println(selectedStaff.get(a));
        }
        if (selectedGroups.isEmpty()) {
            errorCode = 1;
            FacesContext.getCurrentInstance().getExternalContext().redirect("ProgrammeSelection.xhtml");
        } else {
            FacesContext.getCurrentInstance().getExternalContext().redirect("VenueSelection.xhtml");
        }
    }

    public void venueButton() throws IOException {
        for (int i = 0; i < selectedVenue.size(); i++) {
            selectedVenueID.add(selectedVenue.get(i).getVenueID());
        }
        System.out.println("selectedVenue size: " + selectedVenue.size());
        for (int k = 0; k < selectedVenueID.size(); k++) {
            System.out.println(selectedVenueID.get(k));
        }
        if (selectedVenueID.isEmpty()) {
            errorCode = 1;
            FacesContext.getCurrentInstance().getExternalContext().redirect("VenueSelection.xhtml");
        } else {
            xml.generateCourseXML(courseCodeList);
            xml.generateTutorialGroupXML(selectedGroups);
            xml.generateStaffXML(selectedStaff);
            xml.generateVenueXML(selectedVenueID);
            FacesContext.getCurrentInstance().getExternalContext().redirect("setSettings.xhtml");
        }
    }

    public void generateXML() {
        xml.generateCourseXML(courseCodeList);
        xml.generateTutorialGroupXML(selectedGroups);
        xml.generateStaffXML(selectedStaff);
        xml.generateVenueXML(selectedVenueID);
    }

    public boolean checkValue(String venueID) {
        boolean found = false;
        for (int i = 0; i < mustVenueList.size(); i++) {
            if (venueID.equals(mustVenueList.get(i).getVenueID())) {
                found = true;
            }
        }
        return found;
    }

    public List<String> getSelectedVenueID() {
        return selectedVenueID;
    }

    public void setSelectedVenueID(List<String> selectedVenueID) {
        this.selectedVenueID = selectedVenueID;
    }

    public List<Venue> getMustVenueList() {
        return mustVenueList;
    }

    public void setMustVenueList(List<Venue> mustVenueList) {
        this.mustVenueList = mustVenueList;
    }

    public Venue getV() {
        return v;
    }

    public void setV(Venue v) {
        this.v = v;
    }

    public List<Venue> getMustVenue() {
        return mustVenue;
    }

    public void setMustVenue(List<Venue> mustVenue) {
        this.mustVenue = mustVenue;
    }

    public List<Venue> getVenueList() {
        return venueList;
    }

    public void setVenueList(List<Venue> venueList) {
        this.venueList = venueList;
    }

    public List<Venue> getSelectedVenue() {
        return selectedVenue;
    }

    public void setSelectedVenue(List<Venue> selectedVenue) {
        this.selectedVenue = selectedVenue;
    }

    public List<String> getSelectedGroups() {
        return selectedGroups;
    }

    public void setSelectedGroups(List<String> selectedGroups) {
        this.selectedGroups = selectedGroups;
    }

    public List<String> getSelectedStaff() {
        return selectedStaff;
    }

    public void setSelectedStaff(List<String> selectedStaff) {
        this.selectedStaff = selectedStaff;
    }

    public List<Programme> getSelectedProgramme() {
        return selectedProgramme;
    }

    public void setSelectedProgramme(List<Programme> selectedProgramme) {
        this.selectedProgramme = selectedProgramme;
    }

    public TutorialGroup getTg() {
        return tg;
    }

    public void setTg(TutorialGroup tg) {
        this.tg = tg;
    }

    public List<TutorialGroup> getTutorialGroupList() {
        return tutorialGroupList;
    }

    public void setTutorialGroupList(List<TutorialGroup> tutorialGroupList) {
        this.tutorialGroupList = tutorialGroupList;
    }

    public CourseDetails getC() {
        return c;
    }

    public void setC(CourseDetails c) {
        this.c = c;
    }

    public List<CourseDetails> getCourseDetailsList() {
        return courseDetailsList;
    }

    public void setCourseDetailsList(List<CourseDetails> courseDetailsList) {
        this.courseDetailsList = courseDetailsList;
    }

    public Programme getP() {
        return p;
    }

    public void setP(Programme p) {
        this.p = p;
    }

    public List<Programme> getProgrammeList() {
        return programmeList;
    }

    public void setProgrammeList(List<Programme> programmeList) {
        this.programmeList = programmeList;
    }

    public Staff getS() {
        return s;
    }

    public void setS(Staff s) {
        this.s = s;
    }

    public List<Staff> getStaffList() {
        return staffList;
    }

    public void setStaffList(List<Staff> staffList) {
        this.staffList = staffList;
    }

    public Map<String, Boolean> getChecked() {
        return checked;
    }

    public void setChecked(Map<String, Boolean> checked) {
        this.checked = checked;
    }

}
