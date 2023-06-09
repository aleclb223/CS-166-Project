/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */

import java.io.*;
import java.util.*;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.sql.Timestamp;
/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Hotel {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Hotel 
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Hotel(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Hotel

   // Method to calculate euclidean distance between two latitude, longitude pairs. 
   public double calculateDistance (double lat1, double long1, double lat2, double long2){
      double t1 = (lat1 - lat2) * (lat1 - lat2);
      double t2 = (long1 - long2) * (long1 - long2);
      return Math.sqrt(t1 + t2); 
   }
   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
      Statement stmt = this._connection.createStatement ();

      ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
      if (rs.next())
         return rs.getInt(1);
      return -1;
   }

   public int getNewUserID(String sql) throws SQLException {
      Statement stmt = this._connection.createStatement ();
      ResultSet rs = stmt.executeQuery (sql);
      if (rs.next())
         return rs.getInt(1);
      return -1;
   }
   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Hotel.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      Hotel esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Hotel object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Hotel (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Hotels within 30 units");
                System.out.println("2. View Rooms");
                System.out.println("3. Book a Room");
                System.out.println("4. View recent booking history");

                //the following functionalities basically used by managers
                System.out.println("5. Update Room Information");
                System.out.println("6. View 5 recent Room Updates Info");
                System.out.println("7. View booking history of the hotel");
                System.out.println("8. View 5 regular Customers");
                System.out.println("9. Place room repair Request to a company");
                System.out.println("10. View room repair Requests history");

                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewHotels(esql); break;
                   case 2: viewRooms(esql); break;
                   case 3: bookRooms(esql,authorisedUser); break;
                   case 4: viewRecentBookingsfromCustomer(esql,authorisedUser); break;
                   case 5: updateRoomInfo(esql,authorisedUser); break;
                   case 6: viewRecentUpdates(esql, authorisedUser); break;
                   case 7: viewBookingHistoryofHotel(esql,authorisedUser); break;
                   case 8: viewRegularCustomers(esql,authorisedUser); break;
                   case 9: placeRoomRepairRequests(esql, authorisedUser); break;
                   case 10: viewRoomRepairHistory(esql,authorisedUser); break;
                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
   public static void CreateUser(Hotel esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine(); 
         String type="Customer";
			String query = String.format("INSERT INTO USERS (name, password, userType) VALUES ('%s','%s', '%s')", name, password, type);
         esql.executeUpdate(query);
         System.out.println ("User successfully created with userID = " + esql.getNewUserID("SELECT last_value FROM users_userID_seq"));
         
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Hotel esql){
      try{
         System.out.print("\tEnter userID: ");
         String userID = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USERS WHERE userID = '%s' AND password = '%s'", userID, password);
         int userNum = esql.executeQuery(query);
         if (userNum > 0)
            return userID;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here

   public static void viewHotels(Hotel esql) {
	try {//might be wrong but you might have to run all the hotel locations and only give the hotels that are 30 or less
		//change get the input as a double not int
		//

      		// issues the query instruction
		Scanner input = new Scanner(System.in);
		System.out.print("\tEnter Latitude: ");
		double latitude=input.nextDouble();
		System.out.print("\tEnter Longitude: ");
		double longitude= input.nextDouble(); 
		String query1 = ("SELECT hotelName,latitude,longitude FROM Hotel");
		List<List<String>> result = esql.executeQueryAndReturnResult(query1); // executes SQL and saves the output
		for(int i = 0; i<result.size();i++){
			double hLat = Double.parseDouble(result.get(i).get(1));
			double hLong = Double.parseDouble(result.get(i).get(2));
			if(esql.calculateDistance(latitude,longitude,hLat,hLong)<=30){
				System.out.println(result.get(i).get(0));
				}
			}
	
        } catch (Exception e)
    {
        // do something appropriate with the exception, *at least*:
	System.out.print("inside----------------------------------------------------------------->>>>>>>>>>>>>> ");
         System.err.println (e.getMessage ());
	System.err.println (e.getMessage ());
        
    }
 
            // Close resources
            //if (rs != null) {
              //  rs.close();
      
       
   }
   public static void viewRooms(Hotel esql) {
   	try {
		Scanner input = new Scanner(System.in);
		System.out.println("\tEnter Hotel ID: ");
		int hotelID=input.nextInt();
		input.nextLine();
		System.out.println("\tEnter Date (yyyy/MM/dd)ex(2015/05/12) : ");
		String day =input.nextLine();
		//SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		//Date date2 = dateFormat.parse(d);

		System.out.println("\tRooms available on  "+day);
		esql.executeQueryAndPrintResult(String.format ("SELECT roomNumber,price FROM Rooms WHERE hotelID = '%d' AND roomNumber NOT IN(SELECT roomNumber FROM RoomBookings WHERE hotelID = '%d' AND bookingDate= '%s')",hotelID,hotelID,day)); 

		}
	 catch (Exception e){
		System.err.println (e.getMessage ());
	}
   }// end viewRooms
   public static void bookRooms(Hotel esql, String authorisedUser) {
	   try{
		String date;


		System.out.print("\tNow booking rooms: \n");
		   System.out.print("\tInput valid hotel ID: \n");
		 Scanner in1 = new Scanner(System.in);
		 int hotelID = in1.nextInt(); 

		 System.out.print("\tInput valid room number: \n");
		 Scanner in2 = new Scanner(System.in);
		 int rNum = in2.nextInt();


		 System.out.print("\tInput valid date: \n");
		 date = in.readLine();

		 String query = String.format("SELECT * FROM roombookings WHERE bookingdate = '%s' AND hotelid = '%d' AND roomnumber = '%d'", date, hotelID, rNum);

		// esql.executeQueryAndPrintResult(query);

		// System.out.print(query);

		 int isAvailable = (esql.executeQuery(query));
		 System.out.print("\n");


		 if(isAvailable == 0){
			 String query3 = String.format("SELECT price FROM rooms WHERE hotelid = '%d' AND roomnumber = '%d'", hotelID, rNum);
			 System.out.print("Your room is now booked for that date! Your total will be: ") ;
			 esql.executeQueryAndPrintResult(query3);
			 int cusID = Integer.parseInt(authorisedUser);

			 String query2 = String.format("INSERT INTO roombookings (bookingdate, hotelid, roomnumber, customerid) VALUES ('%s','%d', '%d', '%d')", date, hotelID, rNum, cusID);

			esql.executeUpdate(query2);
		 }else{
			System.out.print("The selected room is not available. Please try another option.\n");
		}
	}catch(Exception e){
		 System.err.println (e.getMessage());
	      }	 
   }
   public static void viewRecentBookingsfromCustomer(Hotel esql,String authorisedUser) {
   	try{
		System.out.print("\tNow browsing booking history: \n");

		int cusID = Integer.parseInt(authorisedUser);

		 String query = String.format("SELECT * FROM roombookings WHERE customerid = '%d' ORDER BY bookingdate DESC LIMIT 5",cusID);
		esql.executeQueryAndPrintResult(query);
	 	System.out.print("\n");
	      }catch(Exception e){
		 System.err.println (e.getMessage());
	      }

   }
   public static void updateRoomInfo(Hotel esql,String authorisedUser) {
   //Check if person if manager else return 
   //call the update executeUpdate look at create user for help on updating the sql 
   //get hotel id and room number 
   //only update price and image url of the hotelid for that manager so you gotta check the hotel id with WHERE
   //update rooms and roomupdatelogs. you maybe can ask user to enter date and time for update logs or use a jave timestamp function or sql trigger
   //sql return result for the last 5 update so you can sql query the roomupdateslog for the last 5 or top 5 or whatever
	try{
		int ID = Integer.parseInt(authorisedUser);
		System.out.println("\t"+ID);
		System.out.print("\tEnter hotelID: ");
		Scanner input = new Scanner(System.in);
		int hotelID= input.nextInt();
		input.nextLine();
		String checker = String.format("SELECT hotelName FROM hotel WHERE hotelID = '%d' AND managerUserID = '%d'",hotelID,ID);
		List<List<String>> check = esql.executeQueryAndReturnResult(checker);
		if(check.isEmpty()){
		
		System.out.print("\tYou have no power here ");
		return;
		}
		System.out.print("\tEnter roomNumber: ");
		int roomNumber=input.nextInt();
                String query = String.format("SELECT price,imageURL FROM Rooms WHERE hotelID = '%d' AND roomNumber ='%d'", hotelID, roomNumber);
		List<List<String>> result = esql.executeQueryAndReturnResult(query);
		System.out.println(result); //is this right?
		if(result.isEmpty()){
                        System.out.println("aint nothin here!");
                }else{
			System.out.print("\tUpdate Price: ");
			int price=input.nextInt();
			input.nextLine();
			System.out.print("\tUpdate imageURL: ");
			String imageURL=input.nextLine();
			String query1 = String.format("UPDATE rooms SET  price='%d',imageURL = '%s'  WHERE hotelID = '%d' AND roomNumber = '%d'",price,imageURL,hotelID,roomNumber);
			esql.executeUpdate(query1);
                	String query2 = String.format("SELECT price,imageURL FROM Rooms WHERE hotelID = '%d' AND roomNumber ='%d'", hotelID, roomNumber);
			List<List<String>> result2 = esql.executeQueryAndReturnResult(query2);
			System.out.println(result2);
			String oldStamp = String.format("SELECT updatedOn FROM RoomUpdatesLog WHERE managerID = '%d' AND hotelID = '%d' AND roomNumber ='%d'",ID,hotelID,roomNumber);
			List<List<String>> stamp = esql.executeQueryAndReturnResult(oldStamp);
			System.out.println(stamp);
			//make a sql query that will update the time stamp for you.
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamp1 = timestamp.toString();
			String updateStamp = String.format("UPDATE RoomUpdatesLog SET updatedOn = CURRENT_TIMESTAMP WHERE managerID = '%d' AND hotelID = '%d' AND roomNumber ='%d'",ID,hotelID,roomNumber);
			esql.executeUpdate(updateStamp);
			String newStamp = String.format("SELECT updatedOn FROM RoomUpdatesLog WHERE managerID = '%d' AND hotelID = '%d' AND roomNumber ='%d'",ID,hotelID,roomNumber);
			List<List<String>> nstamp = esql.executeQueryAndReturnResult(newStamp);
			System.out.println(nstamp);
		}
	} catch(Exception e){
		System.err.println(e.getMessage());
	}
   }
   public static void viewRecentUpdates(Hotel esql, String authorisedUser) {
   	try{
		// From PDF: Managers can also view the info of the last 5 recent updates of their hotels
		int ID = Integer.parseInt(authorisedUser);
		System.out.println("\t"+ID);
		String updates = String.format("SELECT * FROM roomUpdatesLog WHERE  managerID = '%d' ORDER BY  updatedOn DESC LIMIT 5", ID);
		List<List<String>> iupdates = esql.executeQueryAndReturnResult(updates);
		//make look nice how?
		//
		for(int i = 0; i<iupdates.size();i++){
				String formated =("HotelID: "+ iupdates.get(i).get(2)+" Room#: "+iupdates.get(i).get(3)+" Timestamp: "+iupdates.get(i).get(4));
				System.out.println(formated);
			}


	} catch(Exception e) {
		System.err.println(e.getMessage());
	}
   }
   public static void viewBookingHistoryofHotel(Hotel esql, String authorisedUser) { //This
	 try{

                int ID = Integer.parseInt(authorisedUser);


                String query1 = String.format("SELECT DISTINCT manageruserid FROM hotel WHERE manageruserid = '%d'", ID);
                int isManager = (esql.executeQuery(query1));

                if(isManager == 1){

                        System.out.print("You are a manager.");
                        System.out.print("\tEnter hotelID: ");
                        Scanner input = new Scanner(System.in);
                        int hotelID= input.nextInt();
                        input.nextLine();

                        String query2 = String.format("SELECT * FROM hotel WHERE manageruserid = '%d' AND hotelid = '%d'", ID, hotelID);
                        int managesHotel = (esql.executeQuery(query2));

                        if(managesHotel == 1){

				System.out.print("Enter start date: ");
				String range1 = in.readLine();
				System.out.print("Enter end date: ");
				String range2 = in.readLine();




                                String query = String.format("SELECT DISTINCT bookingid, hotelid, roomnumber, bookingdate, name FROM roombookings, users WHERE hotelid = '%d' AND bookingdate BETWEEN '%s' AND '%s' AND customerid = userid", hotelID, range1, range2);

                                 esql.executeQueryAndPrintResult(query);
                        }
                        else{
                                System.out.print("You do not manage this hotel.");
                        }




                }
                else{

                        System.out.print("You are not an authorized manager.\n");


		}
        }catch(Exception e) {
                System.err.println(e.getMessage());
        }
   }

   public static void viewRegularCustomers(Hotel esql, String authorisedUser) { //This
	   try{
		
		int ID = Integer.parseInt(authorisedUser);		


		String query1 = String.format("SELECT DISTINCT manageruserid FROM hotel WHERE manageruserid = '%d'", ID);
		int isManager = (esql.executeQuery(query1));
		
		if(isManager == 1){ 

			System.out.print("You are a manager.");
			System.out.print("\tEnter hotelID: ");
                	Scanner input = new Scanner(System.in);
                	int hotelID= input.nextInt();
                	input.nextLine();

			String query2 = String.format("SELECT * FROM hotel WHERE manageruserid = '%d' AND hotelid = '%d'", ID, hotelID);
		        int managesHotel = (esql.executeQuery(query2));	

			if(managesHotel == 1){
				String query = String.format("SELECT customerid FROM roombookings WHERE hotelid = '%d' GROUP BY customerid ORDER BY COUNT (bookingid) DESC LIMIT 5", hotelID);

               			 esql.executeQueryAndPrintResult(query);
			}
			else{
				System.out.print("You do not manage this hotel.");
			}




		}
		else{

			System.out.print("You are not an authorized manager.\n");

}

 
	}catch(Exception e) {
                System.err.println(e.getMessage());
        }
   }
   public static void placeRoomRepairRequests(Hotel esql, String authorisedUser) {
   	try{
		//make sure you're a manager
		int ID = Integer.parseInt(authorisedUser);
                System.out.println("\t"+ID);
                System.out.print("\tEnter hotelID: ");
                Scanner input = new Scanner(System.in);
                int hotelID= input.nextInt();
                input.nextLine();
		System.out.print("\tEnter roomNumber: ");
		int roomNumber= input.nextInt();
                input.nextLine();
		System.out.print("\tEnter companyID: ");
                int companyID= input.nextInt();

                String query = String.format("INSERT INTO roomRepairs (companyID,hotelID,roomNumber,repairDate) VALUES ('%d','%d','%d',CURRENT_DATE)",companyID,hotelID,roomNumber);
        	esql.executeUpdate(query);
		int reparID = esql.getCurrSeqVal("roomRepairs_repairID_seq");
		String query1 = String.format("INSERT INTO roomRepairRequests (managerID,repairID) VALUES ('%d', '%d')",ID,esql.getCurrSeqVal("roomRepairs_repairID_seq"));
		esql.executeUpdate(query1);
		//List<List<String>> r = esql.executeQueryAndReturnResult("SELECT * FROM roomRepairs");
		//System.out.println(r);
        	System.out.println ("successfully created request with ID#" + esql.getCurrSeqVal("roomRepairs_repairID_seq"));
	//	String res = ("SELECT * FROM roomRepairRequests");
		
        //        System.out.println(esql.executeQueryAndReturnResult(res));
	}catch(Exception e) {
                System.err.println(e.getMessage());
        }
   }
   public static void viewRoomRepairHistory(Hotel esql,String authorisedUser) {
   	try{
		int ID = Integer.parseInt(authorisedUser);
		String checker = String.format("SELECT hotelID FROM hotel WHERE  managerUserID = '%d'",ID);
                List<List<String>> check = esql.executeQueryAndReturnResult(checker);
		List<Integer> hID = new ArrayList<Integer>();
                if(check.isEmpty()){

                System.out.print("\tYou have no power here ");
                return;
                }

		
		esql.executeQueryAndPrintResult(String.format("SELECT * FROM roomRepairs WHERE hotelID IN(SELECT hotelID FROM hotel WHERE managerUserID = '%d') ORDER BY repairDate DESC",ID)); 
		}


		
   	catch(Exception e){
   	System.err.println(e.getMessage());
   		}
   }

}//end HotelA
