package com.dev;

import java.sql.*;
import java.util.Scanner;

public class Main {

    // JDBC Driver and db url
    static final String JDBC_DRIVER="oracle.jdbc.driver.OracleDriver";
    static final String DB_URL="jdbc:oracle:thin:@localhost:1521:xe";

    // DB Credentials
    static final String DB_USER="system";
    static final String DB_PASS="oracle";

    // True means an instructor
    public static boolean isInstructor;
    public static String userID;


    public static void main(String[] args) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        Statement stmt = null;

        try{
            Class.forName(JDBC_DRIVER);
        }catch (ClassNotFoundException e){
            System.out.println("Unable to load driver class");
            e.printStackTrace();
        }



        try{
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
//            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            stmt = conn.createStatement();

//            For Testing
//            ResultSet rs = stmt.executeQuery("select * from instructor");
//
//            while(rs.next()){
//
//                System.out.println(rs.getString(1));
//            }
//            conn.close();

//            if (IsInstructorAndAuthenticate(conn)){
//
//            }
        authenticate(conn);

        if(isInstructor){
//            TODO: SHOW INSTRUCTOR MENU HERE
            System.out.println("Instructor");
        }else {
//            TODO: SHOW Student MENU HERE
//            StudentMenu.studentMenu(stmt, );
            System.out.println("Student");
        }


        }catch (SQLException e){
            System.out.println("SQL Exception occurred");
            e.printStackTrace();

        }catch (Exception e){
            System.out.println("Exception occurred");
            e.printStackTrace();
        }

    }

    public static void authenticate(Connection connection){
        String id, name;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome\n");
        System.out.println("ID: ");
        id = scanner.nextLine().toString();

        System.out.println("Name: ");
        name = scanner.nextLine().toString();

        String instructorSql = "SELECT ID, name FROM instructor WHERE ID = ? AND name = ?";
        String studentSql = "SELECT ID, name FROM student WHERE ID = ? AND name = ?";

        try {

            PreparedStatement pstmtInstructor = connection.prepareStatement(instructorSql);
            PreparedStatement pstmtStudent = connection.prepareStatement(studentSql);

            pstmtInstructor.setString(1, id);
            pstmtInstructor.setString(2, name);

            pstmtStudent.setString(1, id);
            pstmtStudent.setString(2, name);

            ResultSet rsInstructor = pstmtInstructor.executeQuery();
            ResultSet rsStudent = pstmtStudent.executeQuery();

            if(!rsInstructor.isBeforeFirst() && !rsStudent.isBeforeFirst()){
                // Not authenticated
                System.out.println("Wrong authentication. Try Again");
                authenticate(connection);

            } else if(rsInstructor.isBeforeFirst() && rsStudent.isBeforeFirst()){
                System.out.println("Data Corrupted");

            } else if(rsInstructor.isBeforeFirst()) {
                rsInstructor.next();
                System.out.println(rsInstructor.getString(1));
                isInstructor = true;
                userID = rsInstructor.getString(1);
            } else{
                rsStudent.next();
                isInstructor =false;
                userID = rsStudent.getString(1);
            }
        }catch (SQLException e){
            System.out.println("SQL Exception occurred");
            e.printStackTrace();
        }

    }

    }



