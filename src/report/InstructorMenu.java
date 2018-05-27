package report;

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
                courseReport(connection, id);
                break;

            case 2:
                adviseeReport(connection, id);
                break;
        }
    }

    private static void adviseeReport(Connection connection, String id) {
        /**
         * Show advisee Report for selected instructor
         * Refer to the below SQL.
         */
        String sql = "SELECT ID, name, dept_name, tot_cred\n" +
                "FROM student\n" +
                "WHERE ID in (\n" +
                "    SELECT S_ID\n" +
                "    FROM advisor\n" +
                "    WHERE I_ID=?\n" +
                ")";
        try{
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, id);

            ResultSet rs = pstmt.executeQuery();
            System.out.println("ID\t\tname\t dept_name\ttot_cred");
            while (rs.next()){
                System.out.print(rs.getString(1)+"\t");
                System.out.print(rs.getString(2)+"\t\t");
                System.out.print(rs.getString(3)+"\t");
                System.out.println(rs.getString(4)+"\t");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void courseReport(Connection connection, String id) {
        /**
         * Show courseReport like #6 on HW# Spec pdf
         * Refer to the SQL below
         */
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
        String yearSem = "select year , semester from teaches where ID = ? order by year desc, semester desc";

        try{
            PreparedStatement pstmt = connection.prepareStatement(sql);
            PreparedStatement pyearsem = connection.prepareStatement(yearSem);
            pstmt.setString(1, id);
            pyearsem.setString(1, id);

            ResultSet rs = pstmt.executeQuery();
            ResultSet rs2 = pyearsem.executeQuery();
            rs2.next();
            System.out.println("Course Report - " + rs2.getString(1) +" "+ rs2.getString(2));
            while(rs.next()){
                System.out.print(rs.getString(1)+"\t");
                System.out.print(rs.getString(2)+"\t");
                System.out.print(rs.getString(3)+"\t");
                System.out.print(rs.getString(4));
                System.out.println(rs.getString(5));
                System.out.println("ID\t\tname\tdept_name grade");
                System.out.print(rs.getString(6)+"\t");
                System.out.print(rs.getString(7)+"\t");
                System.out.print(rs.getString(8)+"\t");
                System.out.println(rs.getString(9) +"\n");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }



}
