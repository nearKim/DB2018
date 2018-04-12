package com.dev;

import oracle.jdbc.proxy.annotation.Pre;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Stream;

public class StudentMenu {

    public static void studentMenu(Connection connection, String id) {
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
                studentReport(connection, id);
                break;
            case 2:
                // TODO: implement below
                showTimeTable(connection, id);
                break;
        }
    }

    private static void showTimeTable(Connection connection, String id) {
        ArrayList<String> selections = new ArrayList<>();
        String sql = "select year, semester from takes where id = ? " +
                "group by year, semester order by year desc";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1,id);

            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                selections.add(rs.getString(1) +" " + rs.getString(2));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

//        selections.forEach(selection->System.out.println(selection));
        System.out.println("Please select semester to view");
        for(int i=0; i<selections.size(); i++){
            System.out.println((i+1) + ") " + selections.get(i));
        }

        Scanner scanner = new Scanner(System.in);
        int select = Integer.parseInt(scanner.nextLine());
        String yearSemester = selections.get(select-1);
//        System.out.println("!!!!!!!"+
//                yearSemester.substring(0,4)+
//                "/"+yearSemester.substring(4,yearSemester.length()));
        showSpecificTimeTable(connection, id, Integer.parseInt(yearSemester.substring(0,4)),yearSemester.substring(4,yearSemester.length()));

    }

    private static void showSpecificTimeTable(Connection connection, String id, int year, String semester) {
        String sql =
                "select course.course_id, course.title, time_slot.day, time_slot.start_hr || ':' || time_slot.start_min as start_time, time_slot.end_hr || ':' || time_slot.end_min as end_time\n" +
                "from section inner join takes\n" +
                "on takes.sec_id = section.sec_id and takes.course_id=section.course_id \n" +
                "inner join time_slot\n" +
                "on section.time_slot_id=time_slot.time_slot_id\n" +
                "inner join course\n" +
                "on course.course_id = takes.course_id\n" +
                "where takes.id=? and takes.year=? and takes.semester= ?";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1,id);
            pstmt.setInt(2,year);
            pstmt.setString(3, semester);

//            FIXME: BELOW RETURNS EMPTY RESULT SET
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                System.out.print(rs.getString(1));
                System.out.print(rs.getString(2));
                System.out.print(rs.getString(3));
                System.out.print(rs.getString(4));
                System.out.print(rs.getString(5));
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