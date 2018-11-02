/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package da;

import domain.Course;
import domain.CourseDetails;
import domain.CourseType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author REPUBLIC
 */
@ManagedBean
@SessionScoped
public class CourseDA {

    public List<CourseDetails> getRelatedCourseRecords(List<String> courseCodeList) throws SQLException {
        Connection connect = null;
        boolean found;
        List<CourseDetails> output = new ArrayList<CourseDetails>();
        connect = DBConnection.getConnection();
        for (String courseCodeStr : courseCodeList) {
            try {
                PreparedStatement pstmt = connect.prepareStatement("SELECT courseCode FROM course WHERE courseCode = ?");
                pstmt.setString(1, courseCodeStr);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    String courseCode = rs.getString(1);
                    PreparedStatement stmt = connect.prepareStatement("SELECT c.courseCode,c.courseName,ct.courseID,ct.courseType,ct.courseDuration FROM course c, coursetype ct WHERE c.courseCode=? AND c.courseCode = ct.courseCode");
                    stmt.setString(1, courseCode);
                    ResultSet rs1 = stmt.executeQuery();
                    while (rs1.next()) {
                        CourseDetails cd = new CourseDetails();
                        cd.setCourseCode(rs1.getString(1));
                        cd.setCourseName(rs1.getString(2));
                        found = false;
                        for (int i = 0; i < output.size(); i++) {
                            if (output.get(i).getCourseCode().equals(rs1.getString(1))) {
                                switch (rs1.getString(4)) {
                                    case "L":
                                        output.get(i).setLecHours(rs1.getDouble(5));
                                        break;
                                    case "P":
                                        output.get(i).setPracHours(rs1.getDouble(5));
                                        break;
                                    default:
                                        output.get(i).setTutHours(rs1.getDouble(5));
                                        break;
                                }
                                found = true;
                            }
                        }
                        if (!found) {
                            switch (rs1.getString(4)) {
                                case "L":
                                    cd.setLecHours(rs1.getDouble(5));
                                    break;
                                case "P":
                                    cd.setPracHours(rs1.getDouble(5));
                                    break;
                                default:
                                    cd.setTutHours(rs1.getDouble(5));
                                    break;
                            }
                            output.add(cd);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        DBConnection.close(connect);
        return output;
    }

    public List<CourseDetails> getAllCourseRecords() throws SQLException {
        Connection connect = null;
        boolean found;
        List<CourseDetails> output = new ArrayList<CourseDetails>();
        connect = DBConnection.getConnection();
        try {
            PreparedStatement pstmt = connect.prepareStatement("SELECT courseCode FROM course");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String courseCode = rs.getString(1);
                PreparedStatement stmt = connect.prepareStatement("SELECT c.courseCode,c.courseName,ct.courseID,ct.courseType,ct.courseDuration FROM course c, coursetype ct WHERE c.courseCode=? AND c.courseCode = ct.courseCode");
                stmt.setString(1, courseCode);
                ResultSet rs1 = stmt.executeQuery();
                while (rs1.next()) {
                    CourseDetails cd = new CourseDetails();
                    cd.setCourseCode(rs1.getString(1));
                    cd.setCourseName(rs1.getString(2));

                    found = false;
                    for (int i = 0; i < output.size(); i++) {
                        if (output.get(i).getCourseCode().equals(rs1.getString(1))) {
                            switch (rs1.getString(4)) {
                                case "L":
                                    output.get(i).setLecHours(rs1.getDouble(5));
                                    break;
                                case "P":
                                    output.get(i).setPracHours(rs1.getDouble(5));
                                    break;
                                default:
                                    output.get(i).setTutHours(rs1.getDouble(5));
                                    break;
                            }
                            found = true;
                        }
                    }
                    if (!found) {
                        switch (rs1.getString(4)) {
                            case "L":
                                cd.setLecHours(rs1.getDouble(5));
                                break;
                            case "P":
                                cd.setPracHours(rs1.getDouble(5));
                                break;
                            default:
                                cd.setTutHours(rs1.getDouble(5));
                                break;
                        }
                        output.add(cd);
                    }

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }

        DBConnection.close(connect);
        return output;

    }

    public List<CourseType> getSelectedRecords(String courseCode) throws SQLException {

        Connection connect = null;
        List<CourseType> output = new ArrayList<CourseType>();

        try {
            connect = DBConnection.getConnection();
            PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM coursetype WHERE courseCode=? ");
            pstmt.setString(1, courseCode);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                CourseType CT = new CourseType();
                CT.setCourseID(rs.getString(1));
                CT.setCourseType(rs.getString(2));
                CT.setCourseDuration(rs.getString(3));
                CT.setCourseCode(rs.getString(4));

                output.add(CT);

            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        DBConnection.close(connect);
        return output;

    }

    public List<Course> getAllRecords() throws SQLException {

        Connection connect = null;

        List<Course> output = new ArrayList<>();
        try {
            connect = DBConnection.getConnection();
            PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM course");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Course C = new Course();
                C.setCourseCode(rs.getString(1));
                C.setCourseName(rs.getString(2));
                C.setCreditHour(rs.getInt(3));

                output.add(C);
            }
        } catch (SQLException e) {

        }

        DBConnection.close(connect);
        return output;

    }

    boolean success, message, delete, update;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isMessage() {
        return message;
    }

    public void setMessage(boolean message) {
        this.message = message;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public void insertCourse(Course c) {
        Connection connect = null;

        try {

            connect = DBConnection.getConnection();

            PreparedStatement pstmt = connect.prepareStatement("INSERT INTO COURSE VALUES(?,?,?)");

            pstmt.setString(1, c.getCourseCode());
            pstmt.setString(2, c.getCourseName());
            pstmt.setDouble(3, c.getCreditHour());

            pstmt.executeUpdate();

            this.success = true;
            this.message = false;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        DBConnection.close(connect);
    }

    public void insertCourseType(CourseType ct) throws SQLException {
        Connection connect = null;
        String courseID = getMaxID();

        try {
            connect = DBConnection.getConnection();
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT courseID FROM courseType");

            ArrayList<Integer> ls = new ArrayList<>();

            while (rs.next()) {
                try {
                    ls.add(Integer.parseInt(rs.getString(1).split("C")[1]));
                } catch (NumberFormatException | SQLException ex) {
                    System.out.println("Invalid Exception");
                }
            }

            if (ls.size() > 0) {
                int max = Collections.max(ls) + 1;
                courseID = "C" + max;
            } else {
                courseID = "C1001";
            }

            connect = DBConnection.getConnection();

            PreparedStatement pstmt = connect.prepareStatement("INSERT INTO COURSETYPE VALUES(?,?,?,?)");

            pstmt.setString(1, ct.getCourseID());
            pstmt.setString(2, ct.getCourseType());
            pstmt.setString(3, ct.getCourseDuration());
            pstmt.setString(4, ct.getCourseCode());

            pstmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        DBConnection.close(connect);

    }

    public List<CourseType> getCourseType(String courseCode) {
        Connection connect;
        List<CourseType> list = new ArrayList<>();
        try {
            connect = DBConnection.getConnection();
            PreparedStatement stmt = connect.prepareStatement("select * from CourseType where courseCode = ?");
            stmt.setString(1, courseCode);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                CourseType ct = new CourseType();
                ct.setCourseID(rs.getString(1));
                ct.setCourseType(rs.getString(2));
                ct.setCourseDuration(rs.getString(3));
                ct.setCourseCode(rs.getString(4));
                list.add(ct);
            }

        } catch (SQLException e) {
            System.out.println(e);
        }
        return list;
    }

    public Course getCourse(String courseCode) {
        Connection connect;
        Course c = new Course();

        try {
            connect = DBConnection.getConnection();
            PreparedStatement pstmt = connect.prepareStatement("select * from Course where courseCode = ?");
            pstmt.setString(1, courseCode);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                c = new Course(rs.getString(1), rs.getString(2), rs.getDouble(3));
            }

        } catch (SQLException e) {
            System.out.println(e);
        }
        return c;
    }

    public void updateCourse(Course c) {
        Connection connect;
        try {
            connect = DBConnection.getConnection();
            PreparedStatement ps = connect.prepareStatement("update Course set courseName=?, creditHour=? where courseCode=?");
            ps.setString(1, c.getCourseName());
            ps.setDouble(2, c.getCreditHour());
            ps.setString(3, c.getCourseCode());
            ps.executeUpdate();
            this.update = true;
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public Course deleteCourse(String courseCode) {
        Connection connect;
        Course c = new Course();
        try {
            connect = DBConnection.getConnection();
            PreparedStatement ps = connect.prepareStatement("delete from Course where courseCode = ?");
            ps.setString(1, courseCode);
            System.out.println(ps);
            ps.executeUpdate();
            ps = connect.prepareStatement("delete from CourseType where courseCode = ?");
            ps.setString(1, courseCode);
            System.out.println(ps);
            ps.executeUpdate();
            this.delete = true;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return c;
    }

    public void deleteCourseType(String courseID) {
        Connection connect;
        Course c = new Course();
        try {
            connect = DBConnection.getConnection();

            PreparedStatement ps = connect.prepareStatement("delete from CourseType where courseID = ?");
            ps.setString(1, courseID);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public String getMaxID() {
        Connection connect;
        String courseID = "";
        try {
            connect = DBConnection.getConnection();
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COURSEID FROM COURSETYPE");

            ArrayList<Integer> ls = new ArrayList<>();

            while (rs.next()) {
                try {
                    ls.add(Integer.parseInt(rs.getString(1).split("C")[1]));
                } catch (NumberFormatException | SQLException ex) {
                    System.out.println("Invalid Exception");
                }
            }

            if (ls.size() > 0) {
                int max = Collections.max(ls) + 1;
                courseID = "C" + max;
            } else {
                courseID = "C";
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
        }
        return courseID;
    }

    public void reset() {
        this.success = false;
        this.update = false;
        this.delete = false;
    }
}
