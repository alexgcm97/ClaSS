/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import domain.Class;
import domain.CourseType;
import domain.Staff;
import domain.Tutorial_Group;
import domain.Venue;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Alex
 */
public class SchedulingAlgorithm {

    Random rand = new Random();

    private ArrayList<CourseType> lecList, courseList;
    private ArrayList<Staff> staffList;
    private ArrayList<Tutorial_Group> groupList;
    private ArrayList<Venue> roomList, labList, hallList;
    private ArrayList<ArrayList<Class>> scheduleList;

    private int studyDays;
    private double studyStart, studyEnd;

    public void initialize() throws ParserConfigurationException, UnsupportedEncodingException, SAXException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        Document doc = dBuilder.parse("test/xml/Course.xml");
        courseList = new ArrayList();
        lecList = new ArrayList();
        NodeList nodes = doc.getElementsByTagName("course");

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            Element e = (Element) node;

            CourseType c = new CourseType(e.getAttribute("courseID"), e.getElementsByTagName("courseType").item(0).getTextContent(), e.getElementsByTagName("courseDuration").item(0).getTextContent(), e.getElementsByTagName("courseCode").item(0).getTextContent());
            if (c.getCourseType().equalsIgnoreCase("L")) {
                lecList.add(c);
            } else {
                courseList.add(c);
            }
        }

        doc = dBuilder.parse("test/xml/Configuration.xml");
        nodes = doc.getElementsByTagName("configuration");

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            Element e = (Element) node;

            studyDays = Integer.parseInt(e.getElementsByTagName("studyDays").item(0).getTextContent());
            studyStart = Double.parseDouble(e.getElementsByTagName("startTime").item(0).getTextContent());
            studyEnd = Double.parseDouble(e.getElementsByTagName("endTime").item(0).getTextContent());
        }

        doc = dBuilder.parse("test/xml/Staff.xml");
        staffList = new ArrayList();
        nodes = doc.getElementsByTagName("staff");

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            Element e = (Element) node;

            Staff stf = new Staff(e.getAttribute("staffID"), e.getElementsByTagName("name").item(0).getTextContent(), "", e.getElementsByTagName("startWork").item(0).getTextContent(), e.getElementsByTagName("endWork").item(0).getTextContent());
            staffList.add(stf);
        }

        doc = dBuilder.parse("test/xml/TutorialGroup.xml");
        groupList = new ArrayList();
        nodes = doc.getElementsByTagName("tutorialGroup");

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            Element e = (Element) node;

            Tutorial_Group tg = new Tutorial_Group(e.getAttribute("groupID"), Integer.parseInt(e.getElementsByTagName("studyYear").item(0).getTextContent()), Integer.parseInt(e.getElementsByTagName("groupNumber").item(0).getTextContent()), Integer.parseInt(e.getElementsByTagName("size").item(0).getTextContent()), e.getElementsByTagName("programmeID").item(0).getTextContent(), e.getElementsByTagName("cohortID").item(0).getTextContent());
            groupList.add(tg);
        }

        doc = dBuilder.parse("test/xml/Venue.xml");
        roomList = new ArrayList();
        labList = new ArrayList();
        hallList = new ArrayList();
        nodes = doc.getElementsByTagName("venue");

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            Element e = (Element) node;

            Venue v = new Venue(e.getAttribute("venueID"), e.getElementsByTagName("venueType").item(0).getTextContent(), Integer.parseInt(e.getElementsByTagName("capacity").item(0).getTextContent()), "");
            if (v.getVenueType().equalsIgnoreCase("Room")) {
                roomList.add(v);
            } else if (v.getVenueType().equalsIgnoreCase("Lab")) {
                labList.add(v);
            } else if (v.getVenueType().equalsIgnoreCase("Hall")) {
                hallList.add(v);
            }
        }

        scheduleList = new ArrayList();
        //Creating Schedule List for each Group
        for (int i = 0; i < groupList.size(); i++) {
            ArrayList<Class> classList = new ArrayList();
            scheduleList.add(classList);
        }
    }

    public void allocation() {
        //Assign Lecture Schedules
        for (int i = 0; i < lecList.size(); i++) {
            assignLecture(lecList.get(i));
        }

        //Assign Other Schedules, ex: Tutorial, Practical
        for (int i = 0; i < courseList.size(); i++) {
            for (int j = 0; j < scheduleList.size(); j++) {
                ArrayList<Class> classList = scheduleList.get(j);
                assignCourse(groupList.get(j).getGroupID(), classList, courseList.get(i));
            }
        }

        //Sort List
        for (int i = 0; i < scheduleList.size(); i++) {
            ArrayList<Class> classList = scheduleList.get(i);
            classList.sort(new Comparator<Class>() {
                @Override
                public int compare(Class o1, Class o2) {
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
                }
            });
        }
    }

    public void validation() {
        for (int i = 0; i < scheduleList.size(); i++) {
            for (int j = i + 1; j < scheduleList.size(); j++) {
                ArrayList<Class> list1 = scheduleList.get(i);
                ArrayList<Class> list2 = scheduleList.get(j);

                for (int index = 0; index < list1.size(); index++) {
                    Class class1 = list1.get(index);
                    if (!class1.getCourseType().equalsIgnoreCase("L")) {
                        for (int index2 = 0; index2 < list2.size(); index2++) {
                            Class class2 = list2.get(index2);
                            if (!class2.getCourseType().equalsIgnoreCase("L")) {
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
                                                venueID = getRandomVenue(class2.getCourseType());
                                            } while (temp.equalsIgnoreCase(venueID));
                                            class2.setVenueID(venueID);
                                        }
                                        if (class1.getStaffID().equalsIgnoreCase(class2.getStaffID())) {
                                            String temp = class2.getStaffID();
                                            String staffID = "";
                                            do {
                                                staffID = getRandomStaff();
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
                ArrayList<Class> list1 = scheduleList.get(i);
                ArrayList<Class> list2 = scheduleList.get(j);

                for (int index = 0; index < list1.size(); index++) {
                    Class class1 = list1.get(index);
                    if (!class1.getCourseType().equalsIgnoreCase("L")) {
                        for (int index2 = 0; index2 < list2.size(); index2++) {
                            Class class2 = list2.get(index2);
                            if (!class2.getCourseType().equalsIgnoreCase("L")) {
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

    public void optimization() {

    }

    public int getIndexOfScheduleList(String groupID) {
        int index = 0;
        for (int i = 0; i < scheduleList.size(); i++) {
            if (scheduleList.get(i).get(0).getGroupID().equalsIgnoreCase(groupID)) {
                index = i;
            }
        }
        return index;
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

        String venueID = getRandomVenue("L");
        String courseID = course.getCourseID();
        String courseType = course.getCourseType();
        double startTime, endTime;
        String staff = "";
        int day = 0;
        Class c;

        do {
            isClash = false;
            startTime = getRandomStartTime();
            endTime = startTime + Double.parseDouble(course.getCourseDuration());
            staff = getRandomStaff();
            day = getRandomDay();
            c = new Class(courseID, venueID, "-", staff, day, startTime, endTime, courseType);
            if (isTimeClash(scheduleList.get(0), c)) {
                isClash = true;
            }
        } while (isClash == true);

        for (int i = 0; i < groupList.size(); i++) {
            c = new Class(courseID, venueID, groupList.get(i).getGroupID(), staff, day, startTime, endTime, courseType);
            scheduleList.get(i).add(c);
        }
    }

    public void assignCourse(String groupID, ArrayList<Class> classList, CourseType course) {
        boolean isClash;
        switch (course.getCourseType()) {
            case "T": {
                String venueID = getRandomVenue("T");
                Class c;
                do {
                    isClash = false;
                    double startTime = getRandomStartTime();
                    double endTime = startTime + Double.parseDouble(course.getCourseDuration());
                    c = new Class(course.getCourseID(), venueID, groupID, getRandomStaff(), getRandomDay(), startTime, endTime, course.getCourseType());
                    if (isTimeClash(classList, c)) {
                        isClash = true;
                    }
                } while (isClash == true);
                classList.add(c);
                break;
            }
            case "P": {
                String venueID = getRandomVenue("P");
                Class c;
                do {
                    isClash = false;
                    double startTime = getRandomStartTime();
                    double endTime = startTime + Double.parseDouble(course.getCourseDuration());
                    c = new Class(course.getCourseID(), venueID, groupID, getRandomStaff(), getRandomDay(), startTime, endTime, course.getCourseType());
                    if (isTimeClash(classList, c)) {
                        isClash = true;
                    }
                } while (isClash == true);
                classList.add(c);
                break;
            }
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
                if ((startTime >= tempStart && startTime < tempEnd) || (endTime > tempStart && endTime <= tempEnd)) {
                    found = true;
                }
            }
        }
        return found;
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

    public String getRandomStaff() {
        return staffList.get(rand.nextInt(staffList.size())).getStaffID();
    }

    public String getRandomVenue(String courseType) {
        String venueID = "";
        int index = 0;
        if (courseType.equalsIgnoreCase("T")) {
            index = rand.nextInt(roomList.size());
            venueID = roomList.get(index).getVenueID();

        } else if (courseType.equalsIgnoreCase("P")) {
            index = rand.nextInt(labList.size());
            venueID = labList.get(index).getVenueID();

        } else if (courseType.equalsIgnoreCase("L")) {
            int totalSize = getTotalSize();
            do {
                index = rand.nextInt(hallList.size());
                venueID = hallList.get(index).getVenueID();
            } while (hallList.get(index).getCapacity() < totalSize);
        }

        return venueID;
    }

    public static void main(String args[]) throws ParserConfigurationException, SAXException, SAXException, IOException {
        SchedulingAlgorithm sa = new SchedulingAlgorithm();
        
        long startTime = System.nanoTime();
        long totalTime = 0;
        do {
            long nowTime = System.nanoTime();
            totalTime = TimeUnit.NANOSECONDS.toSeconds(nowTime - startTime);
            if (totalTime > 10) {
                break;
            } else {
                sa.initialize();
                sa.allocation();
                sa.validation();
            }
        } while (sa.countClash() > 0);

        if (totalTime > 10) {
            System.out.println("Timeout.");
        } else {
            for (int i = 0; i < sa.scheduleList.size(); i++) {
                ArrayList<Class> classList = sa.scheduleList.get(i);
                for (int j = 0; j < classList.size(); j++) {
                    if (j == 0) {
                        System.out.println("Group ID : " + classList.get(j).getGroupID());
                    }
                    System.out.println("Course ID (Staff ID): " + classList.get(j).getCourseID() + "(" + classList.get(j).getStaffID() + ")" + " Venue : " + classList.get(j).getVenueID() + " Day & Time: " + classList.get(j).getDay() + " (" + classList.get(j).getStartTime() + "-" + classList.get(j).getEndTime() + ")");
                }
                System.out.println("");
            }
        }
    }
}
