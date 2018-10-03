/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import da.*;
import domain.Class;
import domain.*;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
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
    private ArrayList<TutorialGroup> groupList;
    private ArrayList<Venue> roomList, labList, hallList;
    private ArrayList<Schedule> scheduleList;

    private int studyDays, blockDay = 99;
    private double studyStart, studyEnd, blockStart, blockEnd, maxBreak;

    private ClassDA cda = new ClassDA();
    private VenueDA vda = new VenueDA();
    private StaffDA sda = new StaffDA();

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

            TutorialGroup tg = new TutorialGroup(e.getAttribute("groupID"), Integer.parseInt(e.getElementsByTagName("studyYear").item(0).getTextContent()), Integer.parseInt(e.getElementsByTagName("groupNumber").item(0).getTextContent()), Integer.parseInt(e.getElementsByTagName("size").item(0).getTextContent()), e.getElementsByTagName("programmeID").item(0).getTextContent(), e.getElementsByTagName("cohortID").item(0).getTextContent());
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
            stf.setClassList(sda.getClassList(stf.getStaffID()));
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
            Class c = new Class("-", "-", "-", "-", blockDay, blockStart, blockEnd, "B");
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

        if (blockDay != 99) {
            clearBlock();
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
                }
                return o1.getDay() - o2.getDay();
            });
        }

        for (int i = 0; i < scheduleList.size(); i++) {
            ArrayList<Class> classList = scheduleList.get(i).getClassList();
            for (int j = 0; j < classList.size(); j++) {
                Class c = classList.get(j);
                if (!c.getCourseType().equalsIgnoreCase("L")) {
                    assignVenueStaff(c);
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
                    for (int index2 = 0; index2 < list2.size(); index2++) {
                        Class class2 = list2.get(index2);
                        if (!class1.getCourseType().equalsIgnoreCase("L") && !class1.getCourseType().equalsIgnoreCase("B") && !class2.getCourseType().equalsIgnoreCase("L") && !class2.getCourseType().equalsIgnoreCase("B")) {
                            if (class1.getDay() == class2.getDay()) {
                                double startTime1 = class1.getStartTime();
                                double endTime1 = class1.getEndTime();

                                double startTime2 = class2.getStartTime();
                                double endTime2 = class2.getEndTime();
                                if ((startTime2 >= startTime1 && startTime2 < endTime1) || (endTime2 > startTime1 && endTime2 <= endTime1)) {
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
                if (c.getCourseType().equals("B")) {
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

    public void assignVenueStaff(Class c) {
        boolean isClash;

        double startTime2 = c.getStartTime();
        double endTime2 = c.getEndTime();
        Venue v = new Venue();
        Staff s = new Staff();
        do {
            isClash = false;
            v = getRandomVenue(c.getCourseType());
            ArrayList<Class> venueClassList = v.getClassList();
            if (!venueClassList.isEmpty()) {
                for (int i = 0; i < venueClassList.size(); i++) {
                    Class temp = venueClassList.get(i);
                    if (temp.getDay() == c.getDay()) {
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
                    for (int j = 0; j < classList.size(); j++) {
                        Class temp = scheduleList.get(i).getClassList().get(j);
                        if (!temp.getVenueID().equalsIgnoreCase("-")) {
                            if (temp.getDay() == c.getDay()) {
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
                }
            }
        } while (isClash == true);
        c.setVenueID(v.getVenueID());

        do {
            isClash = false;
            s = getRandomStaff();
            ArrayList<Class> staffClassList = s.getClassList();
            if (!staffClassList.isEmpty()) {
                for (int i = 0; i < staffClassList.size(); i++) {
                    Class temp = staffClassList.get(i);
                    if (temp.getDay() == c.getDay()) {
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
                        if (!temp.getStaffID().equalsIgnoreCase("-")) {
                            if (temp.getDay() == c.getDay()) {
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
                }
            }
        } while (isClash == true);

        c.setStaffID(s.getStaffID());
    }

    public void assignLecture(CourseType course) {
        boolean isClash;

        String courseID = course.getCourseID();
        String courseType = course.getCourseType();
        String venueID = "", staffID = "";
        Venue v = new Venue();
        Staff s = new Staff();
        double startTime, endTime;
        int day = 0;
        Class c;

        do {
            isClash = false;
            startTime = getRandomStartTime();
            endTime = startTime + Double.parseDouble(course.getCourseDuration());

            day = getRandomDay();
            c = new Class(courseID, "-", "-", "-", day, startTime, endTime, courseType);
            if (isTimeClash(scheduleList.get(0).getClassList(), c)) {
                isClash = true;
            } else {
                s = getRandomStaff();
                ArrayList<Class> staffClassList = s.getClassList();
                if (!staffClassList.isEmpty()) {
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
                ArrayList<Class> venueClassList = v.getClassList();
                if (!venueClassList.isEmpty()) {
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
        } while (isClash);
        venueID = v.getVenueID();
        staffID = s.getStaffID();

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
            c = new Class(course.getCourseID(), "-", groupID, "-", day, startTime, endTime, course.getCourseType());

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

    public int countDBClash() throws SQLException {
        ArrayList<Class> dbList = cda.get("G1004");
        int count = 0;

        for (int i = 0; i < scheduleList.size(); i++) {
            ArrayList<Class> classList = scheduleList.get(i).getClassList();
            for (Class c : classList) {
                for (Class d : dbList) {
                    if (c.getDay() == d.getDay()) {
                        if ((c.getStartTime() >= d.getStartTime() && c.getStartTime() < d.getEndTime()) || (d.getEndTime() > c.getStartTime() && d.getEndTime() <= c.getEndTime())) {
                            if (c.getVenueID().equalsIgnoreCase(d.getVenueID()) || c.getStaffID().equalsIgnoreCase(d.getStaffID())) {
                                count++;
                                System.out.println("Local Class: " + c.getGroupID() + " - " + c.getCourseID() + " - " + c.getDay() + " - " + c.getStaffID() + " - " + c.getVenueID());
                                System.out.println("DB Class: " + d.getGroupID() + " - " + d.getCourseID() + " - " + d.getDay() + " - " + d.getStaffID() + " - " + d.getVenueID());
                            }
                        }
                    }
                }
            }
        }
        return count;
    }

    public void start() throws Exception {
        initialize();
        if (staffList.size() <= groupList.size() + 1) {
            System.out.println("Insufficient staff data to perform allocation. ");
        } else if (hallList.size() + roomList.size() + labList.size() <= courseList.size() + lecList.size()) {
            System.out.println("Insufficient venue data to perform allocation.");
        } else {
            allocation();
            storeData();
            for (int i = 0; i < scheduleList.size(); i++) {
                ArrayList<Class> classList = scheduleList.get(i).getClassList();
                for (int j = 0; j < classList.size(); j++) {
                    if (j == 0) {
                        System.out.println("Group ID : " + classList.get(j).getGroupID());
                    }
                    System.out.println("Course ID (Staff ID): " + classList.get(j).getCourseID() + " (" + classList.get(j).getStaffID() + ")" + " Venue : " + classList.get(j).getVenueID() + " Day & Time: " + classList.get(j).getDay() + " (" + classList.get(j).getStartTime() + "-" + classList.get(j).getEndTime() + ")");
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
