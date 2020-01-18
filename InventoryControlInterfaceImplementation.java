/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment6_2;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pranay
 */
public class InventoryControlInterfaceImplementation implements inventoryControl {

    Connection connect = null; //Connection variable for connecting to database
    Statement statement = null; //Statement variable for executing query
    //  ResultSet ResultSet = null;
    String user = "root";
    String password = "root";
    String database = "csci3901";

    public void Ship_order(int orderNumber) throws OrderException {
        Statement statement = null;
        ResultSet resultset = null;
        Connection connect = null;

        String shipOrderQuery = "select orders.OrderID, orderdetails.ProductID,products.SupplierID, orders.ShippedDate, orderdetails.Quantity, orderdetails.UnitPrice,"
                + "products.UnitsInStock, products.UnitsOnOrder, products.ReorderLevel from orders "
                + "INNER JOIN orderdetails ON orders.OrderID = orderdetails.OrderId "
                + "INNER JOIN products ON orderdetails.ProductID = products.ProductID " + "where orders.OrderID = '" + orderNumber + "' ";

        try {

            Class.forName("com.mysql.jdbc.Driver");  // To load the MySQL driver

            connect = DriverManager.getConnection("jdbc:mysql://localhost:3306", user, password); // To setup the connection with the Database

            statement = connect.createStatement(); // Statement object to allow issue SQL queries to the database

            statement.executeQuery("use " + database + ";");
            //getConnection();

            resultset = statement.executeQuery(shipOrderQuery);

            if (!resultset.isBeforeFirst()) {

                new OrderException("No order Found");

            }

            while (resultset.next()) { //iterating through customer resultset one row at a time in the customerResulset

                int UnitsInStock = resultset.getInt("UnitsInStock");
                int Quantity = resultset.getInt("Quantity");
                String ShippedDate = resultset.getString("ShippedDate");
                int ProductID = resultset.getInt("ProductID");
                int OrderID = resultset.getInt("OrderID");
                if (UnitsInStock > Quantity) {

                    UnitsInStock = UnitsInStock - Quantity;
                    String updateProductQuery = " update products set UnitsInStock = '" + UnitsInStock + "'  where ProductID = '" + ProductID + "' ";
                    statement.executeUpdate(updateProductQuery);
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                    Date date = new Date();
                    String updateOrderQuery = "update orders set ShippedDate = '" + dateFormat.format(date) + "' where OrderID = '" + orderNumber + "' ";
                    statement.executeUpdate(updateOrderQuery);
                }

            }
            // resultset.close();
        } catch (SQLException sql) {

            System.out.println(sql);

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(InventoryControlInterfaceImplementation.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {

                if (resultset != null) { //closing customerResulset

                    resultset.close();
                }

            } catch (Exception e) {

                System.out.println(e);

            }
        }

    }

    public int Issue_reorders(int year, int month, int day) {

        Statement statement = null;
        ResultSet resultset = null;
        Connection connect = null;
        Statement statement2 = null;
        int reorderID = 0;
        try {

            Class.forName("com.mysql.jdbc.Driver");  // To load the MySQL driver

            connect = DriverManager.getConnection("jdbc:mysql://localhost:3306", user, password); // To setup the connection with the Database

            statement = connect.createStatement(); // Statement object to allow issue SQL queries to the database
            statement2 = connect.createStatement();
            statement.executeQuery("use " + database + ";");

            String date = year + "-" + month + "-" + day;
            String getData = "select orderdetails.ProductID,products.SupplierID,UnitsInStock,UnitsOnOrder,ReorderLevel,orders.OrderID  from orders "
                    + "INNER JOIN orderdetails ON orders.OrderID = orderdetails.OrderId "
                    + "INNER JOIN products ON orderdetails.ProductID = products.ProductID "
                    + "where ShippedDate = ' " + date + " ' AND UnitsOnOrder=0 group by ProductID order by ProductID;";

            String createRecordTable = "CREATE TABLE IF NOT EXISTS Reorders ("
                    + "ReorderId int, ProductID int, SupplierID int,Reordered_units int,Reordered_date varchar(255),statusData int);";
            String insertIntoRecord = "INSERT INTO Reorders VALUES";
            StringBuffer buffer = new StringBuffer();

            int flag = 0;
            statement2.executeUpdate(createRecordTable);
            ResultSet resultSet = statement.executeQuery(getData);

            while (resultSet.next()) {

                int productID = resultSet.getInt("ProductID");
                int supplierId = resultSet.getInt("SupplierID");
                int UnitsInStock = resultSet.getInt("UnitsInStock");
                int ReorderLevel = resultSet.getInt("ReorderLevel");

                if (UnitsInStock < ReorderLevel) {
                    if (flag != supplierId) {
                        reorderID++;
                        flag = supplierId;
                    }
                    buffer.append("(" + reorderID + "," + productID + "," + supplierId + "," + ((ReorderLevel * 4) - UnitsInStock) + "," + date + "," + 0 + "),");
                }

                // System.err.println(resultSet.getInt(1));
            }

            if (buffer.length() > 0) {

                String query = insertIntoRecord + buffer.toString();
                String finalQuery = query.substring(0, query.length() - 1);
                System.out.println(finalQuery);
                statement.executeUpdate(finalQuery);
            }

        } catch (SQLException ex) {
            Logger.getLogger(InventoryControlInterfaceImplementation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(InventoryControlInterfaceImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }

        return reorderID;
    }

    public void Receive_order(int internal_order_reference) throws OrderException {

        Statement statement = null;
        ResultSet resultSet = null;
        Connection connect = null;
        try {

            Class.forName("com.mysql.jdbc.Driver");  // To load the MySQL driver
            connect = DriverManager.getConnection("jdbc:mysql://localhost:3306", user, password);
            statement = connect.createStatement();
            statement.executeQuery("USE " + database + ";");
            String query = "select count(*) from purchase_orders inner join purchase_orderdetails on purchase_orders.POrderID = purchase_orderdetails.POrderID where purchase_orders.POrderID=" + internal_order_reference + ";";
            resultSet = statement.executeQuery(query);
            if (resultSet != null) {

                resultSet.last();
                String date = "1998-04-14";
                int cnt = Integer.parseInt(resultSet.getString("count(*)"));
                if (cnt == 0) {
                    throw new OrderException("No pending delivery order found");
                } else if (cnt >= 1) {
                    resultSet.close();
                    query = "update purchase_orders set POrderDate='" + date + "' where purchase_orders.POrderID=" + internal_order_reference + ";";
                    resultSet = statement.executeQuery(query);
                    while (resultSet.next()) {
                    }
                }
            }
            resultSet.close();
        } catch (OrderException exp) {
            System.out.println("Error");
            System.out.println(exp.getMessage());
        } catch (Exception error) {

        }
    }
}
