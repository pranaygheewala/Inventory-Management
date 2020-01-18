/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment6;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Properties;

/**
 *
 * @author Pranay
 */
public class Assignment6 {

    public static String ShippedDate;
    public static String ProductID;
    public static String ProductName;
    public static int DaySales;
    public static int Day_End_Inventory = 0;
    public static int ReorderLevel;
    public static double UnitPrice;
    public static int UnitsOnOrder;
    public static int UnitPriceTotal;
    public static Connection connect = null; //Connection variable for connecting to database
    public static Statement statement = null; //Statement variable for executing query
    public static String insterIntoPurchaseHistoryTableQuery;
    public static StringBuffer buffer = new StringBuffer();
    public static DecimalFormat df2 = new DecimalFormat(".##");
    public static double unitPriceFinalValue;

    String createPurchaseHistoryQuery = "CREATE TABLE IF NOT EXISTS purchasehistory ("
            + "    ShippedDate varchar(50),"
            + "    ProductId varchar(50),"
            + "    ReorderedUnits int,"
            + "    UnitPrice double" + ");";

    public static void main(String[] args) {
        // TODO code application logic here

        CreateHistoryClass createhistory = new CreateHistoryClass();
        CreatePurchaseHistoryTable createpurchasehistorytable = new CreatePurchaseHistoryTable();
        ResultSet historyResultSet = null; //customerResultset variable for getting customer data
        String database = "csci3901";
        String user = "root";
        String password = "root";

        String purchaseHistoryQuery = "select orders.ShippedDate,products.ProductID, products.ProductName,sum(orderdetails.Quantity) as daysales "
                + ",products.UnitsInStock as day_end_inventory , products.ReorderLevel, orderdetails.UnitPrice, products.UnitsOnOrder from orders"
                + " inner join orderdetails on orders.OrderID = orderdetails.OrderID"
                + " inner join products on orderdetails.ProductID = products.ProductID"
                + " where orders.ShippedDate is not null  "
                + " group by orders.ShippedDate,products.ProductID"
                + " order by products.ProductID, orders.ShippedDate desc;";

        insterIntoPurchaseHistoryTableQuery = "INSERT INTO purchasehistory VALUES";

        try {

            Class.forName("com.mysql.jdbc.Driver");  // To load the MySQL driver

            connect = DriverManager.getConnection("jdbc:mysql://localhost:3306", user, password); // To setup the connection with the Database

            statement = connect.createStatement(); // Statement object to allow issue SQL queries to the database

            statement.executeQuery("use " + database + ";"); // Choosing a database to use

            // customerResult set to get the result of the customer SQL query 
            historyResultSet = statement.executeQuery(purchaseHistoryQuery);

            boolean isFirst = true;
            String productID = "1";

            while (historyResultSet.next()) { //iterating through customer resultset one row at a time in the customerResulset

                ShippedDate = historyResultSet.getString("ShippedDate");
                ProductID = historyResultSet.getString("ProductID");
                DaySales = historyResultSet.getInt("daysales");
                // Day_End_Inventory = historyResultSet.getInt("day_end_inventory");
                ReorderLevel = historyResultSet.getInt("ReorderLevel");
                UnitPrice = historyResultSet.getDouble("UnitPrice");

                UnitPrice = UnitPrice / 1.15;

                UnitsOnOrder = historyResultSet.getInt("UnitsOnOrder");

                String nextProductID = ProductID;
                if (!productID.equals(nextProductID)) {
                    isFirst = true;
                    productID = nextProductID;
                }
                if (isFirst) {

                    Day_End_Inventory = historyResultSet.getInt("day_end_inventory");
                    createhistory.createFirstPurchaseHistory();
                    isFirst = false;

                } else {
                    if (Day_End_Inventory > 0 && ReorderLevel == 0 && UnitsOnOrder == 0) {
                        // Day_End_Inventory=historyResultSet.getInt("day_end_inventory");
                        ReorderLevel = historyResultSet.getInt("day_end_inventory") / 4;

                    }
                    createhistory.createPurchaseHistory();

                }

            }

            createpurchasehistorytable.createPurchaseHistoryTable();
            System.out.println(buffer.toString());

        } catch (Exception e) { //catch statement for catching exceptions

            System.out.println(e.getMessage());
        } finally {
            // Always close connections, otherwise the MySQL database runs out of them.

            // Close any of the resultSet, statements, and connections that are open and holding resources.
            try {

                if (historyResultSet != null) { //closing customerResulset

                    historyResultSet.close();
                }

                if (connect != null) { ////closing connection object

                    connect.close();
                }

            } catch (Exception e) {

                System.out.println(e.getMessage());
            }
        }

    }
}
