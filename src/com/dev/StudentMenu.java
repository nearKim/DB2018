package com.dev;

import oracle.jdbc.proxy.annotation.Pre;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Stream;

public class StudentMenu {

    public static void studentMenu(Connection connection, String id) {
        /**
         * Simple method for showing menus when isInstructor flag is false
         * Calls for two method: studentReport() and showTimeTable()
         */
        System.out.println("Please select student menu");
        System.out.println("1) Student report");
        System.out.println("2) View time table");
        System.out.println("0) Exit");

        Scanner scanner = new Scanner(System.in);
        int selection = scanner.nextInt();

        switch (selection) {
            case 0:
                // Exit: Do nothing
                break;

            case 1:
                // TODO: implement below
                studentReport(connection, id);
                break;

            case 2:
                // TODO: implement below
                showTimeTable(connection, id);
                break;
        }
    }

    private static void showTimeTable(Connection connection, String id) {
        /**
         * Connect current prompt to the 'Select semester' prompt.
         *
         * @param: connection a Connection object which connects user to the Oracle
         * @param: id String object which designates current user's id.
         * @returns: void

         */

        // ArraryList for saving resultset's semester and year data
        ArrayList<String> selections = new ArrayList<>();

        String sql = "SELECT year, semester FROM takes WHERE id = ? " +
                "     GROUP BY year, semester " +
                "     ORDER BY year DESC, CASE semester\n"+
                "                       WHEN 'Spring' THEN 1\n" +
                "                       WHEN 'Summer' THEN 2\n" +
                "                       WHEN 'Fall' THEN 3\n" +
                "                       WHEN 'Winter' THEN 4\n" +
                "                       END DESC";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1,id);

            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                // Parse result data to format "year semester"
                selections.add(rs.getString(1) +" " + rs.getString(2));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Please select semester to view");
        for(int i=0; i<selections.size(); i++){
            System.out.println((i+1) + ") " + selections.get(i));
        }

        Scanner scanner = new Scanner(System.in);

        int select = Integer.parseInt(scanner.nextLine());
        String yearSemester = selections.get(select-1);

        // Now show specific time table using id, semester and year data
        System.out.println(id +"/"+Integer.parseInt(yearSemester.substring(0,4)) +"/"+yearSemester.substring(4,yearSemester.length()));
        showSpecificTimeTable(connection, id, Integer.parseInt(yearSemester.substring(0,4)),yearSemester.substring(5,yearSemester.length()));

    }

    private static void showSpecificTimeTable(Connection connection, String id, int year, String semester) {
        /**
         * Show specific time table for selected year and semster. Refer to the sql query below
         * @param connection Connection object for database
         * @param id User id currently using this program
         * @param year,semester selected year and semester for viewing time table
         * @return void
         */
        String sql =
                "SELECT course.course_id, course.title, time_slot.day, time_slot.start_hr || ':' || time_slot.start_min as start_time, time_slot.end_hr || ':' || time_slot.end_min as end_time " +
                "FROM section INNER JOIN takes " +
                "ON takes.sec_id = section.sec_id AND takes.course_id=section.course_id  " +
                "INNER JOIN time_slot " +
                "ON section.time_slot_id=time_slot.time_slot_id " +
                "INNER JOIN course " +
                "ON course.course_id = takes.course_id " +
                "WHERE takes.id= ? AND takes.year= ? AND takes.semester= ?";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1,id);
            pstmt.setInt(2,year);
            pstmt.setString(3, semester);

            ResultSet rs = pstmt.executeQuery();
            System.out.println("course_id \t title\t day\t start_time\tend_time");
            while (rs.next()){
                System.out.print(rs.getString(1) +"  ");
                System.out.print(rs.getString(2) +"   ");
                System.out.print(rs.getString(3) +"\t\t\t");
                System.out.print(rs.getString(4) +" \t");
                System.out.println(rs.getString(5) +"  ");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void studentReport(Connection connection, String id) {
        String reportIntro = "Welcome %s . \n You are a member of %s \n You have taken total %d credits. \n\n Semester Report \n\n";
        String sql = "select name from student where ID = ? ";


    }


}