package com.dev;

import java.sql.*;
import java.util.Scanner;

public class Main {
    // TODO: fill in below vars
    // ODBC Driver and db url
    static final String JDBC_DRIVER="";
    static final String DB_URL="jdbc:oracle:thin:@localhost:1521:orcl";

    // DB Credentials
    static final String DB_NAME="";
    static final String DB_USER="";
    static final String DB_PASS="";

    public boolean userType;

    public static void main(String[] args) {
        try{
            Class.forName("oracle.jdbc.driver.OracleDriver");
        }catch (ClassNotFoundException e){
            System.out.println("Unable to load driver class");
            e.printStackTrace();
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        Statement stmt = null;


        try{
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL+DB_NAME, DB_USER, DB_PASS);

            if (IsInstructorAndAuthenticate(conn)){
//
            }



        }catch (SQLException e){
            System.out.println("SQL Exception occurred");
            e.printStackTrace();

        }catch (Exception e){
            System.out.println("Exception occurred");
            e.printStackTrace();
        }

    }

    public static boolean IsInstructorAndAuthenticate(Connection connection){
        String id, name;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome\n");
        System.out.println("ID: ");
        id = scanner.nextLine();

        System.out.println("Name: ");
        name = scanner.nextLine();

        String instructorSql = "SELECT ID, name FROM instructor WHERE ID = ? AND name = ? ;";
        String studentSql = "SELECT ID, name FROM student WHERE ID = ? AND name = ? ;";

        try {

            PreparedStatement pstmtInstructor = connection.prepareStatement(instructorSql);
            PreparedStatement pstmtStudent = connection.prepareStatement(studentSql);

            pstmtInstructor.setString(1, id);
            pstmtInstructor.setString(2, name);

            pstmtStudent.setString(1, id);
            pstmtStudent.setString(2, name);


            ResultSet rsInstructor = pstmtInstructor.executeQuery(instructorSql);
            ResultSet rsStudent = pstmtStudent.executeQuery(studentSql);


            if(!rsInstructor.first() && !rsStudent.first()){
                // Not authenticated
                System.out.println("Wrong authentication. Try Again");
                IsInstructorAndAuthenticate(connection);

            } else if(rsInstructor.first() && rsStudent.first()){
                System.out.println("Data Corrupted");

            } else if(rsInstructor.first()) {
//                True means this ID belongs to instructor
                return true;
            }
        }catch (SQLException e){
            System.out.println("SQL Exception occurred");
            e.printStackTrace();
        }
        return false;
    }


    }


}
