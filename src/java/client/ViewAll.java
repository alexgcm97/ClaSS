/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import da.DBConnection;
import domain.*;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author DEREK
 */
@ManagedBean(name = "viewAll")
@SessionScoped
public class ViewAll implements Serializable {

    //GET VENUE
    public List<Venue> getAllVenue() throws ClassNotFoundException, SQLException {

        Connection connect;
        connect = DBConnection.getConnection();
        List<Venue> Venue = new ArrayList<Venue>();
        PreparedStatement pstmt = connect
                .prepareStatement("SELECT * FROM venue");
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {

            Venue v = new Venue();
            v.setVenueID(rs.getString("venueID"));
            v.setBlock(rs.getString("block"));
            v.setVenueType(rs.getString("venueType"));
            v.setCapacity(rs.getInt("capacity"));
            v.setCourseCodeList(rs.getString("courseCodeList"));

            Venue.add(v);

        }

        // close resources
        rs.close();
        pstmt.close();
        connect.close();

        return Venue;
    }

    //Get COURSE
    public List<Course> getAllCourse() throws ClassNotFoundException, SQLException {

        Connection connect;
        connect = DBConnection.getConnection();

        List<Course> Course = new ArrayList<>();

        PreparedStatement pstmt = connect
                .prepareStatement("SELECT * FROM COURSE");

        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {

            Course c = new Course();

            c.setCourseCode(rs.getString("courseCode"));
            c.setCourseName(rs.getString("courseName"));
            c.setCreditHour(rs.getInt("creditHour"));

            Course.add(c);

        }
        // close resources
        rs.close();
        pstmt.close();
        connect.close();

        return Course;
    }

    public List<TutorialGroup> getAllTutorialGroup() throws ClassNotFoundException, SQLException {

        Connection connect;
        connect = DBConnection.getConnection();

        List<TutorialGroup> TutorialGroup = new ArrayList<TutorialGroup>();

        PreparedStatement pstmt = connect
                .prepareStatement("SELECT * FROM TutorialGroup");
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {

            TutorialGroup tg = new TutorialGroup();
            tg.setGroupID(rs.getString("groupID"));
            tg.setGroupNumber(rs.getInt("groupNumber"));
            tg.setSize(rs.getInt("size"));
            tg.setCohortID(rs.getString("cohortID"));

            TutorialGroup.add(tg);
        }

        // close resources
        rs.close();
        pstmt.close();
        connect.close();

        return TutorialGroup;
    }

    public List<Staff> getAllStaff() throws ClassNotFoundException, SQLException {

        Connection connect;
        connect = DBConnection.getConnection();
        List<Staff> Staff = new ArrayList<Staff>();
        PreparedStatement pstmt = connect
                .prepareStatement("SELECT * FROM Staff ORDER BY STAFFID");

        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {

            Staff s = new Staff();

            s.setStaffID(rs.getString("staffID"));
            s.setStaffName(rs.getString("staffName"));
            s.setBlockDay(rs.getInt("blockDay"));
            s.setBlockStart(rs.getDouble("blockStart"));
            s.setBlockDuration(rs.getInt("blockDuration"));
            s.setCourseCodeListS(rs.getString("courseCodeList"));
            s.setLecGroupListS(rs.getString("lecGroupList"));
            s.setTutGroupListS(rs.getString("tutGroupList"));
            s.setPracGroupListS(rs.getString("pracGroupList"));

            Staff.add(s);

        }

        // close resources
        rs.close();
        pstmt.close();
        connect.close();

        return Staff;
    }

    public List<Programme> getAllProgrammeCode() throws ClassNotFoundException, SQLException {

        Connection connect;
        connect = DBConnection.getConnection();
        List<Programme> programme = new ArrayList<Programme>();
        PreparedStatement pstmt = connect
                .prepareStatement("SELECT * FROM PROGRAMME ORDER BY PROGRAMMECODE");
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            Programme p = new Programme();
            p.setProgrammeCode(rs.getString("programmeCode"));

            programme.add(p);
        }
        // close resources
        rs.close();
        pstmt.close();
        connect.close();

        return programme;
    }

    public List<ProgrammeCohort> getAllCohortID() throws ClassNotFoundException, SQLException {

        Connection connect;
        connect = DBConnection.getConnection();
        List<ProgrammeCohort> ProgrammeCohort = new ArrayList<ProgrammeCohort>();
        PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM ProgrammeCohort ORDER BY COHORTID");
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            ProgrammeCohort pc = new ProgrammeCohort(rs.getString("cohortID"), rs.getString("entryYear"), rs.getString("programmeCode"), rs.getInt("studyYear"), rs.getString("intakeYear"));
            ProgrammeCohort.add(pc);
        }
        // close resources
        rs.close();
        pstmt.close();
        connect.close();

        return ProgrammeCohort;
    }

    public List<ProgrammeCohort> getAllProgrammeCohort() throws ClassNotFoundException, SQLException {

        Connection connect;
        connect = DBConnection.getConnection();

        List<ProgrammeCohort> ProgrammeCohort = new ArrayList<ProgrammeCohort>();
        PreparedStatement pstmt = connect
                .prepareStatement("SELECT * FROM PROGRAMMECOHORT");
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {

            ProgrammeCohort pc = new ProgrammeCohort();
            pc.setCohortID(rs.getString("cohortID"));
            pc.setEntryYear(rs.getString("entryYear"));
            pc.setProgrammeCode(rs.getString("programmeCode"));
            pc.setStudyYear(rs.getInt("studyYear"));
            pc.setIntakeYear(rs.getString("intakeYear"));
            pc.setCourseCodeList(rs.getString("courseCodeList"));
            ProgrammeCohort.add(pc);

        }
        rs.close();
        pstmt.close();
        connect.close();

        return ProgrammeCohort;
    }

    public List<Programme> getAllProgramme() throws ClassNotFoundException, SQLException {

        Connection connect;
        connect = DBConnection.getConnection();

        List<Programme> Programme = new ArrayList<Programme>();
        PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM PROGRAMME");
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {

            Programme p = new Programme();
            p.setProgrammeCode(rs.getString("programmeCode"));
            p.setProgrammeName(rs.getString("programmeName"));

            Programme.add(p);

        }

        // close resources
        rs.close();
        pstmt.close();
        connect.close();

        return Programme;
    }
}
