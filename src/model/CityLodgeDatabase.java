package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CityLodgeDatabase {
	private boolean isSaved ;

	private final String DB_NAME = "CityLodgeDB";
	private final String ROOM_TABLE_NAME = "HOTEL_ROOMS";
	private final String RECORD_TABLE_NAME = "HIRING_RECORDS";

	private CityLodge lodge;

	public CityLodgeDatabase(CityLodge lodge) {
		this.lodge = lodge;
		this.isSaved = false;
	}

	public CityLodge getCityLodge() {
		return lodge;
	}

	public boolean isSaved() {
		return isSaved;
	}

	public void setSaved(boolean isSaved) {
		this.isSaved = isSaved;
	}
	
	
	public void runConnection() {
		try (Connection con = getConnection(DB_NAME)) {

			System.out.println("Connection to database " + DB_NAME + " created successfully");

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	
	public static Connection getConnection(String dbName) throws SQLException, ClassNotFoundException {
		Class.forName("org.hsqldb.jdbc.JDBCDriver");
		Connection con = DriverManager.getConnection
				("jdbc:hsqldb:file:database/" + dbName, "SA", "");
		return con;
	}

	
	public void dropAllTables() {
		try (Connection con = CityLodgeDatabase.getConnection(DB_NAME);
				Statement stmt = con.createStatement();
				) {
			stmt.executeUpdate("DROP TABLE " + RECORD_TABLE_NAME);
			stmt.executeUpdate("DROP TABLE " + ROOM_TABLE_NAME);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	
	public void createRoomTable() {
		try (Connection con = CityLodgeDatabase.getConnection(DB_NAME);
				Statement stmt = con.createStatement();
				) {
			int result = stmt.executeUpdate("CREATE TABLE HOTEL_ROOMS ("
					+ "roomID VARCHAR(5) NOT NULL,"
					+ "bedrooms INT NOT NULL," 
					+ "features VARCHAR(255) NOT NULL,"
					+ "type VARCHAR(20) NOT NULL,"
					+ "status VARCHAR(20) NOT NULL,"
					+ "lastMaintenanceDate DATETIME,"
					+ "image VARCHAR(50),"
					+ "PRIMARY KEY (roomID))");
			if(result == 0) {
				System.out.println("Table " + ROOM_TABLE_NAME + " has been created successfully");
			} else {
				System.out.println("Table " + ROOM_TABLE_NAME + " is not created");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}


	public void createHiringRecordTable() {
		try (Connection con = CityLodgeDatabase.getConnection(DB_NAME);
				Statement stmt = con.createStatement();
				) {
			int result = stmt.executeUpdate("CREATE TABLE HIRING_RECORDS ("
					+ "roomID VARCHAR(5) NOT NULL,"
					+ "recordID VARCHAR(50) NOT NULL,"
					+ "rentDate DATETIME NOT NULL," 
					+ "estimatedReturnDate DATETIME NOT NULL,"
					+ "actualReturnDate DATETIME,"
					+ "rentalFee FLOAT,"
					+ "lateFee FLOAT,"
					+ "PRIMARY KEY (roomID, recordID),"
					+ "FOREIGN KEY (roomID) REFERENCES HOTEL_ROOMS (roomID))");
			if(result == 0) {
				System.out.println("Table " + RECORD_TABLE_NAME + " has been created successfully");
			} else {
				System.out.println("Table " + RECORD_TABLE_NAME + " is not created");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}


	public void addPresetRooms() {
		try (Connection con = getConnection(DB_NAME);
				Statement stmt = con.createStatement();
				) {					
			String query1 = "INSERT INTO " + ROOM_TABLE_NAME + " VALUES ('R_001', 1, 'WiFi, TV, Mini Fridge', 'Standard', 'Available', null, 'R_001-Small.jpg')";
			String query2 = "INSERT INTO " + ROOM_TABLE_NAME + " VALUES ('R_002', 2, 'WiFi, TV, Mini Fridge', 'Standard', 'Available', null, 'R_002-Small.jpg')";
			String query3 = "INSERT INTO " + ROOM_TABLE_NAME + " VALUES ('R_003', 4, 'WiFi, TV, Mini Fridge', 'Standard', 'Available', null, 'R_003-Small.jpg')";

			String query4 = "INSERT INTO " + ROOM_TABLE_NAME + " VALUES ('S_001', 6, 'WiFi, TV, Spa, Spa', 'Suite', 'Available', '2019-09-26 10:05:30', 'S_001-Small.jpg')";
			String query5 = "INSERT INTO " + ROOM_TABLE_NAME + " VALUES ('S_002', 6, 'WiFi, TV, Spa, Sauna', 'Suite', 'Available', '2019-09-26 10:35:30', 'S_002-Small.jpg')";
			String query6 = "INSERT INTO " + ROOM_TABLE_NAME + " VALUES ('S_003', 6, 'WiFi, TV, Spa, City View', 'Suite', 'Available', '2019-09-26 11:19:50','S_003-Small.jpg')";

			int result = stmt.executeUpdate(query1);
			result += stmt.executeUpdate(query2);
			result += stmt.executeUpdate(query3);

			result += stmt.executeUpdate(query4);
			result += stmt.executeUpdate(query5);
			result += stmt.executeUpdate(query6);

			con.commit();

			System.out.println("Insert into table " + ROOM_TABLE_NAME + " executed successfully");
			System.out.println(result + " row(s) affected");

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}


	public void addPresetRecords() {
		try (Connection con = getConnection(DB_NAME);
				Statement stmt = con.createStatement();
				) {					
			String query1 = "INSERT INTO " + RECORD_TABLE_NAME + " VALUES ('S_001', 'S_001_CUS001_10092019','2019-09-10 10:05:30', '2019-09-15 10:00:00', '2019-09-15 09:05:30', 4995.0, 0.0)";
			String query2 = "INSERT INTO " + RECORD_TABLE_NAME + " VALUES ('S_001', 'S_001_CUS002_17092019','2019-09-17 14:05:30', '2019-09-23 10:00:00', '2019-09-23 09:05:30', 5994.0, 0.0)";

			String query3 = "INSERT INTO " + RECORD_TABLE_NAME + " VALUES ('R_001', 'R_001_CUS003_16092019','2019-09-16 10:05:30', '2019-09-20 10:00:00', '2019-09-20 08:30:10', 236.0, 0.0)";
			String query4 = "INSERT INTO " + RECORD_TABLE_NAME + " VALUES ('R_001', 'R_001_CUS004_22092019','2019-09-22 10:05:30', '2019-09-27 10:00:00', '2019-09-27 09:05:30', 295.0, 0.0)";

			int result = stmt.executeUpdate(query1);
			result += stmt.executeUpdate(query2);
			result += stmt.executeUpdate(query3);
			result += stmt.executeUpdate(query4);
			con.commit();

			System.out.println("Insert into table " + RECORD_TABLE_NAME + " executed successfully");
			System.out.println(result + " row(s) affected");

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	
	public void insertRoom(String roomID, int bedrooms, String features, String type, String status, DateTime lastMaintenanceDate, String image) {
		try (Connection con = getConnection(DB_NAME);
				Statement stmt = con.createStatement();
				) {	

			String query;
			if (type.equalsIgnoreCase("Standard")) {
				query = "INSERT INTO " + ROOM_TABLE_NAME + " VALUES ('"+ roomID +"', "+ bedrooms +", '"+ features +"', '"+ type +"', '"+ status +"', null, '" + image + "')";
			}
			else {
				query = "INSERT INTO " + ROOM_TABLE_NAME + " VALUES ('"+ roomID +"', "+ bedrooms +", '"+ features +"', '"+ type +"', '"+ status +"','" + lastMaintenanceDate.getSqlDate() + "','" + image + "')";
			}

			int result = stmt.executeUpdate(query);

			con.commit();

			System.out.println("Insert into table " + ROOM_TABLE_NAME + " executed successfully");
			System.out.println(result + " row(s) affected");

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	
	public void deleteRecord(String recordID, String roomID) {
		try (Connection con = getConnection(DB_NAME);
				Statement stmt = con.createStatement();
		) {
			String query = "DELETE FROM " + RECORD_TABLE_NAME + 
					" WHERE recordID = '" + recordID + "' AND  roomID = '" + roomID + "'";
			
			int result = stmt.executeUpdate(query);
			
			System.out.println("Delete from table " + RECORD_TABLE_NAME + " executed successfully");
			System.out.println(result + " row(s) affected");

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	public void insertRecord(String roomID, String recordID, DateTime rentDate, DateTime estimatedReturnDate, DateTime actualReturnDate, double rentalFee, double lateFee) {
		try (Connection con = getConnection(DB_NAME);
				Statement stmt = con.createStatement();
				) {
			int result = 0;

			if (actualReturnDate == null) { //Room has not been returned
				String query = "INSERT INTO " + RECORD_TABLE_NAME + " VALUES ('" + roomID + "','" + recordID + "'," + "TO_DATE('"+ rentDate.getFormattedDate() + "', 'DD/MM/YYYY'), TO_DATE('" + estimatedReturnDate.getFormattedDate() + "', 'DD/MM/YYYY'), null, " + rentalFee + ", null)";
				result = stmt.executeUpdate(query);
			}
			else { //room has been returned
				String query = "INSERT INTO " + RECORD_TABLE_NAME + " VALUES ('" + roomID + "','" + recordID + "'," + "TO_DATE('"+ rentDate.getFormattedDate() + "', 'DD/MM/YYYY'), TO_DATE('" + estimatedReturnDate.getFormattedDate() + "', 'DD/MM/YYYY'), TO_DATE('" + actualReturnDate.getFormattedDate() + "', 'DD/MM/YYYY')," + rentalFee + "," + lateFee + ")";
				result = stmt.executeUpdate(query);
			}

			con.commit();

			System.out.println("Insert into table " + RECORD_TABLE_NAME + " executed successfully");
			System.out.println(result + " row(s) affected");

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	
	public void addStandardRoomsToHashmap() {
		try (Connection con = getConnection(DB_NAME);
				Statement stmt = con.createStatement();
				) {

			
			String query = "SELECT * FROM " + ROOM_TABLE_NAME + " WHERE type = 'Standard'";

			try (ResultSet resultSet = stmt.executeQuery(query)) {
				while(resultSet.next()) {

					String roomID = resultSet.getString("roomID");
					int bedrooms = resultSet.getInt("bedrooms");
					String features = resultSet.getString("features");
					String status = resultSet.getString("status");
					String image = resultSet.getString("image");

					if (image.equalsIgnoreCase("null")) {
						image = null;
					}
					
					HotelRoom room = new StandardRoom(roomID, bedrooms, features, null, status, image);

					if (room.getStatus().equalsIgnoreCase("Available")) {
						room.setStatusAvailable();
					}
					else if(status.equalsIgnoreCase("Rented")) {
						room.setStatusRented();
					}
					else if (status.equalsIgnoreCase("Maintenance")) {
						room.setStatusMaintenance();
					}
					lodge.addRoomMap(room);
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}	
	}


	public void addSuitesToHashmap() {
		try (Connection con = getConnection(DB_NAME);
				Statement stmt = con.createStatement();
				) {
			String query = "SELECT * FROM " + ROOM_TABLE_NAME + " WHERE type = 'Suite'";

			try (ResultSet resultSet = stmt.executeQuery(query)) {
				while(resultSet.next()) {

					String roomID = resultSet.getString("roomID");
					String features = resultSet.getString("features");
					String status = resultSet.getString("status");
					String image = resultSet.getString("image");
					Date date = resultSet.getDate("lastMaintenanceDate");

					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
					String today = formatter.format(date);
					String[] split = today.split("/");
					int day = Integer.parseInt(split[0]);					
					int month = Integer.parseInt(split[1]);
					int year = Integer.parseInt(split[2]);
					DateTime lastMaintenanceDate = new DateTime(day, month, year);

					if (image.equalsIgnoreCase("null")) {
						image = null;
					}
					
					HotelRoom room = new Suite(roomID, 0, features, null, status, image, lastMaintenanceDate);
					
					if (room.getStatus().equalsIgnoreCase("Available")) {
						room.setStatusAvailable();
					}
					else if(status.equalsIgnoreCase("Rented")) {
						room.setStatusRented();
					}
					else if (status.equalsIgnoreCase("Maintenance")) {
						room.setStatusMaintenance();
					}				
					lodge.addRoomMap(room);
	
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}	
	}


	public void addHiringRecordsToArray() {
		try (Connection con = getConnection(DB_NAME);
				Statement stmt = con.createStatement();
				) {

			for(HotelRoom room : lodge.getRoomMap().values()) {

				String query = "SELECT * FROM " + RECORD_TABLE_NAME + " WHERE roomID = '"+room.getRoomID()+"'";

				try (ResultSet resultSet = stmt.executeQuery(query)) {
					while(resultSet.next()) {	
						String recordID = resultSet.getString("recordID");

						DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");						
						Date date1 = resultSet.getDate("rentDate");
						String rentDateString = formatter.format(date1);
						String[] split1 = rentDateString.split("/");
						int day = Integer.parseInt(split1[0]);					
						int month = Integer.parseInt(split1[1]);
						int year = Integer.parseInt(split1[2]);
						DateTime rentDate = new DateTime(day, month, year);

						Date date2 = resultSet.getDate("estimatedReturnDate");
						String estimatedReturnDateString = formatter.format(date2);
						String[] split2 = estimatedReturnDateString.split("/");
						int d = Integer.parseInt(split2[0]);					
						int m = Integer.parseInt(split2[1]);
						int y = Integer.parseInt(split2[2]);
						DateTime estimatedReturnDate = new DateTime(d, m, y);

						double rentalFee = resultSet.getDouble("rentalFee");

						HiringRecord record = new HiringRecord(recordID, rentDate, estimatedReturnDate, rentalFee);
						if (resultSet.getDate("actualReturnDate") != null) {	
							Date date3 = resultSet.getDate("actualReturnDate");
							String actualReturnDateString = formatter.format(date3);
							String[] split3 = actualReturnDateString.split("/");
							int dd = Integer.parseInt(split3[0]);					
							int mm = Integer.parseInt(split3[1]);
							int yyyy = Integer.parseInt(split3[2]);
							DateTime actualReturnDate = new DateTime(dd, mm, yyyy);

							double lateFee = resultSet.getDouble("lateFee");

							record.setEstimatedReturnDate(estimatedReturnDate);
							record.setActualReturnDate(actualReturnDate);
							record.setLateFee(lateFee);			
						}
						room.addRecord(record);
					}
				} catch (SQLException e) {
					System.out.println(e.getMessage());
				}

				//Add Rent and Return Count to the Hotel Room objects
				HiringRecord[] records = room.getRecords();
				int recordCount = room.getActualRecordsLength(records);
				room.addToRentCount(recordCount);

				if (room.isRented == true) {
					room.addToReturnCount(recordCount-1);
				}
				else {
					room.addToReturnCount(recordCount);
				}
		
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}


	public void saveAllRooms(CityLodge lodge) {
		try (Connection con = getConnection(DB_NAME);
				Statement stmt = con.createStatement();
				) {
			int result = 0;

			for(HotelRoom room : lodge.getRoomMap().values()) {
				if (room.getType().equalsIgnoreCase("Standard")) {
					String query = "UPDATE " + ROOM_TABLE_NAME + 
							" SET status = '" + room.getStatus() + "'" +
							" WHERE roomID = '" + room.getRoomID() + "'";

					result += stmt.executeUpdate(query);
				}
				else {
					String query = "UPDATE " + ROOM_TABLE_NAME + 
							" SET status = '" + room.getStatus() + "', lastMaintenanceDate = '" + ((Suite) room).getLastMaintenanceDate().getSqlDate() + "'" +
							" WHERE roomID = '" + room.getRoomID() + "'";

					result += stmt.executeUpdate(query);
				}
			}
			System.out.println("Update table " + ROOM_TABLE_NAME + " executed successfully");
			System.out.println(result + " row(s) affected");

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	public void saveAllRecords(CityLodge lodge) {
		for(HotelRoom room : lodge.getRoomMap().values()) {
			HiringRecord[]records = room.getRecords();
			
			if(room.getActualRecordsLength(records) > 0){
				deleteAllRoomRecords(room.getRoomID());
				
				if(room.isRented == true) { //currently being rented	
					records[room.getActualRecordsLength(records)-1].getRecordID();
					
					//Add the most recent incomplete record
					String roomID = room.getRoomID();
					String recordID = records[room.getActualRecordsLength(records)-1].getRecordID();
					DateTime rentDate = records[room.getActualRecordsLength(records)-1].getRentDate();
					DateTime estimatedReturnDate = records[room.getActualRecordsLength(records)-1].getEstimatedReturnDate();
					Double rentalFee = records[room.getActualRecordsLength(records)-1].getRentalFee();
					insertRecord(roomID, recordID, rentDate, estimatedReturnDate, null, rentalFee, 0);
							
					//Add all complete records
					for(int i = room.getActualRecordsLength(records)-2; i >= 0; i--) {
						roomID = room.getRoomID();
						recordID = records[i].getRecordID();
						rentDate = records[i].getRentDate();
						estimatedReturnDate = records[i].getEstimatedReturnDate();
						DateTime actualReturnDate = records[i].getActualReturnDate();
						rentalFee = records[i].getRentalFee();
						double lateFee = records[i].getLateFee();
						insertRecord(roomID, recordID, rentDate, estimatedReturnDate, actualReturnDate, rentalFee, lateFee);						
					}
		
				}
				else {
					for(int i = room.getActualRecordsLength(records)-1; i >= 0; i--) {
						String roomID = room.getRoomID();
						String recordID = records[i].getRecordID();
						DateTime rentDate = records[i].getRentDate();
						DateTime estimatedReturnDate = records[i].getEstimatedReturnDate();
						DateTime actualReturnDate = records[i].getActualReturnDate();
						double rentalFee = records[i].getRentalFee();
						double lateFee = records[i].getLateFee();
						insertRecord(roomID, recordID, rentDate, estimatedReturnDate, actualReturnDate, rentalFee, lateFee);						
					}
				}
			}
		}
	}
	
	
	public void deleteAllRoomRecords(String roomID) {
		try (Connection con = getConnection(DB_NAME);
				Statement stmt = con.createStatement();
		) {
			String query = "DELETE FROM " + RECORD_TABLE_NAME + 
					" WHERE roomID = '" + roomID + "'"; 
			
			int result = stmt.executeUpdate(query);
			
			System.out.println("Delete from table " + RECORD_TABLE_NAME + " executed successfully");
			System.out.println(result + " row(s) affected");

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
