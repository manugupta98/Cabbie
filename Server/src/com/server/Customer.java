package com.server;


import java.io.*;
import java.sql.*;
import java.util.ArrayList;

public class Customer{
    private SessionRunnable session;
    private BufferedReader in;
    private PrintWriter out;
    private String userId;
    private String userName;
    private String phoneNumber;
    private String emailId;
    Trip trip;
    Node tripStart;
    Node tripDestination;

    Customer(BufferedReader in, PrintWriter out, String userId, String userName, String phoneNumber, String emailId){
        this.userId = userId;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.emailId = emailId;
        this.in = in;
        this.out = out;
        trip = null;
        tripStart = null;
        tripDestination = null;


        session =  new SessionRunnable(this);
    }

    BufferedReader getBufferedReader(){
        return in;
    }

    PrintWriter getPrintWriter() {
        return out;
    }

    String getUserName(){
        return userName;
    }

    String getPhoneNumber(){
        return phoneNumber;
    }

    String getEmailId(){
        return emailId;
    }

    String getUserId(){
        return userId;
    }
//
//    Trip getTrip(){
//        return trip;
//    }
//
//    Node getTripStart(){
//        return tripStart;
//    }
//
//    Node getTripDestination(){
//        return tripDestination;
//    }

    static boolean isValidPhoneNumber(String phoneNumber){
        try {
            Long num = Long.parseLong(phoneNumber);
            return (num < 10000000000L && num >= 1000000000L);
        }
        catch (Exception e){
            return false;
        }
    }

    static boolean isValidEmailId(String emailId){
        return (emailId.contains("@") && (emailId.endsWith(".com") || emailId.endsWith(".in")));
    }

    boolean changePhoneNumber(String newPhoneNumber){
        if (!isValidPhoneNumber(newPhoneNumber)){
            return false;
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/uber?useSSL=false", "root", "sql@connect");
            Statement st = conn.createStatement();
            ResultSet accountSet = st.executeQuery("SELECT * FROM accounts WHERE phone_number = '" + newPhoneNumber + "';");
            if (accountSet.next() && !phoneNumber.equals(newPhoneNumber)) {
                return false;
            }
            st.executeUpdate("Update accounts set phone_number = '" + newPhoneNumber + "' where uid = '" + userId + "';");
            phoneNumber = newPhoneNumber;

            st.close();
            conn.close();
        }
        catch (Exception e){
            System.out.println(e + " in CustomerThread getBalance");
        }
        return true;
    }

    boolean changeEmailId(String newEmailId){
        if (!isValidEmailId(newEmailId)){
            return false;
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/uber?useSSL=false", "root", "sql@connect");
            Statement st = conn.createStatement();
            ResultSet accountSet = st.executeQuery("SELECT * FROM accounts WHERE email_id = '" + newEmailId + "';");
            if (accountSet.next() && !emailId.equals(newEmailId)) {
                return false;
            }
            st.executeUpdate("Update accounts set email_id = '" + newEmailId + "' where uid = '" + userId + "';");
            emailId = newEmailId;

            st.close();
            conn.close();
        }
        catch (Exception e){
            System.out.println(e + " in CustomerThread getBalance");
        }
        return true;
    }

    String getPassword(){
        String password = new String();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/uber?useSSL=false", "root", "sql@connect");
            Statement st = conn.createStatement();

            ResultSet userSet = st.executeQuery("SELECT * FROM accounts WHERE uid = '" + userId + "';");
            userSet.next();
            password = userSet.getString("password");

            userSet.close();
            st.close();
            conn.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    return password;
    }

    Boolean changePassword(String oldPassword, String newPassword){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/uber?useSSL=false", "root", "sql@connect");
            Statement st = conn.createStatement();

            if (getPassword().equals(oldPassword)){
                st.executeUpdate("Update accounts set password = '" + newPassword + "' where uid = '" + userId + "';");
                return true;
            }

            st.close();
            conn.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    int getBalance(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/uber?useSSL=false", "root", "sql@connect");
            Statement st = conn.createStatement();

            ResultSet userWallet = st.executeQuery("SELECT * FROM wallets WHERE uid = '" + userId + "';");
            userWallet.next();
            int balance = userWallet.getInt("balance");

            userWallet.close();
            st.close();
            conn.close();

            return balance;
        }
        catch (Exception e){
            System.out.println(e + " in CustomerThread getBalance");
        }
        return -1;
    }

    Boolean addMoney(double amount){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/uber?useSSL=false", "root", "sql@connect");
            Statement st = conn.createStatement();

            st.executeUpdate("UPDATE wallets SET balance = balance + " + amount + " WHERE uid = '" + userId + "';");

            st.close();
            conn.close();
            return true;
        }
        catch (Exception e){
            System.out.println(e + " in CustomerThread addMoney");
        }
        return false;
    }

    void newLogin(BufferedReader in, PrintWriter out){
        this.in = in;
        this.out = out;
        if (session != null) {
            session.end();
        }
        session =  new SessionRunnable(this);
    }

    void logout(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/uber?useSSL=false", "root", "sql@connect");
            Statement st = conn.createStatement();

            st.executeUpdate("UPDATE accounts SET logged_in = false WHERE uid = '" + userId + "';");
//            out.println("Session expired");
            session = null;

            st.close();
            conn.close();
        }
        catch (Exception e){
            System.out.println(e + " in CustomerThread logout");
        }
    }
}


class SessionRunnable implements Runnable{
    private Customer customer;
    private BufferedReader in;
    private PrintWriter out;
    private Boolean loggedOut;
    Map map;
    TripControl tripControl;

    SessionRunnable(Customer customer){
        this.customer = customer;
        loggedOut = false;
        in = customer.getBufferedReader();
        out = customer.getPrintWriter();
        map = Map.getMap();
        tripControl = TripControl.getTripControl();
        (new Thread(this)).start();
    }

    void end(){
        loggedOut = true;
    }

    public void run() {
        String command;
        try {
            while ((command = in.readLine()) != null && !loggedOut) {
                System.out.println(customer.getUserId() + "'s command : " + command);
                switch (command) {
                    case "get phone number":out.println(customer.getPhoneNumber());
                        out.println("end");
                        break;

                    case "get username":out.println(customer.getUserName());
                        out.println("end");
                        break;

                    case "get emailid":out.println(customer.getEmailId());
                        out.println("end");
                        break;

                    case "get userid":out.println(customer.getUserId());
                        out.println("end");
                        break;

                    case "change phone number":if (!customer.getPassword().equals(in.readLine())){
                            out.println("wrong password");
                            in.readLine();
                        }
                        else if (!customer.changePhoneNumber(in.readLine())){
                            out.println("invalid phone number");
                        }
                        out.println("successful");
                        out.println("end");
                        break;

                    case "change emailid":if (!customer.getPassword().equals(in.readLine())){
                            in.readLine();
                            out.println("wrong password");
                        }
                        else if (!customer.changeEmailId(in.readLine())){
                            out.println("invalid emailid");
                        }

                        out.println("successful");
                        out.println("end");
                        break;

                    case "change password":if (customer.changePassword(in.readLine(), in.readLine())){
                            out.println("successful");
                        }
                        else {
                            out.println("wrong old password");
                        }
                        out.println("end");
                        break;

                    case "check balance": out.println(customer.getBalance());
                        out.println("end");
                        break;

                    case "add money":String amount = in.readLine();
                        if (customer.addMoney(Integer.parseInt(amount))){
                            out.println("successful");
                        }
                        out.println("end");
                        break;

                    case "logout":customer.logout();
                        loggedOut = true;
                        out.println("end");
                        return;

                    case "from":int startX = Integer.parseInt(in.readLine());
                        int startY = Integer.parseInt(in.readLine());
                        System.out.println(startX + " " + startY);
                        customer.tripStart = map.getClosestNode(startX, startY);
//                        System.out.println(tripStart);
                        System.out.println(customer.tripStart.getX() + " " + customer.tripStart.getY());
                        out.println(customer.tripStart.getX());
                        out.println(customer.tripStart.getY());
                        out.println("end");
                        break;

                    case "clear from":customer.tripStart = null;
                        out.println("end");
                        break;

                    case "get from":if(customer.tripStart==null){
                            out.println("not available");
                        }
                        else {
                            out.println(customer.tripStart.getX());
                            out.println(customer.tripStart.getY());
                        }
                        out.println("end");
                        break;

                    case "to":int destinationX = Integer.parseInt(in.readLine());
                        int destinationY = Integer.parseInt(in.readLine());
                        System.out.println(destinationX + " " + destinationY);
                        customer.tripDestination = map.getClosestNode(destinationX, destinationY);
//                        System.out.println(tripDestination);
                        System.out.println(customer.tripDestination.getX() + " " + customer.tripDestination.getY());
                        out.println(customer.tripDestination.getX());
                        out.println(customer.tripDestination.getY());
                        out.println("end");
                        break;

                    case "get trip estimate":if (customer.tripDestination != null && customer.tripStart != null) {
                            ArrayList<Node> path = map.getShortestPath(customer.tripStart, customer.tripDestination);
                            out.println(tripControl.getCostOfTrip(path));
                            out.println(map.getTimeOfPath(path));
                        }
                        out.println("end");
                        break;

                    case "clear to":customer.tripDestination = null;
                        out.println("end");
                        break;

                    case "get to":if(customer.tripDestination==null){
                            out.println("not available");
                        }
                        else {
                            out.println(customer.tripDestination.getX());
                            out.println(customer.tripDestination.getY());
                        }
                        out.println("end");
                        break;


                    case "nearby cabs":if (customer.tripStart != null) {
                        ArrayList<Driver> nearbyDrivers = tripControl.getNearbyDrivers(customer.tripStart);
//                        System.out.println(nearbyDrivers.get(0));
                            for (Driver driver : nearbyDrivers) {
                                out.println("next driver");
                                System.out.println(driver.getName());
                                out.println(driver.getX());
                                out.println(driver.getY());
                                out.println(driver.getOrientation());
                                System.out.println(driver.getX() + " " + driver.getY());
                            }
                            out.println("finished");
                        }
                        else {
                            out.println("from not available");
                        }
                        out.println("end");
                        break;

                    case "book":if (customer.tripStart != null && customer.tripDestination != null && customer.trip == null){
                            customer.trip = tripControl.startTrip(customer.tripStart, customer.tripDestination, customer);
                            if (customer.trip == null){
                                out.println("unsuccessful");
                                out.println("end");
                                System.out.println("unsuccessful");
                                break;
                            }
                            out.println("successful");
                            System.out.println("successful");
                        }
                        out.println("end");
                        break;

                    case "driver data":if (customer.trip != null){
                            ArrayList<String> data = customer.trip.getDriverData();
                            for (String string: data){
                                out.println(string);
                            }
                        }
                        out.println("end");
                        break;

                    case "trip data":if (customer.trip == null){
                            out.println("does not exist");
                            out.println("end");
                            break;
                        }
                        else if(customer.trip.hasFinished()){
                            out.println("finished");
                            out.println("end");
                            customer.trip = null;
                            customer.tripStart = null;
                            customer.tripDestination = null;
//                            System.out.println("finished");
                            break;
                        }
                        else if (customer.trip.hasStarted()){
                            out.println("started");
//                            System.out.println("started");
                        }
                        else {
                            out.println("not started");
//                            System.out.println("not started");
                        }
                        out.println(customer.trip.getOrientation());
                        out.println(customer.trip.getDriverX());
                        out.println(customer.trip.getDriverY());
//                        System.out.println(customer.driver.getX() + " " + customer.driver.getY());
                        out.println("end");
                        break;

                    case "rating":tripControl.rate(Integer.parseInt(in.readLine()), Integer.parseInt(in.readLine()));
                        out.println("end");
                        break;

                    case "number of trips":out.println(tripControl.noOfOldTrips(customer.getUserId()));
                        out.println("end");
                        break;

                    case "old trips":ArrayList<String> outputs = tripControl.oldTripData(Integer.parseInt(in.readLine()), customer.getUserId()  );
                        for (String string: outputs){
                            out.println(string);
                        }
                        out.println("end");
                        break;
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        if (! loggedOut) {
            customer.logout();
        }
        out.println("logged out");
        out.println("end");
    }
}



/*

book button : succesful or not
                driver info

getters for detail

 */