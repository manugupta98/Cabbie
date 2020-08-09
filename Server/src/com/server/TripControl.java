package com.server;

import java.sql.*;
import java.time.DateTimeException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;


public class TripControl{
    private static TripControl tripControl = null;
    private Map map;
    private HashMap<Integer, Trip> trips;   //change integer to driver class by writing hashcode and equals method
    private HashMap<Node, ArrayList<Driver>> driversToNode;
    private HashMap<Node, ArrayList<Driver>> driversFromNode;

    private TripControl(){
        map = Map.getMap();

        trips = new HashMap<Integer, Trip>();
        driversToNode = new HashMap<Node , ArrayList<Driver>>();
        driversFromNode = new HashMap<Node , ArrayList<Driver>>();
        driversToNode.put(null, new ArrayList<Driver>());
        driversFromNode.put(null, new ArrayList<Driver>());
        for (Node node: map.nodes){
            driversToNode.put(node, new ArrayList<Driver>());
            driversFromNode.put(node, new ArrayList<Driver>());
        }


        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/uber?useSSL=false&allowPublicKeyRetrieval=true", "root", "sql@connect");
            Statement st = conn.createStatement();

            int i = 5;
            ResultSet driverSet = st.executeQuery("SELECT * FROM drivers;");
            while (driverSet.next() && i > 0){
                Driver driver = new Driver(driverSet);
                driversFromNode.get(driver.prevNode).add(driver);
                driversToNode.get(driver.nextNode).add(driver);
                new DriverRunnable(driver);
//                i--;
            }

            st.close();
            conn.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    static TripControl getTripControl(){
        if (tripControl == null){
            tripControl = new TripControl();
        }
        return tripControl;
    }

    void dfs(Node node, ArrayList<Driver> nearbyDrivers,int maxTime, double currentTime, ArrayList<Node> visited){
        visited.add(node);
        if (currentTime > maxTime){
            visited.remove(node);
            return;
        }
        else {
            for (Driver driver: driversFromNode.get(node)){
                if (!nearbyDrivers.contains(driver)){
                    nearbyDrivers.add(driver);
                }
            }
            for (Driver driver: driversToNode.get(node)){
                if (!nearbyDrivers.contains(driver)){
                    nearbyDrivers.add(driver);
                }
            }

            for (Edge edge:node.edges){
                if (!visited.contains(edge.getDestination())){
                    dfs(edge.getDestination(), nearbyDrivers, maxTime, currentTime + map.getTimeToAdjacentNode(node, edge.getDestination()), visited);
                }
            }
        }
        visited.remove(node);
    }

    synchronized ArrayList<Driver> getNearbyDrivers(Node node){
        ArrayList<Driver> nearbyDrivers = new ArrayList<Driver>();
        dfs(node, nearbyDrivers, 25, 0, new ArrayList<Node>());
        return nearbyDrivers;
    }

    synchronized double getNearestDriver(Node node, ArrayList<Driver> nearestDriver, double nearestDriverTime, int maxTime, double currentTime, ArrayList<Node> visited){
        visited.add(node);
        if (currentTime > maxTime){
            visited.remove(node);
            return nearestDriverTime;
        }
        else {
            double time;
            for (Driver driver: driversFromNode.get(node)){
                time = currentTime + map.getTimeToAdjacentNode(driver.prevNode, driver.nextNode) - driver.getEta();
                if (time < nearestDriverTime || nearestDriver.size() == 0){
                    nearestDriver.add(0,driver);
                    nearestDriverTime = time;
                }
                else if (time == nearestDriverTime && driver.getRating() > nearestDriver.get(0).getRating()){
                    nearestDriver.add(0,driver);
                    nearestDriverTime = time;
                }
            }
            for (Driver driver: driversToNode.get(node)){
                time = currentTime + driver.getEta();
                if (time < nearestDriverTime || nearestDriver.size() == 0){
                    nearestDriver.add(0,driver);
                    nearestDriverTime = time;
                }
                else if (time == nearestDriverTime && driver.getRating() > nearestDriver.get(0).getRating()){
                    nearestDriver.add(0,driver);
                    nearestDriverTime = time;
                }
            }

            for (Edge edge:node.edges){
                if (!visited.contains(edge.getDestination())){
                    getNearestDriver(edge.getDestination(), nearestDriver, nearestDriverTime,  maxTime, currentTime + map.getTimeToAdjacentNode(node, edge.getDestination()), visited);
                }
            }
        }
        visited.remove(node);
        return nearestDriverTime;
    }

    synchronized Driver getNearestDriver(Node node){
        ArrayList<Driver> nearestDriver = new ArrayList<Driver>();
        getNearestDriver(node, nearestDriver, -1,  30, 0, new ArrayList<Node>());
        if (nearestDriver.size() == 0){
            return null;
        }
        return nearestDriver.get(0);
    }

    boolean isTripAvailable(Driver driver){
        return trips.containsKey(driver.getId());
    }

    double getCostOfTrip(ArrayList<Node> path){
        int bookingCharges = 20;
        double basicCharges = map.getTimeOfPath(path) * 2;
        return bookingCharges + basicCharges;
    }

    Trip getTrip(Driver driver){
        return trips.get(driver.getId());
    }

    Trip startTrip(Node start, Node destination, Customer customer){
        ArrayList<Node> path = map.getShortestPath(start, destination);
//        for (Node node:path){
//            System.out.print(node.getSector() + ":" + node.getBlock() + " ");
//        }
//        System.out.println(" ");
        Driver driver = getNearestDriver(start);
        if (driver == null){
            return null;
        }
        Trip trip = new Trip(customer, driver, path, getCostOfTrip(path));
        trips.put(driver.getId(), trip);
        return trip;
    }

    void rate(int rating, int tripId){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/uber?useSSL=false&allowPublicKeyRetrieval=true", "root", "sql@connect");
            Statement st = conn.createStatement();

            st.executeUpdate("Update trips set rating = "+  rating + " , is_trip_rated = 1 where trip_id = " + tripId + " ;");
            ResultSet tripSet = st.executeQuery("Select driver_id from trips where trip_id = " + tripId + ";");
            tripSet.next();
            int driverId = tripSet.getInt("driver_id");
            st.executeUpdate("Update drivers set rating = (rating * rated_trips + " +  rating + ")/(rated_trips+1), rated_trips = rated_trips + 1 where id = " + driverId + ";");

            tripSet.close();
            st.close();
            conn.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    int noOfOldTrips(String userId){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/uber?useSSL=false&allowPublicKeyRetrieval=true", "root", "sql@connect");
            Statement st = conn.createStatement();

            ResultSet tripSet = st.executeQuery("Select count(*) from trips where customer_uid = '" + userId + "';");

            tripSet.next();
            int count =  tripSet.getInt("count(*)");

            st.close();
            conn.close();

            return count;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    ArrayList<String> oldTripData(int index, String userId){
        ArrayList<String> data = new ArrayList<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/uber?useSSL=false&allowPublicKeyRetrieval=true", "root", "sql@connect");
            Statement st = conn.createStatement();

            ResultSet tripSet = st.executeQuery("Select * from trips where customer_uid = '" + userId + "' ORDER BY time DESC;");
            for (int i = 0; i <= index; i++){
                tripSet.next();
            }
            Node start = map.getNode(tripSet.getInt("trip_start_sector"), tripSet.getString("trip_start_block").charAt(0));
            Node destination = map.getNode(tripSet.getInt("trip_end_sector"), tripSet.getString("trip_end_block").charAt(0));
            data.add(Integer.toString(start.getX()));
            data.add(Integer.toString(start.getY()));
            data.add(Integer.toString(destination.getX()));
            data.add(Integer.toString(destination.getY()));

            data.add(Integer.toString(tripSet.getInt("trip_id")));
            data.add(tripSet.getString("driver_name"));
            data.add(Integer.toString(tripSet.getInt("cost")));
            LocalDateTime time   = tripSet.getTimestamp("time", Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"))).toLocalDateTime();
//            System.out.println(time);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd LLL yyyy hh:mm a");
            data.add(time.format(formatter));

            if (tripSet.getBoolean("is_trip_rated")){
                data.add("true");
                data.add(Integer.toString(tripSet.getInt("rating")));
            }
            else {
                data.add("false");
            }

            st.close();
            conn.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }

    void finish(Trip trip, Driver driver){
        trips.remove(driver.getId());
        trip.finish();
    }

    synchronized void setDriversToNode(Node prevNode, Node newNode, Driver driver){
        driversToNode.get(prevNode).remove(driver);
        driversToNode.get(newNode).add(driver);
    }

    synchronized void setDriversFromNode(Node prevNode, Node newNode, Driver driver){
        driversFromNode.get(prevNode).remove(driver);
        driversFromNode.get(newNode).add(driver);
    }
}