import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.ResultSet;

public class Hotelreservation {
    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "bishal123";
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        try{
           Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }
        try{
            Connection connection = DriverManager.getConnection(url, username, password);

            while(true){
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner sc = new Scanner(System.in);
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservation");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservation");
                System.out.println("0. Exit");
                System.out.println("Choose an option: ");
                int choice = sc.nextInt();
                switch(choice){
                    case 1:
                       reserveRoom(connection, sc);
                       break;
                    case 2:
                       viewReservations(connection);
                       break;
                    case 3:
                       getRoomNumber(connection, sc);
                       break;
                    case 4:
                       updateReservation(connection, sc);
                       break;
                    case 5:
                       deleteReservation(connection, sc);
                       break;
                    case 0:
                       exit();
                       sc.close();
                       return;
                    default:
                       System.out.println("Invalid choice. Try agin");
                }
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }catch(InterruptedException e){
            throw new RuntimeException(e);
        }
    }
    private static void reserveRoom(Connection connection,Scanner sc){
        try{
            System.out.println("enter guest name");
            String guestName = sc.next();
            sc.nextLine();
            System.out.println("enter room number: ");
            int roomNumber = sc.nextInt();
            System.out.println("enter contact number");
            String contactNumber = sc.next();

            String sql = "INSERT INTO reservations(guest_name, room_number, contact_number)" +
                         "VALUES ('"+ guestName + "', " + roomNumber + ",'" + contactNumber +"')";
        try(Statement statement = connection.createStatement()){
            int affecteRows = statement.executeUpdate(sql);
            
            if(affecteRows>0){
                System.out.println("Reservation successfull");
            }else{
                System.out.println("Reservation Failed");
            }
            
        }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    private static void viewReservations(Connection connection)throws SQLException{
        String sql = "SELECT reservation_id , guest_name, room_number, contact_number, reservation_date FROM reservations";
        try(Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql)){
            System.out.println("Current Reservation");
            System.out.println("+---------------+-----------+--------+-------+");
            System.out.println("| Reservation ID | Guest | Room NUmber | Contact Number | Reservation Date |");
            System.out.println("+-------------+---------------+-------------+");
        while(resultSet.next()){
            int reservationId = resultSet.getInt("reservation_id");
            String guestName = resultSet.getString("guest_name");
            int roomNumber = resultSet.getInt("room_number");
            String contactNumber = resultSet.getString("contact_number");
            String reservationDate = resultSet.getTimestamp("reservation_date").toString();
            System.out.printf("| %-14d | %-15s |%-13d |%-20s |%-19s |\n",
            reservationId, guestName, roomNumber, contactNumber, reservationDate);
        }
        System.out.println("+-----------+--------------+----------+");
        }
    }

    private static void getRoomNumber(Connection connection,Scanner sc){
        try{
            System.out.println("Enter reservation ID");
            int reservationId = sc.nextInt();
            System.out.println("enter guest name: ");
            String guestName = sc.next();
            String sql = "SELECT room_number FROM reservation" +
                         "WHERE reservation_id = " + reservationId +
                         "AND guest_name = '" + guestName + "'";
        try(Statement statement = connection.createStatement();
           ResultSet resultSet = statement.executeQuery(sql)){
            if(resultSet.next()){
                int roomNumber = resultSet.getInt("room_number");
                System.out.println("Room number for Reservation ID" + reservationId +
                "and Guest" + guestName + "is:" + roomNumber);
            }else{
                System.out.println("Reservation not found for the given ID and guest name");
            }

           }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private static void updateReservation(Connection connection,Scanner sc){
        try{
            System.out.println("enter reservation ID to update");
            int reservationId = sc.nextInt();
            sc.nextLine();
            if(!reservationExists(connection,reservationId)){
                System.out.println("Reservation not found for the given ID");
                return;
            }
            System.out.println("enter new guest name");
            String newGuestName = sc.nextLine();
            System.out.println("enter the new room number");
            int newRoomNumber=sc.nextInt();
            System.out.println("enter the new contact number");
            String newContactNumber = sc.next();

            String sql="UPDATE reservation SET guest_name ='"+ newGuestName + "', " +
                        "room_number = "+ newRoomNumber + ", "+
                        "contact_number = '"+ newContactNumber + "' " +
                        "WHERE reservation_id = " + reservationId;
            try(Statement statement = connection.createStatement()){
                int affectsRows = statement.executeUpdate(sql);

            if(affectsRows>0){
                System.out.println("Reservation update successfull");
            }else{
                System.out.println("Reservation update failde");
            }

            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    private static void deleteReservation(Connection connection,Scanner sc){
        try{
            System.out.println("enter reservation ID to delete");
            int reservationId = sc.nextInt();
            if(!reservationExists(connection,reservationId)){
                System.out.println("Reservation not found for the given ID");
                return;
            }
         

            String sql= "DELETE FROM reservations WHERE reservation_id = "+ reservationId;
            try(Statement statement = connection.createStatement()){
                int affectsRows = statement.executeUpdate(sql);

            if(affectsRows>0){
                System.out.println("Reservation delete successfull");
            }else{
                System.out.println("Reservation update failed");
            }

            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(Connection connection,int reservationId){
        try{
           
            String sql= "SELECT reservation_id FROM WHERE reservation_id = "+ reservationId;
            
         

            try(Statement statement = connection.createStatement();
                ResultSet resultset = statement.executeQuery(sql)){
                    return resultset.next();
                }

         
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public static void exit() throws InterruptedException{
        System.out.println("existing system");
        int i = 5;
        while(i!=0){
            System.out.println(".");
            Thread.sleep(450);
            i--;
        }
        System.out.println();
        System.out.println("Thankyou for using hotel reservation system");
    }
    
    
}
 