/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package da;

import domain.Programme;
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
public class ProgrammeDA {
    
    public List<Programme> getAllProgrammeRecords() throws SQLException {

        Connection connect = null;

        List<Programme> output = new ArrayList<Programme>();
        try {
            connect = DBConnection.getConnection();
            PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM programme");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Programme p = new Programme(rs.getString(1), rs.getString(2), rs.getString(3));
                output.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }

        DBConnection.close(connect);
        return output;

    }
}
