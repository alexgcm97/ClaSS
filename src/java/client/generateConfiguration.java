/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author Alex
 */
@ManagedBean
@RequestScoped
public class generateConfiguration {

    private int blockDay;
    private double maxBreak, blockStart, blockEnd;
    private boolean balanceClass;

    public int getBlockDay() {
        return blockDay;
    }

    public void setBlockDay(int blockDay) {
        this.blockDay = blockDay;
    }

    public double getMaxBreak() {
        return maxBreak;
    }

    public void setMaxBreak(double maxBreak) {
        this.maxBreak = maxBreak;
    }

    public double getBlockStart() {
        return blockStart;
    }

    public void setBlockStart(double blockStart) {
        this.blockStart = blockStart;
    }

    public double getBlockEnd() {
        return blockEnd;
    }

    public void setBlockEnd(double blockEnd) {
        this.blockEnd = blockEnd;
    }

    public boolean isBalanceClass() {
        return balanceClass;
    }

    public void setBalanceClass(boolean balanceClass) {
        this.balanceClass = balanceClass;
    }

}
