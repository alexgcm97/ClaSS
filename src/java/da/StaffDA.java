/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package da;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import domain.Class;
import java.util.ArrayList;

/**
 *
 * @author Alex
 */
public class StaffDA {

    public ArrayList<Class> getClassList(String staffID) throws SQLException {
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

        PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM class WHERE staffID = ?");
        pstmt.setString(1, staffID);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            Class c = new Class(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getDouble(6), rs.getDouble(7));
            classList.add(c);
        }
        return classList;
    }
}
