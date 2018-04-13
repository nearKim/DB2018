package com.dev;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class InstructorMenu {

    public static void instructorMenu(Connection connection, String id){
        /**
         * Simple method for showing menus when isInstructor flag is true
         */
        System.out.println("Please select student menu");
        System.out.println("1) Course report");
        System.out.println("2) Advisee report");
        System.out.println("0) Exit");

        Scanner scanner = new Scanner(System.in);
        int selection = scanner.nextInt();

        switch (selection) {
            case 0:
                // Exit: Do nothing
                break;

            case 1:
                // TODO: implement below
                courseReport(connection, id);
                break;

            case 2:
                // TODO: implement below
//                adviseeReport(connection, id);
                break;
        }
    }

    private static void courseReport(Connection connection, String id) {
        String sql = "WITH courses AS(SELECT course_ID, sec_ID\n" +
                "    FROM teaches NATURAL JOIN (SELECT * FROM (SELECT *\n" +
                "        FROM teaches\n" +
                "        WHERE ID = ? \n" +
                "        ORDER BY year desc, CASE semester\n" +
                "            WHEN 'Spring' THEN 1\n" +
                "            WHEN 'Summer' THEN 2\n" +
                "            WHEN 'Fall' THEN 3\n" +
                "            WHEN 'Winter' THEN 4\n" +
                "        END desc) WHERE ROWNUM=1))\n" +
                "SELECT *\n" +
                "FROM (SELECT course_ID, title, \'[\' || building || room_number || \']\',\'(\' || day || \', \', start_hr || ':' || start_min || '-' || end_hr || ':' ||end_min ||\')\'" +
                "      FROM courses NATURAL JOIN section NATURAL JOIN time_slot NATURAL JOIN course),\n" +
                "     (SELECT ID, name, dept_name, grade\n" +
                "      FROM courses NATURAL JOIN section NATURAL JOIN takes NATURAL JOIN student)";
        try{
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, id);

            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                System.out.println(rs.getString(1));
                System.out.println(rs.getString(2));
                System.out.print(rs.getString(3));
                System.out.print(rs.getString(4));
                System.out.println(rs.getString(5));
//                System.out.println(rs.getString(6));
//                System.out.println(rs.getString(7));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }



}
