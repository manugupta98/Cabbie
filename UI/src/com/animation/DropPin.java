package com.animation;

import com.main.LoginPage;
import com.mainPage.UserUI;
import com.talk.Talk;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class DropPin implements Runnable{
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private int x, y, q, set;
    JLabel pin, mapImg;
    Thread t;

    public void init(JLabel pin, JLabel mapImg, int set) {
        this.pin = pin;
        this.set = set;
        this.mapImg = mapImg;
        x = pin.getX();
        q = pin.getY();

        if(UserUI.preTrips == false) {
            x = (int)(((double)x/screenSize.width)*1680.0);
            q = (int)(((double)q/screenSize.height)*1050.0);

            ArrayList<String> outputs = new ArrayList<>();
            if (set == 1) {
                outputs.add("to");
            } else if (set == 0) {
                outputs.add("from");
            }
            System.out.println(x + " " + q);
            outputs.add(Integer.toString(x));
            outputs.add(Integer.toString(q));
            ArrayList<String> inputs = Talk.send(outputs);
            x = Integer.parseInt(inputs.remove(0));
            y = Integer.parseInt(inputs.remove(0));
            x = (int)((x/1680.0)*screenSize.width);
            y = (int)((y/1050.0)*screenSize.height);
            q = y;
            y = y-20;
        }
        else {
            x = (int) ((x / 1680.0) * screenSize.width);
            q = (int) ((q / 1050.0) * screenSize.height);
            y = q;
            y = y-20;
        }

        t = new Thread(this, "MyThread");
        t.start();
    }

    public void update() {
        y = y + 1;
        if(UserUI.preTrips == false)
            pin.setLocation(x-(int)((15/1680.0)*screenSize.width), y-(int)((42/1050.0)*screenSize.height));
        else
            pin.setLocation(x,y);
    }

    public void run() {
        while(y != q) {
            mapImg.revalidate();
            mapImg.repaint();
            update();
            try {
                Thread.sleep(10);
            }
            catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
