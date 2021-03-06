/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import da.*;
import domain.*;
import domain.Class;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Alex
 */
@ManagedBean
@SessionScoped
public class SchedulingAlgorithm implements Serializable {

    private final Random rand = new Random();

    private ArrayList<CourseType> lecList, courseList;
    private ArrayList<Staff> staffList;
    private ArrayList<TutorialGroup> groupList, dbGroupList;
    private ArrayList<Venue> roomList, labList, hallList, allPurposeLabList;
    private ArrayList<Schedule> scheduleList;

    private int studyDays = 0, blockDay = 0, errorCode = 0, classLimit;
    private boolean toBalance = false;
    private double studyStart, studyEnd, blockStart, blockEnd, maxBreak = 99, noOfClassPerDay = 99;
    private String errorMsg;
    private Class blockClass;
    private ArrayList<Class> dbList = new ArrayList();

    private final int assignLimit = 120, firstVLimit = 30, secondVLimit = 60, exitLimit = 10000;
    private final double longDurationLimit = 4.0, assignRatio = 0.95, classRatio = 0.07, timeRatio = 0.50;
    private final ClassDA cda = new ClassDA();
    private final VenueDA vda = new VenueDA();
    private final StaffDA sda = new StaffDA();
    private final TutorialGroupDA tgda = new TutorialGroupDA();

    private final String filePath = XMLPath.getXMLPath();

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public boolean initialize() throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Element e;

        String fileName = filePath + "TutorialGroup.xml";
        Document doc = dBuilder.parse(fileName);
        groupList = new ArrayList();
        dbGroupList = tgda.getAll();
        NodeList nodes = doc.getElementsByTagName("tutorialGroup");

        for (int i = 0; i < nodes.getLength(); i++) {
            e = (Element) nodes.item(i);

            TutorialGroup tg = new TutorialGroup(e.getAttribute("groupID"), Integer.parseInt(e.getElementsByTagName("groupNumber").item(0).getTextContent()), Integer.parseInt(e.getElementsByTagName("size").item(0).getTextContent()), e.getElementsByTagName("cohortID").item(0).getTextContent(), e.getElementsByTagName("courseCodeList").item(0).getTextContent());
            cda.deleteRecords(tg.getGroupID());
            groupList.add(tg);
        }

        if (groupList.isEmpty()) {
            errorMsg = "Tutorial Group XML file is empty.";
            errorCode = 1;
            FacesContext.getCurrentInstance().getExternalContext().redirect("menu.xhtml");
            return false;
        }

        fileName = filePath + "Course.xml";
        doc = dBuilder.parse(fileName);
        courseList = new ArrayList();
        lecList = new ArrayList();
        nodes = doc.getElementsByTagName("course");

        for (int i = 0; i < nodes.getLength(); i++) {
            e = (Element) nodes.item(i);

            CourseType c = new CourseType(e.getAttribute("courseID"), e.getElementsByTagName("courseType").item(0).getTextContent(), e.getElementsByTagName("courseDuration").item(0).getTextContent(), e.getElementsByTagName("courseCode").item(0).getTextContent());
            if (c.getCourseType().equals("L")) {
                lecList.add(c);
            } else {
                courseList.add(c);
            }
        }

        if (lecList.isEmpty() && courseList.isEmpty()) {
            errorCode = 1;
            errorMsg = "Course XML file is empty.";
            FacesContext.getCurrentInstance().getExternalContext().redirect("menu.xhtml");
            return false;
        }

        fileName = filePath + "Configuration.xml";
        doc = dBuilder.parse(fileName);
        nodes = doc.getElementsByTagName("configuration");

        e = (Element) nodes.item(0);
        if (e.getElementsByTagName("studyDays").getLength() > 0) {
            studyDays = Integer.parseInt(e.getElementsByTagName("studyDays").item(0).getTextContent());
        } else {
            errorCode = 1;
        }
        if (e.getElementsByTagName("startTime").getLength() > 0) {
            studyStart = Double.parseDouble(e.getElementsByTagName("startTime").item(0).getTextContent());
        } else {
            errorCode = 1;
        }
        if (e.getElementsByTagName("endTime").getLength() > 0) {
            studyEnd = Double.parseDouble(e.getElementsByTagName("endTime").item(0).getTextContent());
        } else {
            errorCode = 1;
        }

        nodes = doc.getElementsByTagName("constraints");
        if (nodes.getLength() > 0) {
            e = (Element) nodes.item(0);
            if (e.getElementsByTagName("block").getLength() > 0) {
                blockDay = Integer.parseInt(e.getElementsByTagName("day").item(0).getTextContent());
                blockStart = Double.parseDouble(e.getElementsByTagName("startTime").item(0).getTextContent());
                blockEnd = Double.parseDouble(e.getElementsByTagName("endTime").item(0).getTextContent());
            }

            if (e.getElementsByTagName("maxBreak").getLength() > 0) {
                maxBreak = Double.parseDouble(e.getElementsByTagName("maxBreak").item(0).getTextContent());
            }

            if (e.getElementsByTagName("balanceClass").getLength() > 0) {
                toBalance = Boolean.parseBoolean(e.getElementsByTagName("balanceClass").item(0).getTextContent());
            }
        }
        if (errorCode == 1) {
            errorMsg = "Configuration XML file is empty or incomplete.";
            FacesContext.getCurrentInstance().getExternalContext().redirect("menu.xhtml");
            return false;
        }

        fileName = filePath + "Staff.xml";
        doc = dBuilder.parse(fileName);
        staffList = new ArrayList();
        nodes = doc.getElementsByTagName("staff");

        for (int i = 0; i < nodes.getLength(); i++) {
            e = (Element) nodes.item(i);

            Staff stf = new Staff(e.getAttribute("staffID"), e.getElementsByTagName("name").item(0).getTextContent());
            String tempStr = e.getElementsByTagName("courseCodeList").item(0).getTextContent();

            String[] tempArr;
            if (tempStr.contains("|")) {
                tempArr = tempStr.split("\\|");
                for (String s : tempArr) {
                    stf.addCourseCodeToList(s);
                }
            } else {
                stf.addCourseCodeToList(tempStr);
            }

            tempStr = e.getElementsByTagName("lecGroupList").item(0).getTextContent();
            if (tempStr.contains("|")) {
                tempArr = tempStr.split("\\|");
                for (String s : tempArr) {
                    stf.addLecGroupToList(s);
                }
            } else {
                stf.addLecGroupToList(tempStr);
            }

            tempStr = e.getElementsByTagName("tutGroupList").item(0).getTextContent();
            if (tempStr.contains("|")) {
                tempArr = tempStr.split("\\|");
                for (String s : tempArr) {
                    stf.addTutGroupToList(s);
                }
            } else {
                stf.addTutGroupToList(tempStr);
            }

            String pracGroupStr = e.getElementsByTagName("pracGroupList").item(0).getTextContent();
            if (pracGroupStr.contains("|")) {
                String pracGroupArr[] = pracGroupStr.split("\\|");
                for (String s : pracGroupArr) {
                    stf.addPracGroupToList(s);
                }
            } else {
                stf.addPracGroupToList(pracGroupStr);
            }

            if (e.getElementsByTagName("blockDay").getLength() > 0) {
                stf.setBlockDay(Integer.parseInt(e.getElementsByTagName("blockDay").item(0).getTextContent()));
                stf.setBlockStart(Double.parseDouble(e.getElementsByTagName("blockStart").item(0).getTextContent()));
                stf.setBlockDuration(Double.parseDouble(e.getElementsByTagName("blockDuration").item(0).getTextContent()));
            }

            stf.setClassList(sda.getClassList(stf.getStaffID()));
            staffList.add(stf);
        }
        if (staffList.isEmpty()) {
            errorCode = 1;
            errorMsg = "Staff XML file is empty.";
            FacesContext.getCurrentInstance().getExternalContext().redirect("menu.xhtml");
            return false;
        }

        fileName = filePath + "Venue.xml";
        doc = dBuilder.parse(fileName);
        roomList = new ArrayList();
        labList = new ArrayList();
        hallList = new ArrayList();
        allPurposeLabList = vda.getAllPurposeLab();
        nodes = doc.getElementsByTagName("venue");

        for (int i = 0; i < nodes.getLength(); i++) {
            e = (Element) nodes.item(i);

            Venue v = new Venue(e.getAttribute("venueID"), e.getElementsByTagName("block").item(0).getTextContent(), e.getElementsByTagName("venueType").item(0).getTextContent(), Integer.parseInt(e.getElementsByTagName("capacity").item(0).getTextContent()), e.getElementsByTagName("courseCodeList").item(0).getTextContent());
            v.setClassList(vda.getClassList(v.getVenueID()));
            if (v.getVenueType().equalsIgnoreCase("room")) {
                roomList.add(v);
            } else if (v.getVenueType().equalsIgnoreCase("lab")) {
                labList.add(v);
            } else if (v.getVenueType().equalsIgnoreCase("hall")) {
                hallList.add(v);
            }
        }

        if (roomList.isEmpty() || labList.isEmpty() || hallList.isEmpty()) {
            errorCode = 1;
            errorMsg = "Venue XML file is empty or incomplete.";
            if (roomList.isEmpty()) {
                errorMsg += "\\nNo rooms are selected for tutorial or blended classes.";
            }
            if (labList.isEmpty()) {
                errorMsg += "\\nNo labs are selected for practical classes.";
            }
            if (hallList.isEmpty()) {
                errorMsg += "\\nNo lecture halls are selected for lecture classes.";
            }
            FacesContext.getCurrentInstance().getExternalContext().redirect("menu.xhtml");
            return false;
        }

        //Initialize Schedule List
        scheduleList = new ArrayList();
        //Creating Schedule List for each Group
        for (int i = 0; i < groupList.size(); i++) {
            ArrayList<Class> classList = new ArrayList();
            Schedule s = new Schedule(groupList.get(i).getGroupID(), groupList.get(i).getCohortID(), classList, groupList.get(i).getCourseCodeList());
            int count = 0;
            for (CourseType ct : courseList) {
                if (groupList.get(i).getCourseCodeList().contains(ct.getCourseCode())) {
                    count++;
                }
            }

            for (CourseType ct : lecList) {
                if (groupList.get(i).getCourseCodeList().contains(ct.getCourseCode())) {
                    count++;
                }
            }
            s.setRequiredNoOfClass(count);
            scheduleList.add(s);
        }

        classLimit = (int) Math.floor(scheduleList.size() * classRatio);
        dbList = cda.getAll();
        return true;
    }

    public void allocation() throws IOException, CloneNotSupportedException {
        //Reset the classList to empty
        for (int i = 0; i < scheduleList.size(); i++) {
            scheduleList.get(i).resetClassList();
        }

        //If insert blockClass is selected, it will insert a block class with no data to block the time
        if (blockDay > 0) {
            blockClass = new Class("-", "-", "-", "-", blockDay, blockStart, blockEnd, "BLK");
            for (int i = 0; i < scheduleList.size(); i++) {
                scheduleList.get(i).addClassToList(blockClass);
            }
        }

        //Assign Lecture Classes
        for (int i = 0; i < lecList.size(); i++) {
            assignLecture(lecList.get(i));
        }

        //Assign Other Schedules, ex: Tutorial, Practical
        for (int i = 0; i < courseList.size(); i++) {
            for (int j = 0; j < scheduleList.size(); j++) {
                if (scheduleList.get(j).getCourseCodeList().contains(courseList.get(i).getCourseCode())) {
                    assignCourse(scheduleList.get(j).getRequiredNoOfClass(), scheduleList.get(j).getGroupID(), scheduleList.get(j).getClassList(), courseList.get(i));
                }
            }
        }

        //Optimize All Courses Break
        if (maxBreak != 99) {
            sortList();
            optimizeBreak(1);
            sortList();
            optimizeBreak(2);
        }

        //Remove the block class added previously
        if (blockDay > 0) {
            clearBlock();
        }

        //Sorting List & assigning venues to the courses except lecture classes
        sortList();
        for (int i = 0; i < scheduleList.size(); i++) {
            ArrayList<Class> classList = scheduleList.get(i).getClassList();
            for (int j = 0; j < classList.size(); j++) {
                Class previousClass = null;
                Class thisClass = classList.get(j);
                if (j > 0) {
                    previousClass = classList.get(j - 1);
                }
                if (!thisClass.getCourseType().equals("L")) {
                    assignVenue(i, previousClass, thisClass);
                }
            }
        }
        sortList();
    }

    public void sortList() {
        //Sort List by Day and StartTime
        for (int i = 0; i < scheduleList.size(); i++) {
            ArrayList<Class> classList = scheduleList.get(i).getClassList();
            classList.sort((Class o1, Class o2) -> {
                if (o1.getDay() == o2.getDay()) {
                    double time1 = o1.getStartTime(), time2 = o2.getStartTime();
                    if (time1 == time2) {
                        return 0;
                    } else if (time1 > time2) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
                return o1.getDay() - o2.getDay();
            });
        }
    }

    //Check if the classList has any clashes with another classList
    public boolean hasClashWithOtherLists() {
        boolean isClash = false;
        for (int i = 0; i < scheduleList.size() - 1; i++) {
            for (int j = i + 1; j < scheduleList.size(); j++) {
                ArrayList<Class> list1 = scheduleList.get(i).getClassList(), list2 = scheduleList.get(j).getClassList();
                for (int index = 0; index < list1.size(); index++) {
                    for (int index2 = 0; index2 < list2.size(); index2++) {
                        Class class1 = list1.get(index), class2 = list2.get(index2);
                        if (!class1.getCourseType().equals("BLK") && !class2.getCourseType().equals("BLK")) {
                            if (class1.getDay() == class2.getDay()) {
                                double startTime1 = class1.getStartTime(), endTime1 = class1.getEndTime(), startTime2 = class2.getStartTime(), endTime2 = class2.getEndTime();

                                if (class1.getCourseType().equals("L") && class2.getCourseType().equals("L")) {
                                    if (!class1.getCourseID().equals(class2.getCourseID())) {
                                        if ((startTime2 >= startTime1 && startTime2 < endTime1) || (endTime2 > startTime1 && endTime2 <= endTime1) || (startTime1 >= startTime2 && startTime1 < endTime2) || (endTime1 > startTime2 && endTime1 <= endTime2)) {
                                            if (class1.getVenueID().equals(class2.getVenueID()) || class1.getStaffID().equals(class2.getStaffID())) {

                                                //System.out.println("Class 1: " + class1.getCourseID() + "-" + class1.getVenueID() + "-" + class1.getStaffID());
                                                //System.out.println("Class 2: " + class2.getCourseID() + "-" + class2.getVenueID() + "-" + class2.getStaffID());
                                                isClash = true;
                                                break;
                                            }
                                        }
                                    } else {
                                        if (class1.getStartTime() != class2.getStartTime() || class1.getEndTime() != class2.getEndTime()) {
                                            if ((startTime2 >= startTime1 && startTime2 < endTime1) || (endTime2 > startTime1 && endTime2 <= endTime1) || (startTime1 >= startTime2 && startTime1 < endTime2) || (endTime1 > startTime2 && endTime1 <= endTime2)) {
                                                if (class1.getVenueID().equals(class2.getVenueID()) || class1.getStaffID().equals(class2.getStaffID())) {
                                                    //System.out.println("Class 1: " + class1.getCourseID() + "-" + class1.getVenueID() + "-" + class1.getStaffID());
                                                    //System.out.println("Class 2: " + class2.getCourseID() + "-" + class2.getVenueID() + "-" + class2.getStaffID());
                                                    isClash = true;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    if ((startTime2 >= startTime1 && startTime2 < endTime1) || (endTime2 > startTime1 && endTime2 <= endTime1) || (startTime1 >= startTime2 && startTime1 < endTime2) || (endTime1 > startTime2 && endTime1 <= endTime2)) {
                                        if (class1.getVenueID().equals(class2.getVenueID()) || class1.getStaffID().equals(class2.getStaffID())) {
                                            //System.out.println("Class 1: " + class1.getCourseID() + class1.getCourseType() + "-" + class1.getVenueID() + "-" + class1.getStaffID() + "(" + class1.getStartTime() + "-" + class1.getEndTime() + ")");
                                            //System.out.println("Class 2: " + class2.getCourseID() + class2.getCourseType() + "-" + class2.getVenueID() + "-" + class2.getStaffID() + "(" + class2.getStartTime() + "-" + class2.getEndTime() + ")");
                                            isClash = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return isClash;
    }

    public void optimizeBreak(int step) throws CloneNotSupportedException {
        double moveDuration = 0;
        ArrayList<Class> classList;
        switch (step) {
            case 1:
                for (int i = 1; i <= studyDays; i++) {
                    for (int index = 0; index < scheduleList.size(); index++) {
                        classList = scheduleList.get(index).getClassList();
                        if (countClassBeforeHalfDay(i, classList) == 0 && countLecClassPerDay(i, classList) == 0) {
                            for (Class c : classList) {
                                if (c.getDay() == i) {
                                    if (!c.getCourseType().equals("BLK") && !c.getCourseType().equals("L")) {
                                        if (moveDuration == 0) {
                                            moveDuration = c.getStartTime() - studyStart;
                                        }
                                        Class newC = (Class) c.clone();
                                        newC.moveLeft(moveDuration);

                                        if (!isClashWithOtherLists(newC)) {
                                            scheduleList.get(index).moveLeft(c, moveDuration, studyStart);
                                        }
                                    }
                                }
                            }
                            moveDuration = 0;
                        }
                    }
                }
                break;
            case 2:
                for (int index = 0; index < scheduleList.size(); index++) {
                    classList = scheduleList.get(index).getClassList();
                    for (int i = 0; i < classList.size() - 1; i++) {
                        int j = i + 1;
                        Class c1 = classList.get(i);
                        Class c2 = classList.get(j);
                        if (!c1.getCourseType().equals("BLK")) {
                            if (!c1.getCourseType().equals("L") || !c2.getCourseType().equals("L")) {
                                if (c2.getCourseType().equals("BLK")) {
                                    if (j < classList.size() - 1) {
                                        c2 = classList.get(j + 1);
                                    } else {
                                        break;
                                    }
                                }
                                if (c1.getDay() == c2.getDay()) {
                                    double breakTime = c2.getStartTime() - c1.getEndTime();
                                    if (breakTime > maxBreak) {
                                        moveDuration = breakTime - getRandomMoveDuration();
                                        if (c1.getCourseType().equals("L") && !c2.getCourseType().equals("L")) {
                                            Class newC = (Class) c2.clone();
                                            newC.moveLeft(moveDuration);

                                            if (!isClashWithOtherLists(newC)) {
                                                scheduleList.get(index).moveLeft(c2, moveDuration, studyStart);
                                            }
                                        } else if (c2.getCourseType().equals("L") && !c1.getCourseType().equals("L")) {
                                            Class newC = (Class) c1.clone();
                                            newC.moveRight(moveDuration);

                                            if (!isClashWithOtherLists(newC)) {
                                                scheduleList.get(index).moveRight(c1, moveDuration, studyEnd);
                                            }
                                        } else if (!c1.getCourseType().equals("L") && !c2.getCourseType().equals("L")) {
                                            if (c1.getStartTime() == studyStart) {
                                                Class newC = (Class) c1.clone();
                                                newC.moveRight(moveDuration);

                                                if (!isClashWithOtherLists(newC)) {
                                                    scheduleList.get(index).moveRight(c1, moveDuration, studyEnd);
                                                }
                                            } else {
                                                Class newC = (Class) c2.clone();
                                                newC.moveLeft(moveDuration);

                                                if (!isClashWithOtherLists(newC)) {
                                                    scheduleList.get(index).moveLeft(c2, moveDuration, studyStart);
                                                }
                                            }
                                        }
                                    } else if (breakTime == 0) {
                                        moveDuration = 0.5;
                                        if (c1.getCourseType().equals("L") && !c2.getCourseType().equals("L")) {
                                            Class newC = (Class) c2.clone();
                                            newC.moveRight(moveDuration);

                                            if (!isClashWithOtherLists(newC)) {
                                                scheduleList.get(index).moveRight(c2, moveDuration, studyEnd);
                                            }
                                        } else {
                                            if (c1.getStartTime() == studyStart) {
                                                Class newC = (Class) c1.clone();
                                                newC.moveRight(moveDuration);

                                                if (!isClashWithOtherLists(newC)) {
                                                    scheduleList.get(index).moveRight(c1, moveDuration, studyEnd);
                                                }
                                            } else {
                                                Class newC = (Class) c1.clone();
                                                newC.moveLeft(moveDuration);

                                                if (!isClashWithOtherLists(newC)) {
                                                    scheduleList.get(index).moveLeft(c1, moveDuration, studyStart);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                break;
        }
    }

    public int countLecClassPerDay(int day, ArrayList<Class> classList) {
        int count = 0;
        for (Class c : classList) {
            if (c.getDay() == day) {
                if (c.getCourseType().equals("L")) {
                    count++;
                }
            }
        }
        return count;
    }

    public int countClassBeforeHalfDay(int day, ArrayList<Class> classList) {
        int count = 0;
        for (Class c : classList) {
            if (c.getDay() == day) {
                if (c.getEndTime() <= ((studyStart + studyEnd) / 2)) {
                    if (!c.getCourseType().equals("BLK")) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public void insertData() throws SQLException {
        for (int i = 0; i < scheduleList.size(); i++) {
            ArrayList<Class> classList = scheduleList.get(i).getClassList();

            for (Class c : classList) {
                cda.insert(c);
            }
        }
    }

    public void clearBlock() {
        for (int i = 0; i < scheduleList.size(); i++) {
            scheduleList.get(i).getClassList().remove(blockClass);
        }
    }

    public int getTotalSize(String lecGroupStr, String courseCode) {
        int totalSize = 0;
        for (TutorialGroup tg : dbGroupList) {
            if (lecGroupStr.contains(courseCode) && lecGroupStr.contains(tg.getCohortID())) {
                totalSize += tg.getSize();
            }
        }
        return totalSize;
    }

    public void assignLecture(CourseType course) throws IOException {
        boolean isClash, found, isSame = false;

        String courseID = course.getCourseID(), courseType = course.getCourseType(), courseCode = course.getCourseCode();
        Venue v;
        ArrayList<Staff> lecStaffList = getLecStaffList(courseCode, courseType);
        ArrayList<Integer> indexList = new ArrayList();
        double startTime = 0, endTime = 0, courseDuration = Double.parseDouble(course.getCourseDuration());
        int day = 0;
        Class c = new Class();

        for (Staff s : lecStaffList) {
            for (String courseCodeStr : s.getCourseCodeList()) {
                if (courseCodeStr.contains(courseCode) && courseCodeStr.contains(courseType)) {
                    for (String lecGroupStr : s.getLecGroupList()) {
                        if (lecGroupStr.contains(courseCode)) {
                            found = false;
                            for (Class dbClass : dbList) {
                                if (course.getCourseID().equals(dbClass.getCourseID()) && s.getStaffID().equals(dbClass.getStaffID()) && lecGroupStr.contains(dbClass.getCohortID())) {
                                    c = new Class(dbClass.getCourseID(), dbClass.getVenueID(), "-", dbClass.getStaffID(), dbClass.getDay(), dbClass.getStartTime(), dbClass.getEndTime(), dbClass.getCourseType());
                                    found = true;
                                    break;
                                }
                            }

                            if (found) {
                                isSame = false;
                                for (int i = 0; i < scheduleList.size(); i++) {
                                    if (lecGroupStr.contains(scheduleList.get(i).getCohortID())) {
                                        isSame = true;
                                        Class temp = new Class(c.getCourseID(), c.getVenueID(), scheduleList.get(i).getGroupID(), c.getStaffID(), c.getDay(), c.getStartTime(), c.getEndTime(), c.getCourseType());
                                        temp.setMoveFlag(false);
                                        scheduleList.get(i).addClassToList(temp);
                                    }
                                }
                            }
                            if (!isSame) {
                                do {
                                    isClash = false;
                                    boolean toLoop = false;
                                    do {
                                        startTime = getRandomStartTimeWithRatio();
                                        endTime = startTime + Double.parseDouble(course.getCourseDuration());
                                        day = getRandomDay();
                                        for (int i = 0; i < scheduleList.size(); i++) {
                                            if (lecGroupStr.contains(scheduleList.get(i).getCohortID())) {
                                                toLoop = false;
                                                if (toBalance) {
                                                    noOfClassPerDay = Math.floor((double) scheduleList.get(i).getRequiredNoOfClass() / (studyDays)) + 1;
                                                }
                                                int classCount = 1;
                                                for (Class temp : scheduleList.get(i).getClassList()) {
                                                    if (temp.getDay() == day && !temp.getCourseType().equals("BLK")) {
                                                        classCount++;
                                                    }
                                                }
                                                if (classCount > noOfClassPerDay) {
                                                    toLoop = true;
                                                }
                                            }
                                        }
                                    } while (toLoop || startTime < studyStart || endTime > studyEnd);

                                    if (s.getBlockDay() == day) {
                                        double startTime1 = s.getBlockStart(), endTime1 = s.getBlockStart() + s.getBlockDuration();
                                        if ((startTime >= startTime1 && startTime < endTime1) || (endTime > startTime1 && endTime <= endTime1) || (startTime1 >= startTime && startTime1 < endTime) || (endTime1 > startTime && endTime1 <= endTime)) {
                                            isClash = true;
                                        }
                                    }

                                    ArrayList<Class> staffClassList = s.getClassList();
                                    if (staffClassList.size() > 0) {
                                        for (int i = 0; i < staffClassList.size(); i++) {
                                            Class temp = staffClassList.get(i);
                                            if (temp.getDay() == day) {
                                                double startTime1 = temp.getStartTime(), endTime1 = temp.getEndTime();

                                                if ((startTime >= startTime1 && startTime < endTime1) || (endTime > startTime1 && endTime <= endTime1) || (startTime1 >= startTime && startTime1 < endTime) || (endTime1 > startTime && endTime1 <= endTime)) {
                                                    if (temp.getStaffID().equals(s.getStaffID())) {
                                                        isClash = true;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    v = getLecVenue(lecGroupStr, courseCode, s.getStaffID());

                                    ArrayList<Class> venueClassList = v.getClassList();
                                    if (venueClassList.size() > 0) {
                                        for (int i = 0; i < venueClassList.size(); i++) {
                                            Class temp = venueClassList.get(i);
                                            if (temp.getDay() == day) {
                                                double startTime1 = temp.getStartTime(), endTime1 = temp.getEndTime();
                                                if ((startTime >= startTime1 && startTime < endTime1) || (endTime > startTime1 && endTime <= endTime1) || (startTime1 >= startTime && startTime1 < endTime) || (endTime1 > startTime && endTime1 <= endTime)) {
                                                    if (temp.getVenueID().equals(v.getVenueID())) {
                                                        isClash = true;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    for (Schedule sch : scheduleList) {
                                        for (Class temp : sch.getClassList()) {
                                            if (temp.getDay() == day) {
                                                double startTime1 = temp.getStartTime(), endTime1 = temp.getEndTime();
                                                if ((startTime >= startTime1 && startTime < endTime1) || (endTime > startTime1 && endTime <= endTime1) || (startTime1 >= startTime && startTime1 < endTime) || (endTime1 > startTime && endTime1 <= endTime)) {
                                                    if (temp.getVenueID().equals(v.getVenueID()) || temp.getStaffID().equals(s.getStaffID())) {
                                                        isClash = true;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    c = new Class(courseID, v.getVenueID(), "-", s.getStaffID(), day, startTime, endTime, courseType);
                                    indexList = new ArrayList();
                                    for (int i = 0; i < scheduleList.size(); i++) {
                                        if (lecGroupStr.contains(scheduleList.get(i).getCohortID())) {
                                            if (isTimeClashWithClassList(scheduleList.get(i).getClassList(), c)) {
                                                isClash = true;
                                                break;
                                            } else {
                                                indexList.add(i);
                                            }
                                        }
                                    }
                                } while (isClash);

                                for (int i : indexList) {
                                    c = new Class(courseID, v.getVenueID(), scheduleList.get(i).getGroupID(), s.getStaffID(), day, startTime, endTime, courseType);
                                    scheduleList.get(i).addClassToList(c);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void assignCourse(int requiredNoOfClass, String groupID, ArrayList<Class> classList, CourseType course) throws IOException {
        boolean isClash, isBreak = false;
        int day, classCount, runCount = 0;
        double startTime, endTime, courseDuration = Double.parseDouble(course.getCourseDuration());
        Class thisClass = new Class();

        Staff s = getStaffWithGroup(course.getCourseType(), course.getCourseCode(), groupID);
        do {
            if (runCount == assignLimit) {
                isBreak = true;
                break;
            } else {
                isClash = false;
                do {
                    if (toBalance) {
                        noOfClassPerDay = Math.floor((double) requiredNoOfClass / (studyDays)) + 1;
                    }
                    classCount = 1;
                    day = getRandomDay();
                    if (course.getCourseType().equalsIgnoreCase("P")) {
                        do {
                            startTime = getRandomStartTimeForLab(courseDuration);
                            endTime = startTime + Double.parseDouble(course.getCourseDuration());
                        } while (endTime > 18);
                    } else {
                        if (countClassBeforeHalfDay(day, classList) < Math.floor(noOfClassPerDay / 2) && runCount < (assignLimit * assignRatio)) {
                            startTime = getRandomStartTimeWithRatio();
                        } else {
                            startTime = getRandomStartTime(courseDuration);
                        }
                        endTime = startTime + Double.parseDouble(course.getCourseDuration());
                    }
                    for (Class temp : classList) {
                        if (temp.getDay() == day && !temp.getCourseType().equals("BLK")) {
                            classCount++;
                        }
                    }
                } while (classCount > noOfClassPerDay || startTime < studyStart || endTime > studyEnd);

                thisClass = new Class(course.getCourseID(), "-", groupID, s.getStaffID(), day, startTime, endTime, course.getCourseType());
                if (isTimeClashWithClassList(classList, thisClass)) {
                    isClash = true;
                } else {
                    if (s.getBlockDay() == thisClass.getDay()) {
                        double startTime1 = s.getBlockStart(), endTime1 = s.getBlockStart() + s.getBlockDuration();
                        if ((startTime >= startTime1 && startTime < endTime1) || (endTime > startTime1 && endTime <= endTime1) || (startTime1 >= startTime && startTime1 < endTime) || (endTime1 > startTime && endTime1 <= endTime)) {
                            isClash = true;
                        }
                    }

                    if (!isClash) {
                        ArrayList<Class> staffClassList = s.getClassList();
                        if (staffClassList != null) {
                            for (int i = 0; i < staffClassList.size(); i++) {
                                Class temp = staffClassList.get(i);
                                if (temp.getDay() == thisClass.getDay()) {
                                    double startTime1 = temp.getStartTime(), endTime1 = temp.getEndTime();
                                    if ((startTime >= startTime1 && startTime < endTime1) || (endTime > startTime1 && endTime <= endTime1) || (startTime1 >= startTime && startTime1 < endTime) || (endTime1 > startTime && endTime1 <= endTime)) {
                                        if (temp.getStaffID().equals(s.getStaffID())) {
                                            isClash = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        for (int i = 0; i < classList.size(); i++) {
                            Class temp = classList.get(i);
                            if (temp.getDay() == thisClass.getDay()) {
                                double startTime1 = temp.getStartTime(), endTime1 = temp.getEndTime();
                                if ((startTime >= startTime1 && startTime < endTime1) || (endTime > startTime1 && endTime <= endTime1) || (startTime1 >= startTime && startTime1 < endTime) || (endTime1 > startTime && endTime1 <= endTime)) {
                                    if (temp.getStaffID() != null && !temp.getStaffID().equals("-")) {
                                        if (temp.getStaffID().equals(s.getStaffID())) {
                                            isClash = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        double startTime1 = thisClass.getStartTime(), endTime1 = thisClass.getEndTime();
                        for (int i = 0; i < scheduleList.size(); i++) {
                            ArrayList<Class> tempList = scheduleList.get(i).getClassList();
                            if (!scheduleList.get(i).getGroupID().equals(thisClass.getGroupID()) && tempList != null) {
                                for (int j = 0; j < tempList.size(); j++) {
                                    Class temp = tempList.get(j);
                                    if (temp.getDay() == thisClass.getDay()) {
                                        double startTime2 = temp.getStartTime(), endTime2 = temp.getEndTime();
                                        if ((startTime2 >= startTime1 && startTime2 < endTime1) || (endTime2 > startTime1 && endTime2 <= endTime1) || (startTime1 >= startTime2 && startTime1 < endTime2) || (endTime1 > startTime2 && endTime1 <= endTime2)) {
                                            if (temp.getStaffID() != null && !temp.getStaffID().equals("-")) {
                                                if (temp.getStaffID().equals(thisClass.getStaffID())) {
                                                    isClash = true;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                runCount++;
            }
        } while (isClash);

        if (!isBreak) {
            classList.add(thisClass);
        }
    }

    public void assignVenue(int index, Class previousClass, Class thisClass) throws IOException, CloneNotSupportedException {
        boolean isClash, isBreak = false;
        int runCount = 0;
        double startTime2 = thisClass.getStartTime(), endTime2 = thisClass.getEndTime();
        String courseCode = searchCourseCode(thisClass);
        Venue v = new Venue();

        do {
            if (runCount == assignLimit) {
                isBreak = true;
                break;
            } else {
                isClash = false;

                v = getCourseVenue(thisClass.getCourseType(), courseCode);

                if (previousClass != null) {
                    if (!previousClass.getCourseType().equals("BLK")) {
                        if (previousClass.getDay() == thisClass.getDay() && previousClass.getEndTime() == thisClass.getStartTime()) {
                            double moveDuration = 0.5;
                            Class newC = thisClass.clone();
                            boolean nextLoop = false;
                            if (previousClass.getCourseType().equals(thisClass.getCourseType())) {
                                Venue previousV = searchVenue(previousClass.getCourseType(), previousClass.getVenueID());
                                if (previousV.getVenueID() != null && !previousV.getVenueID().equals("-")) {
                                    if (runCount < firstVLimit) {
                                        if (thisClass.getCourseType().equals("P") && previousClass.getCourseType().equals("P")) {
                                            if (previousV.getCourseCodeList().contains(courseCode)) {
                                                v = previousV;
                                            }
                                        } else {
                                            v = previousV;
                                        }
                                    } else if (runCount < secondVLimit) {
                                        if (!thisClass.getCourseType().equals("P")) {
                                            v = getTutVenueWithBlock(previousV.getBlock());
                                        } else {
                                            do {
                                                nextLoop = false;
                                                moveDuration += 0.5;
                                                newC.moveRight(0.5);
                                                if (isClashWithOtherLists(newC)) {
                                                    nextLoop = true;
                                                } else {
                                                    scheduleList.get(index).moveRight(thisClass, moveDuration, studyEnd);
                                                }
                                            } while (nextLoop);
                                        }
                                    } else {
                                        do {
                                            nextLoop = false;
                                            moveDuration += 0.5;
                                            newC.moveRight(0.5);
                                            if (isClashWithOtherLists(newC)) {
                                                nextLoop = true;
                                            } else {
                                                scheduleList.get(index).moveRight(thisClass, moveDuration, studyEnd);
                                            }
                                        } while (nextLoop);
                                    }
                                }
                            } else {
                                do {
                                    nextLoop = false;
                                    moveDuration += 0.5;
                                    newC.moveRight(0.5);
                                    if (isClashWithOtherLists(newC)) {
                                        nextLoop = true;
                                    } else {
                                        scheduleList.get(index).moveRight(thisClass, moveDuration, studyEnd);
                                    }
                                } while (nextLoop);
                            }
                        }
                    }
                }

                ArrayList<Class> venueClassList = v.getClassList();
                if (venueClassList != null) {
                    for (int i = 0; i < venueClassList.size(); i++) {
                        Class temp = venueClassList.get(i);
                        if (temp.getDay() == thisClass.getDay()) {
                            double startTime1 = temp.getStartTime(), endTime1 = temp.getEndTime();

                            if ((startTime2 >= startTime1 && startTime2 < endTime1) || (endTime2 > startTime1 && endTime2 <= endTime1) || (startTime1 >= startTime2 && startTime1 < endTime2) || (endTime1 > startTime2 && endTime1 <= endTime2)) {
                                if (temp.getVenueID().equals(v.getVenueID())) {
                                    isClash = true;
                                    break;
                                }
                            }
                        }
                    }
                }

                if (!isClash) {
                    for (int i = 0; i < scheduleList.size(); i++) {
                        ArrayList<Class> classList = scheduleList.get(i).getClassList();
                        if (classList != null) {
                            for (int j = 0; j < classList.size(); j++) {
                                Class temp = classList.get(j);
                                if (temp.getDay() == thisClass.getDay()) {
                                    double startTime1 = temp.getStartTime(), endTime1 = temp.getEndTime();
                                    if ((startTime2 >= startTime1 && startTime2 < endTime1) || (endTime2 > startTime1 && endTime2 <= endTime1) || (startTime1 >= startTime2 && startTime1 < endTime2) || (endTime1 > startTime2 && endTime1 <= endTime2)) {
                                        if (temp.getVenueID() != null && !temp.getVenueID().equals("-")) {
                                            if (temp.getVenueID().equals(v.getVenueID())) {
                                                isClash = true;
                                                break;
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
                runCount++;
            }
        } while (isClash);

        if (!isBreak) {
            thisClass.setVenueID(v.getVenueID());
        }
    }

    public double getRandomMoveDuration() {
        if (maxBreak <= 1) {
            if (rand.nextBoolean()) {
                return 0.5;
            } else {
                return 1;
            }
        } else {
            if (rand.nextBoolean()) {
                return rand.nextInt((int) (maxBreak)) + 1;
            } else {
                return rand.nextInt((int) (maxBreak)) + 0.5;
            }
        }
    }

    public double getRandomStartTimeWithRatio() {
        if (rand.nextBoolean()) {
            return rand.nextInt((int) (Math.ceil((studyEnd - studyStart) * timeRatio))) + studyStart;
        } else {
            return (rand.nextInt((int) (Math.ceil((studyEnd - studyStart) * timeRatio))) + studyStart) + 0.5;
        }
    }

    public double getRandomStartTime(double classDuration) {
        if (rand.nextBoolean()) {
            return rand.nextInt((int) Math.floor(studyEnd - studyStart - classDuration)) + studyStart;
        } else {
            return (rand.nextInt((int) Math.floor(studyEnd - studyStart - classDuration)) + studyStart) + 0.5;
        }
    }

    public double getRandomStartTimeForLab(double classDuration) {
        if (rand.nextBoolean()) {
            return rand.nextInt((int) Math.floor(18 - studyStart - classDuration)) + studyStart;
        } else {
            return (rand.nextInt((int) Math.floor(18 - studyStart - classDuration)) + studyStart) + 0.5;
        }
    }

    public int getRandomDay() {
        return rand.nextInt(studyDays) + 1;
    }

    public ArrayList<Staff> getLecStaffList(String courseCode, String courseType) throws IOException {
        ArrayList<Staff> qualifiedList = new ArrayList();
        for (Staff s : staffList) {
            for (String courseCodeList : s.getCourseCodeList()) {
                if (courseCodeList.contains(courseCode) && courseCodeList.contains(courseType)) {
                    qualifiedList.add(s);
                }
            }
        }
        if (qualifiedList.isEmpty()) {
            errorCode = 1;
            errorMsg = "Unable to generate schedule due to staff linking error for course code." + "\\nCourse Code: " + courseCode + courseType;
            System.out.println(errorMsg);
            FacesContext.getCurrentInstance().getExternalContext().redirect("menu.xhtml");
        }
        return qualifiedList;
    }

    public Staff getStaffWithGroup(String courseType, String courseCode, String groupID) throws IOException {
        ArrayList<Staff> qualifiedList = new ArrayList();
        for (Staff s : staffList) {
            for (String courseCodeList : s.getCourseCodeList()) {
                if (courseCodeList.contains(courseCode) && courseCodeList.contains(courseType)) {
                    switch (courseType) {
                        case "P":
                            for (String pracGroupList : s.getPracGroupList()) {
                                if (pracGroupList.contains(courseCode) && pracGroupList.contains(groupID)) {
                                    qualifiedList.add(s);
                                }
                            }
                            break;
                        default:
                            for (String tutGroupList : s.getTutGroupList()) {
                                if (tutGroupList.contains(courseCode) && tutGroupList.contains(groupID)) {
                                    qualifiedList.add(s);
                                }
                            }
                            break;
                    }
                }
            }
        }
        if (qualifiedList.isEmpty()) {
            errorCode = 1;
            errorMsg = "Unable to generate schedule due to staff linking error for course code." + "\\nGroup ID: " + groupID + "\\nCourse Code: " + courseCode + courseType;
            System.out.println(errorMsg);
            FacesContext.getCurrentInstance().getExternalContext().redirect("menu.xhtml");
        }
        return qualifiedList.get(rand.nextInt(qualifiedList.size()));
    }

    public Venue getLecVenue(String lecGroupStr, String courseCode, String staffID) throws IOException {
        ArrayList<Venue> qualifiedList = new ArrayList();
        int totalSize = getTotalSize(lecGroupStr, courseCode);
        Venue venue = new Venue();

        if (totalSize <= 30) {
            qualifiedList = roomList;
        } else {
            for (Venue v : hallList) {
                if (v.getCapacity() >= totalSize && getVenueUseCount(v.getVenueID()) <= 10) {
                    qualifiedList.add(v);
                }
            }
        }

        if (qualifiedList.isEmpty()) {
            errorCode = 1;
            errorMsg = "Unable to generate schedule due to lecture hall capacity error. \\nStaff ID: " + staffID + "\\nCourse Code & Cohort IDs: " + lecGroupStr + "";
            System.out.println(errorMsg);
            FacesContext.getCurrentInstance().getExternalContext().redirect("menu.xhtml");
        } else {
            venue = qualifiedList.get(rand.nextInt(qualifiedList.size()));
        }
        return venue;
    }

    public int getVenueUseCount(String venueID) {
        ArrayList<String> courseIDList = new ArrayList();
        for (Schedule s : scheduleList) {
            for (Class c : s.getClassList()) {
                if (c.getVenueID().equals(venueID) && !courseIDList.contains(c.getCourseID())) {
                    courseIDList.add(c.getCourseID());
                }
            }
        }
        return courseIDList.size();
    }

    public Venue getCourseVenue(String courseType, String courseCode) throws IOException {
        Venue venue = new Venue();
        ArrayList<Venue> qualifiedList = new ArrayList();
        if (courseType.equals("P")) {
            for (Venue v : labList) {
                if (v.getCourseCodeList().contains(courseCode)) {
                    qualifiedList.add(v);
                }
            }
            if (qualifiedList.isEmpty()) {
                for (Venue v : labList) {
                    if (v.getCourseCodeList().equals("-")) {
                        qualifiedList.add(v);
                    }
                }
                if (qualifiedList.isEmpty()) {
                    qualifiedList = allPurposeLabList;
                }
            }
        } else {
            venue = roomList.get(rand.nextInt(roomList.size()));
        }
        if (courseType.equals("P")) {
            if (qualifiedList.isEmpty()) {
                errorCode = 1;
                errorMsg = "Lab venue error for course code " + courseCode + courseType + ".";
                System.out.println(errorMsg);
                FacesContext.getCurrentInstance().getExternalContext().redirect("menu.xhtml");
            } else {
                venue = qualifiedList.get(rand.nextInt(qualifiedList.size()));
            }
        }
        return venue;
    }

    public Venue getTutVenueWithBlock(String block) {
        ArrayList<Venue> qualifiedList = new ArrayList();
        for (Venue v : roomList) {
            if (v.getBlock().equals(block)) {
                qualifiedList.add(v);
            }
        }
        return qualifiedList.get(rand.nextInt(qualifiedList.size()));
    }

    public String searchCourseCode(Class c) {
        ArrayList<CourseType> tempList;
        String courseCode = "";
        if (c.getCourseType().equals("L")) {
            tempList = lecList;
        } else {
            tempList = courseList;
        }
        for (CourseType ct : tempList) {
            if (ct.getCourseID().equals(c.getCourseID())) {
                courseCode = ct.getCourseCode();
            }
        }
        return courseCode;
    }

    public Staff searchStaff(String staffID) {
        Staff staff = new Staff();
        for (Staff s : staffList) {
            if (s.getStaffID().equals(staffID)) {
                staff = s;
            }
        }
        return staff;
    }

    public Venue searchVenue(String courseType, String venueID) {
        Venue venue = new Venue();
        ArrayList<Venue> tempList;
        switch (courseType) {
            case "P":
                tempList = labList;
                break;
            case "L":
                tempList = hallList;
                break;
            default:
                tempList = roomList;
                break;
        }
        for (Venue v : tempList) {
            if (v.getVenueID().equals(venueID)) {
                venue = v;
            }
        }
        return venue;
    }

    public boolean isClashWithOtherLists(Class c) {
        boolean found = false;
        for (Schedule s : scheduleList) {
            for (Class d : s.getClassList()) {
                if (d.getDay() == c.getDay() && !c.getGroupID().equals(d.getGroupID())) {
                    double startTime = c.getStartTime(), endTime = c.getEndTime(), tempStart = d.getStartTime(), tempEnd = d.getEndTime();
                    if ((startTime >= tempStart && startTime < tempEnd) || (endTime > tempStart && endTime <= tempEnd) || (tempStart >= startTime && tempStart < endTime) || (tempEnd > startTime && tempEnd <= endTime)) {
                        if (c.getStaffID().equals(d.getStaffID()) || c.getVenueID().equals(d.getVenueID())) {
                            found = true;
                            break;
                        }
                    }
                }
            }
        }
        return found;
    }

    public boolean isTimeClashWithClassList(ArrayList<Class> classList, Class c) {
        boolean found = false;
        for (int i = 0; i < classList.size(); i++) {
            if (classList.get(i).getDay() == c.getDay()) {
                double startTime = c.getStartTime(), endTime = c.getEndTime(), tempStart = classList.get(i).getStartTime(), tempEnd = classList.get(i).getEndTime();
                if ((startTime >= tempStart && startTime < tempEnd) || (endTime > tempStart && endTime <= tempEnd) || (tempStart >= startTime && tempStart < endTime) || (tempEnd > startTime && tempEnd <= endTime)) {
                    found = true;
                    break;
                }
            }
        }
        return found;
    }

    public boolean hasClashWithinList() {
        boolean isClash = false;
        for (int i = 0; i < scheduleList.size(); i++) {
            ArrayList<Class> classList = scheduleList.get(i).getClassList();
            for (int index1 = 0; index1 < classList.size(); index1++) {
                for (int index2 = index1 + 1; index2 < classList.size() - 1; index2++) {
                    Class class1 = classList.get(index1), class2 = classList.get(index2);
                    if (class1.getDay() == class2.getDay()) {
                        if ((class2.getStartTime() >= class1.getStartTime() && class2.getStartTime() < class1.getEndTime()) || (class2.getEndTime() > class1.getStartTime() && class2.getEndTime() <= class1.getEndTime()) || (class1.getStartTime() >= class2.getStartTime() && class1.getStartTime() < class2.getEndTime()) || (class1.getEndTime() > class2.getStartTime() && class1.getEndTime() <= class2.getEndTime())) {
                            //System.out.println("Class 1: " + class1.getCourseID() + class1.getCourseType() + "," + class1.getStartTime() + "-" + class1.getEndTime());
                            //System.out.println("Class 2: " + class2.getCourseID() + class2.getCourseType() + "," + class2.getStartTime() + "-" + class2.getEndTime());
                            isClash = true;
                            break;
                        }
                    }
                }
            }
        }
        return isClash;
    }

    public boolean hasClashWithDBList() throws SQLException {
        boolean isClash = false;
        if (!dbList.isEmpty()) {
            for (int i = 0; i < scheduleList.size(); i++) {
                ArrayList<Class> classList = scheduleList.get(i).getClassList();
                for (Class c : classList) {
                    for (Class d : dbList) {
                        if (c.getDay() == d.getDay()) {
                            double startTime1 = c.getStartTime(), endTime1 = c.getEndTime(), startTime2 = d.getStartTime(), endTime2 = d.getEndTime();

                            if (c.getCourseType().equals("L") && d.getCourseType().equals("L")) {
                                if (!c.getCourseID().equals(d.getCourseID())) {
                                    if ((startTime2 >= startTime1 && startTime2 < endTime1) || (endTime2 > startTime1 && endTime2 <= endTime1) || (startTime1 >= startTime2 && startTime1 < endTime2) || (endTime1 > startTime2 && endTime1 <= endTime2)) {
                                        if (c.getVenueID().equals(d.getVenueID()) || c.getStaffID().equals(d.getStaffID())) {
                                            isClash = true;
                                            break;
                                        }
                                    }
                                } else {
                                    if (c.getStartTime() != d.getStartTime() || c.getEndTime() != d.getEndTime()) {
                                        if ((startTime2 >= startTime1 && startTime2 < endTime1) || (endTime2 > startTime1 && endTime2 <= endTime1) || (startTime1 >= startTime2 && startTime1 < endTime2) || (endTime1 > startTime2 && endTime1 <= endTime2)) {
                                            if (c.getVenueID().equals(d.getVenueID()) || c.getStaffID().equals(d.getStaffID())) {
                                                isClash = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            } else {
                                if ((startTime2 >= startTime1 && startTime2 < endTime1) || (endTime2 > startTime1 && endTime2 <= endTime1) || (startTime1 >= startTime2 && startTime1 < endTime2) || (endTime1 > startTime2 && endTime1 <= endTime2)) {
                                    if (c.getVenueID().equals(d.getVenueID()) || c.getStaffID().equals(d.getStaffID())) {
                                        isClash = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return isClash;
    }

    public boolean hasClashWithBlockClass() {
        boolean isClash = false;
        double tempStart, tempEnd, startTime, endTime;
        if (blockClass != null) {
            for (int i = 0; i < scheduleList.size(); i++) {
                ArrayList<Class> classList = scheduleList.get(i).getClassList();
                for (Class c : classList) {
                    tempStart = c.getStartTime();
                    tempEnd = c.getEndTime();

                    Staff staff = searchStaff(c.getStaffID());
                    startTime = staff.getBlockStart();
                    endTime = staff.getBlockStart() + staff.getBlockDuration();

                    if (c.getDay() == staff.getBlockDay()) {
                        if ((startTime >= tempStart && startTime < tempEnd) || (endTime > tempStart && endTime <= tempEnd) || (tempStart >= startTime && tempStart < endTime) || (tempEnd > startTime && tempEnd <= endTime)) {
                            isClash = true;
                            break;
                        }
                    }

                    startTime = blockClass.getStartTime();
                    endTime = blockClass.getEndTime();
                    if (c.getDay() == blockClass.getDay()) {
                        if ((startTime >= tempStart && startTime < tempEnd) || (endTime > tempStart && endTime <= tempEnd) || (tempStart >= startTime && tempStart < endTime) || (tempEnd > startTime && tempEnd <= endTime)) {
                            isClash = true;
                            break;
                        }
                    }
                }
            }
        }

        return isClash;
    }

    public boolean hasIncompleteClassData() {
        boolean hasIncomplete = false;
        for (int i = 0; i < scheduleList.size(); i++) {
            ArrayList<Class> classList = scheduleList.get(i).getClassList();
            for (Class c : classList) {
                if (c.getVenueID() == null || c.getStaffID() == null || c.getVenueID().equals("-") || c.getStaffID().equals("-")) {
//                    System.out.println("Group ID: " + c.getGroupID());
//                    System.out.println("Course ID: " + c.getCourseID() + c.getCourseType());
                    hasIncomplete = true;
                    break;
                }
            }
        }
        return hasIncomplete;
    }

    public boolean hasInvalidTime() {
        boolean hasInvalid = false;
        for (int i = 0; i < scheduleList.size(); i++) {
            ArrayList<Class> classList = scheduleList.get(i).getClassList();
            for (Class c : classList) {
                if (c.getStartTime() < studyStart || c.getEndTime() > studyEnd) {
                    System.out.println("Course ID: " + c.getCourseID() + c.getCourseType());
                    System.out.println("Start Time: " + c.getStartTime());
                    System.out.println("End Time: " + c.getEndTime());
                    hasInvalid = true;
                    break;
                }
            }
        }
        return hasInvalid;
    }

    public boolean hasEnoughClass() {
        boolean hasEnough = true;
        for (int i = 0; i < scheduleList.size(); i++) {
            if (scheduleList.get(i).getNoOfClass() < scheduleList.get(i).getRequiredNoOfClass()) {
                hasEnough = false;
                //System.out.println("Group ID: " + scheduleList.get(i).getGroupID());
                //System.out.println("Actual No: " + scheduleList.get(i).getNoOfClass());
                //System.out.println("Required No: " + scheduleList.get(i).getRequiredNoOfClass());
                //for (Class c : scheduleList.get(i).getClassList()) {
                //    System.out.println("Course ID (Staff ID): " + c.getCourseID() + c.getCourseType() + " (" + c.getStaffID() + ")" + " Venue : " + c.getVenueID() + " Day & Time: " + c.getDay() + " (" + c.getStartTime() + "-" + c.getEndTime() + ")");
                //}
            }
        }
        return hasEnough;
    }

    public boolean hasInvalidNoOfClass() {
        boolean hasInvalid = false;
        for (int i = 0; i < scheduleList.size(); i++) {
            ArrayList<Class> classList = scheduleList.get(i).getClassList();
            if (toBalance) {
                noOfClassPerDay = Math.floor((double) scheduleList.get(i).getRequiredNoOfClass() / studyDays) + 1;
            }
            int[] noOfClasses = new int[7];
            for (Class c : classList) {
                switch (c.getDay()) {
                    case 0:
                        noOfClasses[0]++;
                        break;
                    case 1:
                        noOfClasses[1]++;
                        break;
                    case 2:
                        noOfClasses[2]++;
                        break;
                    case 3:
                        noOfClasses[3]++;
                        break;
                    case 4:
                        noOfClasses[4]++;
                        break;
                    case 5:
                        noOfClasses[5]++;
                        break;
                    case 6:
                        noOfClasses[6]++;
                        break;
                }
                for (int no : noOfClasses) {
                    if (no > noOfClassPerDay) {
                        hasInvalid = true;
                        break;
                    }
                }
            }
        }
        return hasInvalid;
    }

    public boolean hasLongDurationClass() {
        boolean hasLongDuration = false;
        int classCount = 0;
        for (int i = 0; i < scheduleList.size(); i++) {
            ArrayList<Class> classList = scheduleList.get(i).getClassList();
            OUTER:
            for (int j = 0; j < classList.size() - 1; j++) {
                int nextIndex = j + 1;
                Class c1 = classList.get(j), c2 = classList.get(nextIndex);
                double totalDuration = c1.getDuration(), endTime = c1.getEndTime();
                while (nextIndex < classList.size() && c1.getDay() == c2.getDay() && c2.getStartTime() == endTime) {
                    endTime = c2.getEndTime();
                    totalDuration += c2.getDuration();
                    nextIndex++;
                    if (totalDuration > longDurationLimit) {
                        classCount++;
                        break OUTER;
                    } else {
                        if (nextIndex < classList.size()) {
                            c2 = classList.get(nextIndex);
                        }
                    }
                }

            }
        }
        if (classCount > classLimit) {
            hasLongDuration = true;
        }
        return hasLongDuration;
    }

    public boolean hasInvalidBreak() {
        boolean hasInvalid = false;
        for (int index = 0; index < scheduleList.size(); index++) {
            ArrayList<Class> classList = scheduleList.get(index).getClassList();
            for (int i = 0; i < classList.size() - 1; i++) {
                int j = i + 1;
                Class c1 = classList.get(i), c2 = classList.get(j);
                if (c1.getDay() == c2.getDay()) {
                    if ((c2.getStartTime() - c1.getEndTime()) > maxBreak) {
                        hasInvalid = true;
                        break;
                    }
                }
            }
        }
        return hasInvalid;
    }

    //Method to start the algorithm task from JSF
    public void start() throws Exception {
        if (initialize()) {
            int runCount = 0, loopCount = 1;
            double oriMaxBreak = maxBreak;
            int oriStudyDays = studyDays;
            boolean toRestart, isBreak = false;

            do {
                toRestart = false;
                if (runCount == exitLimit) {
                    if (studyDays == 6 && maxBreak == 4) {
                        isBreak = true;
                        break;
                    } else {
                        if (maxBreak < 4.0) {
                            if (studyDays <= 5) {
                                maxBreak += 0.5;
                                studyDays = oriStudyDays;
                                runCount = 0;
                                toRestart = true;
                                loopCount++;
                            }
                        } else {
                            studyDays++;
                            maxBreak = oriMaxBreak;
                            runCount = 0;
                            toRestart = true;
                            loopCount++;
                        }
                    }
                } else {
                    if (runCount != 0 && studyDays < 5 && (runCount % Math.floor(exitLimit * 0.25)) == 0) {
                        studyDays++;
                    } else if (runCount != 0 && studyDays == 6 && (runCount % Math.floor(exitLimit * 0.25)) == 0) {
                        maxBreak += 0.5;
                    }
                    allocation();
                    runCount++;
                    System.out.println("Loop " + loopCount + " Run " + runCount + " (StudyDays: " + studyDays + " - MaxBreak: " + maxBreak + " h)");
                    System.out.println(hasEnoughClass() + "-" + hasIncompleteClassData() + "-" + hasInvalidTime() + "-" + hasInvalidNoOfClass() + "-" + hasLongDurationClass() + "-" + hasClashWithinList() + "-" + hasClashWithOtherLists() + "-" + hasClashWithBlockClass() + "-" + hasClashWithDBList());
                }
            } while (toRestart || !hasEnoughClass() || hasIncompleteClassData() || hasInvalidTime() || hasInvalidNoOfClass() || hasLongDurationClass() || hasClashWithinList() || hasClashWithOtherLists() || hasClashWithBlockClass() || hasClashWithDBList());

            if (!isBreak) {
                insertData();
                printClass();
                FacesContext.getCurrentInstance().getExternalContext().redirect("ViewTimetable.xhtml");
            } else {
                errorCode = 1;
                errorMsg = "Unable to generate schedule, possibly due to insufficient staff or venue provided.";
                System.out.println(errorMsg);
                FacesContext.getCurrentInstance().getExternalContext().redirect("menu.xhtml");
            }
        }
    }

    public void printClass() {
        System.out.println("");
        for (int i = 0; i < scheduleList.size(); i++) {
            ArrayList<Class> classList = scheduleList.get(i).getClassList();
            for (int j = 0; j < classList.size(); j++) {
                Class c = classList.get(j);
                if (j == 0) {
                    System.out.println("Group ID : " + c.getGroupID());
                }
                System.out.println("Course ID (Staff ID): " + c.getCourseID() + c.getCourseType() + " (" + c.getStaffID() + ")" + " Venue : " + c.getVenueID() + " Day & Time: " + c.getDay() + " (" + c.getStartTime() + "-" + c.getEndTime() + ")");

            }
            System.out.println("-----------------------------------------------------------------------------");
        }
    }

    public static void main(String args[]) throws Exception {
        SchedulingAlgorithm sa = new SchedulingAlgorithm();
        sa.start();
    }

}
