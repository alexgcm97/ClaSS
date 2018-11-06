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
    private ArrayList<TutorialGroup> groupList;
    private ArrayList<Venue> roomList, labList, hallList;
    private ArrayList<Schedule> scheduleList;

    private int studyDays, blockDay = 0, errorCode = 0;
    private boolean toBalance = false;
    private double studyStart, studyEnd, blockStart, blockEnd, maxBreak = 99, noOfClassPerDay = 99;
    private Class blockClass;
    private ArrayList<Class> dbList = new ArrayList();

    private final int assignLimit = 120, firstVLimit = 30, secondVLimit = 60, exitLimit = 140000;
    private final ClassDA cda = new ClassDA();
    private final VenueDA vda = new VenueDA();
    private final StaffDA sda = new StaffDA();
    private final TutorialGroupDA tgda = new TutorialGroupDA();

    private final String filePath = XMLPath.getXMLPath();

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public void initialize() throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Element e;

        String fileName = filePath + "TutorialGroup.xml";
        Document doc = dBuilder.parse(fileName);
        groupList = new ArrayList();
        NodeList nodes = doc.getElementsByTagName("tutorialGroup");

        for (int i = 0; i < nodes.getLength(); i++) {
            e = (Element) nodes.item(i);

            TutorialGroup tg = new TutorialGroup(e.getAttribute("groupID"), Integer.parseInt(e.getElementsByTagName("groupNumber").item(0).getTextContent()), Integer.parseInt(e.getElementsByTagName("size").item(0).getTextContent()), e.getElementsByTagName("cohortID").item(0).getTextContent(), e.getElementsByTagName("courseCodeList").item(0).getTextContent());
            cda.deleteRecords(tg.getGroupID());
            groupList.add(tg);
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

        fileName = filePath + "Configuration.xml";
        doc = dBuilder.parse(fileName);
        nodes = doc.getElementsByTagName("configuration");

        e = (Element) nodes.item(0);

        studyDays = Integer.parseInt(e.getElementsByTagName("studyDays").item(0).getTextContent());
        studyStart = Double.parseDouble(e.getElementsByTagName("startTime").item(0).getTextContent());
        studyEnd = Double.parseDouble(e.getElementsByTagName("endTime").item(0).getTextContent());

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

        fileName = filePath + "Venue.xml";
        doc = dBuilder.parse(fileName);
        roomList = new ArrayList();
        labList = new ArrayList();
        hallList = new ArrayList();
        nodes = doc.getElementsByTagName("venue");

        for (int i = 0; i < nodes.getLength(); i++) {
            e = (Element) nodes.item(i);

            Venue v = new Venue(e.getAttribute("venueID"), e.getElementsByTagName("block").item(0).getTextContent(), e.getElementsByTagName("venueType").item(0).getTextContent(), Integer.parseInt(e.getElementsByTagName("capacity").item(0).getTextContent()), e.getElementsByTagName("courseCodeList").item(0).getTextContent());
            v.setClassList(vda.getClassList(v.getVenueID()));
            if (v.getVenueType().equals("Room")) {
                roomList.add(v);
            } else if (v.getVenueType().equals("Lab")) {
                labList.add(v);
            } else if (v.getVenueType().equals("Hall")) {
                hallList.add(v);
            }
        }

        //Initialize Schedule List
        scheduleList = new ArrayList();
        //Creating Schedule List for each Group
        for (int i = 0; i < groupList.size(); i++) {
            ArrayList<Class> classList = new ArrayList();
            Schedule s = new Schedule(groupList.get(i).getGroupID(), groupList.get(i).getCohortID(), classList);
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
        dbList = cda.getAll();
    }

    public void allocation() throws IOException {
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

        //Optimize Lecture Classes Break
        if (maxBreak != 99) {
            sortList();
            optimizeBreak(1);
            sortList();
            optimizeBreak(2);
        }

        //Assign Other Schedules, ex: Tutorial, Practical
        for (int i = 0; i < courseList.size(); i++) {
            for (int j = 0; j < scheduleList.size(); j++) {
                TutorialGroup tg = searchTutorialGroup(scheduleList.get(j).getGroupID());
                if (tg.getCourseCodeList().contains(courseList.get(i).getCourseCode())) {
                    assignCourse(scheduleList.get(j).getRequiredNoOfClass(), scheduleList.get(j).getGroupID(), scheduleList.get(j).getClassList(), courseList.get(i));
                }
            }
        }

        //Optimize All Courses Break
        if (maxBreak != 99) {
            sortList();
            optimizeBreak(3);
            sortList();
            optimizeBreak(4);
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
                    assignVenue(previousClass, thisClass);
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
    public boolean isClashWithOtherLists() {
        boolean isClash = false;
        for (int i = 0; i < scheduleList.size() - 1; i++) {
            int j = i + 1;
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
                                            isClash = true;
                                            break;
                                        }
                                    }
                                } else {
                                    if (class1.getStartTime() != class2.getStartTime() || class1.getEndTime() != class2.getEndTime()) {
                                        if ((startTime2 >= startTime1 && startTime2 < endTime1) || (endTime2 > startTime1 && endTime2 <= endTime1) || (startTime1 >= startTime2 && startTime1 < endTime2) || (endTime1 > startTime2 && endTime1 <= endTime2)) {
                                            if (class1.getVenueID().equals(class2.getVenueID()) || class1.getStaffID().equals(class2.getStaffID())) {
                                                isClash = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            } else {
                                if ((startTime2 >= startTime1 && startTime2 < endTime1) || (endTime2 > startTime1 && endTime2 <= endTime1) || (startTime1 >= startTime2 && startTime1 < endTime2) || (endTime1 > startTime2 && endTime1 <= endTime2)) {
                                    if (class1.getVenueID().equals(class2.getVenueID()) || class1.getStaffID().equals(class2.getStaffID())) {
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

    public void optimizeBreak(int step) {
        double moveDuration = 0;
        ArrayList<Class> classList;
        switch (step) {
            case 1:
                for (int i = 1; i <= studyDays; i++) {
                    for (int index = 0; index < scheduleList.size(); index++) {
                        classList = scheduleList.get(index).getClassList();
                        if (countClassBeforeHalfDay(i, classList) == 0) {
                            for (Class c : classList) {
                                if (c.getDay() == i) {
                                    if (!c.getCourseType().equals("BLK")) {
                                        if (moveDuration == 0) {
                                            moveDuration = c.getStartTime() - studyStart;
                                        }
                                        c.moveLeft(moveDuration);
                                    }
                                }
                            }
                            moveDuration = 0;
                        }
                    }
                }
                break;
            case 2:
                ArrayList<Class> classCheckList = new ArrayList();
                for (int i = 0; i < scheduleList.size(); i++) {
                    classList = scheduleList.get(i).getClassList();
                    for (Class c : classList) {
                        boolean exist = false;
                        for (Class d : classCheckList) {
                            if (c.getDay() == d.getDay() && c.getCourseID().equals(d.getCourseID()) && c.getStaffID().equals(d.getStaffID()) && c.getStartTime() == d.getStartTime() && c.getEndTime() == d.getEndTime()) {
                                exist = true;
                            }
                        }
                        if (!exist) {
                            c.setOriStartTime(c.getStartTime());
                            c.setOriEndTime(c.getEndTime());
                            classCheckList.add(c);
                        }
                    }
                }

                classCheckList.sort((Class o1, Class o2) -> {
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

                for (int thisIndex = 0; thisIndex < classCheckList.size() - 1; thisIndex++) {
                    int nextIndex = thisIndex + 1;
                    Class c1 = classCheckList.get(thisIndex), c2 = classCheckList.get(nextIndex);
                    if (!c1.getCourseType().equals("BLK")) {
                        if (c2.getCourseType().equals("BLK")) {
                            if (nextIndex < classCheckList.size() - 1) {;
                                c2 = classCheckList.get(nextIndex + 1);
                            } else {
                                break;
                            }
                        }
                        if (c1.getDay() == c2.getDay()) {
                            double breakTime = c2.getStartTime() - c1.getEndTime();
                            if (c1.getVenueID().equals(c2.getVenueID())) {
                                moveDuration = breakTime;
                                if (thisIndex > 0) {
                                    Class previousC = classCheckList.get(thisIndex - 1);
                                    if (previousC.getDay() == c1.getDay()) {
                                        if (previousC.getEndTime() == c1.getStartTime()) {
                                            double totalDuration = previousC.getDuration() + c1.getDuration() + c2.getDuration();
                                            if (totalDuration > 4) {
                                                moveDuration = breakTime - getRandomMoveDuration();
                                            }
                                        }
                                    }
                                }
                            } else {
                                if (breakTime == 0) {
                                    moveDuration = 0.5;
                                } else if (breakTime > maxBreak) {
                                    moveDuration = breakTime - getRandomMoveDuration();
                                }
                            }
                            c2.moveLeft(moveDuration);
                        }
                    }
                }

                for (int index = 0; index < scheduleList.size(); index++) {
                    classList = scheduleList.get(index).getClassList();
                    for (Class c : classList) {
                        for (Class d : classCheckList) {
                            if (c.getDay() == d.getDay() && c.getCourseID().equals(d.getCourseID()) && c.getStaffID().equals(d.getStaffID()) && d.getOriStartTime() == c.getStartTime() && d.getOriEndTime() == c.getEndTime()) {
                                if (c.isMoveFlag()) {
                                    c.setStartTime(d.getStartTime());
                                    c.setEndTime(d.getEndTime());
                                }
                            }
                        }
                    }
                }
                break;
            case 3:
                for (int i = 1; i <= studyDays; i++) {
                    for (int index = 0; index < scheduleList.size(); index++) {
                        classList = scheduleList.get(index).getClassList();
                        if (countClassBeforeHalfDay(i, classList) == 0 && countLecClassPerDay(i, classList) == 0) {
                            for (Class c : classList) {
                                if (c.getDay() == i) {
                                    if (!c.getCourseType().equals("BLK")) {
                                        if (moveDuration == 0) {
                                            moveDuration = c.getStartTime() - studyStart;
                                        }
                                        c.moveLeft(moveDuration);
                                    }
                                }
                            }
                            moveDuration = 0;
                        }
                    }
                }
                break;
            case 4:
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
                                    moveDuration = breakTime - getRandomMoveDuration();
                                    if (breakTime > maxBreak) {
                                        if (c1.getCourseType().equals("L") && !c2.getCourseType().equals("L")) {
                                            c2.moveLeft(moveDuration);
                                        } else if (c2.getCourseType().equals("L") && !c1.getCourseType().equals("L")) {
                                            c1.moveRight(moveDuration);
                                        } else if (!c1.getCourseType().equals("L") && !c2.getCourseType().equals("L")) {
                                            if (c1.getStartTime() == studyStart) {
                                                c1.moveRight(moveDuration);
                                            } else {
                                                c2.moveLeft(moveDuration);
                                            }
                                        }
                                    } else if (breakTime == 0) {
                                        moveDuration = 0.5;
                                        if (c1.getCourseType().equals("L") && !c2.getCourseType().equals("L")) {
                                            c2.moveRight(moveDuration);
                                        } else {
                                            if (c1.getStartTime() == studyStart) {
                                                c1.moveRight(moveDuration);
                                            } else {
                                                c1.moveLeft(moveDuration);
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

    public int getTotalSize(String lecGroupStr, String courseCode, String staffID) {
        int totalSize = 0;
        ArrayList<TutorialGroup> tgList = tgda.getGroupsWithCourseCode(courseCode);
        for (TutorialGroup tg : tgList) {
            if (lecGroupStr.contains(courseCode) && lecGroupStr.contains(tg.getCohortID())) {
                totalSize += tg.getSize();
            }
        }
        return totalSize;
    }

    public void assignLecture(CourseType course) throws IOException {
        boolean isClash, found = false, isSame = false;

        String courseID = course.getCourseID(), courseType = course.getCourseType(), courseCode = course.getCourseCode();
        Venue v;
        ArrayList<Staff> lecStaffList = getLecStaffList(courseCode, courseType);
        double startTime = 0, endTime = 0;
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
                                    startTime = getRandomStartTime();
                                    endTime = startTime + Double.parseDouble(course.getCourseDuration());
                                    day = getRandomDay();

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
                                } while (isClash);

                                for (int i = 0; i < scheduleList.size(); i++) {
                                    if (lecGroupStr.contains(scheduleList.get(i).getCohortID())) {
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
    }

    public void assignCourse(int requiredNoOfClass, String groupID, ArrayList<Class> classList, CourseType course) throws IOException {
        boolean isClash, isBreak = false;
        int day, classCount, runCount = 0;
        double startTime, endTime;
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
                    startTime = getRandomStartTime();
                    endTime = startTime + Double.parseDouble(course.getCourseDuration());
                    for (Class temp : classList) {
                        if (temp.getDay() == day) {
                            classCount++;
                        }
                    }
                } while (classCount > noOfClassPerDay);

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

                        double startTime2 = thisClass.getStartTime(), endTime2 = thisClass.getEndTime();
                        for (int i = 0; i < scheduleList.size(); i++) {
                            ArrayList<Class> tempList = scheduleList.get(i).getClassList();
                            if (tempList != null) {
                                for (int j = 0; j < tempList.size(); j++) {
                                    Class temp = tempList.get(j);
                                    if (temp.getDay() == thisClass.getDay()) {
                                        double startTime1 = temp.getStartTime(), endTime1 = temp.getEndTime();
                                        if ((startTime2 >= startTime1 && startTime2 < endTime1) || (endTime2 > startTime1 && endTime2 <= endTime1) || (startTime1 >= startTime2 && startTime1 < endTime2) || (endTime1 > startTime2 && endTime1 <= endTime2)) {
                                            if (temp.getStaffID() != null && !temp.getStaffID().equals("-")) {
                                                if (temp.getStaffID().equals(s.getStaffID())) {
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

    public void assignVenue(Class previousClass, Class thisClass) throws IOException {
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
                                            thisClass.moveRight(moveDuration);
                                        }
                                    } else {
                                        thisClass.moveRight(moveDuration);
                                    }
                                }
                            } else {
                                thisClass.moveRight(moveDuration);
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
        } while (isClash == true);

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

    public double getRandomStartTime() {
        if (rand.nextBoolean() == true) {
            return (rand.nextInt((int) (studyEnd - studyStart - 1)) + studyStart);
        } else {
            return (rand.nextInt((int) (studyEnd - studyStart - 2)) + studyStart) + 0.5;
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
            errorCode = 2;
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
                        case "L":
                            for (String lecGroupList : s.getLecGroupList()) {
                                if (lecGroupList.contains(courseCode) && lecGroupList.contains(groupID)) {
                                    qualifiedList.add(s);
                                }
                            }
                            break;
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
            errorCode = 2;
            FacesContext.getCurrentInstance().getExternalContext().redirect("menu.xhtml");
        }
        return qualifiedList.get(rand.nextInt(qualifiedList.size()));
    }

    public Venue getLecVenue(String lecGroupStr, String courseCode, String staffID) throws IOException {
        ArrayList<Venue> qualifiedList = new ArrayList();
        int totalSize = getTotalSize(lecGroupStr, courseCode, staffID);
        Venue venue = new Venue();
        for (Venue v : hallList) {
            if (v.getCapacity() >= totalSize) {
                qualifiedList.add(v);
            }
        }
        if (qualifiedList.isEmpty()) {
            errorCode = 3;
            FacesContext.getCurrentInstance().getExternalContext().redirect("menu.xhtml");
        } else {
            venue = qualifiedList.get(0);
            for (int i = 1; i < qualifiedList.size(); i++) {
                if (qualifiedList.get(i).getCapacity() < venue.getCapacity()) {
                    venue = qualifiedList.get(i);
                }
            }
        }
        return venue;
    }

    public Venue getCourseVenue(String courseType, String courseCode) throws IOException {
        Venue venue = new Venue();
        ArrayList<Venue> qualifiedList = new ArrayList(), otherList = new ArrayList();

        int index;
        if (courseType.equals("P")) {
            for (Venue v : labList) {
                if (v.getCourseCodeList().contains(courseCode)) {
                    qualifiedList.add(v);
                }
                if (v.getCourseCodeList().equals("-")) {
                    otherList.add(v);
                }
            }
            if (qualifiedList.isEmpty()) {
                venue = otherList.get(rand.nextInt(otherList.size()));
            } else {
                venue = qualifiedList.get(rand.nextInt(qualifiedList.size()));
            }
        } else {
            index = rand.nextInt(roomList.size());
            venue = roomList.get(index);
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

    public TutorialGroup searchTutorialGroup(String groupID) {
        TutorialGroup tg = new TutorialGroup();
        for (TutorialGroup temp : groupList) {
            if (temp.getGroupID().equals(groupID)) {
                tg = temp;
            }
        }
        return tg;
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

    public boolean isClashWithinList() {
        boolean isClash = false;
        for (int i = 0; i < scheduleList.size(); i++) {
            ArrayList<Class> classList = scheduleList.get(i).getClassList();
            for (int index1 = 0; index1 < classList.size(); index1++) {
                for (int index2 = index1 + 1; index2 < classList.size() - 1; index2++) {
                    Class class1 = classList.get(index1), class2 = classList.get(index2);
                    if (class1.getDay() == class2.getDay()) {
                        if ((class2.getStartTime() >= class1.getStartTime() && class2.getStartTime() < class1.getEndTime()) || (class2.getEndTime() > class1.getStartTime() && class2.getEndTime() <= class1.getEndTime()) || (class1.getStartTime() >= class2.getStartTime() && class1.getStartTime() < class2.getEndTime()) || (class1.getEndTime() > class2.getStartTime() && class1.getEndTime() <= class2.getEndTime())) {
                            isClash = true;
                            break;
                        }
                    }
                }
            }
        }
        return isClash;
    }

    public boolean isClashWithDB() throws SQLException {
        boolean isClash = false;
        if (!dbList.isEmpty()) {
            for (int i = 0; i < scheduleList.size(); i++) {
                ArrayList<Class> classList = scheduleList.get(i).getClassList();
                for (Class c : classList) {
                    double startTime = c.getStartTime(), endTime = c.getEndTime();
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

    public boolean isClashWithBlockClass() {
        boolean isClash = false;
        double tempStart, tempEnd, startTime, endTime;
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

                if (blockClass != null) {
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

    public boolean isClassListDataCompleted() {
        boolean isComplete = true;
        for (int i = 0; i < scheduleList.size(); i++) {
            ArrayList<Class> classList = scheduleList.get(i).getClassList();
            for (Class c : classList) {
                if (c.getVenueID() == null || c.getStaffID() == null || c.getVenueID().equals("-") || c.getStaffID().equals("-")) {
                    isComplete = false;
                    break;
                }
            }
        }
        return isComplete;
    }

    public boolean hasInvalidTime() {
        boolean hasInvalid = false;
        for (int i = 0; i < scheduleList.size(); i++) {
            ArrayList<Class> classList = scheduleList.get(i).getClassList();
            for (Class c : classList) {
                if (c.getStartTime() < studyStart || c.getEndTime() > studyEnd) {
                    hasInvalid = true;
                    break;
                }
            }
        }
        return hasInvalid;
    }

    public boolean isClassEnough() {
        boolean isEnough = true;
        for (int i = 0; i < scheduleList.size(); i++) {
            if (scheduleList.get(i).getNoOfClass() < scheduleList.get(i).getRequiredNoOfClass()) {
                isEnough = false;
            }
        }
        return isEnough;
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
        for (int i = 0; i < scheduleList.size(); i++) {
            ArrayList<Class> classList = scheduleList.get(i).getClassList();
            for (int j = 0; j < classList.size() - 1; j++) {
                int nextIndex = j + 1;
                Class c1 = classList.get(j), c2 = classList.get(nextIndex);
                double totalDuration = c1.getDuration();
                while (nextIndex < classList.size() && c1.getDay() == c2.getDay() && c2.getStartTime() == c1.getEndTime()) {
                    totalDuration += c2.getDuration();
                    nextIndex++;
                    if (nextIndex < classList.size()) {
                        c2 = classList.get(nextIndex);
                    }
                }
                if (totalDuration > 4) {
                    hasLongDuration = true;
                    break;
                }
            }
        }
        return hasLongDuration;
    }

    public boolean hasLongBreak() {
        boolean hasLongBreak = false;
        for (int i = 0; i < scheduleList.size(); i++) {
            ArrayList<Class> classList = scheduleList.get(i).getClassList();
            for (int j1 = 0; j1 < classList.size(); j1++) {
                for (int j2 = j1 + 1; j2 < classList.size() - 1; j2++) {
                    Class c1 = classList.get(j1), c2 = classList.get(j2);
                    if (c1.getDay() == c2.getDay()) {
                        if ((c2.getStartTime() - c1.getEndTime()) > maxBreak) {
                            hasLongBreak = true;
                            break;
                        }
                    }
                }
            }
        }
        return hasLongBreak;
    }

    //Method to start the algorithm task from JSF
    public void start() throws Exception {
        initialize();
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
                //System.out.println(isClassEnough() + "-" + isClassListDataCompleted() + "-" + hasLongBreak() + "-" + hasInvalidTime() + "-" + hasInvalidNoOfClass() + "-" + hasLongDurationClass() + "-" + isClashWithinList() + "-" + isClashWithOtherLists() + "-" + isClashWithBlockClass() + "-" + isClashWithDB());
            }
        } while (toRestart || !isClassEnough() || !isClassListDataCompleted() || hasInvalidTime() || hasInvalidNoOfClass() || hasLongDurationClass() || isClashWithinList() || isClashWithOtherLists() || isClashWithBlockClass() || isClashWithDB());

        if (!isBreak) {
            insertData();
            printClass();
            FacesContext.getCurrentInstance().getExternalContext().redirect("ViewTimetable.xhtml");
        } else {
            errorCode = 1;
            FacesContext.getCurrentInstance().getExternalContext().redirect("menu.xhtml");
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
