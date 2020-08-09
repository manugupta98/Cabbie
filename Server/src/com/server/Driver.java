package com.server;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

public class Driver{
	private String name, vehicleName, vehicleColor, vehicleNumberPlate, phoneNumber;
	private int id, age;
	private char gender;
	private Map map;
	Node prevNode;
	Node nextNode;
	private double eta;

	Driver(ResultSet rs){
		try{
			id = Integer.parseInt(rs.getString("id"));
			name = rs.getString("name");
			age = rs.getInt("age");
			phoneNumber = rs.getString("phone_number");
			gender = rs.getString("gender").charAt(0);
			vehicleName = rs.getString("vehicle_name");
			vehicleColor = rs.getString("vehicle_color");
			vehicleNumberPlate = rs.getString("vehicle_plate");
			map = Map.getMap();
			prevNode = map.getNode(rs.getInt("node_sector"), rs.getString("node_block").charAt(0));
			nextNode = getRandomAdjacentNode(prevNode);
			eta = map.getTimeToAdjacentNode(prevNode, nextNode);
		}
		catch(Exception e){
			System.out.println(e + " Driver Creation");
		}
	}

	int getId(){
		return id;
	}

	int getAge(){
		return age;
	}

	String getName(){
		return name;
	}

	String getPhoneNumber(){
		return phoneNumber;
	}

	String getVehicleName(){
		return vehicleName;
	}

	String getVehicleColor(){
		return vehicleColor;
	}

	String getVehicleNumberPlate(){
		return vehicleNumberPlate;
	}

	char getGender(){
		return gender;
	}

	double getRating(){
		double rating = 0;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/uber?useSSL=false&allowPublicKeyRetrieval=true", "root", "sql@connect");
			Statement st = conn.createStatement();

			ResultSet driverSet = st.executeQuery("Select rating from drivers where id = " + getId() + ";");
			driverSet.next();
			rating = driverSet.getDouble("rating");

			driverSet.close();
			st.close();
			conn.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return rating;
	}

	double getEta(){
		return eta;
	}

	void setEta(double newEta){
		if (newEta < 0){
			eta = 0;
			return;
		}
		eta = newEta;
	}

	void driveForward(){
		try {
			Thread.sleep(10);
			setEta(eta - 0.01);
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	int getX(){
		double time = map.getTimeToAdjacentNode(prevNode, nextNode);
		return (int) (prevNode.getX() + ((time - eta)/time) * (nextNode.getX() - prevNode.getX()));
	}

	int getY(){
		double time = map.getTimeToAdjacentNode(prevNode, nextNode);
		return (int)(prevNode.getY() + ((time - eta)/time)*(nextNode.getY() - prevNode.getY()));
	}

	String getOrientation(){
		int deltaX = nextNode.getX() - prevNode.getX();
		int deltaY = nextNode.getY() - prevNode.getY();
		if (Math.abs(deltaX) > Math.abs(deltaY)){
			if (deltaX > 0){
				return "right";
			}
			return "left";
		}
		else {
			if (deltaY > 0){
				return "down";
			}
		}
		return "up";
	}

	Node getRandomAdjacentNode(Node node){
		Random rand = new Random(System.currentTimeMillis());
		return node.edges.get(rand.nextInt(node.edges.size())).getDestination();
	}
}


class DriverRunnable implements Runnable{
	private Driver driver;
	private Map map;
	private TripControl tripControl;
	private Trip trip;
	boolean onTrip;

	DriverRunnable(Driver driver){

		this.driver = driver;
		map = Map.getMap();
		(new Thread(this)).start();
	}

	public void run(){
		System.out.println("driver created " + driver.getId() + ": " + driver.getName());
		try {
			Thread.currentThread().sleep(1000);
		}
		catch (Exception e){
			System.out.println(e);
		}
		tripControl = TripControl.getTripControl();
		while (true){
//			System.out.println(driver.name + "'s location : (" + driver.getX() + ", " + driver.getY() + ") " + driver.eta);
			if (tripControl.isTripAvailable(driver)){
				trip = tripControl.getTrip(driver);
				tripControl.setDriversFromNode(driver.prevNode, null,driver);
				tripControl.setDriversToNode(driver.nextNode, null,driver);
				startTrip(trip);
				tripControl.setDriversFromNode(null, driver.prevNode,driver);
				tripControl.setDriversToNode(null, driver.nextNode,driver);
			}

			if (driver.getEta() == 0){
				tripControl.setDriversFromNode(driver.prevNode, driver.nextNode,driver);
				driver.prevNode = driver.nextNode;
				driver.nextNode = driver.getRandomAdjacentNode(driver.prevNode);
				tripControl.setDriversToNode(driver.prevNode, driver.nextNode,driver);
				driver.setEta(map.getTimeToAdjacentNode(driver.prevNode, driver.nextNode));
			}
			else {
				driver.driveForward();
			}
		}
	}

	private void startTrip(Trip trip){
		ArrayList<Node> path;
		ArrayList<Node> path1 = map.getShortestPath(driver.nextNode, trip.getPath().get(0));
		ArrayList<Node> path2 = map.getShortestPath(driver.prevNode, trip.getPath().get(0));

		if (map.getTimeOfPath(path1) + 2 * driver.getEta() < map.getTimeOfPath(path2) + map.getTimeToAdjacentNode(driver.prevNode, driver.nextNode)) {
			path = path1;
		} else {
			path = path2;
			driver.prevNode = driver.nextNode;
			driver.nextNode = path.get(0);
			driver.setEta(map.getTimeToAdjacentNode(driver.prevNode, driver.nextNode) - driver.getEta());
		}

		for (Node node: path){
			if (driver.getEta() == 0) {
				driver.prevNode = driver.nextNode;
				driver.nextNode = node;
				driver.setEta(map.getTimeToAdjacentNode(driver.prevNode, driver.nextNode));
			}

			while (driver.getEta() != 0){
				driver.driveForward();
			}
		}
		trip.start();

		ArrayList<Node> path3 = trip.getPath();
		for (Node node: path3){
			if (driver.getEta() == 0) {
				driver.prevNode = driver.nextNode;
				driver.nextNode = node;
				driver.setEta(map.getTimeToAdjacentNode(driver.prevNode, driver.nextNode));
			}
			while (driver.getEta() != 0){
				driver.driveForward();
			}
		}
		tripControl.finish(trip, driver);
	}
}

/*

CREATE TABLE drivers(id int primary key, name varchar(100), phone_number varchar(10), gender varchar(1), age int, node_sector int, node_block varchar(1), rating double, vehicle_name varchar(100), vehicle_color varchar(100), vehicle_plate varchar(100));

INSERT INTO drivers VALUES(0, 'Bruce Wayne', '9650496155', 'M', 48, 2, 'H', 0, 5, 'Batmobile', 'Black', 'WK BAT 1939');
INSERT INTO drivers VALUES(1, 'Oliver Queen', '9990452398', 'M', 34, 3, 'A', 0, 5, 'Arrowcar', 'Green', 'WK 5FR 1941');

INSERT INTO drivers VALUES(2, 'Barry Allen', '8826153846', 'M', 30, 4, 'H', 0, 5, 'Batmobile', 'Red', 'WK 7T6 1956');
INSERT INTO drivers VALUES(4, 'Clark Kent', '9560456816', 'M', 48, 2, 'V', 0, 5, 'Batmobile', 'Black', 'Gotham');
INSERT INTO drivers VALUES(5, 'Steven Rogers', '9505261954', 'M', 48, 0, 1, 'D', 0, 5, 'Batmobile', 'Black', 'Gotham');
INSERT INTO drivers VALUES(7, 'Diana', '9306125498', 'F', 48, 3, 'M', 0, 5, 'Batmobile', 'Black', 'Gotham');
INSERT INTO drivers VALUES(8, 'Wanda Maximoff', '8961234569', 'F', 48, 1, 'G', 0, 5, 'Batmobile', 'Black', 'Gotham');


INSERT INTO drivers VALUES(3, 'Oracle', '9718234916', 'F', 48, 3, 'C', 0, 5, 'Batmobile', 'Black', 'Gotham');
INSERT INTO drivers VALUES(6, 'Vision', '9949246981', 'M', 48, 2, 'Y', 0, 5, 'Batmobile', 'Black', 'Gotham');
INSERT INTO drivers VALUES(9, 'Hawkeye', '8037156324', 'M', 48, 4, 'J', 0, 5, 'Batmobile', 'Black', 'Gotham');
INSERT INTO drivers VALUES(10, 'Black Widow', '9634568216', 'F', 48, 2, 'Q', 0, 5, 'Batmobile', 'Black', 'Gotham');
INSERT INTO drivers VALUES(11, 'Falcon', '8448031021', 'M', 48, 1, 'S', 0, 5, 'Batmobile', 'Black', 'Gotham');
INSERT INTO drivers VALUES(12, 'Ant Man', '9830165032', 'M', 48, 4, 'N', 0, 5, 'Batmobile', 'Black', 'Gotham');
INSERT INTO drivers VALUES(13, 'Spider man', '9728364218', 'M', 48, 3, 'O', 0, 5, 'Batmobile', 'Black', 'Gotham');



 */