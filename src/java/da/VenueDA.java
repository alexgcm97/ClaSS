/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package da;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import domain.Class;

/**
 *
 * @author Alex
 */
public class VenueDA {

    private Connection connect;

    public ArrayList<Class> getClassList(String venueID) throws SQLException {
        ArrayList<Class> classList = new ArrayList();

        try {
            connect = DBConnection.getConnection();

            PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM class WHERE venueID = ?");
            pstmt.setString(1, venueID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Class c = new domain.Class(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getDouble(6), rs.getDouble(7));
                classList.add(c);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        connect.close();
        return classList;
    }
}
