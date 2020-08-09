package com.server;

import java.io.*;
import java.sql.*;
import java.net.*;
import java.util.HashMap;

public class ClientsAdderThread extends Thread{
    private ServerSocket serverSocket = null;
    Socket socket = null;
    HashMap<String, Customer> customers = new HashMap<String, Customer>();

    ClientsAdderThread(int port){
        super();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/uber?useSSL=false", "root", "sql@connect");
            Statement st = conn.createStatement();

            st.executeUpdate("UPDATE accounts SET logged_in = false, on_trip = false, driver_id = null;");

            serverSocket = new ServerSocket(port);
            st.close();
            conn.close();
        }
        catch (Exception e){
            System.out.println(e + " clintadder thread creation");
        }
        start();
    }

    public void run() {
        try {
            while ((socket = (serverSocket.accept())) != null) {
                socket.setSoTimeout(300000);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println("connection established");
                String command = in.readLine();
                System.out.println(command);
                switch (command){
                    case "signup" : System.out.println("signup initiated");
                        signUp(in);
                        break;
                    case "login" : System.out.println("login initiated");
                        login(in);
                        break;
                }
            }
        }
        catch (Exception e){
            System.out.println(e + " clientadder thread run");
        }
    }

    private void signUp(BufferedReader in){
        try {
            String userId = in.readLine();
            String password = in.readLine();
            String userName = in.readLine();
            String phoneNumber = in.readLine();
            String emailId = in.readLine();
            boolean errorFound = false;

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/uber?useSSL=false", "root", "sql@connect");
            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery("SELECT * FROM accounts WHERE uid = '" + userId + "';");
            if (rs.next()) {
                out.println("userid taken");
                System.out.println("userid taken");
                errorFound = true;
            }


            if (Customer.isValidPhoneNumber(phoneNumber)) {
                rs = st.executeQuery("SELECT * FROM accounts WHERE phone_number = '" + phoneNumber + "';");
                if (rs.next()) {
                    out.println("phone number taken");
                    System.out.println("phone number taken");
                    errorFound = true;
                }
            }
            else {
                out.println("phone number invalid");
            }



            if (Customer.isValidEmailId(emailId)) {
                rs = st.executeQuery("SELECT * FROM accounts WHERE email_id = '" + emailId + "';");
                if (rs.next()) {
                    out.println("emailid taken");
                    System.out.println("emailid taken");
                    errorFound = true;
                }
            }
            else {
                out.println("emailid invalid");
            }

            if (errorFound) {
                out.println("errors finished");
                out.println("end");
                in.close();
                out.close();
                socket.close();
            } else {
                System.out.println("signup correct");
                System.out.println(phoneNumber);
                st.executeUpdate("INSERT INTO accounts VALUES('" + userId + "', '" + password + "', '" + userName
                        + "', '" + phoneNumber + "', '" + emailId + "', true, false, null);");
                st.executeUpdate("INSERT INTO wallets VALUES('" + userId + "', " + 0 + ");");
                out.println("successful");
//            Start new user Thread
                out.println("end");
                customers.put(userId,new Customer(in, out, userId, userName, phoneNumber, emailId));
                System.out.println("signup complete");
            }
            st.close();
            conn.close();
        }
        catch (Exception e){
            System.out.println(e + " clientadder thread signup");
        }
    }

    private void login(BufferedReader in){
        try {
            String userId = in.readLine();
            String password = in.readLine();

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/uber?useSSL=false", "root", "sql@connect");
            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery("SELECT * FROM accounts WHERE uid = '" + userId + "' AND password = '" + password + "';");
            if (rs.next()){
                if (rs.getBoolean("logged_in")){
                    System.out.println("logged in elsewhere");
                    customers.get(userId).newLogin(in, out);
                    out.println("successful");
                    out.println("end");
                }
                else {
                    System.out.println("login complete");
                    out.println("successful");
                    out.println("end");
//                Start new User Thread
                    if (customers.containsKey(userId)){

                        customers.get(userId).newLogin(in, out);
                    }
                    else {
                        customers.put(userId,new Customer(in, out, userId, rs.getString("user_name"), rs.getString("phone_number"), rs.getString("email_id")));
                    }
                    st.executeUpdate("UPDATE accounts SET logged_in = true WHERE uid = '" + userId + "';");
                }
            }
            else {
                out.println("wrong credentials");
                System.out.println("wrong details");
                out.println("end");
            }
            st.close();
            conn.close();
        }
        catch (Exception e){
            System.out.println(e + " client adder thread login");
        }
    }
}