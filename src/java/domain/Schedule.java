/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.util.ArrayList;

/**
 *
 * @author Alex
 */
public class Schedule {

    private String groupID, cohortID;
    private int requiredNoOfClass;
    private ArrayList<Class> classList;

    public Schedule() {

    }

    public Schedule(String groupID, String cohortID, ArrayList<Class> classList) {
        this.groupID = groupID;
        this.cohortID = cohortID;
        this.classList = classList;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getCohortID() {
        return cohortID;
    }

    public void setCohortID(String cohortID) {
        this.cohortID = cohortID;
    }

    public ArrayList<Class> getClassList() {
        return classList;
    }

    public void setClassList(ArrayList<Class> classList) {
        this.classList = classList;
    }

    public void addClassToList(Class c) {
        this.classList.add(c);
    }

    public int getRequiredNoOfClass() {
        return requiredNoOfClass;
    }

    public void setRequiredNoOfClass(int requiredNoOfClass) {
        this.requiredNoOfClass = requiredNoOfClass;
    }

    public int getNoOfClass() {
        return this.classList.size();
    }

    public void resetClassList() {
        this.classList = new ArrayList();
    }
}
