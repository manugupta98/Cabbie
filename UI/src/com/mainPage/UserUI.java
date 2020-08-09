package com.mainPage;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import java.awt.image.ImageObserver;
import java.io.*;
import com.animation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.attribute.UserPrincipal;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.function.Predicate;

import com.signUp.*;
import com.main.*;
import com.talk.Talk;
import com.wallet.*;
import com.cabAnimation.*;

public class UserUI implements ActionListener, MouseListener {
    private enum Actions {
        WALLET,
        LOGOUT,
        CHANGECURRENTLOCATION,
        CHANGEDROPLOCATION,
        STARTRIDE,
        CANCELRIDE,
        PREVIOUSTRIPS,
        GOTOMAINPAGE,
        NEXT,
        PREVIOUS
    }
    private int countPin[] = new int[2], numberOfTrips, indexOfTrip;
    String email, phno;
    JPanel userData;
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public static boolean preTrips = false, isTripRated, tripStarted = false;
    int screenHeight = screenSize.height, tripRating, tripId;
    int screenWidth = screenSize.width;
    public JPanel panel, driverData;
    String name;
    JLabel welcome, mapImg, userInfo, driverName, driverAge, driverPhone, driverGender, driverRating, dCabPlate, dCabColor, dCabModel, cost, time, rate1, rate2, rate3, rate4, rate5;
    public JLabel dropPin, startPin;
    JLabel emailid, phoneNo, oldPassword, newPassword, retypePassword;
    JTextField location, emailidTxt, phoneNoTxt, oldPasswordTxt, newPasswordTxt, retypePasswordTxt;
    JButton  wallet, logout, changeCurrentLocation, changeDropLocation, startRide, cancelRide, previousTrips, goToMainPage, next, previous, update;
    JFrame frame;
    public UserUI(JFrame frame) {
        preTrips = false;
        tripStarted = false;
        rate1 = new JLabel();
        rate2 = new JLabel();
        rate3 = new JLabel();
        rate4 = new JLabel();
        rate5 = new JLabel();

        rate1.setOpaque(true);
        rate2.setOpaque(true);
        rate3.setOpaque(true);
        rate4.setOpaque(true);
        rate5.setOpaque(true);

        rate1.setBackground(Color.white);
        rate2.setBackground(Color.white);
        rate3.setBackground(Color.white);
        rate4.setBackground(Color.white);
        rate4.setBackground(Color.white);

        rate1.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        rate2.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        rate3.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        rate4.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        rate5.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        userData = new JPanel();
        update = new JButton();

        ArrayList<String> outputs = new ArrayList<>();
        userInfo = new JLabel();
        driverData = new JPanel();
        outputs.add("get username");
        ArrayList<String> inputs = Talk.send(outputs);
        name = inputs.remove(0);
        previousTrips = new JButton();
        next = new JButton();
        previous = new JButton();
        this.frame = frame;
        welcome = new JLabel();
        panel = new JPanel();
        driverName = new JLabel();
        driverRating = new JLabel();
        location = new JTextField();
        panel.setLayout(null);
        goToMainPage = new JButton();
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        JPanel p = new JPanel();
        p.setLayout(null);
        p.setBounds(0,0,screenWidth,(int)((100/1050.0)*screenHeight));
        p.setBackground(new Color(0,35,102));
        panel.setBackground(Color.white);
        panel.add(p);
        cost = new JLabel();
        time = new JLabel();

        mapImg = createMap();

        takeUpdatedInput();

        welcome.setText("WELCOME " + name);
        welcome.setFont(new Font("Helvetica Neue", Font.BOLD, (int)((30/1050.0)*screenHeight)));
        welcome.setBounds((int)((30/1680.0)*screenWidth),(int)((20/1050.0)*screenHeight),(int)((500/1680.0)*screenWidth),(int)((50/1050.0)*screenHeight));
        welcome.setForeground(Color.white);

        previousTrips.setText("Previous Trips");
        previousTrips.setToolTipText("your previous trips");
        previousTrips.setBounds(screenWidth - (int)((310/1680.0)*screenWidth), (int)((20/1050.0)*screenHeight), (int)((150/1680.0)*screenWidth), (int)((50/1050.0)*screenHeight));

        ImageIcon logoutLogo = getImageButton("images/logout_img.jpg", (int)((50/1680.0)*screenWidth), (int)((50/1050.0)*screenHeight));
        logout = new JButton(logoutLogo);
        logout.setBounds(screenWidth - (int)((70/1680.0)*screenWidth), (int)((20/1050.0)*screenHeight), (int)((50/1680.0)*screenWidth),(int)((50/1050.0)*screenHeight));
        logout.setOpaque(false);
        logout.setContentAreaFilled(false);
        logout.setBorderPainted(false);

        ImageIcon walletLogo = getImageButton("images/wallet_img.png", (int)((50/1680.0)*screenWidth),(int)((50/1050.0)*screenHeight));
        wallet = new JButton(walletLogo);
        wallet.setBounds(screenWidth - (int)((140/1680.0)*screenWidth),(int)((20/1050.0)*screenHeight),(int)((50/1680.0)*screenWidth),(int)((50/1050.0)*screenHeight));
        wallet.setOpaque(false);
        wallet.setContentAreaFilled(false);
        wallet.setBorderPainted(false);

        logout.setToolTipText("logout");
        wallet.setToolTipText("wallet");

        changeCurrentLocation = new JButton();
        changeCurrentLocation.setBounds((int)((1160/1680.0)*screenWidth), (int)((820/1050.0)*screenHeight), (int)((200/1680.0)*screenWidth), (int)((50/1050.0)*screenHeight));
        changeCurrentLocation.setOpaque(false);
        changeCurrentLocation.setText("Change current location");
        changeCurrentLocation.setContentAreaFilled(false);

        changeDropLocation = new JButton();
        changeDropLocation.setBounds((int)((1160/1680.0)*screenWidth), (int)((750/1050.0)*screenHeight), (int)((200/1680.0)*screenWidth), (int)((50/1050.0)*screenHeight));
        changeDropLocation.setOpaque(false);
        changeDropLocation.setText("Change drop location");
        changeDropLocation.setContentAreaFilled(false);

        startRide = new JButton();
        startRide.setBounds((int)((1380/1680.0)*screenWidth), (int)((820/1050.0)*screenHeight), (int)((200/1680.0)*screenWidth), (int)((50/1050.0)*screenHeight));
        startRide.setOpaque(false);
        startRide.setText("Start ride");
        startRide.setContentAreaFilled(false);

        cancelRide = new JButton();
        cancelRide.setBounds((int)((1380/1680.0)*screenWidth), (int)((750/1050.0)*screenHeight), (int)((200/1680.0)*screenWidth), (int)((50/1050.0)*screenHeight));
        cancelRide.setOpaque(false);
        cancelRide.setText("Cancel ride");
        cancelRide.setContentAreaFilled(false);

        userInfo.setBounds((int)((30/1680.0)*screenWidth), (int)((870/1050.0)*screenHeight), (int)((900/1680.0)*screenWidth), (int)((50/1050.0)*screenHeight));;

        panel.add(userInfo);
        panel.add(changeCurrentLocation);
        panel.add(changeDropLocation);
        panel.add(cancelRide);
        panel.add(startRide);

        p.add(wallet);
        p.add(previousTrips);
        p.add(logout);
        p.add(welcome);
        frame.add(panel);
        frame.setContentPane(panel);
        frame.setVisible(true);
        frame.setTitle("CABBIE");
        outputs.clear();
        String s;
        outputs.add("trip data");
        inputs = Talk.send(outputs);
        s = inputs.remove(0);
        if(s.equals("started")||s.equals("not started")) {
            inputs.remove(0);
            tripStarted = true;
            int x, y;
            x = Integer.parseInt(inputs.remove(0));
            y = Integer.parseInt(inputs.remove(0));
            outputs.clear();
            outputs.add("get from");
            inputs = Talk.send(outputs);
            addPins(Integer.parseInt(inputs.remove(0)), Integer.parseInt(inputs.remove(0)));
            outputs.clear();
            outputs.add("get to");
            inputs = Talk.send(outputs);
            addPins(Integer.parseInt(inputs.remove(0)), Integer.parseInt(inputs.remove(0)));
            addCab(x, y, 1);
            userInfo.setText("Trip is in progress");
            outputs.clear();
            outputs.add("driver data");
            inputs = Talk.send(outputs);
            addDriverData();
            s = inputs.remove(0);
            driverName.setText("Name: "+s);
            s = inputs.remove(0);
            driverGender.setText("Gender: "+s);
            s = inputs.remove(0);
            driverPhone.setText("Phone: "+s);
            s = inputs.remove(0);
            driverRating.setText("Rating: "+s);
            s = inputs.remove(0);
            driverAge.setText("Age: "+s);
            s = inputs.remove(0);
            dCabColor.setText("Cab color: "+s);
            s = inputs.remove(0);
            dCabModel.setText("Cab model: "+s);
            s = inputs.remove(0);
            dCabPlate.setText("Cab number: "+s);
            cost.setBounds((int)((1160/1680.0)*screenWidth), (int)((670/1050.0)*screenHeight), (int)((420/1680.0)*screenWidth), (int)((30/1050.0)*screenHeight));
            time.setBounds((int)((1160/1680.0)*screenWidth), (int)((700/1050.0)*screenHeight), (int)((420/1680.0)*screenWidth), (int)((30/1050.0)*screenHeight));
            panel.add(cost);
            panel.add(time);
            panel.remove(cancelRide);
            panel.remove(startRide);
            panel.remove(changeCurrentLocation);
            panel.remove(changeDropLocation);
            panel.revalidate();
            panel.repaint();
        }
        if(tripStarted == false)
            addUser();
        mapImg.addMouseListener(this);
        rate1.addMouseListener(this);
        rate2.addMouseListener(this);
        rate3.addMouseListener(this);
        rate4.addMouseListener(this);
        rate5.addMouseListener(this);
        changeCurrentLocation.setActionCommand(Actions.CHANGECURRENTLOCATION.name());
        next.setActionCommand(Actions.NEXT.name());
        previous.setActionCommand(Actions.PREVIOUS.name());
        goToMainPage.setActionCommand(Actions.GOTOMAINPAGE.name());
        changeDropLocation.setActionCommand(Actions.CHANGEDROPLOCATION.name());
        cancelRide.setActionCommand(Actions.CANCELRIDE.name());
        previousTrips.setActionCommand(Actions.PREVIOUSTRIPS.name());
        startRide.setActionCommand(Actions.STARTRIDE.name());
        wallet.setActionCommand(Actions.WALLET.name());
        logout.setActionCommand(Actions.LOGOUT.name());
        wallet.addActionListener(this);
        logout.addActionListener(this);
        changeDropLocation.addActionListener(this);
        changeCurrentLocation.addActionListener(this);
        cancelRide.addActionListener(this);
        startRide.addActionListener(this);
        previousTrips.addActionListener(this);
        goToMainPage.addActionListener(this);
        next.addActionListener(this);
        previous.addActionListener(this);
        update.addActionListener(this);
    }
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand() == Actions.WALLET.name()) {
            WalletPanel wall = new WalletPanel();
        } else if(e.getActionCommand() == Actions.LOGOUT.name()) {
            Login l = new Login(frame);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            ArrayList<String> outputs = new ArrayList<>();
            outputs.add("logout");
            Talk.send(outputs);
            try {
                LoginPage.sk.close();
            }
            catch (Exception ea){
                ea.printStackTrace();
            }
        } else if(startPin != null && e.getActionCommand() == Actions.CHANGECURRENTLOCATION.name()) {
            tripStarted = true;
            countPin[0] = 0;
            mapImg.remove(startPin);
            startPin = null;

            ArrayList<String> outputs = new ArrayList<>();
            outputs.add("clear from");
            Talk.send(outputs);

            userInfo.setText("Current location removed successfully");
            cost.setText("");
            time.setText("");
            mapImg.revalidate();
            mapImg.repaint();
        } else if(dropPin != null && e.getActionCommand() == Actions.CHANGEDROPLOCATION.name()) {
            countPin[1] = 0;
            mapImg.remove(dropPin);
            dropPin = null;

            ArrayList<String> outputs = new ArrayList<>();
            outputs.add("clear to");
            Talk.send(outputs);
            cost.setText("");
            time.setText("");

            userInfo.setText("Drop location removed successfully");
            mapImg.revalidate();
            mapImg.repaint();
        } else if(startPin != null && dropPin != null && e.getActionCommand() == Actions.CANCELRIDE.name()) {
            tripStarted = true;
            countPin[0] = 0;
            countPin[1] = 0;
            mapImg.remove(startPin);
            mapImg.remove(dropPin);

            ArrayList<String> outputs = new ArrayList<>();
            outputs.add("clear from");
            Talk.send(outputs);
            outputs.clear();
            outputs.add("clear to");
            Talk.send(outputs);
            cost.setText("");
            time.setText("");

            startPin = null;
            dropPin = null;
            userInfo.setText("Trip cancelled");
            mapImg.revalidate();
            mapImg.repaint();
        } else if(startPin != null && dropPin != null && e.getActionCommand() == Actions.STARTRIDE.name()) {
            int x, y;
            ArrayList<String> outputs = new ArrayList<>();
            outputs.add("check balance");
            ArrayList<String> inputs = Talk.send(outputs);
            int amount_to_disp = Integer.parseInt(inputs.remove(0));
            if(amount_to_disp < 300) {
                WalletPanel wall = new WalletPanel();
                return;
            }

            tripStarted = true;
            panel.remove(userData);

            outputs.clear();
            outputs.add("book");
            inputs = Talk.send(outputs);
            String s = inputs.remove(0);
            if(s.equals("successful")) {
                outputs = new ArrayList<>();
                outputs.add("driver data");
                inputs = Talk.send(outputs);
                userInfo.setText("Trip is in progress");
                addDriverData();
                s = inputs.remove(0);
                driverName.setText("Name: "+s);
                s = inputs.remove(0);
                driverGender.setText("Gender: "+s);
                s = inputs.remove(0);
                driverPhone.setText("Phone: "+s);
                s = inputs.remove(0);
                driverRating.setText("Rating: "+s);
                s = inputs.remove(0);
                driverAge.setText("Age: "+s);
                s = inputs.remove(0);
                dCabColor.setText("Cab color: "+s);
                s = inputs.remove(0);
                dCabModel.setText("Cab model: "+s);
                s = inputs.remove(0);
                dCabPlate.setText("Cab number: "+s);
                outputs.clear();
                outputs.add("trip data");
                inputs = Talk.send(outputs);
                inputs.remove(0);
                inputs.remove(0);
                x = Integer.parseInt(inputs.remove(0));
                y = Integer.parseInt(inputs.remove(0));
                panel.remove(cancelRide);
                panel.remove(startRide);
                panel.remove(changeCurrentLocation);
                panel.remove(changeDropLocation);
                panel.revalidate();
                panel.repaint();

                addCab(x, y, 1);
            }
            else if(s.equals("unsuccessful")){
                userInfo.setText("No cabs available");
            }
        }
        else if(e.getActionCommand() == Actions.PREVIOUSTRIPS.name()) {
            ArrayList<String> outputs = new ArrayList<>();
            outputs.add("number of trips");
            ArrayList<String> inputs = Talk.send(outputs);
            numberOfTrips = Integer.parseInt(inputs.remove(0));
            if(numberOfTrips == 0) {
                JOptionPane.showMessageDialog(frame, "No Previous trips available", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            driverData.removeAll();

            driverData.setBackground(Color.white);
            driverData.setBounds((int)((1060/1680.0)*screenWidth),(int)((100/1050.0)*screenHeight),(int)((600/1680.0)*screenWidth), (int)((450/1050.0)*screenHeight));
            driverData.setLayout(null);
            preTrips = true;


            cost.setFont(new Font("Helvetica Neue", Font.PLAIN, (int)((30/1050.0)*screenHeight)));
            time.setFont(new Font("Helvetica Neue", Font.PLAIN, (int)((30/1050.0)*screenHeight)));



            driverName.setBounds((int)((0/1680.0)*screenWidth), (int)((120/1050.0)*screenHeight),(int)((400/1680.0)*screenWidth), (int)((50/1050.0)*screenHeight));
            cost.setBounds((int)((0/1680.0)*screenWidth), (int)((190/1050.0)*screenHeight),(int)((400/1680.0)*screenWidth), (int)((50/1050.0)*screenHeight));
            time.setBounds((int)((0/1680.0)*screenWidth), (int)((260/1050.0)*screenHeight),(int)((500/1680.0)*screenWidth), (int)((50/1050.0)*screenHeight));
            driverName.setFont(new Font("Helvetica Neue", Font.PLAIN, (int)((30/1050.0)*screenHeight)));

            goToMainPage.setText("Main page");
            goToMainPage.setOpaque(false);
            goToMainPage.setContentAreaFilled(false);
            next.setText("Next");
            previous.setText("Previous");
            next.setOpaque(false);
            next.setContentAreaFilled(false);
            previous.setOpaque(false);
            previous.setContentAreaFilled(false);
            next.setBounds((int)((1160/1680.0)*screenWidth), (int)((820/1050.0)*screenHeight), (int)((200/1680.0)*screenWidth), (int)((50/1050.0)*screenHeight));
            previous.setBounds((int)((1380/1680.0)*screenWidth), (int)((820/1050.0)*screenHeight), (int)((200/1680.0)*screenWidth), (int)((50/1050.0)*screenHeight));
            goToMainPage.setBounds((int)((1160/1680.0)*screenWidth), (int)((750/1050.0)*screenHeight), (int)((200/1680.0)*screenWidth), (int)((50/1050.0)*screenHeight));
            panel.remove(cancelRide);
            panel.remove(startRide);
            panel.remove(changeCurrentLocation);
            panel.remove(changeDropLocation);
            indexOfTrip = 0;

            driverData.add(cost);
            driverData.add(time);
            driverData.add(driverName);
            panel.add(goToMainPage);
            panel.add(next);
            panel.add(previous);
            panel.add(driverData);

            setPreviousTrips(indexOfTrip);
            panel.revalidate();
            panel.repaint();
        }
        else if(e.getActionCommand() == Actions.GOTOMAINPAGE.name()) {
            preTrips = false;
            UserUI ui = new UserUI(frame);
        }
        else if(e.getActionCommand() == Actions.NEXT.name()) {
            indexOfTrip++;
            indexOfTrip = indexOfTrip%numberOfTrips;
            setPreviousTrips(indexOfTrip);
            panel.revalidate();
            panel.repaint();
        }
        else if(e.getActionCommand() == Actions.PREVIOUS.name()) {
            if(indexOfTrip == 0) {
                indexOfTrip = numberOfTrips-1;
            }
            else {
                indexOfTrip--;
            }
            setPreviousTrips(indexOfTrip);
            panel.revalidate();
            panel.repaint();
        }
        else if(e.getActionCommand().equals("Update"))
        {
            phoneNoTxt.setForeground(Color.black);
            emailidTxt.setForeground(Color.black);
            oldPasswordTxt.setForeground(Color.black);
            newPasswordTxt.setForeground(Color.black);
            retypePasswordTxt.setForeground(Color.black);
            ArrayList<String> outputs = new ArrayList<>();
            outputs.add("change emailid");
            outputs.add(oldPasswordTxt.getText());
            outputs.add(emailidTxt.getText());
            ArrayList<String>inputs = Talk.send(outputs);
            if(inputs.get(0).equals("invalid emailid")) {
                JOptionPane.showMessageDialog(frame, "New email is not valid", "Wrong Entries", JOptionPane.ERROR_MESSAGE);
                emailidTxt.setForeground(Color.RED);
            }
            else if(inputs.get(0).equals("wrong password")) {
                JOptionPane.showMessageDialog(frame, "Your password entered doesn't match", "Wrong Entries", JOptionPane.ERROR_MESSAGE);
                oldPasswordTxt.setForeground(Color.RED);
                return;
            }
            else {
                JOptionPane.showMessageDialog(frame, "Your emailid Updated successfully", "Update successful", JOptionPane.INFORMATION_MESSAGE);
                emailidTxt.setText(email);
            }

            outputs.clear();
            outputs = new ArrayList<>();
            outputs.add("change phone number");
            outputs.add(oldPasswordTxt.getText());
            outputs.add(phoneNoTxt.getText());
            inputs = Talk.send(outputs);
            if(inputs.get(0).equals("invalid phone number")) {
                JOptionPane.showMessageDialog(frame, "Phone number entered is invalid", "Wrong Entries", JOptionPane.ERROR_MESSAGE);
                phoneNoTxt.setForeground(Color.RED);
            }
            else if(inputs.get(0).equals("wrong password")) {
                JOptionPane.showMessageDialog(frame, "Your password entered doesn't match", "Wrong Entries", JOptionPane.ERROR_MESSAGE);
                oldPasswordTxt.setForeground(Color.RED);
                return;
            }
            else {
                JOptionPane.showMessageDialog(frame, "Your phone number Updated successfully", "Update successful", JOptionPane.INFORMATION_MESSAGE);
                phoneNoTxt.setText(phno);
            }

            if(!((retypePasswordTxt.getText()).equals(newPasswordTxt.getText())) ) {
                JOptionPane.showMessageDialog(frame, "New password entered don't match", "Wrong Entries", JOptionPane.ERROR_MESSAGE);
                newPasswordTxt.setForeground(Color.RED);
                retypePasswordTxt.setForeground(Color.RED);
            }
            else if(retypePasswordTxt.getText().equals("")) {
                return;
            }
            else
            {
                outputs.clear();
                outputs = new ArrayList<>();
                outputs.add("change password");
                outputs.add(oldPasswordTxt.getText());
                outputs.add(newPasswordTxt.getText());
                if(newPasswordTxt.getText().length() < 8) {
                    JOptionPane.showMessageDialog(frame, "New Password should be of minimum 8 characters.", "Wrong credentials", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                inputs = Talk.send(outputs);
                if(inputs.remove(0).equals("successful"))
                {
                    JOptionPane.showMessageDialog(frame, "Your password Updated successfully", "Update successful", JOptionPane.INFORMATION_MESSAGE);
                    takeUpdatedInput();
                    emailidTxt.setText(email);
                    phoneNoTxt.setText(phno);
                    oldPasswordTxt.setText("");
                    oldPasswordTxt.setForeground(Color.BLACK);
                    newPasswordTxt.setText("");
                    newPasswordTxt.setForeground(Color.BLACK);
                    retypePasswordTxt.setText("");
                    retypePasswordTxt.setForeground(Color.BLACK);
                }
            }

        }
        else {
            if(startPin == null || dropPin == null) {
                JOptionPane.showMessageDialog(frame, "Please select your current location or drop location.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    public void mouseReleased(MouseEvent e) {
        if(preTrips == false) {
            int x, y;
            x = e.getX();
            y = e.getY();
            tripStarted = false;
            addPins(x, y);
        }
    }
    public void mouseExited(MouseEvent e) {
        if(isTripRated == false) {
            rate1.setBackground(Color.white);
            rate2.setBackground(Color.white);
            rate3.setBackground(Color.white);
            rate4.setBackground(Color.white);
            rate5.setBackground(Color.white);
        }
    }
    public void mouseEntered(MouseEvent e) {
        if(isTripRated == false) {
            if (e.getComponent() == rate5) {
                rate1.setBackground(Color.yellow);
                rate2.setBackground(Color.yellow);
                rate3.setBackground(Color.yellow);
                rate4.setBackground(Color.yellow);
                rate5.setBackground(Color.yellow);
            } else if (e.getComponent() == rate4) {
                rate1.setBackground(Color.yellow);
                rate2.setBackground(Color.yellow);
                rate3.setBackground(Color.yellow);
                rate4.setBackground(Color.yellow);
            } else if (e.getComponent() == rate3) {
                rate1.setBackground(Color.yellow);
                rate2.setBackground(Color.yellow);
                rate3.setBackground(Color.yellow);
            } else if (e.getComponent() == rate2) {
                rate1.setBackground(Color.yellow);
                rate2.setBackground(Color.yellow);
            } else if (e.getComponent() == rate1) {
                rate1.setBackground(Color.yellow);
            }
        }
    }
    public void mousePressed(MouseEvent e) {
        if(isTripRated == false) {
            if (e.getComponent() == rate5) {
                tripRating = 5;
            } else if (e.getComponent() == rate4) {
                tripRating = 4;
            } else if (e.getComponent() == rate3) {
                tripRating = 3;
            } else if (e.getComponent() == rate2) {
                tripRating = 2;
            } else if (e.getComponent() == rate1) {
                tripRating = 1;
            }
            if(e.getComponent() == rate1 || e.getComponent() == rate2 || e.getComponent() == rate3 || e.getComponent() == rate4 || e.getComponent() == rate5) {
                ArrayList<String> outputs = new ArrayList<>();
                outputs.add("rating");
                outputs.add(Integer.toString(tripRating));
                outputs.add(Integer.toString(tripId));
                Talk.send(outputs);
                isTripRated = true;
            }
        }
    }
    public void mouseClicked(MouseEvent e) {
    }


    JLabel createMap() {
        //creates a map for the user interaction
        JLabel mapImg = new JLabel();
        mapImg.setBounds((int)((30/1680.0)*screenWidth),(int)((130/1050.0)*screenHeight),(int)((900/1680.0)*screenWidth),(int)((740/1050.0)*screenHeight));
        ImageIcon img = getImageLabel("images/mapv2.png", mapImg);
        userInfo.setText("Please mark your current location on map");
        userInfo.setFont(new Font("Helvetica Neue", Font.PLAIN, (int)((20/1050.0)*screenHeight)));
        userInfo.setHorizontalAlignment(SwingConstants.CENTER);
        mapImg.setIcon(img);
        panel.add(mapImg);
        return mapImg;
    }

    void addPins(int x, int y) {
        //drop pins at the locations specified by the user
        if(preTrips == true) {
            if(countPin[0] == 0) {
                startPin = new JLabel();
                startPin.setBounds(x-(int)((15/1680.0)*screenWidth) , y-(int)((42/1050.0)*screenHeight), (int)((30/1680.0)*screenWidth), (int)((42/1050.0)*screenHeight));
                ImageIcon img = getImageLabel("images/pin.png", startPin);
                startPin.setIcon(img);
                mapImg.add(startPin);
                DropPin animate = new DropPin();
                animate.init(startPin, mapImg, 0);
                mapImg.revalidate();
                mapImg.repaint();
                countPin[0] = 1;
            }
            else if(countPin[1] == 0) {
                dropPin = new JLabel();
                dropPin.setBounds(x-(int)((15/1680.0)*screenWidth) , y-(int)((42/1050.0)*screenHeight), (int)((30/1680.0)*screenWidth), (int)((42/1050.0)*screenHeight));
                ImageIcon img = getImageLabel("images/drop pin.png", dropPin);
                dropPin.setIcon(img);
                mapImg.add(dropPin);
                DropPin animate = new DropPin();
                animate.init(dropPin, mapImg, 0);
                mapImg.revalidate();
                mapImg.repaint();
                countPin[1] = 1;
            }
            return;
        }
        else if(countPin[0] == 0) {
            startPin = new JLabel();
            startPin.setBounds(x , y, (int)((30/1680.0)*screenWidth), (int)((42/1050.0)*screenHeight));
            ImageIcon img = getImageLabel("images/pin.png", startPin);
            startPin.setIcon(img);
            mapImg.add(startPin);

            DropPin animate = new DropPin();
            animate.init(startPin, mapImg, 0);
            userInfo.setText("Current location marked successfully");
            mapImg.revalidate();
            mapImg.repaint();
            countPin[0] = 1;


            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<String> outputs = new ArrayList<>();
                    outputs.add("nearby cabs");
                    ArrayList<String> inputs;
                    while (tripStarted == false && preTrips == false) {
                        inputs = Talk.send(outputs);
                        while (inputs.remove(0).equals("next driver")) {
                            addCab(Integer.parseInt(inputs.remove(0)), Integer.parseInt(inputs.remove(0)), inputs.remove(0));
                        }
                        try {
                            Thread.sleep(20);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(300);
                    }
                    catch(Exception ex)
                    {
                    }
                    mapImg.revalidate();
                    mapImg.repaint();
                }
            }).start();



        }
        else if(countPin[1] == 0) {
            dropPin = new JLabel();
            dropPin.setBounds(x, y, (int) ((30 / 1680.0) * screenWidth), (int) ((42 / 1050.0) * screenHeight));
            ImageIcon img = getImageLabel("images/drop pin.png", dropPin);
            dropPin.setIcon(img);
            userInfo.setText("Drop location marked successfully");
            mapImg.add(dropPin);
            DropPin animate = new DropPin();
            animate.init(dropPin, mapImg, 1);
            mapImg.revalidate();
            mapImg.repaint();
            countPin[1] = 1;
        }
        else if(countPin[1] == 1 && countPin[0] == 1){
            JOptionPane.showMessageDialog(frame, "No more locations can be selected.\nTo change a location press the \"Change drop location\" or \"Change current location\" button.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        if(countPin[0] == 1 && countPin[1] == 1) {
            cost.setBounds((int)((1160/1680.0)*screenWidth), (int)((670/1050.0)*screenHeight), (int)((420/1680.0)*screenWidth), (int)((30/1050.0)*screenHeight));
            time.setBounds((int)((1160/1680.0)*screenWidth), (int)((700/1050.0)*screenHeight), (int)((420/1680.0)*screenWidth), (int)((30/1050.0)*screenHeight));
            ArrayList<String> outputs = new ArrayList<>();
            outputs.add("get trip estimate");
            ArrayList<String> inputs = Talk.send(outputs);
            Double n = Double.parseDouble(inputs.remove(0));
            cost.setText("Cost: "+ BigDecimal.valueOf(n).setScale(2, RoundingMode.HALF_UP).doubleValue());
            cost.setFont(new Font("Helvetica Neue", Font.PLAIN, (int)((20/1050.0)*screenHeight)));
            n = Double.parseDouble(inputs.remove(0));
            if((int)(n/60) != 0)
                time.setText("Time: "+(int)(n/60)+ " min " + BigDecimal.valueOf(n-60*(int)(n/60)).setScale(2,RoundingMode.HALF_UP).doubleValue()+" sec");
            else
                time.setText("Time: "+BigDecimal.valueOf(n).setScale(2,RoundingMode.HALF_UP).doubleValue()+" sec");
            time.setFont(new Font("Helvetica Neue", Font.PLAIN, (int)((20/1050.0)*screenHeight)));
            panel.add(cost);
            panel.add(time);
            panel.revalidate();
            panel.repaint();
        }
    }

    void addCab(int x, int y, int set) {
        //shows the cab on the map
        JLabel cab = new JLabel();
        cab.setBounds(x, y, (int)((50/1680.0)*screenWidth), (int)((33/1050.0)*screenHeight));
        mapImg.add(cab);
        CabAnimation animate = new CabAnimation();
        animate.init(cab, mapImg, set, frame);
    }
    void addCab(int x, int y, String orientation) {
        //shows the cab on the map
        ImageIcon img;
        JLabel cab = new JLabel();
        if(orientation.equals("up")) {
            x-= 16;
            y-= 25;
            cab.setBounds(x, y, (int)((33/1680.0)*screenSize.width), (int)((50/1050.0)*screenSize.height));
            img = getImageLabel("images/cab up.png", cab);
        }
        else if(orientation.equals("down")) {
            x-= 16;
            y-= 25;
            cab.setBounds(x, y, (int)((33/1680.0)*screenSize.width), (int)((50/1050.0)*screenSize.height));
            img = getImageLabel("images/cab down.png", cab);
        }
        else if(orientation.equals("right")) {
            y-= 16;
            x-= 25;
            cab.setBounds(x, y, (int)((50/1680.0)*screenSize.width), (int)((33/1050.0)*screenSize.height));
            img = getImageLabel("images/cab right.png", cab);
        }
        else {
            y-= 16;
            x-= 25;
            cab.setBounds(x, y, (int)((50/1680.0)*screenSize.width), (int)((33/1050.0)*screenSize.height));
            img = getImageLabel("images/cab left.png", cab);
        }
        cab.setIcon(img);
        mapImg.add(cab);
        CabAnimation animate = new CabAnimation();
        animate.init(cab, mapImg, 0, frame);
    }

    ImageIcon getImageLabel(String address, JLabel label) {
        //sets an image on a JLabel
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(address));
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        Image dimg = img.getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon imageIcon = new ImageIcon(dimg);
        return imageIcon;
    }
    ImageIcon getImageButton(String address, int width, int height) {
        //sets image on a JButton
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(address));
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        Image dimg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon imageIcon = new ImageIcon(dimg);
        return imageIcon;
    }

    void addDriverData() {
        //adds driver data components on the panel
        driverData.setBackground(Color.white);
        driverData.setBounds((int)((1060/1680.0)*screenWidth),(int)((100/1050.0)*screenHeight),(int)((600/1680.0)*screenWidth), (int)((550/1050.0)*screenHeight));
        driverData.setLayout(null);
        JLabel driverInfo = new JLabel();
        driverAge = new JLabel();
        driverGender = new JLabel();
        driverName = new JLabel();
        driverPhone = new JLabel();
        driverRating = new JLabel();
        dCabColor = new JLabel();
        dCabModel = new JLabel();
        dCabPlate = new JLabel();

        driverInfo.setBounds(0,0,(int)((600/1680.0)*screenWidth), (int)((100/1050.0)*screenHeight));

        driverAge.setBounds((int)((0/1680.0)*screenWidth), (int)((260/1050.0)*screenHeight),(int)((120/1680.0)*screenWidth), (int)((50/1050.0)*screenHeight));
        driverAge.setFont(new Font("Helvetica Neue", Font.PLAIN, (int)((30/1050.0)*screenHeight)));
        driverRating.setBounds((int)((290/1680.0)*screenWidth), (int)((260/1050.0)*screenHeight),(int)((200/1680.0)*screenWidth), (int)((50/1050.0)*screenHeight));
        driverRating.setFont(new Font("Helvetica Neue", Font.PLAIN, (int)((30/1050.0)*screenHeight)));
        driverPhone.setBounds((int)((0/1680.0)*screenWidth), (int)((190/1050.0)*screenHeight),(int)((400/1680.0)*screenWidth), (int)((50/1050.0)*screenHeight));
        driverPhone.setFont(new Font("Helvetica Neue", Font.PLAIN, (int)((30/1050.0)*screenHeight)));
        driverName.setBounds((int)((0/1680.0)*screenWidth), (int)((120/1050.0)*screenHeight),(int)((400/1680.0)*screenWidth), (int)((50/1050.0)*screenHeight));
        driverName.setFont(new Font("Helvetica Neue", Font.PLAIN, (int)((30/1050.0)*screenHeight)));
        driverGender.setBounds((int)((120/1680.0)*screenWidth), (int)((260/1050.0)*screenHeight),(int)((150/1680.0)*screenWidth), (int)((50/1050.0)*screenHeight));
        driverGender.setFont(new Font("Helvetica Neue", Font.PLAIN, (int)((30/1050.0)*screenHeight)));
        dCabPlate.setBounds((int)((0/1680.0)*screenWidth), (int)((330/1050.0)*screenHeight),(int)((400/1680.0)*screenWidth), (int)((50/1050.0)*screenHeight));
        dCabPlate.setFont(new Font("Helvetica Neue", Font.PLAIN, (int)((30/1050.0)*screenHeight)));
        dCabModel.setBounds((int)((0/1680.0)*screenWidth), (int)((400/1050.0)*screenHeight),(int)((400/1680.0)*screenWidth), (int)((50/1050.0)*screenHeight));
        dCabModel.setFont(new Font("Helvetica Neue", Font.PLAIN, (int)((30/1050.0)*screenHeight)));
        dCabColor.setBounds((int)((0/1680.0)*screenWidth), (int)((470/1050.0)*screenHeight),(int)((400/1680.0)*screenWidth), (int)((50/1050.0)*screenHeight));
        dCabColor.setFont(new Font("Helvetica Neue", Font.PLAIN, (int)((30/1050.0)*screenHeight)));

        driverData.add(driverName);
        driverData.add(driverAge);
        driverData.add(driverGender);
        driverData.add(driverPhone);
        driverData.add(driverRating);
        driverData.add(dCabColor);
        driverData.add(dCabModel);
        driverData.add(dCabPlate);

        panel.add(driverData);
    }


    void setPreviousTrips(int indexOfTrip) {
        rate1.setBackground(Color.white);
        rate2.setBackground(Color.white);
        rate3.setBackground(Color.white);
        rate4.setBackground(Color.white);
        rate5.setBackground(Color.white);

        countPin[0] = 0;
        countPin[1] = 0;

        preTrips = true;
        driverName.setBounds((int)((0/1680.0)*screenWidth), (int)((120/1050.0)*screenHeight),(int)((400/1680.0)*screenWidth), (int)((50/1050.0)*screenHeight));

        panel.remove(mapImg);
        mapImg = createMap();
        userInfo.setText("");
        countPin[0] = 0;
        countPin[1] = 0;
        ArrayList<String> outputs = new ArrayList<>();
        outputs.add("number of trips");
        ArrayList<String> inputs = Talk.send(outputs);
        numberOfTrips = Integer.parseInt(inputs.remove(0));
        outputs.clear();
        outputs.add("old trips");
        outputs.add(Integer.toString(indexOfTrip));
        inputs = Talk.send(outputs);
        String s;


        addPins(Integer.parseInt(inputs.remove(0)), Integer.parseInt(inputs.remove(0)));
        addPins(Integer.parseInt(inputs.remove(0)), Integer.parseInt(inputs.remove(0)));
        tripId = Integer.parseInt(inputs.remove(0));
        s = inputs.remove(0);
        driverName.setText("Name: "+s);
        Double n = Double.parseDouble(inputs.remove(0));
        cost.setText("Cost: "+ BigDecimal.valueOf(n).setScale(2, RoundingMode.HALF_UP).doubleValue());
        time.setText("Time: " + inputs.remove(0));
        isTripRated = Boolean.parseBoolean(inputs.remove(0));
        if(isTripRated == true) {
            tripRating = Integer.parseInt(inputs.remove(0));
            if(tripRating == 5) {
                rate1.setBackground(Color.yellow);
                rate2.setBackground(Color.yellow);
                rate3.setBackground(Color.yellow);
                rate4.setBackground(Color.yellow);
                rate5.setBackground(Color.yellow);
            }
            else if(tripRating == 4) {
                rate1.setBackground(Color.yellow);
                rate2.setBackground(Color.yellow);
                rate3.setBackground(Color.yellow);
                rate4.setBackground(Color.yellow);
            }
            else if(tripRating == 3) {
                rate1.setBackground(Color.yellow);
                rate2.setBackground(Color.yellow);
                rate3.setBackground(Color.yellow);
            }
            else if(tripRating == 2) {
                rate1.setBackground(Color.yellow);
                rate2.setBackground(Color.yellow);
            }
            else if(tripRating == 1) {
                rate1.setBackground(Color.yellow);
            }
            driverRating.setText("Rate: ");
            driverRating.setFont(new Font("Helvetica Neue", Font.PLAIN, 30));
            driverRating.setBounds((int)((1050/1680.0)*screenWidth),(int)((600/1050.0)*screenHeight),(int)((100/1680.0)*screenWidth),(int)((50/1050.0)*screenHeight));
            rate1.setBounds((int)((1160/1680.0)*screenWidth),(int)((600/1050.0)*screenHeight),(int)((50/1680.0)*screenWidth),(int)((50/1050.0)*screenHeight));
            rate2.setBounds((int)((1220/1680.0)*screenWidth),(int)((600/1050.0)*screenHeight),(int)((50/1680.0)*screenWidth),(int)((50/1050.0)*screenHeight));
            rate3.setBounds((int)((1280/1680.0)*screenWidth),(int)((600/1050.0)*screenHeight),(int)((50/1680.0)*screenWidth),(int)((50/1050.0)*screenHeight));
            rate4.setBounds((int)((1340/1680.0)*screenWidth),(int)((600/1050.0)*screenHeight),(int)((50/1680.0)*screenWidth),(int)((50/1050.0)*screenHeight));
            rate5.setBounds((int)((1400/1680.0)*screenWidth),(int)((600/1050.0)*screenHeight),(int)((50/1680.0)*screenWidth),(int)((50/1050.0)*screenHeight));

            panel.add(driverRating);
            panel.add(rate1);
            panel.add(rate2);
            panel.add(rate3);
            panel.add(rate4);
            panel.add(rate5);
        }
        else {

            driverRating.setText("Rate: ");
            driverRating.setFont(new Font("Helvetica Neue", Font.PLAIN, 30));
            driverRating.setBounds((int)((1050/1680.0)*screenWidth),(int)((600/1050.0)*screenHeight),(int)((100/1680.0)*screenWidth),(int)((50/1050.0)*screenHeight));
            rate1.setBounds((int)((1160/1680.0)*screenWidth),(int)((600/1050.0)*screenHeight),(int)((50/1680.0)*screenWidth),(int)((50/1050.0)*screenHeight));
            rate2.setBounds((int)((1220/1680.0)*screenWidth),(int)((600/1050.0)*screenHeight),(int)((50/1680.0)*screenWidth),(int)((50/1050.0)*screenHeight));
            rate3.setBounds((int)((1280/1680.0)*screenWidth),(int)((600/1050.0)*screenHeight),(int)((50/1680.0)*screenWidth),(int)((50/1050.0)*screenHeight));
            rate4.setBounds((int)((1340/1680.0)*screenWidth),(int)((600/1050.0)*screenHeight),(int)((50/1680.0)*screenWidth),(int)((50/1050.0)*screenHeight));
            rate5.setBounds((int)((1400/1680.0)*screenWidth),(int)((600/1050.0)*screenHeight),(int)((50/1680.0)*screenWidth),(int)((50/1050.0)*screenHeight));

            panel.add(driverRating);
            panel.add(rate1);
            panel.add(rate2);
            panel.add(rate3);
            panel.add(rate4);
            panel.add(rate5);
        }
        panel.remove(userData);
        outputs.clear();
        driverData.revalidate();
        driverData.repaint();
    }

    void takeUpdatedInput()
    {
        ArrayList<String> outputs = new ArrayList<>();
        ArrayList<String> inputs;


        outputs = new ArrayList<>();
        outputs.add("get emailid");
        inputs = Talk.send(outputs);
        email = inputs.remove(0);


        outputs = new ArrayList<>();
        outputs.add("get phone number");
        inputs = Talk.send(outputs);
        phno = inputs.remove(0);
    }

    void addUser() {
        //adds user data components on the panel

        userData.setBackground(Color.white);
        userData.setBounds((int)((1080/1680.0)*screenWidth),(int)((130/1050.0)*screenHeight),(int)((550/1680.0)*screenWidth), (int)((400/1050.0)*screenHeight));
        userData.setLayout(null);
        update.setText("Update");
        update.setBounds((int)((160/1680.0)*screenWidth),(int)((330/1050.0)*screenHeight),(int)((200/1680.0)*screenWidth),(int)((50/1050.0)*screenHeight));
        update.setOpaque(false);
        update.setContentAreaFilled(false);

        newPassword = new JLabel();
        newPassword.setText("New Password : ");
        newPassword.setFont(new Font("Helvetica Neue", Font.PLAIN, 25));
        newPassword.setBounds((int)((15/1680.0)*screenWidth), (int)((210/1050.0)*screenHeight),(int)((200/1680.0)*screenWidth),(int)((50/1050.0)*screenHeight));
        newPassword.setForeground(Color.BLACK);


        oldPassword = new JLabel();
        oldPassword.setText("Old Password : ");
        oldPassword.setFont(new Font("Helvetica Neue", Font.PLAIN, 25));
        oldPassword.setBounds((int)((15/1680.0)*screenWidth), (int)((150/1050.0)*screenHeight),(int)((200/1680.0)*screenWidth),(int)((50/1050.0)*screenHeight));
        oldPassword.setForeground(Color.BLACK);

        emailid = new JLabel();
        emailid.setText("Email id :");
        emailid.setFont(new Font("Helvetica Neue", Font.PLAIN, 25));
        emailid.setBounds((int)((15/1680.0)*screenWidth), (int)((30/1050.0)*screenHeight),(int)((200/1680.0)*screenWidth),(int)((50/1050.0)*screenHeight));
        emailid.setForeground(Color.BLACK);

        phoneNo = new JLabel();
        phoneNo.setText("Phone :");
        phoneNo.setFont(new Font("Helvetica Neue", Font.PLAIN, 25));
        phoneNo.setForeground(Color.WHITE);
        phoneNo.setBounds((int)((15/1680.0)*screenWidth), (int)((90/1050.0)*screenHeight),(int)((200/1680.0)*screenWidth),(int)((50/1050.0)*screenHeight));
        phoneNo.setForeground(Color.BLACK);

        retypePassword = new JLabel();
        retypePassword.setText("Retype password :");
        retypePassword.setFont(new Font("Helvetica Neue", Font.PLAIN, 25));
        retypePassword.setBounds((int)((15/1680.0)*screenWidth), (int)((270/1050.0)*screenHeight),(int)((230/1680.0)*screenWidth),(int)((50/1050.0)*screenHeight));
        retypePassword.setForeground(Color.BLACK);

        oldPasswordTxt = new JPasswordField();
        oldPasswordTxt.setFont(new Font("Helvetica Neue", Font.PLAIN, 25));
        oldPasswordTxt.setBounds((int)((245/1680.0)*screenWidth), (int)((150/1050.0)*screenHeight),(int)((250/1680.0)*screenWidth),(int)((40/1050.0)*screenHeight));

        emailidTxt = new JTextField(30);
        emailidTxt.setFont(new Font("Helvetica Neue", Font.PLAIN, 25));
        emailidTxt.setText(email);
        emailidTxt.setBounds((int)((245/1680.0)*screenWidth), (int)((30/1050.0)*screenHeight),(int)((250/1680.0)*screenWidth),(int)((40/1050.0)*screenHeight));

        phoneNoTxt = new JTextField(30);
        phoneNoTxt.setFont(new Font("Helvetica Neue", Font.PLAIN, 25));
        phoneNoTxt.setText(phno);
        phoneNoTxt.setBounds((int)((245/1680.0)*screenWidth), (int)((90/1050.0)*screenHeight),(int)((250/1680.0)*screenWidth),(int)((40/1050.0)*screenHeight));

        newPasswordTxt = new JPasswordField();
        newPasswordTxt.setFont(new Font("Helvetica Neue", Font.PLAIN, 25));
        newPasswordTxt.setBounds((int)((245/1680.0)*screenWidth),(int)((210/1050.0)*screenHeight),(int)((250/1680.0)*screenWidth),(int)((40/1050.0)*screenHeight));

        retypePasswordTxt = new JPasswordField();
        retypePasswordTxt.setFont(new Font("Helvetica Neue", Font.PLAIN, 25));
        retypePasswordTxt.setBounds((int)((245/1680.0)*screenWidth),(int)((270/1050.0)*screenHeight),(int)((250/1680.0)*screenWidth),(int)((40/1050.0)*screenHeight));

        userData.add(emailid);
        userData.add(phoneNo);
        userData.add(phoneNoTxt);
        userData.add(newPassword);
        userData.add(oldPassword);
        userData.add(oldPasswordTxt);
        userData.add(retypePassword);
        userData.add(retypePasswordTxt);
        userData.add(newPasswordTxt);
        userData.add(emailidTxt);
        userData.add(update);

        panel.add(userData);
        panel.revalidate();
        panel.repaint();
    }

}
