/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment6;

import java.sql.SQLException;

/**
 *
 * @author Pranay
 */
public class CreatePurchaseHistoryTable extends Assignment6 {

    CreateHistoryClass createhistory = new CreateHistoryClass();

    public void createPurchaseHistoryTable() throws SQLException {

        statement.executeUpdate(createPurchaseHistoryQuery);
        String query = insterIntoPurchaseHistoryTableQuery + buffer.toString();
        String finalQuery = query.substring(0, query.length() - 1);
        statement.executeUpdate(finalQuery);

    }

}
