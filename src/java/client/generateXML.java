package client;

import da.CourseDA;
import domain.CourseType;
import da.StaffDA;
import domain.Staff;
import da.VenueDA;
import domain.Venue;
import da.TutorialGroupDA;
import domain.TutorialGroup;
import java.io.File;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

@ManagedBean
@SessionScoped
public class generateXML implements Serializable {

    //Course
    private CourseDA cda = new CourseDA();

    //Staff
    private StaffDA sda = new StaffDA();

    //Venue
    private VenueDA vda = new VenueDA();

    //Tutorial Group
    private TutorialGroupDA tgda = new TutorialGroupDA();

    private final String filePath = XMLPath.getXMLPath();

    public void generateCourseXML(List<String> courseList) {
        List<CourseType> list = new ArrayList<CourseType>();
        List<CourseType> recordList = new ArrayList<CourseType>();
        String xmlFilePath = filePath + "Course.xml";

        try {
            for (String id : courseList) {
                list = cda.getSelectedRecords(id);
                for (CourseType item : list) {
                    CourseType record = new CourseType(item.getCourseID(), item.getCourseType(), item.getCourseDuration(), item.getCourseCode());
                    recordList.add(record);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(generateXML.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();

            //root element
            Element root = document.createElement("courses");
            document.appendChild(root);

            for (CourseType item : recordList) {
                // course element
                Element course = document.createElement("course");
                root.appendChild(course);

                // set an attribute to course element
                Attr attr = document.createAttribute("courseID");
                attr.setValue(item.getCourseID());
                course.setAttributeNode(attr);

                // courseType element
                Element courseType = document.createElement("courseType");
                courseType.appendChild(document.createTextNode(item.getCourseType()));
                course.appendChild(courseType);

                // courseDuration element
                Element courseDuration = document.createElement("courseDuration");
                courseDuration.appendChild(document.createTextNode(item.getCourseDuration()));
                course.appendChild(courseDuration);

                // courseCode element
                Element courseCode = document.createElement("courseCode");
                courseCode.appendChild(document.createTextNode(item.getCourseCode()));
                course.appendChild(courseCode);

            }

            //create the xml file
            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(xmlFilePath));
            transformer.transform(domSource, streamResult);
            System.out.println("Done generating Course.xml XML File");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }

    }

    public void generateVenueXML(List<String> venueList) {
        List<Venue> list = new ArrayList<Venue>();
        List<Venue> recordList = new ArrayList<Venue>();
        String xmlFilePath = filePath + "Venue.xml";

        try {
            for (String id : venueList) {
                list = vda.getSelectedRecords(id);
                for (Venue item : list) {
                    Venue record = new Venue(item.getVenueID(), item.getBlock(), item.getVenueType(), item.getCapacity(), item.getCourseCodeList());
                    recordList.add(record);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(generateXML.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();

            //root element
            Element root = document.createElement("venues");
            document.appendChild(root);

            for (Venue item : recordList) {
                // venue element
                Element venue = document.createElement("venue");
                root.appendChild(venue);

                // set an attribute to staff element
                Attr attr = document.createAttribute("venueID");
                attr.setValue(item.getVenueID());
                venue.setAttributeNode(attr);

                // block element
                Element block = document.createElement("block");
                block.appendChild(document.createTextNode(item.getBlock()));
                venue.appendChild(block);

                // venueType element
                Element venueType = document.createElement("venueType");
                venueType.appendChild(document.createTextNode(item.getVenueType()));
                venue.appendChild(venueType);

                // capacity element
                Element capacity = document.createElement("capacity");
                capacity.appendChild(document.createTextNode(Integer.toString(item.getCapacity())));
                venue.appendChild(capacity);

                // courseCodeList element
                Element courseCodeList = document.createElement("courseCodeList");
                if (item.getCourseCodeList() == "") {
                    courseCodeList.appendChild(document.createTextNode("-"));
                } else {
                    courseCodeList.appendChild(document.createTextNode(item.getCourseCodeList()));
                }
                venue.appendChild(courseCodeList);

            }

            //create the xml file
            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(xmlFilePath));
            transformer.transform(domSource, streamResult);
            System.out.println("Done generating Venue.xml XML File");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }

    }

    public void generateStaffXML(List<String> staffList) {
        List<Staff> list = new ArrayList<Staff>();
        List<Staff> recordList = new ArrayList<Staff>();
        String xmlFilePath = filePath + "Staff.xml";

        for (String id : staffList) {
            list = sda.getSelectedStaff(id);
            for (Staff item : list) {
                Staff record = new Staff(item.getStaffID(), item.getStaffName(), item.getBlockDay(), item.getBlockStart(),
                        item.getBlockDuration(), item.getCourseCodeListS(), item.getLecGroupListS(), item.getTutGroupListS(), item.getPracGroupListS());
                recordList.add(record);
            }
        }

        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();

            //root element
            Element root = document.createElement("staffs");
            document.appendChild(root);

            for (Staff item : recordList) {
                // staff element
                Element staff = document.createElement("staff");
                root.appendChild(staff);

                // set an attribute to staff element
                Attr attr = document.createAttribute("staffID");
                attr.setValue(item.getStaffID());
                staff.setAttributeNode(attr);

                // name element
                Element staffName = document.createElement("name");
                staffName.appendChild(document.createTextNode(item.getStaffName()));
                staff.appendChild(staffName);

                // blockDay element
                Element blockDay = document.createElement("blockDay");
                if (item.getBlockDay() == 0) {
                    blockDay.appendChild(document.createTextNode("-"));

                } else {
                    blockDay.appendChild(document.createTextNode(Integer.toString(item.getBlockDay())));
                }
                staff.appendChild(blockDay);

                // blockStart element
                Element blockStart = document.createElement("blockStart");
                if (item.getBlockStart() == 0.0) {
                    blockStart.appendChild(document.createTextNode("-"));

                } else {
                    blockStart.appendChild(document.createTextNode(Double.toString(item.getBlockStart())));
                }
                staff.appendChild(blockStart);

                // blockDuration element
                Element blockDuration = document.createElement("blockDuration");
                if (item.getBlockDuration() == 0.0) {
                    blockDuration.appendChild(document.createTextNode("-"));

                } else {
                    blockDuration.appendChild(document.createTextNode(Double.toString(item.getBlockDuration())));
                }
                staff.appendChild(blockDuration);

                // courseCodeList element
                Element courseCodeList = document.createElement("courseCodeList");
                if (item.getCourseCodeListS().equals("")) {
                    courseCodeList.appendChild(document.createTextNode("-"));

                } else {
                    courseCodeList.appendChild(document.createTextNode(item.getCourseCodeListS()));
                }
                staff.appendChild(courseCodeList);

                // lecGroupList element
                Element lecGroupList = document.createElement("lecGroupList");
                if (item.getLecGroupListS().equals("")) {
                    lecGroupList.appendChild(document.createTextNode("-"));
                } else {

                    lecGroupList.appendChild(document.createTextNode(item.getLecGroupListS()));
                }
                staff.appendChild(lecGroupList);

                // tutGroupList element
                Element tutGroupList = document.createElement("tutGroupList");
                if (item.getTutGroupListS().equals("")) {
                    tutGroupList.appendChild(document.createTextNode("-"));

                } else {
                    tutGroupList.appendChild(document.createTextNode(item.getTutGroupListS()));
                }
                staff.appendChild(tutGroupList);

                // pracGroupList element
                Element pracGroupList = document.createElement("pracGroupList");
                if (item.getPracGroupListS().equals("")) {
                    pracGroupList.appendChild(document.createTextNode("-"));

                } else {
                    pracGroupList.appendChild(document.createTextNode(item.getPracGroupListS()));
                }
                staff.appendChild(pracGroupList);

            }

            //create the xml file
            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(xmlFilePath));
            transformer.transform(domSource, streamResult);
            System.out.println("Done generating Staff.xml XML File");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }

    }

    public void generateTutorialGroupXML(List<String> tgList) {
        List<TutorialGroup> list = new ArrayList<TutorialGroup>();
        List<TutorialGroup> recordList = new ArrayList<TutorialGroup>();
        String xmlFilePath = filePath + "TutorialGroup.xml";

        try {
            for (String id : tgList) {
                list = tgda.getSelectedRecords(id);
                for (TutorialGroup item : list) {
                    TutorialGroup record = new TutorialGroup(item.getGroupID(), item.getStudyYear(), item.getGroupNumber(), item.getSize(), item.getProgrammeID(), item.getCohortID(), item.getCourseCodeList());
                    recordList.add(record);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(generateXML.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();

            //root element
            Element root = document.createElement("tutorialGroups");
            document.appendChild(root);

            for (TutorialGroup item : recordList) {
                // tutorialGroup element
                Element tutorialGroup = document.createElement("tutorialGroup");
                root.appendChild(tutorialGroup);

                // set an attribute to staff element
                Attr attr = document.createAttribute("groupID");
                attr.setValue(item.getGroupID());
                tutorialGroup.setAttributeNode(attr);

                // studyYear element
                Element studyYear = document.createElement("studyYear");
                studyYear.appendChild(document.createTextNode(String.valueOf(item.getStudyYear())));
                tutorialGroup.appendChild(studyYear);

                // groupNumber element
                Element groupNumber = document.createElement("groupNumber");
                groupNumber.appendChild(document.createTextNode(String.valueOf(item.getGroupNumber())));
                tutorialGroup.appendChild(groupNumber);

                // size element
                Element size = document.createElement("size");
                size.appendChild(document.createTextNode(String.valueOf(item.getSize())));
                tutorialGroup.appendChild(size);

                // programmeID element
                Element programmeID = document.createElement("programmeID");
                programmeID.appendChild(document.createTextNode(item.getProgrammeID()));
                tutorialGroup.appendChild(programmeID);

                //cohortID element
                Element cohortID = document.createElement("cohortID");
                cohortID.appendChild(document.createTextNode(item.getCohortID()));
                tutorialGroup.appendChild(cohortID);

                //courseCodeList element
                Element courseCodeList = document.createElement("courseCodeList");
                courseCodeList.appendChild(document.createTextNode(item.getCourseCodeList()));
                tutorialGroup.appendChild(courseCodeList);

            }

            //create the xml file
            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(xmlFilePath));
            transformer.transform(domSource, streamResult);
            System.out.println("Done generating TutorialGroup.xml XML File");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }

    }
}
