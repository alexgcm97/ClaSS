/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package da;

import domain.CourseType;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author DEREK
 */
@ManagedBean
@SessionScoped
public class CourseTypeDA implements Serializable {

    private CourseType c;
    boolean success, message;

    public void ini() {
        c = new CourseType();
    }

    public CourseType getCourse() {
        return c;
    }

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
     public List<CourseType> getAllRecords() throws SQLException {

        Connection connect = null;

        List<CourseType> output = new ArrayList<>();
        try {
            connect = DBConnection.getConnection();
            PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM coursetype");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                CourseType CT = new CourseType();
                CT.setCourseID(rs.getString(1));
                CT.setCourseType(rs.getString(2));
                CT.setCourseDuration(rs.getString(3));
                CT.setCourseCode(rs.getString(4));

                output.add(CT);
            }
        } catch (SQLException e) {
        }
        
        DBConnection.close(connect);
        return output;

    }

    public void addCourseInfo() throws SQLException, ClassNotFoundException {
        int id2 = -1;
        FacesContext fc = FacesContext.getCurrentInstance();
        Map<String, String> params = fc.getExternalContext().getRequestParameterMap();
        String courseCode = params.get("courseCode");
        String courseType = params.get("courseType");
        String courseDuration = params.get("courseDuration");
        String courseID = Integer.toString(id2) + 1;

        Connection connect = null;
        String url = "jdbc:derby://localhost:1527/schedule";
        String username = "schedule";
        String password = "schedule";

        Statement st2 = connect.createStatement();
        ResultSet idMax = st2.executeQuery("select (max(COURSECODE),0) maxID from COURSETYPE");

        if (idMax.next()) {
            id2 = idMax.getInt("maxID");
        }

        try {
            connect = DriverManager.getConnection(url, username, password);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        try {
            PreparedStatement pstmt = connect.prepareStatement("INSERT INTO COURSETYPE VALUES(?,?,?,?)");

            pstmt.setString(1, courseID);
            pstmt.setString(2, courseType);
            pstmt.setString(3, courseDuration);
            pstmt.setString(3, courseCode);

            pstmt.executeUpdate();
            this.success = true;
            this.message = false;

        } catch (SQLException ex) {
            this.message = true;
            this.success = false;
            System.out.println(ex.getMessage());
        }

    }

    public String getMaxID() {

        FacesContext fc = FacesContext.getCurrentInstance();
        Map<String, String> params = fc.getExternalContext().getRequestParameterMap();

        Connection connect = null;

        String url = "jdbc:derby://localhost:1527/schedule";
        String username = "schedule";
        String password = "schedule";
        String courseID = "";

        try {

            connect = DriverManager.getConnection(url, username, password);
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COURSEID FROM COURSETYPE");

            ArrayList<Integer> ls = new ArrayList<>();

            while (rs.next()) {
                try {
                    ls.add(Integer.parseInt(rs.getString(1).split("C")[1]));
                } catch (Exception ex) {
                    System.out.println("Invalid Exception");
                }
            }

            if (ls.size() > 0) {
                int max = Collections.max(ls) + 1;
                courseID = "C" + max;
            } else {
                courseID = "C";
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(courseID);
        return courseID;
    }

}
