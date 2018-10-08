package javaapplication2;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sfeengineer
 */
public class connect {
    private static Connection dbConnect;
    public static Connection getConnection(){
        if(dbConnect == null){
            try{
                String url = "jdbc:mysql://localhost:3306/dbabsenjava";
                String user="root"; // user database
                String pass=""; // password database
                DriverManager.registerDriver(new com.mysql.jdbc.Driver());
                dbConnect = (com.mysql.jdbc.Connection) DriverManager.getConnection(url, user, pass);
                System.out.println("sukses");
            }catch(SQLException e){
                System.out.println("koneksi gagal\n"+e);
            }
        }
        return dbConnect;
    }
    
    public static void main(String[] args){
        getConnection();
    }
        
    
}
