package report;

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
        // initialize variables
        Connection conn = null;
        PreparedStatement pstmt = null;
        Statement stmt = null;

        // Register driver
        try{
            Class.forName(JDBC_DRIVER);
        }catch (ClassNotFoundException e){
            System.out.println("Unable to load driver class");
            e.printStackTrace();
        }

        try{
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            stmt = conn.createStatement();

            // 1. Authenticate user and set isInstructor flag
            authenticate(conn);

            // 2. Show menu
            if(isInstructor){
                InstructorMenu.instructorMenu(conn, userID);
            }else {
                StudentMenu.studentMenu(conn, userID);
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
        /**
         * Show authentication prompt after connected to local DB.
         * Input ID and Name are validated by searching both studnet and instructor relation.
         *
         * If both results are empty, reauthenticate by re-calling this method.
         * Else if both results are non-empty, there exists a tuple both on two relations. Hence corrupted data.
         * Else, if only one of the result is empty, set userType corresponding to the name of the relation that
         * returned non-empty result.
         * @return void
         * @exception when sql exception occurs
         */
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
                // Appears on both relation? Corrupted data
                System.out.println("Data Corrupted");

            } else if(rsInstructor.isBeforeFirst()) {
                // Appears only on instructor relation
                rsInstructor.next();
                isInstructor = true;
                userID = rsInstructor.getString(1);
            } else{
                // Appears only on student relation
                rsStudent.next();
                isInstructor = false;
                userID = rsStudent.getString(1);
            }
        }catch (SQLException e){
            System.out.println("SQL Exception occurred");
            e.printStackTrace();
        }

    }

    }



