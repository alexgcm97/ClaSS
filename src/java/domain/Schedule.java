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

    private String groupID;
    private int requiredNoOfClass;
    private ArrayList<Class> classList;

    public Schedule() {

    }

    public Schedule(String groupID, ArrayList<Class> classList) {
        this.groupID = groupID;
        this.classList = classList;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
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
}
