/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import da.*;
import domain.Class;
import domain.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
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
@RequestScoped
public class SchedulingAlgorithm {

    private final Random rand = new Random();

    private ArrayList<CourseType> lecList, courseList;
    private ArrayList<Staff> staffList;
    private ArrayList<TutorialGroup> groupList;
    private ArrayList<Venue> roomList, labList, hallList;
    private ArrayList<Schedule> scheduleList;

    private int studyDays, totalClass = 0, blockDay = 99, runLimit = 50;
    private double studyStart, studyEnd, blockStart, blockEnd, maxBreak = 99, noOfClassPerDay = 99;
    private boolean isGenerationEnd = false;
    private Class blockClass;

    private final ClassDA cda = new ClassDA();
    private final VenueDA vda = new VenueDA();
    private final StaffDA sda = new StaffDA();

    private final String filePath = "C:\\Users\\Alex\\Documents\\NetBeansProjects\\ClaSS\\src\\java\\xml\\";

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

            TutorialGroup tg = new TutorialGroup(e.getAttribute("groupID"), Integer.parseInt(e.getElementsByTagName("studyYear").item(0).getTextContent()), Integer.parseInt(e.getElementsByTagName("groupNumber").item(0).getTextContent()), Integer.parseInt(e.getElementsByTagName("size").item(0).getTextContent()), e.getElementsByTagName("programmeID").item(0).getTextContent(), e.getElementsByTagName("cohortID").item(0).getTextContent());
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
            if (c.getCourseType().equalsIgnoreCase("L")) {
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
            if (e.getElementsByTagName("balanceClass").getLength() > 0) {
                boolean toBalance = Boolean.parseBoolean(e.getElementsByTagName("balanceClass").item(0).getTextContent());
                if (toBalance) {
                    totalClass = lecList.size() + courseList.size();
                    noOfClassPerDay = Math.floor((double) totalClass / studyDays) + 1;
                }
            }
            if (e.getElementsByTagName("maxBreak").getLength() > 0) {
                maxBreak = Double.parseDouble(e.getElementsByTagName("maxBreak").item(0).getTextContent());
            }
        }

        fileName = filePath + "Staff.xml";
        doc = dBuilder.parse(fileName);
        staffList = new ArrayList();
        nodes = doc.getElementsByTagName("staff");

        for (int i = 0; i < nodes.getLength(); i++) {
            e = (Element) nodes.item(i);

            Staff stf = new Staff(e.getAttribute("staffID"), e.getElementsByTagName("name").item(0).getTextContent(), "", e.getElementsByTagName("startWork").item(0).getTextContent(), e.getElementsByTagName("endWork").item(0).getTextContent());
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

            Venue v = new Venue(e.getAttribute("venueID"), e.getElementsByTagName("block").item(0).getTextContent(), e.getElementsByTagName("venueType").item(0).getTextContent(), Integer.parseInt(e.getElementsByTagName("capacity").item(0).getTextContent()), "");
            v.setClassList(vda.getClassList(v.getVenueID()));
            if (v.getVenueType().equalsIgnoreCase("Room")) {
                roomList.add(v);
            } else if (v.getVenueType().equalsIgnoreCase("Lab")) {
                labList.add(v);
            } else if (v.getVenueType().equalsIgnoreCase("Hall")) {
                hallList.add(v);
            }
        }
    }

    public void allocation() {
        scheduleList = new ArrayList();
        //Creating Schedule List for each Group
        for (int i = 0; i < groupList.size(); i++) {
            ArrayList<Class> classList = new ArrayList();
            Schedule s = new Schedule(groupList.get(i).getGroupID(), classList);
            scheduleList.add(s);
        }

        if (blockDay != 99) {
            blockClass = new Class("-", "-", "-", "-", blockDay, blockStart, blockEnd, "BLK");
            for (int i = 0; i < scheduleList.size(); i++) {
                scheduleList.get(i).addClassToList(blockClass);
            }
        }

        //Assign Lecture Schedules
        for (int i = 0; i < lecList.size(); i++) {
            assignLecture(lecList.get(i));
        }

        if (maxBreak != 99) {
            sortList();
            optimizeBreak(1);
        }

        //Assign Other Schedules, ex: Tutorial, Practical
        for (int i = 0; i < courseList.size(); i++) {
            for (int j = 0; j < scheduleList.size(); j++) {
                assignCourse(scheduleList.get(j).getGroupID(), scheduleList.get(j).getClassList(), courseList.get(i));
            }
        }

        if (maxBreak != 99) {
            sortList();
            optimizeBreak(2);
            sortList();
            optimizeBreak(3);
        }

        if (blockDay != 99) {
            clearBlock();
        }

        sortList();
        for (int i = 0; i < scheduleList.size(); i++) {
            ArrayList<Class> classList = scheduleList.get(i).getClassList();
            for (int j = 0; j < classList.size(); j++) {
                Class previousClass = null;
                Class thisClass = classList.get(j);
                if (j > 0) {
                    previousClass = classList.get(j - 1);
                }
                if (!thisClass.getCourseType().equalsIgnoreCase("L")) {
                    assignVenueStaff(previousClass, thisClass);
                }
            }
        }
    }

    public void sortList() {
        //Sort List
        for (int i = 0; i < scheduleList.size(); i++) {
            ArrayList<Class> classList = scheduleList.get(i).getClassList();
            classList.sort((Class o1, Class o2) -> {
                if (o1.getDay() == o2.getDay()) {
                    double time1 = o1.getStartTime();
                    double time2 = o2.getStartTime();
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

    public int countClash() {
        int clashNo = 0;
        for (int i = 0; i < scheduleList.size(); i++) {
            for (int j = i + 1; j < scheduleList.size(); j++) {
                ArrayList<Class> list1 = scheduleList.get(i).getClassList();
                ArrayList<Class> list2 = scheduleList.get(j).getClassList();

                for (int index = 0; index < list1.size(); index++) {
                    Class class1 = list1.get(index);
                    for (int index2 = 0; index2 < list2.size(); index2++) {
                        Class class2 = list2.get(index2);
                        if (!class1.getCourseType().equalsIgnoreCase("L") && !class1.getCourseType().equals("BLK") && !class2.getCourseType().equalsIgnoreCase("L") && !class2.getCourseType().equals("BLK")) {
                            if (class1.getDay() == class2.getDay()) {
                                double startTime1 = class1.getStartTime();
                                double endTime1 = class1.getEndTime();

                                double startTime2 = class2.getStartTime();
                                double endTime2 = class2.getEndTime();
                                if ((startTime2 >= startTime1 && startTime2 < endTime1) || (endTime2 > startTime1 && endTime2 <= endTime1) || (startTime1 >= startTime2 && startTime1 < endTime2) || (endTime1 > startTime2 && endTime1 <= endTime2)) {
                                    if (class1.getVenueID().equalsIgnoreCase(class2.getVenueID()) || class1.getStaffID().equalsIgnoreCase(class2.getStaffID())) {
                                        clashNo++;
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
        return clashNo;
    }

    public int countEmpty() {
        int emptyNo = 0;
        for (int i = 0; i < scheduleList.size(); i++) {
            ArrayList<Class> classList = scheduleList.get(i).getClassList();
            for (Class c : classList) {
                if (c.getVenueID() == null || c.getStaffID() == null || c.getVenueID().equalsIgnoreCase("-") || c.getStaffID().equalsIgnoreCase("-")) {
                    emptyNo++;
                }
            }
        }
        return emptyNo;
    }

    public void optimizeBreak(int step) {
        double moveDuration = 0;
        ArrayList<Class> classList;
        switch (step) {
            case 1:
                classList = scheduleList.get(0).getClassList();
                for (int i = 0; i < classList.size() - 1; i++) {
                    int j = i + 1;
                    Class c1 = classList.get(i);
                    Class c2 = classList.get(j);
                    if (!c1.getCourseType().equals("BLK")) {
                        if (c2.getCourseType().equals("BLK")) {
                            if (j < scheduleList.size() - 1) {
                                c2 = classList.get(j + 1);
                            } else {
                                break;
                            }
                        }
                        if (c1.getDay() == c2.getDay()) {
                            double breakTime = c2.getStartTime() - c1.getEndTime();
                            int day = c1.getDay();
                            if (c1.getVenueID().equalsIgnoreCase(c2.getVenueID())) {
                                moveDuration = breakTime;
                            } else {
                                if (breakTime == 0) {
                                    moveDuration = 0.5;
                                } else if (breakTime > maxBreak) {
                                    moveDuration = breakTime - getRandomMoveDuration();
                                }
                            }
                            for (int index = 0; index < scheduleList.size(); index++) {
                                classList = scheduleList.get(index).getClassList();
                                for (int insideIndex = j; insideIndex < classList.size(); insideIndex++) {
                                    Class c = classList.get(insideIndex);
                                    if (c.getDay() == day) {
                                        c.moveLeft(moveDuration);
                                    } else {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case 2:
                for (int i = 1; i <= studyDays; i++) {
                    for (int index = 0; index < scheduleList.size(); index++) {
                        classList = scheduleList.get(index).getClassList();
                        if (countClassBeforeHalfDay(i, classList) == 0 && countLecClassPerDay(i, classList) == 0) {
                            for (Class c : classList) {
                                if (c.getDay() == i) {
                                    if (!c.getCourseType().equals("BLK")) {
                                        if (moveDuration == 0) {
                                            moveDuration = c.getStartTime() - studyStart - getRandomMoveDuration();
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
            case 3:
                for (int index = 0; index < scheduleList.size(); index++) {
                    classList = scheduleList.get(index).getClassList();
                    for (int i = 0; i < classList.size() - 1; i++) {
                        int j = i + 1;
                        Class c1 = classList.get(i);
                        Class c2 = classList.get(j);
                        if (!c1.getCourseType().equals("BLK")) {
                            if (!c1.getCourseType().equalsIgnoreCase("L") || !c2.getCourseType().equalsIgnoreCase("L")) {
                                if (c2.getCourseType().equals("BLK")) {
                                    if (j != classList.size() - 1) {
                                        c2 = classList.get(j + 1);
                                    } else {
                                        break;
                                    }
                                }
                                if (c1.getDay() == c2.getDay()) {
                                    double breakTime = c2.getStartTime() - c1.getEndTime();
                                    moveDuration = breakTime - getRandomMoveDuration();
                                    if (breakTime > maxBreak) {
                                        if (c1.getCourseType().equalsIgnoreCase("L") && !c2.getCourseType().equalsIgnoreCase("L")) {
                                            c2.moveLeft(moveDuration);
                                        } else if (c2.getCourseType().equalsIgnoreCase("L") && !c1.getCourseType().equalsIgnoreCase("L")) {
                                            c1.moveRight(moveDuration);
                                        } else if (!c1.getCourseType().equalsIgnoreCase("L") && !c2.getCourseType().equalsIgnoreCase("L")) {
                                            if (c1.getStartTime() == studyStart) {
                                                c1.moveRight(moveDuration);
                                            } else {
                                                c2.moveLeft(moveDuration);
                                            }
                                        }
                                    } else if (breakTime == 0) {
                                        moveDuration = 0.5;
                                        if (c1.getCourseType().equalsIgnoreCase("L") && !c2.getCourseType().equalsIgnoreCase("L")) {
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
                if (c.getCourseType().equalsIgnoreCase("L")) {
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

    public void storeData() throws SQLException {
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

    public int getTotalSize() {
        int totalSize = 0;
        for (int i = 0; i < groupList.size(); i++) {
            totalSize += groupList.get(i).getSize();
        }
        return totalSize;
    }

    public void assignLecture(CourseType course) {
        boolean isClash;

        String courseID = course.getCourseID();
        String courseType = course.getCourseType();
        String venueID = "", staffID = "";
        Venue v = new Venue();
        Staff s = new Staff();
        double startTime = 0, endTime = 0;
        int day = 0, runCount = 0;
        Class c;
        boolean isBreak = false;

        do {
            if (runCount == runLimit) {
                isBreak = true;
                break;
            } else {
                isClash = false;
                int noOfClass;
                do {
                    noOfClass = 1;
                    startTime = getRandomStartTime();
                    endTime = startTime + Double.parseDouble(course.getCourseDuration());
                    ArrayList<Class> classList = scheduleList.get(0).getClassList();
                    day = getRandomDay();
                    for (int i = 0; i < classList.size(); i++) {
                        if (day == classList.get(i).getDay()) {
                            noOfClass++;
                        }
                    }
                } while (noOfClass > noOfClassPerDay);

                c = new Class(courseID, "-", "-", "-", day, startTime, endTime, courseType);
                if (isTimeClash(scheduleList.get(0).getClassList(), c)) {
                    isClash = true;
                } else {
                    s = getRandomStaff();
                    ArrayList<Class> staffClassList = s.getClassList();
                    if (staffClassList.size() > 0) {
                        for (int i = 0; i < staffClassList.size(); i++) {
                            Class temp = staffClassList.get(i);
                            if (temp.getDay() == c.getDay()) {
                                double startTime1 = temp.getStartTime();
                                double endTime1 = temp.getEndTime();

                                if ((startTime >= startTime1 && startTime < endTime1) || (endTime > startTime1 && endTime <= endTime1) || (startTime1 >= startTime && startTime1 < endTime) || (endTime1 > startTime && endTime1 <= endTime)) {
                                    if (temp.getStaffID().equalsIgnoreCase(s.getStaffID())) {
                                        isClash = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    v = getRandomVenue(courseType);
                    ArrayList<Class> classList = scheduleList.get(0).getClassList();
                    if (classList.size() > 0) {
                        for (int i = 0; i < classList.size(); i++) {
                            Class temp = classList.get(i);
                            if (temp.getDay() == c.getDay()) {
                                if (temp.getEndTime() == c.getStartTime()) {
                                    if (!temp.getVenueID().equalsIgnoreCase("-")) {
                                        Class previousClass = temp;
                                        Venue previousV = searchVenue(previousClass.getCourseType(), previousClass.getVenueID());
                                        if (runCount < 30) {
                                            v = previousV;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    ArrayList<Class> venueClassList = v.getClassList();
                    if (venueClassList.size() > 0) {
                        for (int i = 0; i < venueClassList.size(); i++) {
                            Class temp = venueClassList.get(i);
                            if (temp.getDay() == c.getDay()) {
                                double startTime1 = temp.getStartTime();
                                double endTime1 = temp.getEndTime();

                                if ((startTime >= startTime1 && startTime < endTime1) || (endTime > startTime1 && endTime <= endTime1) || (startTime1 >= startTime && startTime1 < endTime) || (endTime1 > startTime && endTime1 <= endTime)) {
                                    if (temp.getVenueID().equalsIgnoreCase(v.getVenueID())) {
                                        isClash = true;
                                        break;
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
            venueID = v.getVenueID();
            staffID = s.getStaffID();

            for (int i = 0; i < scheduleList.size(); i++) {
                c = new Class(courseID, venueID, scheduleList.get(i).getGroupID(), staffID, day, startTime, endTime, courseType);
                scheduleList.get(i).addClassToList(c);
            }
        }
    }

    public void assignCourse(String groupID, ArrayList<Class> classList, CourseType course) {
        boolean isClash;
        int day;
        double startTime, endTime;
        Class c;
        do {
            isClash = false;
            int noOfClass;
            do {
                noOfClass = 1;
                day = getRandomDay();
                startTime = getRandomStartTime();
                endTime = startTime + Double.parseDouble(course.getCourseDuration());
                for (int i = 0; i < classList.size(); i++) {
                    if (day == classList.get(i).getDay()) {
                        noOfClass++;
                    }
                }
            } while (noOfClass > noOfClassPerDay);
            c = new Class(course.getCourseID(), "-", groupID, "-", day, startTime, endTime, course.getCourseType());

            if (isTimeClash(classList, c)) {
                isClash = true;
            }
        } while (isClash == true);

        classList.add(c);
    }

    public void assignVenueStaff(Class previousClass, Class thisClass) {
        boolean isClash, isBreak = false;
        int runCount = 0;
        double startTime2 = thisClass.getStartTime();
        double endTime2 = thisClass.getEndTime();
        Venue v = new Venue();
        Staff s = new Staff();

        do {
            if (runCount == runLimit) {
                isBreak = true;
                break;
            } else {
                isClash = false;
                v = getRandomVenue(thisClass.getCourseType());

                if (previousClass != null) {
                    if (!previousClass.getCourseType().equals("BLK")) {
                        if (previousClass.getDay() == thisClass.getDay() && previousClass.getEndTime() == thisClass.getStartTime()) {
                            double moveDuration = 0.5;
                            if (previousClass.getCourseType().equals(thisClass.getCourseType())) {
                                Venue previousV = searchVenue(previousClass.getCourseType(), previousClass.getVenueID());
                                if (runCount < 15) {
                                    v = previousV;
                                } else if (runCount < 30) {
                                    v = getRandomVenueFromBlock(previousV.getBlock(), thisClass.getCourseType());
                                } else {
                                    thisClass.moveRight(moveDuration);
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
                            double startTime1 = temp.getStartTime();
                            double endTime1 = temp.getEndTime();

                            if ((startTime2 >= startTime1 && startTime2 < endTime1) || (endTime2 > startTime1 && endTime2 <= endTime1) || (startTime1 >= startTime2 && startTime1 < endTime2) || (endTime1 > startTime2 && endTime1 <= endTime2)) {
                                if (temp.getVenueID().equalsIgnoreCase(v.getVenueID())) {
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
                                    double startTime1 = temp.getStartTime();
                                    double endTime1 = temp.getEndTime();
                                    if ((startTime2 >= startTime1 && startTime2 < endTime1) || (endTime2 > startTime1 && endTime2 <= endTime1) || (startTime1 >= startTime2 && startTime1 < endTime2) || (endTime1 > startTime2 && endTime1 <= endTime2)) {
                                        if (temp.getVenueID() != null && !temp.getVenueID().equals("-")) {
                                            if (temp.getVenueID().equalsIgnoreCase(v.getVenueID())) {
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
            do {
                if (runCount == runLimit) {
                    isBreak = true;
                    break;
                } else {
                    isClash = false;
                    s = getRandomStaff();
                    ArrayList<Class> staffClassList = s.getClassList();
                    if (staffClassList != null) {
                        for (int i = 0; i < staffClassList.size(); i++) {
                            Class temp = staffClassList.get(i);
                            if (temp.getDay() == thisClass.getDay()) {
                                double startTime1 = temp.getStartTime();
                                double endTime1 = temp.getEndTime();

                                if ((startTime2 >= startTime1 && startTime2 < endTime1) || (endTime2 > startTime1 && endTime2 <= endTime1) || (startTime1 >= startTime2 && startTime1 < endTime2) || (endTime1 > startTime2 && endTime1 <= endTime2)) {
                                    if (temp.getStaffID().equalsIgnoreCase(s.getStaffID())) {
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
                            for (int j = 0; j < classList.size(); j++) {
                                Class temp = scheduleList.get(i).getClassList().get(j);
                                if (temp.getDay() == thisClass.getDay()) {
                                    double startTime1 = temp.getStartTime();
                                    double endTime1 = temp.getEndTime();

                                    if ((startTime2 >= startTime1 && startTime2 < endTime1) || (endTime2 > startTime1 && endTime2 <= endTime1) || (startTime1 >= startTime2 && startTime1 < endTime2) || (endTime1 > startTime2 && endTime1 <= endTime2)) {
                                        if (temp.getStaffID() != null && !temp.getStaffID().equals("-")) {
                                            if (temp.getStaffID().equalsIgnoreCase(s.getStaffID())) {
                                                isClash = true;
                                                break;
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
        }

        if (!isBreak) {
            thisClass.setVenueID(v.getVenueID());
            thisClass.setStaffID(s.getStaffID());
        }
    }

    public boolean isTimeClash(ArrayList<Class> classList, Class c) {
        boolean found = false;
        for (int i = 0; i < classList.size(); i++) {
            if (classList.get(i).getDay() == c.getDay()) {
                double startTime = c.getStartTime();
                double endTime = c.getEndTime();
                double tempStart = classList.get(i).getStartTime();
                double tempEnd = classList.get(i).getEndTime();
                if ((startTime >= tempStart && startTime < tempEnd) || (endTime > tempStart && endTime <= tempEnd) || (tempStart >= startTime && tempStart < endTime) || (tempEnd > startTime && tempEnd <= endTime)) {
                    found = true;
                    break;
                }
            }
        }
        return found;
    }

    public int countTimeClashes() {
        int count = 0;
        for (int i = 0; i < scheduleList.size(); i++) {
            ArrayList<Class> classList = scheduleList.get(i).getClassList();
            for (int index1 = 0; index1 < classList.size(); index1++) {
                for (int index2 = index1 + 1; index2 < classList.size(); index2++) {
                    Class class1 = classList.get(index1);
                    Class class2 = classList.get(index2);
                    if (class1.getDay() == class2.getDay()) {
                        if ((class2.getStartTime() >= class1.getStartTime() && class2.getStartTime() < class1.getEndTime()) || (class2.getEndTime() > class1.getStartTime() && class2.getEndTime() <= class1.getEndTime()) || (class1.getStartTime() >= class2.getStartTime() && class1.getStartTime() < class2.getEndTime()) || (class1.getEndTime() > class2.getStartTime() && class1.getEndTime() <= class2.getEndTime())) {
                            count++;
                        }
                    }
                }
            }
        }
        return count;
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

    public Staff getRandomStaff() {
        return staffList.get(rand.nextInt(staffList.size()));
    }

    public Venue getRandomVenue(String courseType) {
        Venue venue = new Venue();
        int index = 0;
        if (courseType.equals("P")) {
            index = rand.nextInt(labList.size());
            venue = labList.get(index);
        } else if (courseType.equals("L")) {
            int totalSize = getTotalSize();
            do {
                index = rand.nextInt(hallList.size());
                venue = hallList.get(index);
            } while (hallList.get(index).getCapacity() < totalSize);
        } else {
            index = rand.nextInt(roomList.size());
            venue = roomList.get(index);
        }
        return venue;
    }

    public Venue getRandomVenueFromBlock(String block, String courseType) {
        Venue venue = new Venue();
        int index;
        if (courseType.equals("P")) {
            do {
                index = rand.nextInt(labList.size());
                venue = labList.get(index);
            } while (!venue.getBlock().equalsIgnoreCase(block));
        } else {
            do {
                index = rand.nextInt(roomList.size());
                venue = roomList.get(index);
            } while (!venue.getBlock().equalsIgnoreCase(block));
        }
        return venue;
    }

    public Venue searchVenue(String courseType, String venueID) {
        Venue venue = new Venue();
        ArrayList<Venue> tempList;
        if (courseType.equals("P")) {
            tempList = labList;
        } else if (courseType.equals("L")) {
            tempList = hallList;
        } else {
            tempList = roomList;
        }
        for (Venue v : tempList) {
            if (v.getVenueID().equalsIgnoreCase(venueID)) {
                venue = v;
            }
        }
        return venue;
    }

    public int countDBClash() throws SQLException {
        ArrayList<String> groupIDList = cda.getAllGroupID();
        int count = 0;
        if (groupIDList != null) {
            for (String groupID : groupIDList) {
                ArrayList<Class> dbList = cda.get(groupID);
                if (dbList != null) {
                    for (int i = 0; i < scheduleList.size(); i++) {
                        ArrayList<Class> classList = scheduleList.get(i).getClassList();
                        for (Class c : classList) {
                            for (Class d : dbList) {
                                if (c.getDay() == d.getDay()) {
                                    if ((c.getStartTime() >= d.getStartTime() && c.getStartTime() < d.getEndTime()) || (d.getEndTime() > c.getStartTime() && d.getEndTime() <= c.getEndTime())) {
                                        if (c.getVenueID().equalsIgnoreCase(d.getVenueID()) || c.getStaffID().equalsIgnoreCase(d.getStaffID())) {
                                            count++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return count;
    }

    public int countBlockClash() {
        Class d = blockClass;
        int count = 0;
        if (d != null) {
            for (int i = 0; i < scheduleList.size(); i++) {
                ArrayList<Class> classList = scheduleList.get(i).getClassList();
                for (Class c : classList) {
                    if (c.getDay() == d.getDay()) {
                        if ((c.getStartTime() >= d.getStartTime() && c.getStartTime() < d.getEndTime()) || (d.getEndTime() > c.getStartTime() && d.getEndTime() <= c.getEndTime())) {
                            count++;
                        }
                    }
                }
            }
        }
        return count;
    }

    public int countInvalidBreak() {
        int count = 0;
        for (int index = 0; index < scheduleList.size(); index++) {
            ArrayList<Class> classList = scheduleList.get(index).getClassList();
            for (int i = 0; i < classList.size() - 1; i++) {
                int j = i + 1;
                Class c1 = classList.get(i);
                Class c2 = classList.get(j);
                if (c1.getDay() == c2.getDay()) {
                    if ((c2.getStartTime() - c1.getEndTime()) > maxBreak) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public boolean getIsGenerationEnd() {
        return isGenerationEnd;
    }

    public void start() throws Exception {
        initialize();

        do {
            allocation();
        } while (countEmpty() > 0 || countClash() > 0 || countTimeClashes() > 0 || countBlockClash() > 0 || countInvalidBreak() > 0 || countDBClash() > 0);

        sortList();
        isGenerationEnd = true;
        storeData();

        printClass();
        FacesContext.getCurrentInstance().getExternalContext().redirect("ViewTimetable.xhtml");
    }

    public void printClass() {
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
