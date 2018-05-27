package report;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

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
                studentReport(connection, id);
                break;

            case 2:
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
        String reportIntro = "Welcome %s . \nYou are a member of %s \nYou have taken total %d credits.\n\n Semester Report \n\n";
        String introSql = "SELECT name, dept_name, tot_cred FROM student WHERE id = ?";

        String sql = "select year, semester\n" +
                "from takes ,student\n" +
                "where takes.id=student.id  and takes.id=?\n" +
                "group by year, semester\n" +
                "order by year desc , CASE semester\n" +
                "                                 WHEN 'Spring' THEN 1\n" +
                "                                   WHEN 'Summer' THEN 2\n" +
                "                                   WHEN 'Fall' THEN 3\n" +
                "                                   WHEN 'Winter' THEN 4\n" +
                "                              END desc";



        try {
            PreparedStatement yearSemPstmt = connection.prepareStatement(sql);
            PreparedStatement introPstmt = connection.prepareStatement(introSql);

            yearSemPstmt.setString(1, id);
            introPstmt.setString(1, id);
            ResultSet rsYearSem = yearSemPstmt.executeQuery();
            ResultSet introResult = introPstmt.executeQuery();
//
            introResult.next();
            System.out.println(String.format(reportIntro, introResult.getString(1),
                                                            introResult.getString(2),
                                                            introResult.getInt(3)));

            while(rsYearSem.next()){
                printReport(connection, id, rsYearSem.getInt("year"), rsYearSem.getString("semester"));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void  printReport(Connection connection, String id, int year, String semester){
        /**
         * Print specific report of the specific year and semester
         * @param connection Connection object connecting the DB
         * @param id User id
         * @param year year of which this report would report
         * @param semester semester of which this report would report
         * @return void
         */
        String sql = "WITH taken AS (SELECT name, student.dept_name AS s_dept, tot_cred, year, semester, course_ID, title, course.dept_name AS c_dept, credits, grade\n" +
                "               FROM student NATURAL JOIN takes JOIN course USING(course_ID)\n" +
                "               WHERE ID=?)\n" +
                "SELECT name, s_dept, tot_cred, year, semester, gpa, course_ID, title, c_dept, credits, grade\n" +
                "FROM taken NATURAL JOIN\n" +
                "(SELECT year, semester, round(sum(credits* CASE grade\n" +
                "                                        WHEN 'A+' THEN 4.3 WHEN 'A' THEN 4 WHEN 'A-' THEN 3.7\n" +
                "                                        WHEN 'B+' THEN 3.3 WHEN 'B' THEN 3 WHEN 'B-' THEN 2.7\n" +
                "                                        WHEN 'C+' THEN 2.3 WHEN 'C' THEN 2 WHEN 'C-' THEN 1.7\n" +
                "                                        WHEN 'D+' THEN 1.3 WHEN 'D' THEN 1 WHEN 'D-' THEN 0.7\n" +
                "                                        WHEN 'F' THEN 0 END)/sum(credits),5) AS GPA\n" +
                "FROM taken\n" +
                "WHERE taken.year= ? AND taken.semester = ? " +
                "GROUP BY year, semester)\n" +
                "ORDER BY year desc, CASE semester\n" +
                "                    WHEN 'Spring' THEN 1\n" +
                "                    WHEN 'Summer' THEN 2\n" +
                "                    WHEN 'Fall' THEN 3\n" +
                "                    WHEN 'Winter' THEN 4\n" +
                "               END desc, course_ID";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.setInt(2, year);
            pstmt.setString(3, semester);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                System.out.println("====================================");
                System.out.println(rs.getInt("year")+"\t"+rs.getString("semester")+"\t"+rs.getFloat("gpa"));
                System.out.println("course_id\t"+"title\t\t"+"dept_name\t"+"credits\t"+"grade");
                System.out.print(rs.getString("course_id")+"\t");
                System.out.print(rs.getString("title")+"\t");
                System.out.print(rs.getString("c_dept")+"\t");
                System.out.print(rs.getInt("credits")+"\t");
                System.out.println(rs.getString("grade"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

}