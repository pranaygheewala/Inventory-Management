/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment6;

/**
 *
 * @author Pranay
 */
public class CreateHistoryClass extends Assignment6 {

    public static void createPurchaseHistory() {

        if (ReorderLevel == 0 && UnitsOnOrder == 0) {

            ReorderLevel = 20;

        }

        int Salesdayendinventory = 0;
        int reorderunit = 0;
        int DayStartinventory = 0;

        if (Day_End_Inventory >= (ReorderLevel * 4)) {

            Salesdayendinventory = ReorderLevel;
            reorderunit = Day_End_Inventory - ReorderLevel;
        } else {

            Salesdayendinventory = Day_End_Inventory;
            reorderunit = 0;
        }
        DayStartinventory = Salesdayendinventory + DaySales;
        Day_End_Inventory = DayStartinventory;
        // System.out.println(Day_End_Inventory);
        if (reorderunit > 0) {
            buffer.append("('" + ShippedDate + "','" + ProductID + "'," + reorderunit + "," + UnitPrice +  "),");
        }

    }

    public static void createFirstPurchaseHistory() {

        int Salesdayendinventory = 0;
        int reorderunit = 0;
        int DayStartinventory = 0;

        if (UnitsOnOrder > 0) {

            reorderunit = UnitsOnOrder;
            Salesdayendinventory = Day_End_Inventory;
            DayStartinventory = Salesdayendinventory + DaySales;
            Day_End_Inventory = DayStartinventory;
            if (reorderunit > 0) {

                buffer.append("('" + ShippedDate + "','" + ProductID + "'," + reorderunit + "," + UnitPrice + " ),");
            }
        }
        
        else if (ReorderLevel == 0 && Day_End_Inventory != 0 && UnitsOnOrder == 0) {

            Salesdayendinventory = Day_End_Inventory / 4;
            reorderunit = Day_End_Inventory - Salesdayendinventory;
            Day_End_Inventory = Salesdayendinventory + DaySales;
            buffer.append("('" + ShippedDate + "','" + ProductID + "'," + reorderunit + "," + UnitPrice + "),");

        }
        
        else {

            createPurchaseHistory();
        }

    }

}
