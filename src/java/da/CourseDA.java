/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package da;

import domain.CourseDetails;
import domain.CourseType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author REPUBLIC
 */
public class CourseDA {
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
                                case "T":
                                    output.get(i).setTutHours(rs1.getDouble(5));
                                    break;
                                case "P":
                                    output.get(i).setPracHours(rs1.getDouble(5));
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
                            case "T":
                                cd.setTutHours(rs1.getDouble(5));
                                break;
                            case "P":
                                cd.setPracHours(rs1.getDouble(5));
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
}
