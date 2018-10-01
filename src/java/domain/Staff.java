/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.sql.Time;

/**
 *
 * @author Alex
 */
public class Staff {

    private String staffID, staffName, remark, startWork, endWork;

    public Staff(String staffID, String staffName, String remark, String startWork, String endWork) {
        this.staffID = staffID;
        this.staffName = staffName;
        this.remark = remark;
        this.startWork = startWork;
        this.endWork = endWork;
    }

    public String getStaffID() {
        return staffID;
    }

    public void setStaffID(String staffID) {
        this.staffID = staffID;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStartWork() {
        return startWork;
    }

    public void setStartWork(String startWork) {
        this.startWork = startWork;
    }

    public String getEndWork() {
        return endWork;
    }

    public void setEndWork(String endWork) {
        this.endWork = endWork;
    }

}
