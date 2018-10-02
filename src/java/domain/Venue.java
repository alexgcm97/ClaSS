/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Alex
 */
package domain;

import java.util.ArrayList;

public class Venue {

    private String venueID, block, venueType, remark;
    private int capacity;

    public Venue() {

    }

    public Venue(String venueID, String block, String venueType, int capacity, String remark) {
        this.venueID = venueID;
        this.block = block;
        this.venueType = venueType;
        this.capacity = capacity;
        this.remark = remark;
    }

    public String getVenueID() {
        return venueID;
    }

    public void setVenueID(String venueID) {
        this.venueID = venueID;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getVenueType() {
        return venueType;
    }

    public void setVenueType(String venueType) {
        this.venueType = venueType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

}
