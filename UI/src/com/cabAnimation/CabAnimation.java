package com.cabAnimation;
import com.main.LoginPage;
import com.mainPage.UserUI;
import com.talk.Talk;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class CabAnimation implements Runnable {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private int x, y, set;
    JLabel cab, mapImg;
    JFrame frame;
    Thread t;
    ImageIcon img;

    public void init(JLabel cab, JLabel mapImg, int set, JFrame frame) {
        this.cab = cab;
        this.frame = frame;
        this.set = set;
        this.mapImg = mapImg;
        x = cab.getX();
        y = cab.getY();
        t = new Thread(this, "MyThread");
        t.start();
    }

    public void run() {
        int xTemp, yTemp, flag = 0;
        String s;
        if(set == 1) {
            ArrayList<String> outputs = new ArrayList<>();
            outputs.add("trip data");
            while(flag == 0) {
                ArrayList<String> inputs = Talk.send(outputs);
                s = inputs.remove(0);
                if (s.equals("finished")||s.equals("does not exist")) {
                    flag = 1;
                    UserUI.tripStarted = false;
                } else if (s.equals("started") || s.equals("not started")) {
                    xTemp = Integer. parseInt(inputs.get(1));
                    yTemp = Integer.parseInt(inputs.get(2));
                    xTemp = (int)((xTemp/1680.0)*screenSize.width);
                    yTemp = (int)((yTemp/1050.0)*screenSize.height);
                    if(inputs.get(0).equals("up")) {
                        xTemp-= (int)((16/1680.0)*screenSize.width);
                        yTemp-= (int)((25/1050.0)*screenSize.height);
                        cab.setBounds(xTemp, yTemp, (int)((33/1680.0)*screenSize.width), (int)((50/1050.0)*screenSize.height));
                        img = getImageLabel("images/cab up.png", cab);
                    }
                    else if(inputs.get(0).equals("down")) {
                        xTemp-= (int)((16/1680.0)*screenSize.width);
                        yTemp-= (int)((25/1050.0)*screenSize.height);
                        cab.setBounds(xTemp, yTemp, (int)((33/1680.0)*screenSize.width), (int)((50/1050.0)*screenSize.height));
                        img = getImageLabel("images/cab down.png", cab);
                    }
                    else if(inputs.get(0).equals("right")) {
                        yTemp-= (int)((16/1050.0)*screenSize.height);
                        xTemp-= (int)((25/1680.0)*screenSize.width);
                        cab.setBounds(xTemp, yTemp, (int)((50/1680.0)*screenSize.width), (int)((33/1050.0)*screenSize.height));
                        img = getImageLabel("images/cab right.png", cab);
                    }
                    else if(inputs.get(0).equals("left")) {
                        yTemp-= (int)((16/1050.0)*screenSize.height);
                        xTemp-= (int)((25/1680.0)*screenSize.width);
                        cab.setBounds(xTemp, yTemp, (int)((50/1680.0)*screenSize.width), (int)((33/1050.0)*screenSize.height));
                        img = getImageLabel("images/cab left.png", cab);
                    }
                    inputs.remove(0);
                    cab.setIcon(img);

                        cab.setLocation(xTemp, yTemp);
                        mapImg.revalidate();
                        mapImg.repaint();
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            if(UserUI.preTrips == false) {
                UserUI ui = new UserUI(frame);
            }
        }
        else if(set == 0) {


            cab.setLocation((int)((x/1680.0)*screenSize.width), (int)((y/1050.0)*screenSize.height));
            mapImg.revalidate();
            mapImg.repaint();
            try {
                Thread.sleep(200);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            mapImg.remove(cab);
        }
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
}
