package com.talk;

import com.main.LoginPage;
import com.signUp.Login;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;


public class Talk{
    private static PrintWriter out = null;
    private static BufferedReader in = null;

    synchronized public static ArrayList<String> send(Socket socket, ArrayList<String> outputs){
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return send(outputs);
    }

    synchronized public static ArrayList<String> send(ArrayList<String> outputs){
        for (String string: outputs){
            out.println(string);
        }
        ArrayList<String> inputs = new ArrayList<>();
        try {
            String string;
            while ((string = in.readLine()) != null && string.equals("end") == false) {
                inputs.add(string);
            }
        }
        catch (Exception e){
        }
        if (inputs.size() > 0 && inputs.get(inputs.size()-1).equals("logged out")) {
            JFrame frame = Login.frame;
            Login l = new Login(frame);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            JOptionPane.showMessageDialog(frame, "You were logged out due to inactivity.", "SESSION TIME OUT", JOptionPane.ERROR_MESSAGE);
            try {
                LoginPage.sk.close();
            }
            catch (Exception ea){
                ea.printStackTrace();
            }
        }
        return inputs;
    }

}
