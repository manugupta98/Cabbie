package com.server;

import java.sql.*;
import java.util.ArrayList;

public class Trip {
    private Driver driver;
    private Customer customer;
    private ArrayList<Node> path;
    private double cost;
    private boolean tripStarted;
    private boolean tripFinished;

    Trip(Customer customer, Driver driver, ArrayList<Node> path, double cost){
        this.customer = customer;
        this.driver = driver;
        this.path = path;
        this.cost = cost;
        tripStarted = false;
    }

    ArrayList<Node> getPath(){
        return path;
    }


    Node getNextNode(Node currentNode){
        for (int i = 0; i < path.size() - 1; i++){
            if (path.get(i).equals(currentNode)){
                return path.get(i+1);
            }
        }
        return null;
    }

    void start(){
        try {
            Thread.sleep(2000);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        tripStarted = true;
    }

    void finish(){
        try {
            Thread.sleep(2000);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        tripFinished = true;
        customer.addMoney(-cost);
        customer.tripStart = null;
        customer.tripDestination = null;
        saveTrip();
    }

    void saveTrip(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/uber?useSSL=false&allowPublicKeyRetrieval=true", "root", "sql@connect");
            Statement st = conn.createStatement();

            st.executeUpdate("INSERT INTO trips VALUES(NULL, "+ path.get(0).getSector() + ", '" + path.get(0).getBlock() +"', "+ path.get(path.size()-1).getSector() + ", '" + path.get(path.size()-1).getBlock() +"', '" + pathString()+ "', "+ driver.getId() + ", '"+ driver.getName() + "', '" + customer.getUserId() + "', " + cost +", current_timestamp, default(is_trip_rated), default(rating));");

            st.close();
            conn.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    String pathString(){
        String string = new String();
        for (Node node: path){
            string = string + node.getSector() + node.getBlock();
        }
        return string;
    }

    boolean hasStarted(){
        return tripStarted;
    }

    boolean hasFinished(){
        return tripFinished;
    }

    int getDriverX(){
        return driver.getX();
    }

    int getDriverY(){
        return driver.getY();
    }

    ArrayList<String> getDriverData(){
        ArrayList<String> data = new ArrayList<>();
        data.add(driver.getName());
        data.add(Character.toString(driver.getGender()));
        data.add(driver.getPhoneNumber());
        data.add(Double.toString(driver.getRating()));
        data.add(Integer.toString(driver.getAge()));
        data.add(driver.getVehicleColor());
        data.add(driver.getVehicleName());
        data.add(driver.getVehicleNumberPlate());
        return data;
    }

    String getOrientation(){
        return driver.getOrientation();
    }

    double getCost(){
        return cost;
    }
}


/*

CREATE TABLE trips(trip_id int Primary Key not null auto_increment, trip_start_sector int, trip_start_block varchar(1), trip_end_sector int, trip_end_block varchar(1), path varchar(400), driver_id int, driver_name varchar(100), customer_uid varchar(100), cost int, time timestamp default current_timestamp, is_trip_rated boolean default false, rating int default 0);
INSERT INTO trips VALUES(null, 1, 'A', 1, 'H', '1A1B1H', 12, 'Batman', 'abc', 123, current_timestamp, default(is_trip_rated), default(rating));

 */