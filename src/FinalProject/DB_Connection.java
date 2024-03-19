/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FinalProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author deemashaath
 */
public class DB_Connection {
    
    public static Connection db_connection() throws SQLException {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("connected to driver");
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3308/OrdersApplication?serverTimezone=UTC", "root", "root");
            System.out.println("connected to database");
        } catch (ClassNotFoundException e) {
            System.out.println("");
            e.getMessage();

        }
        return connection;
    }
}
