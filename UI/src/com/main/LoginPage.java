package com.main;

import javax.swing.*;
import com.signUp.*;

import java.awt.*;
import java.net.*;


public class LoginPage {
    public static Socket sk;
    public static void main(String arg[]) {
        try {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Login login = new Login(frame);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
}


