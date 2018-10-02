/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import da.ClassDA;
import domain.Class;
import domain.CourseType;
import domain.Schedule;
import domain.Staff;
import domain.Tutorial_Group;
import domain.Venue;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
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
    private ArrayList<Tutorial_Group> groupList;
    private ArrayList<Venue> roomList, labList, hallList;
    private ArrayList<Schedule> scheduleList;

    private int studyDays, blockDay = 99;
    private double studyStart, studyEnd, blockStart, blockEnd, maxBreak;

    private ClassDA cda = new ClassDA();

    public void initialize() throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Element e;

        String fileName = "../xml/TutorialGroup.xml";
        InputStream inputStream = getClass().getResourceAsStream(fileName);
        Document doc = dBuilder.parse(inputStream);
        groupList = new ArrayList();
        NodeList nodes = doc.getElementsByTagName("tutorialGroup");

        for (int i = 0; i < nodes.getLength(); i++) {
            e = (Element) nodes.item(i);

            Tutorial_Group tg = new Tutorial_Group(e.getAttribute("groupID"), Integer.parseInt(e.getElementsByTagName("studyYear").item(0).getTextContent()), Integer.parseInt(e.getElementsByTagName("groupNumber").item(0).getTextContent()), Integer.parseInt(e.getElementsByTagName("size").item(0).getTextContent()), e.getElementsByTagName("programmeID").item(0).getTextContent(), e.getElementsByTagName("cohortID").item(0).getTextContent());
            cda.deleteRecords(tg.getGroupID());
            groupList.add(tg);
        }

        fileName = "../xml/Course.xml";
        inputStream = getClass().getResourceAsStream(fileName);
        doc = dBuilder.parse(inputStream);
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

        fileName = "../xml/Configuration.xml";
        inputStream = getClass().getResourceAsStream(fileName);
        doc = dBuilder.parse(inputStream);
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
            maxBreak = Double.parseDouble(e.getElementsByTagName("maxBreak").item(0).getTextContent());
        }

        fileName = "../xml/Staff.xml";
        inputStream = getClass().getResourceAsStream(fileName);
        doc = dBuilder.parse(inputStream);
        staffList = new ArrayList();
        nodes = doc.getElementsByTagName("staff");

        for (int i = 0; i < nodes.getLength(); i++) {
            e = (Element) nodes.item(i);

            Staff stf = new Staff(e.getAttribute("staffID"), e.getElementsByTagName("name").item(0).getTextContent(), "", e.getElementsByTagName("startWork").item(0).getTextContent(), e.getElementsByTagName("endWork").item(0).getTextContent());
            staffList.add(stf);
        }

        fileName = "../xml/Venue.xml";
        inputStream = getClass().getResourceAsStream(fileName);
        doc = dBuilder.parse(inputStream);
        roomList = new ArrayList();
        labList = new ArrayList();
        hallList = new ArrayList();
        nodes = doc.getElementsByTagName("venue");

        for (int i = 0; i < nodes.getLength(); i++) {
            e = (Element) nodes.item(i);

            Venue v = new Venue(e.getAttribute("venueID"), e.getElementsByTagName("block").item(0).getTextContent(), e.getElementsByTagName("venueType").item(0).getTextContent(), Integer.parseInt(e.getElementsByTagName("capacity").item(0).getTextContent()), "");
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
            Class c = new Class("-", "-", "-", "-", blockDay, blockStart, blockEnd, "-");
            for (int i = 0; i < scheduleList.size(); i++) {
                scheduleList.get(i).addClassToList(c);
            }
        }

        //Assign Lecture Schedules
        for (int i = 0; i < lecList.size(); i++) {
            assignLecture(lecList.get(i));
        }

        //Assign Other Schedules, ex: Tutorial, Practical
        for (int i = 0; i < courseList.size(); i++) {
            for (int j = 0; j < scheduleList.size(); j++) {
                assignCourse(scheduleList.get(j).getGroupID(), scheduleList.get(j).getClassList(), courseList.get(i));
            }
        }

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
                } else {
                    return o1.getDay() - o2.getDay();
                }
            });
        }
    }

    //Validate Venue & Staff Clashes
    public void validation() {
        for (int i = 0; i < scheduleList.size(); i++) {
            for (int j = i + 1; j < scheduleList.size(); j++) {
                ArrayList<Class> list1 = scheduleList.get(i).getClassList();
                ArrayList<Class> list2 = scheduleList.get(j).getClassList();

                for (int index = 0; index < list1.size(); index++) {
                    Class class1 = list1.get(index);
                    if (!class1.getCourseType().equalsIgnoreCase("L") && !class1.getCourseType().equalsIgnoreCase("-")) {
                        for (int index2 = 0; index2 < list2.size(); index2++) {
                            Class class2 = list2.get(index2);
                            if (!class2.getCourseType().equalsIgnoreCase("L") && !class2.getCourseType().equalsIgnoreCase("-")) {
                                if (class1.getDay() == class2.getDay()) {
                                    double startTime1 = class1.getStartTime();
                                    double endTime1 = class1.getEndTime();

                                    double startTime2 = class2.getStartTime();
                                    double endTime2 = class2.getEndTime();

                                    if ((startTime2 >= startTime1 && startTime2 < endTime1) || (endTime2 > startTime1 && endTime2 <= endTime2)) {
                                        if (class1.getVenueID().equalsIgnoreCase(class2.getVenueID())) {
                                            String temp = class2.getVenueID();
                                            String venueID = "";
                                            do {
                                                venueID = getRandomVenue(class2.getCourseType()).getVenueID();
                                            } while (temp.equalsIgnoreCase(venueID));
                                            class2.setVenueID(venueID);
                                        }
                                        if (class1.getStaffID().equalsIgnoreCase(class2.getStaffID())) {
                                            String temp = class2.getStaffID();
                                            String staffID = "";
                                            do {
                                                staffID = getRandomStaff().getStaffID();
                                            } while (temp.equalsIgnoreCase(staffID));
                                            class2.setStaffID(staffID);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
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
                    if (!class1.getCourseType().equalsIgnoreCase("L") && !class1.getCourseType().equalsIgnoreCase("-")) {
                        for (int index2 = 0; index2 < list2.size(); index2++) {
                            Class class2 = list2.get(index2);
                            if (!class2.getCourseType().equalsIgnoreCase("L") && !class2.getCourseType().equalsIgnoreCase("-")) {
                                if (class1.getDay() == class2.getDay()) {
                                    double startTime1 = class1.getStartTime();
                                    double endTime1 = class1.getEndTime();

                                    double startTime2 = class2.getStartTime();
                                    double endTime2 = class2.getEndTime();
                                    if ((startTime2 >= startTime1 && startTime2 < endTime1) || (endTime2 > startTime1 && endTime2 <= endTime2)) {
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
        }
        return clashNo;
    }

    public void optimizeBreak(int step) {
        switch (step) {
            case 1:

                break;
            case 2:
                break;
        }
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
        Class temp = new Class();
        for (int i = 0; i < scheduleList.size(); i++) {
            ArrayList<Class> classList = scheduleList.get(i).getClassList();
            for (Class c : classList) {
                if (c.getCourseID().equals("-")) {
                    temp = c;
                }
            }
            classList.remove(temp);
        }
    }

    public CourseType searchCourse(String courseID) {
        CourseType course = new CourseType();
        for (int i = 0; i < courseList.size(); i++) {
            if (courseList.get(i).getCourseID().equalsIgnoreCase(courseID)) {
                course = courseList.get(i);
            }
        }
        return course;
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
        String staffID = "", venueID = "";
        double startTime, endTime;
        int day = 0;
        Class c;

        do {
            isClash = false;
            startTime = getRandomStartTime();
            endTime = startTime + Double.parseDouble(course.getCourseDuration());
            venueID = getRandomVenue(course.getCourseType()).getVenueID();
            staffID = getRandomStaff().getStaffID();
            day = getRandomDay();
            c = new Class(courseID, venueID, "-", staffID, day, startTime, endTime, courseType);
            if (isTimeClash(scheduleList.get(0).getClassList(), c)) {
                isClash = true;
            }
        } while (isClash == true);

        for (int i = 0; i < scheduleList.size(); i++) {
            c = new Class(courseID, venueID, scheduleList.get(i).getGroupID(), staffID, day, startTime, endTime, courseType);
            scheduleList.get(i).addClassToList(c);
        }
    }

    public void assignCourse(String groupID, ArrayList<Class> classList, CourseType course) {
        boolean isClash;
        int day;
        double startTime, endTime;

        Class c;
        do {
            isClash = false;
            startTime = getRandomStartTime();
            endTime = startTime + Double.parseDouble(course.getCourseDuration());
            day = getRandomDay();
            c = new Class(course.getCourseID(), getRandomVenue(course.getCourseType()).getVenueID(), groupID, getRandomStaff().getStaffID(), day, startTime, endTime, course.getCourseType());

            if (isTimeClash(classList, c)) {
                isClash = true;
            }
        } while (isClash == true);

        classList.add(c);
    }

    public boolean isTimeClash(ArrayList<Class> classList, Class c) {
        boolean found = false;
        for (int i = 0; i < classList.size(); i++) {
            if (classList.get(i).getDay() == c.getDay()) {
                double startTime = c.getStartTime();
                double endTime = c.getEndTime();
                double tempStart = classList.get(i).getStartTime();
                double tempEnd = classList.get(i).getEndTime();
                if ((startTime >= tempStart && startTime < tempEnd) || (endTime > tempStart && endTime <= tempEnd)) {
                    found = true;
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
                        if ((class2.getStartTime() >= class1.getStartTime() && class2.getStartTime() < class1.getEndTime()) || (class2.getEndTime() > class1.getStartTime() && class2.getEndTime() <= class1.getEndTime())) {
                            count++;
                        }
                    }
                }
            }
        }
        return count;
    }

    public double getRandomStartTime() {
        if (rand.nextBoolean() == true) {
            return (rand.nextInt((int) (studyEnd - studyStart - 2)) + studyStart);
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
        if (courseType.equalsIgnoreCase("T")) {
            index = rand.nextInt(roomList.size());
            venue = roomList.get(index);
        } else if (courseType.equalsIgnoreCase("P")) {
            index = rand.nextInt(labList.size());
            venue = labList.get(index);
        } else if (courseType.equalsIgnoreCase("L")) {
            int totalSize = getTotalSize();
            do {
                index = rand.nextInt(hallList.size());
                venue = hallList.get(index);
            } while (hallList.get(index).getCapacity() < totalSize);
        }
        return venue;
    }

    public void start() throws Exception {
        initialize();

        long startTime = System.nanoTime();
        long totalTime = 0;

        do {
            long nowTime = System.nanoTime();
            totalTime = TimeUnit.NANOSECONDS.toSeconds(nowTime - startTime);
            if (totalTime > 10) {
                break;
            } else {
                allocation();
                validation();
            }
        } while (countTimeClashes() > 0 || countClash() > 0);
        
        if (totalTime > 10) {
            System.out.println("Timeout.");
        } else {
            if (blockDay != 99) {
                clearBlock();
            }
            storeData();
            for (int i = 0; i < scheduleList.size(); i++) {
                ArrayList<Class> classList = scheduleList.get(i).getClassList();
                for (int j = 0; j < classList.size(); j++) {
                    if (j == 0) {
                        System.out.println("Group ID : " + classList.get(j).getGroupID());
                    } else {
                        System.out.println("Course ID (Staff ID): " + classList.get(j).getCourseID() + " (" + classList.get(j).getStaffID() + ")" + " Venue : " + classList.get(j).getVenueID() + " Day & Time: " + classList.get(j).getDay() + " (" + classList.get(j).getStartTime() + "-" + classList.get(j).getEndTime() + ")");
                    }
                }
                System.out.println("-----------------------------------------------------------------------------");
            }
        }
    }

    public static void main(String args[]) throws Exception {
        SchedulingAlgorithm sa = new SchedulingAlgorithm();
        sa.start();
    }
}
