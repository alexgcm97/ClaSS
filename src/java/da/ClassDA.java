/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package da;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import domain.Class;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author Alex
 */
@ManagedBean
@RequestScoped
public class ClassDA {

    public ArrayList<Class> get(String groupID) throws SQLException {
        ArrayList<Class> classList = new ArrayList();
        Connection connect = null;
        String url = "jdbc:derby://localhost:1527/schedule";

        String username = "schedule";
        String password = "schedule";

        try {
            connect = DriverManager.getConnection(url, username, password);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM class WHERE groupID = ?");
        pstmt.setString(1, groupID);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            Class c = new domain.Class(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getDouble(6), rs.getDouble(7));
            classList.add(c);
        }
        return classList;
    }

    public boolean checkExist(String groupID) throws SQLException {
        boolean found = false;
        Connection connect = null;
        String url = "jdbc:derby://localhost:1527/schedule";

        String username = "schedule";
        String password = "schedule";

        try {
            connect = DriverManager.getConnection(url, username, password);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM class WHERE groupID = ?");
        pstmt.setString(1, groupID);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            found = true;
        }
        return found;
    }

    public void deleteRecords(String groupID) throws SQLException {
        boolean found = true;
        Connection connect = null;
        String url = "jdbc:derby://localhost:1527/schedule";

        String username = "schedule";
        String password = "schedule";

        try {
            connect = DriverManager.getConnection(url, username, password);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        PreparedStatement pstmt = connect.prepareStatement("DELETE FROM class WHERE groupID = ?");
        pstmt.setString(1, groupID);
        pstmt.executeUpdate();
    }

    public void insert(Class c) throws SQLException {
        Connection connect = null;
        String url = "jdbc:derby://localhost:1527/schedule";

        String username = "schedule";
        String password = "schedule";

        try {
            connect = DriverManager.getConnection(url, username, password);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        PreparedStatement pstmt = connect.prepareStatement("INSERT INTO class VALUES(?,?,?,?,?,?,?)");
        pstmt.setString(1, c.getCourseID());
        pstmt.setString(2, c.getVenueID());
        pstmt.setString(3, c.getGroupID());
        pstmt.setString(4, c.getStaffID());
        pstmt.setInt(5, c.getDay());
        pstmt.setDouble(6, c.getStartTime());
        pstmt.setDouble(7, c.getEndTime());
        pstmt.executeUpdate();
    }
}
