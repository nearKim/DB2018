package com.dev;

import java.sql.Statement;
import java.util.Scanner;

public class StudentMenu {
    // TODO: fill in below vars
    // ODBC Driver and db url
    static final String ODBC_DRIVER = "";
    static final String DB_URL = "";

    // DB Credentials
    static final String DB_NAME = "";
    static final String DB_USER = "";
    static final String DB_PASS = "";

    public static void studentMenu(Statement stmt, String id) {
        System.out.println("Please select student menu");
        System.out.println("1) Student report");
        System.out.println("2) View time table");
        System.out.println("0) Exit");

        Scanner scanner = new Scanner(System.in);
        int selection = scanner.nextInt();
        switch (selection) {
            case 0:
                break;
            case 1:
                System.out.println("Student Menu");
                // TODO: implement below
                studentReport(stmt, id);
                break;
            case 2:
                // TODO: implement below
//                TimeTable(stmt, id);
                break;
        }
    }

    private static void studentReport(Statement stmt, String id) {
        String reportIntro = "Welcome %s . \n You are a member of %s \n You have taken total %d credits. \n\n Semester Report \n\n";


    }


}