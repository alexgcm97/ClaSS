/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.io.Serializable;

/**
 *
 * @author Teck Siong
 */
public class Programme implements Serializable {

    String programmeCode, programmeName;

    public Programme() {
    }

    public Programme(String programmeCode, String programmeName) {
        this.programmeCode = programmeCode;
        this.programmeName = programmeName;
    }

    public String getProgrammeCode() {
        return programmeCode;
    }

    public void setProgrammeCode(String programmeCode) {
        this.programmeCode = programmeCode;
    }

    public String getProgrammeName() {
        return programmeName;
    }

    public void setProgrammeName(String programmeName) {
        this.programmeName = programmeName;
    }

}
