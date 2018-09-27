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
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
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

    ArrayList<CourseType> courseList;
    ArrayList<Staff> staffList;
    ArrayList<Tutorial_Group> groupList;
    ArrayList<Venue> roomList, labList, hallList;
    ArrayList<ArrayList<Class>> scheduleList = new ArrayList();

    public void initialize() throws ParserConfigurationException, UnsupportedEncodingException, SAXException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        Document doc = dBuilder.parse("test/xml/Course.xml");
        courseList = new ArrayList();
        NodeList nodes = doc.getElementsByTagName("course");

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            Element e = (Element) node;

            CourseType c = new CourseType(e.getAttribute("courseID"), e.getElementsByTagName("courseType").item(0).getTextContent(), e.getElementsByTagName("courseDuration").item(0).getTextContent(), e.getElementsByTagName("courseCode").item(0).getTextContent());
            courseList.add(c);
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
    }

    public void allocation() {
        //Creating Schedule List for each Group
        for (int i = 0; i < groupList.size(); i++) {
            ArrayList<Class> classList = new ArrayList();
            scheduleList.add(classList);
        }
        for (int i = 0; i < courseList.size(); i++) {
            assignCourse(courseList.get(i));
        }
    }

    public int getTotalSize() {
        int totalSize = 0;
        for (int i = 0; i < groupList.size(); i++) {
            totalSize += groupList.get(i).getSize();
        }
        return totalSize;
    }

    public void assignCourse(CourseType course) {
        boolean isClash = false;
        switch (course.getCourseType()) {
            case "L": {
                String venueID = getRandomVenue("L");
                Class c;
                do {
                    isClash = false;
                    double startTime = getRandomStartTime();
                    double endTime = startTime + Double.parseDouble(course.getCourseDuration());
                    c = new Class(course.getCourseID(), venueID, "-", getRandomStaff(), getRandomDay(), startTime + "", endTime + "");
                    for (int i = 0; i < scheduleList.size(); i++) {
                        if (isTimeClash(scheduleList.get(i), c)) {
                            isClash = true;
                        }
                    }
                } while (isClash);

                for (int i = 0; i < groupList.size(); i++) {
                    c.setGroupID(groupList.get(i).getGroupID());
                    scheduleList.get(i).add(c);
                }
                break;
            }
            case "T": {
                for (int i = 0; i < groupList.size(); i++) {
                    String venueID = getRandomVenue("T");
                    do {
                        isClash = false;
                        double startTime = getRandomStartTime();
                        double endTime = startTime + Double.parseDouble(course.getCourseDuration());
                        Class c = new Class(course.getCourseID(), venueID, groupList.get(i).getGroupID(), getRandomStaff(), getRandomDay(), startTime + "", endTime + "");
                        if (isTimeClash(scheduleList.get(i), c)) {
                            isClash = true;
                        } else {
                            scheduleList.get(i).add(c);
                            break;
                        }
                    } while (isClash == true);
                }
                break;
            }
            case "P": {
                for (int i = 0; i < groupList.size(); i++) {
                    String venueID = getRandomVenue("P");
                    do {
                        isClash = false;
                        double startTime = getRandomStartTime();
                        double endTime = startTime + Double.parseDouble(course.getCourseDuration());
                        Class c = new Class(course.getCourseID(), venueID, groupList.get(i).getGroupID(), getRandomStaff(), getRandomDay(), startTime + "", endTime + "");
                        if (isTimeClash(scheduleList.get(i), c)) {
                            isClash = true;
                        } else {
                            scheduleList.get(i).add(c);
                            break;
                        }
                    } while (isClash == true);
                }
                break;
            }
        }
    }

    public boolean isTimeClash(ArrayList<Class> classList, Class c) {
        boolean found = false;
        for (int i = 0; i < classList.size(); i++) {
            if (classList.get(i).getDay() == c.getDay()) {
                double startTime = Double.parseDouble(c.getStartTime());
                double endTime = Double.parseDouble(c.getEndTime());
                double tempStart = Double.parseDouble(classList.get(i).getStartTime());
                double tempEnd = Double.parseDouble(classList.get(i).getEndTime());
                if ((startTime >= tempStart && startTime <= tempEnd) || (endTime >= tempStart && endTime <= tempEnd)) {
                    found = true;
                    break;
                }
            }
        }
        return found;
    }

    public double getRandomStartTime() {
        if (rand.nextBoolean()) {
            return (rand.nextInt(8) + 8);
        } else {
            return (rand.nextInt(8) + 8) + 0.5;
        }
    }

    public int getRandomDay() {
        return rand.nextInt(5) + 1;
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
        sa.initialize();
        sa.allocation();
        for (int i = 0; i < sa.scheduleList.size(); i++) {
            ArrayList<Class> classList = sa.scheduleList.get(i);
            classList.sort(new Comparator<Class>() {
                @Override
                public int compare(Class o1, Class o2) {
                    if (o1.getDay() == o2.getDay()) {
                        double time1 = Double.parseDouble(o1.getStartTime());
                        double time2 = Double.parseDouble(o2.getStartTime());
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

            for (int j = 0; j < classList.size(); j++) {
                if (j == 0) {
                    System.out.println("Group ID : " + classList.get(j).getGroupID());
                }
                System.out.println("Course ID: " + classList.get(j).getCourseID() + " Venue : " + classList.get(j).getVenueID() + " Day & Time: " + classList.get(j).getDay() + " (" + classList.get(j).getStartTime() + "-" + classList.get(j).getEndTime() + ")");
            }
            System.out.println("");

        }
    }
}
