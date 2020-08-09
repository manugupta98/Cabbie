package com.wallet;
import com.main.LoginPage;
import com.talk.Talk;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;

public class WalletPanel
{
    public WalletPanel(){
        try{
            JFrame frame =new JFrame();
            frame.setSize(500,300);
            frame.setLocationRelativeTo(null);
            DisplayMoney dm = new DisplayMoney(frame);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
}
class DisplayMoney implements ActionListener
{
    String amount_to_disp;
    JButton addMoney;
    JFrame frame;
    JPanel panel;
    JLabel amount_disp,amount,insuff_bal;
    DisplayMoney(JFrame frame) {
        this.frame = frame;

        amount_disp = new JLabel();
        amount_disp.setText("Amount:");
        amount_disp.setFont(new Font("Helvetica Neue", Font.PLAIN, 20));
        amount_disp.setForeground(Color.WHITE);

        ArrayList<String> outputs = new ArrayList<>();
        outputs.add("check balance");
        ArrayList<String> inputs = Talk.send(outputs);
        amount_to_disp = inputs.remove(0);

        amount = new JLabel();
        amount.setText(amount_to_disp);
        amount.setFont(new Font("Helvetica Neue", Font.PLAIN, 20));
        amount.setForeground(Color.WHITE);

        insuff_bal = new JLabel();
        if (Integer.parseInt(amount.getText()) < 300)
            insuff_bal.setText("Insufficient Balance to take any ride !! PLEASE ADD MONEY");
        insuff_bal.setFont(new Font("Helvetica Neue", Font.BOLD, 14));
        insuff_bal.setForeground(Color.RED);

        addMoney = new JButton("ADD MONEY");
        addMoney.setFont(new Font("Helvetica Neue", Font.PLAIN, 15));

        panel = new JPanel(null);
        panel.setBackground(new Color(0, 35, 102));
        amount_disp.setBounds(175, 25, 200, 130);
        amount.setBounds(280, 25, 350, 130);
        addMoney.setBounds(175, 130, 150, 40);
        insuff_bal.setBounds(35, 210, 450, 20);

        panel.add(amount_disp);
        panel.add(amount);
        panel.add(addMoney);
        panel.add(insuff_bal);

        frame.setTitle("WALLET MONEY");
        frame.add(panel, BorderLayout.CENTER);
        frame.setContentPane(panel);
        frame.setVisible(true);
        addMoney.addActionListener(this);

    }
    public void actionPerformed(ActionEvent ae){
        Addmoney am = new Addmoney(frame);
    }
}
class Addmoney implements ActionListener {
    JButton add, Cancel;
    JFrame frame;
    JPanel panel;
    JFormattedTextField cardNotxt1,cardNotxt2,cardNotxt3,cardNotxt4;
    JTextField amounttxt;
    JLabel cardNo, amount;

    Addmoney(JFrame frame) {
        this.frame = frame;
        frame.setTitle("Add Money To Wallet");

        amount = new JLabel();
        amount.setText("Amount:");
        amount.setFont(new Font("Helvetica Neue", Font.PLAIN, 20));
        amount.setForeground(Color.WHITE);

        cardNo = new JLabel();
        cardNo.setText("Card Number : ");
        cardNo.setFont(new Font("Helvetica Neue", Font.PLAIN, 20));
        cardNo.setForeground(Color.WHITE);
        try {
            cardNotxt1 = new JFormattedTextField(new MaskFormatter("####"));        //used mask formatter to make sure
            cardNotxt1.setToolTipText("enter 16 digit of your Card Number");        //only positive integers are given as inputs in
            cardNotxt1.setFont(new Font("Helvetica Neue", Font.PLAIN, 17));  // card number entry
            cardNotxt1.setColumns(4);

            cardNotxt2 = new JFormattedTextField(new MaskFormatter("####"));
            cardNotxt2.setToolTipText("enter 16 digit of your Card Number");
            cardNotxt2.setFont(new Font("Helvetica Neue", Font.PLAIN, 17));

            cardNotxt3 = new JFormattedTextField(new MaskFormatter("####"));
            cardNotxt3.setToolTipText("enter 16 digit of your Card Number");
            cardNotxt3.setFont(new Font("Helvetica Neue", Font.PLAIN, 17));

            cardNotxt4 = new JFormattedTextField(new MaskFormatter("####"));
            cardNotxt4.setToolTipText("enter 16 digit of your Card Number");
            cardNotxt4.setFont(new Font("Helvetica Neue", Font.PLAIN, 17));

            amounttxt = new JTextField();
            amounttxt.setToolTipText("Enter amount to enter");
            amounttxt.setFont(new Font("Helvetica Neue", Font.PLAIN, 12));

        }
        catch (ParseException pe)
        {
            pe.printStackTrace();
        }

        add = new JButton("ADD");
        add.setFont(new Font("Helvetica Neue", Font.PLAIN, 15));

        Cancel = new JButton("CANCEL");
        Cancel.setFont(new Font("Helvetica Neue", Font.PLAIN, 15));

        panel = new JPanel(null);
        panel.setBackground(new Color(0,35,102));

        cardNo.setBounds(80, 40, 150, 30);
        amount.setBounds(80, 80, 300, 30);
        cardNotxt1.setBounds(240, 40, 50, 30);
        cardNotxt2.setBounds(300, 40, 50, 30);
        cardNotxt3.setBounds(360, 40, 50, 30);
        cardNotxt4.setBounds(420, 40, 50, 30);
        amounttxt.setBounds(240, 80, 230, 30);
        add.setBounds(90, 160, 150, 30);
        Cancel.setBounds(280, 160, 150, 30);


        panel.add(cardNo);
        panel.add(amount);
        panel.add(cardNotxt1);
        panel.add(cardNotxt2);
        panel.add(cardNotxt3);
        panel.add(cardNotxt4);
        panel.add(amounttxt);
        panel.add(add);
        panel.add(Cancel);

        frame.add(panel);
        frame.setContentPane(panel);
        frame.setVisible(true);

        add.addActionListener(this);
        Cancel.addActionListener(this);
    }


    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("ADD")) {
            if ((cardNotxt1.getValue()) == null || (cardNotxt2.getValue()) == null || (cardNotxt3.getValue()) == null || (cardNotxt4.getValue()) == null) {
                JOptionPane.showMessageDialog(frame, "Enter Card Number Correctly", "Wrong Credentials", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    if(Integer.parseInt(amounttxt.getText()) < 0 || Integer.parseInt(amounttxt.getText()) > 100000) {
                        JOptionPane.showMessageDialog(frame, "Amount should be positive integers and should be less than 10000" , "Wrong Credentials", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                catch(Exception e) {
                    JOptionPane.showMessageDialog(frame, "Amount should be positive integers and should be less than 10000", "Wrong Credentials", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                ArrayList<String> outputs = new ArrayList<>();
                outputs.add("add money");
                outputs.add(amounttxt.getText());
                Talk.send(outputs);
                JOptionPane.showMessageDialog(frame, "Money added succesfully", "Succesful Transaction", JOptionPane.INFORMATION_MESSAGE);
                DisplayMoney dm1 = new DisplayMoney(frame);
            }
        }
        else {
            DisplayMoney dm1 = new DisplayMoney(frame);
        }
    }
}
