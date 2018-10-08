/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Alex
 */
@ManagedBean
@SessionScoped
public class Configuration {

    private int blockDay, blockStart, studyDays = 5;
    private double maxBreak, blockDuration, startTime = 8, endTime = 20;
    private boolean toBalanceClass, toBlockTime, setBreak;

    private final String filePath = "C:\\Users\\Alex\\Documents\\NetBeansProjects\\ClaSS\\src\\java\\xml\\";

    public void generateConfiguration() throws ParserConfigurationException, TransformerConfigurationException, TransformerException, FileNotFoundException, UnsupportedEncodingException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();
        Element rootElement = doc.createElement("configuration");
        doc.appendChild(rootElement);
        Element days = doc.createElement("studyDays");
        days.appendChild(doc.createTextNode(studyDays + ""));
        rootElement.appendChild(days);
        Element studyStart = doc.createElement("startTime");
        studyStart.appendChild(doc.createTextNode(startTime + ""));
        rootElement.appendChild(studyStart);
        Element studyEnd = doc.createElement("endTime");
        studyEnd.appendChild(doc.createTextNode(endTime + ""));
        rootElement.appendChild(studyEnd);
        Element constraints = doc.createElement("constraints");

        if (toBalanceClass == true) {
            Element balanceClass = doc.createElement("balanceClass");
            balanceClass.appendChild(doc.createTextNode("true"));
            constraints.appendChild(balanceClass);
        }
        if (toBlockTime == true) {
            Element block = doc.createElement("block");
            Element temp = doc.createElement("day");
            temp.appendChild(doc.createTextNode(blockDay + ""));
            block.appendChild(temp);
            temp = doc.createElement("startTime");
            temp.appendChild(doc.createTextNode(blockStart + ""));
            block.appendChild(temp);
            temp = doc.createElement("endTime");
            double blockEnd = blockStart + blockDuration;
            temp.appendChild(doc.createTextNode(blockEnd + ""));
            block.appendChild(temp);
            constraints.appendChild(block);
        }
        if (setBreak == true) {
            Element temp = doc.createElement("maxBreak");
            temp.appendChild(doc.createTextNode(maxBreak + ""));
            constraints.appendChild(temp);
        }

        rootElement.appendChild(constraints);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filePath + "Configuration.xml"));
        transformer.transform(source, result);
        FacesContext.getCurrentInstance().getExternalContext().redirect("generateSchedule.xhtml");
    }

    public Configuration getInstance() {
        return this;
    }

    public String getValidatorMessage() {
        double tempStart = startTime;
        double tempEnd = endTime - 2;
        String startStr, endStr;
        if (tempEnd == 12) {
            startStr = tempStart + "0 p.m.";
        } else if (tempStart > 12) {
            tempStart -= 12;
            startStr = tempStart + "0 p.m.";
        } else {
            startStr = tempStart + "0 a.m.";
        }
        if (tempEnd == 12) {
            endStr = tempEnd + "0 p.m.";
        } else if (tempEnd > 12) {
            tempEnd -= 12;
            endStr = tempEnd + "0 p.m.";
        } else {
            endStr = tempEnd + "0 a.m.";
        }
        return "Please enter time between " + startStr + " and " + endStr + " in 24 hours format.";
    }

    public int getStudyDays() {
        return studyDays;
    }

    public void setStudyDays(int studyDays) {
        this.studyDays = studyDays;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }

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

    public int getBlockStart() {
        return blockStart;
    }

    public void setBlockStart(int blockStart) {
        this.blockStart = blockStart;
    }

    public double getBlockDuration() {
        return blockDuration;
    }

    public void setBlockDuration(double blockDuration) {
        this.blockDuration = blockDuration;
    }

    public boolean isToBalanceClass() {
        return toBalanceClass;
    }

    public void setToBalanceClass(boolean toBalanceClass) {
        this.toBalanceClass = toBalanceClass;
    }

    public boolean isToBlockTime() {
        return toBlockTime;
    }

    public void setToBlockTime(boolean toBlockTime) {
        this.toBlockTime = toBlockTime;
    }

    public boolean isSetBreak() {
        return setBreak;
    }

    public void setSetBreak(boolean setBreak) {
        this.setBreak = setBreak;
    }

}
