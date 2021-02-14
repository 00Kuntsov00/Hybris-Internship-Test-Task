import java.io.FileInputStream;
import java.sql.*;
import java.util.HashSet;
import java.util.Properties;
import java.util.Scanner;

public class database_With_JDBC {

    public static final String PATH_TO_PROPERTIES = "src/local.properties";

    public static void main(String[] args) {

        Connection conn = null;
        Statement stmt = null;
        String CreatedAt;
        String command = "";
        int x = 0;
        Scanner scan = new Scanner(System.in);
        HashSet orIdSet = new HashSet<>();

        FileInputStream fileInputStream;

        Properties prop = new Properties();
        ResultSet rs = null;

        try{
            fileInputStream = new FileInputStream(PATH_TO_PROPERTIES);
            prop.load(fileInputStream);

            String DB_URL = prop.getProperty("DB_URL");
            String USER = prop.getProperty("USER");
            String PASS = prop.getProperty("PASS");

            // Open a connection
            conn = DriverManager.getConnection(DB_URL, USER, PASS);



            // Execute a query
            stmt = conn.createStatement();

            String table_orders = "CREATE TABLE IF NOT EXISTS ORDERS " +
                    "(id INT , " +
                    " user_id INT, " +
                    " status VARCHAR(255), " +
                    " created_at VARCHAR(255), " +
                    " PRIMARY KEY (id))";

            stmt.executeUpdate(table_orders);



            String table_products = "CREATE TABLE IF NOT EXISTS PRODUCTS " +
                    "(id INT , " +
                    " name VARCHAR(255), " +
                    " price INT, " +
                    " status enum('out of stock', 'in stock', 'running low'), " +
                    " created_at DATETIME," +
                    " PRIMARY KEY (id))";

            stmt.executeUpdate(table_products);


            String table_order_items = "CREATE TABLE IF NOT EXISTS ORDER_ITEMS " +
                    "(order_id INT, " +
                    " product_id INT, " +
                    " quantity INT, " +
                    " FOREIGN KEY (product_id) REFERENCES PRODUCTS (Id) ON DELETE CASCADE ON UPDATE CASCADE," +
                    " FOREIGN KEY (order_id) REFERENCES ORDERS (Id) ON DELETE CASCADE ON UPDATE CASCADE)";


            stmt.executeUpdate(table_order_items);



            // This is a simple menu that allows to control the program
            // When user enters "10" the program will finishes
            while (!"10".equals(command)) {
                System.out.println("+--------------------------------------+\n" +
                                   "| To create new product enter '1';     |\n" +
                                   "| To create new order enter '2';       |\n" +
                                   "| To task 4.a enter '3';               |\n" +
                                   "| To task 4.b enter '4';               |\n" +
                                   "| To task 4.c enter '5';               |\n" +
                                   "| To task 4.c enter '6';               |\n" +
                                   "| To clear all tables enter '9';       |\n" +
                                   "| To exit enter '10';                  |\n" +
                                   "+--------------------------------------+\n");

                // This part counts a number of entries in both tables
                rs = stmt.executeQuery("SELECT COUNT(*) AS COUNT FROM PRODUCTS");

                while(rs.next()) {
                    System.out.println("The count of products is " + rs.getInt("COUNT"));
                }
                rs = stmt.executeQuery("SELECT COUNT(*) AS COUNT FROM ORDERS");

                while(rs.next()) {
                    System.out.println("The count of orders is " + rs.getInt("COUNT"));
                }

                String IDsOfProducts = "SELECT id, name, status FROM products;";
                rs = stmt.executeQuery(IDsOfProducts);
                while (rs.next()) {
                    String productsIDs = rs.getObject(1).toString();
                    orIdSet.add(productsIDs);
                }

                System.out.print("Enter the command: ");
                Scanner scanCommand = new Scanner(System.in);
                command = scanCommand.nextLine();


                try{
                    x = Integer.parseInt(command);
                } catch (NumberFormatException e){
                    System.out.println("Wrong command");
                }
                switch (x){

                    /*
                    *  If user enters '1', program creates new entry in PRODUCTS
                    *  ID of products creates randomly
                    *  The rest of the elements are entered by the user following the prompts
                    *  The entry inserts in the table
                    */
                    case 1:

                        int product_id = ((int)(Math.random()*100));

                        System.out.print("Enter the Status( out of stock , in stock , running low ): ");
                        String status = scan.nextLine();

                        System.out.print("Enter the created at (YYYY.MM.DD): ");
                        CreatedAt = scan.nextLine();

                        System.out.print("Enter the name: ");
                        String name = scan.nextLine();

                        System.out.print("Enter the price: ");
                        String price = scan.nextLine();

                        String insertInProducts = "INSERT INTO PRODUCTS VALUES ('" + product_id + "', '" + name +
                                "', '" + price + "', '" + status + "', '" + CreatedAt + "')";
                        stmt.executeUpdate(insertInProducts);

                        String copyProductIDToOrderItems = "UPDATE ORDER_ITEMS SET product_id = " + product_id;
                        stmt.executeUpdate(copyProductIDToOrderItems);

                        break;

                     /*
                     * If user enters '2', program creates new entry in ORDERS
                     * ID of users and orders creates randomly
                     * The rest of the elements are entered by the user following the prompts
                     * The entry inserts in the table
                     */
                    case 2:

                        int user_id = ((int)(Math.random()*100));

                        int order_id = ((int)(Math.random()*100));

                        System.out.print("Enter the status: ");
                        String statusOrder = scan.nextLine();

                        System.out.print("Enter the created at: ");
                        CreatedAt = scan.nextLine();

                        System.out.print("Enter the quantity: ");
                        String quantity = scan.nextLine();


                        System.out.println("Enter the id of product which ordered ");
                        System.out.println("-----------------------------------------");
                        System.out.format("%1s%15s%20s", "ID", "NAME", "STATUS");
                        String prods = "SELECT id, name, status FROM products;";
                        System.out.println();
                        rs = stmt.executeQuery(prods);
                        while (rs.next()) {
                            String prID = rs.getObject(1).toString();
                            String prName = rs.getObject(2).toString();
                            String prStatus = rs.getObject(3).toString();

                            System.out.format("%1s%15s%20s", prID, prName, prStatus);
                            System.out.println();
                        }
                        System.out.println("-----------------------------------------");
                        String orderedId = scan.nextLine();

                        if (orIdSet.contains(orderedId)){


                        String insertInOrders = "INSERT INTO ORDERS VALUES ('" + order_id + "', '" + user_id +
                                "', '" + statusOrder + "', '" + CreatedAt + "')";
                        stmt.executeUpdate(insertInOrders);

                        String insertInOrderItems = "INSERT INTO ORDER_ITEMS VALUES('" + order_id + "', '" + orderedId + "', '" + quantity + "')";
                        stmt.executeUpdate(insertInOrderItems);

                        //String copyOrderIDToOrderItems = "UPDATE ORDER_ITEMS SET order_id = " + order_id;
                        //stmt.executeUpdate(copyOrderIDToOrderItems);


                }

                        System.out.println("Data inserted in ORDERS...");
                        break;

                        /*
                         * If user enters '3', program shows
                         * | Product Name | Product Price | Product Status |
                         * for all products
                         */
                    case 3:
                        try {

                            String query = "SELECT name, price, status FROM PRODUCTS;";

                            rs = stmt.executeQuery(query);

                            System.out.format("%10s%15s%20s", "NAME", "PRICE", "STATUS");
                            System.out.println();

                            while (rs.next()) {
                                String prName = rs.getObject(1).toString();
                                String prPrice = rs.getObject(2).toString();
                                String prStatus = rs.getObject(3).toString();
                                System.out.format("%10s%15s%20s", prName, prPrice, prStatus);
                                System.out.println();
                            }


                        } catch (SQLException e) {
                            e.printStackTrace();
                            for(Throwable ex : e) {
                                System.err.println("Error occurred " + ex);
                            }
                            System.out.println("Error in fetching data");
                        }
                        break;

                        /*
                         * If user enters '4', program shows
                         * list of all products, which have been ordered at least once,
                         * with total ordered quantity sorted descending by the quantity.
                         */
                    case 4:
                        try {

                            String query = "SELECT product_id, name, status, quantity " +
                                           "FROM PRODUCTS LEFT JOIN ORDER_ITEMS ON PRODUCTS.id = ORDER_ITEMS.product_id " +
                                           "WHERE quantity > 0 " +
                                           "ORDER BY product_id DESC;";

                            rs = stmt.executeQuery(query);

                            System.out.format("%10s%15s%20s%20s", "ID", "NAME", "STATUS", "QUANTITY");
                            System.out.println();

                            while (rs.next()) {
                                String prID = rs.getObject(1).toString();
                                String prName = rs.getObject(2).toString();
                                String prStatus = rs.getObject(3).toString();
                                String prQuantity = rs.getObject(4).toString();
                                System.out.format("%10s%15s%20s%20s", prID, prName, prStatus, prQuantity);
                                System.out.println();
                            }


                        } catch (SQLException e) {
                            e.printStackTrace();
                            for(Throwable ex : e) {
                                System.err.println("Error occurred " + ex);
                            }
                            System.out.println("Error in fetching data");
                        }
                        break;


                    /*
                     * If user enters '5', program shows
                     * | Order ID | Products total Price | Product Name | Products Quantity in orderEntry
                     * | Order Created Date [YYYY-MM-DD HH:MM ] | by order Id
                     */
                    case 5:
                        try {

                            String query = "select order_id, (price*quantity), name, quantity, orders.created_at " +
                                    "from products " +
                                    "left join order_items on products.id = order_items.product_id " +
                                    "join orders on order_items.order_id = orders.id " +
                                    "order by order_id;";

                            rs = stmt.executeQuery(query);

                            System.out.format("%10s%25s%15s%35s%25s", "ORDER ID", "Products total Price", "Product Name", "Products Quantity in orderEntry",
                                     "Order Created Date");
                            System.out.println();

                            while (rs.next()) {
                                String orId = rs.getObject(1).toString();
                                String prTPrice = rs.getObject(2).toString();
                                String prName = rs.getObject(3).toString();
                                String prQu = rs.getObject(4).toString();
                                String prCreated = rs.getObject(5).toString();
                                System.out.format("%5s%20s%20s%30s%30s", orId, prTPrice, prName, prQu, prCreated);
                                System.out.println();
                            }


                        } catch (SQLException e) {
                            e.printStackTrace();
                            for(Throwable ex : e) {
                                System.err.println("Error occurred " + ex);
                            }
                            System.out.println("Error in fetching data");
                        }
                        break;

                        /*
                         * If user enters '6', program shows
                         * list of all orders using previous view
                         */
                    case 6:
                        try {

                            String query = "SELECT order_id, status, quantity " +
                                           "FROM ORDERS LEFT JOIN ORDER_ITEMS ON ORDERS.id = ORDER_ITEMS.order_id " +
                                           "WHERE quantity > 0 " +
                                           "ORDER BY product_id;";

                            rs = stmt.executeQuery(query);

                            System.out.format("%10s%15s%20s", "ID", "STATUS", "QUANTITY");
                            System.out.println();

                            while (rs.next()) {
                                String prID = rs.getObject(1).toString();
                                String prStatus = rs.getObject(2).toString();
                                String prQuantity = rs.getObject(3).toString();
                                System.out.format("%10s%15s%20s", prID, prStatus, prQuantity);
                                System.out.println();
                            }


                        } catch (SQLException e) {
                            e.printStackTrace();
                            for(Throwable ex : e) {
                                System.err.println("Error occurred " + ex);
                            }
                            System.out.println("Error in fetching data");
                        }
                        break;


                    /*
                     * If the user enters "6",
                     * the program prompts him to enter the id of the product
                     * to be removed
                     */
                    case 8:

                        System.out.println("enter the id of product which you want to delete: ");
                        String delProduct = scan.nextLine();

                        String deletingProduct = "DELETE FROM Products " +
                                "WHERE id = '" + delProduct + "';";
                        stmt.executeUpdate(deletingProduct);
                        orIdSet.remove(delProduct);
                        break;


                        /*
                         * If the user enters "6",
                         * drop all tables in the database
                         * and then create them again
                         */
                    case 9:

                            String clearTables = "drop table order_items;";
                            stmt.executeUpdate(clearTables);
                            String clearTables2 = "drop table orders, products;";
                            stmt.executeUpdate(clearTables2);

                            orIdSet.clear();

                            stmt.executeUpdate(table_orders);

                            stmt.executeUpdate(table_products);

                            stmt.executeUpdate(table_order_items);



                        break;


                }

            }




    }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try{
                if(stmt!=null)
                    conn.close();
            }catch(SQLException se){
            }
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
        System.out.println("Goodbye!");
    }//end main
}