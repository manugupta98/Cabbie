package com.signUp;
import com.mainPage.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

import com.main.*;
import com.talk.*;

public class Login implements ActionListener
{
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int screen_widht = screenSize.width;
    int screen_height = screenSize.height;
    public static JFrame frame;
    JButton submit,createOne;
    public JPanel panel;
    JLabel username,paaswd,imgLabel,signup_msg, cab;
    final JTextField  usernametxt,passwdtxt;
    public Login(JFrame frame)
    {
        this.frame = frame;
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        cab = new JLabel();
        cab.setBounds((int)((20/1536.0)*screen_widht),(int)((20/864.0)*screen_height),screen_widht/2 - (int)((20/1536.0)*screen_widht),screen_height - (int)((110/864.0)*screen_height));
        ImageIcon img = getImageLabel("images/taxi.jpg", cab);
        cab.setIcon(img);

        username = new JLabel();
        username.setText("User Id");
        username.setFont(new Font("Helvetica Neue", Font.PLAIN, (int)((20/864.0)*screen_height)));
        username.setForeground(Color.WHITE);

        usernametxt = new JTextField(30);
        usernametxt.setFont(new Font("Helvetica Neue", Font.PLAIN, (int)((20/864.0)*screen_height)));

        paaswd = new JLabel();
        paaswd.setText("Password");
        paaswd.setFont(new Font("Helvetica Neue", Font.PLAIN, (int)((20/864.0)*screen_height)));
        paaswd.setForeground(Color.WHITE);

        passwdtxt = new JPasswordField(30);
        passwdtxt.setFont(new Font("Helvetica Neue", Font.PLAIN, (int)((20/864.0)*screen_height)));


        signup_msg = new JLabel();
        signup_msg.setText("Don't have a account yet ?");
        signup_msg.setFont(new Font("Helvetica Neue", Font.PLAIN, (int)((16/864.0)*screen_height)));
        signup_msg.setForeground(Color.WHITE);

        createOne = new JButton("Create One");
        createOne.setFont(new Font("Helvetica Neue", Font.PLAIN, (int)((16/864.0)*screen_height)));
        createOne.setForeground(Color.YELLOW);
        createOne.setOpaque(false);
        createOne.setContentAreaFilled(false);
        createOne.setBorderPainted(false);

        submit = new JButton("SUBMIT");

        panel = new JPanel(null);
        panel.setBackground(new Color(0,35,102));


        username.setBounds((int)((1090/1536.0)*screen_widht),(int)((170/864.0)*screen_height),(int)((100/1536.0)*screen_widht),(int)((30/864.0)*screen_height));
        paaswd.setBounds((int)((1090/1536.0)*screen_widht),(int)((300/864.0)*screen_height),(int)((100/1536.0)*screen_widht),(int)((30/864.0)*screen_height));
        usernametxt.setBounds((int)((940/1536.0)*screen_widht),(int)((220/864.0)*screen_height),(int)((400/1536.0)*screen_widht),(int)((50/864.0)*screen_height));
        passwdtxt.setBounds((int)((940/1536.0)*screen_widht),(int)((350/864.0)*screen_height),(int)((400/1536.0)*screen_widht),(int)((50/864.0)*screen_height));
        signup_msg.setBounds((int)((940/1536.0)*screen_widht),(int)((470/864.0)*screen_height),(int)((400/1536.0)*screen_widht),(int)((30/864.0)*screen_height));
        submit.setBounds((int)((1090/1536.0)*screen_widht),(int)((420/864.0)*screen_height),(int)((100/1536.0)*screen_widht),(int)((40/864.0)*screen_height));
        createOne.setBounds((int)((1120/1536.0)*screen_widht),(int)((470/864.0)*screen_height),(int)((150/1536.0)*screen_widht),(int)((30/864.0)*screen_height));

        panel.add(username);
        panel.add(usernametxt);
        panel.add(paaswd);
        panel.add(passwdtxt);
        panel.add(submit);
        panel.add(createOne);
        panel.add(cab);
        panel.add(signup_msg);

        frame.add(panel,BorderLayout.CENTER);
        frame.setTitle("LOGIN FORM");
        frame.setContentPane(panel);

        submit.addActionListener(this);
        createOne.addActionListener(this);

    }
    public void actionPerformed(ActionEvent ae)
    {
        if(ae.getActionCommand().equals("SUBMIT")) {
            try {
                ArrayList<String> outputs = new ArrayList<>();
                LoginPage.sk = new Socket("192.168.43.208",8001);
                outputs.add("login");
                String value1 = usernametxt.getText();
                outputs.add(value1);
                String value2 = passwdtxt.getText();
                outputs.add(value2);
                ArrayList<String> inputs = Talk.send(LoginPage.sk, outputs);
                String s = inputs.remove(0);
                if(s.equals("successful")) {
                    UserUI ui = new UserUI(frame);
                } else {
                    JOptionPane.showMessageDialog(frame, "Incorrect login or password", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            SignUp sign = new SignUp(frame);
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

class SignUp implements ActionListener {

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int screen_widht = screenSize.width;
    int screen_height = screenSize.height;

    JFrame frame;
    JButton submit, cancel;
    JPanel panel;
    JLabel userid, passwd,name,emailid,phoneno,imgLabel,mandate,cab;
    JTextField  usertxt, passtxt,nametxt,phonetxt,emailidtxt;

    public SignUp(JFrame frame) {
        this.frame = frame;
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.setTitle("SIGNUP FORM");

        cab = new JLabel();
        cab.setBounds((int)((20/1536.0)*screen_widht),(int)((20/864.0)*screen_height),screen_widht/2 - (int)((20/1536.0)*screen_widht),screen_height - (int)((110/864.0)*screen_height));
        ImageIcon img = getImageLabel("images/taxi.jpg", cab);
        cab.setIcon(img);

        userid = new JLabel();
        userid.setText("User Id:*");
        userid.setFont(new Font("Helvetica Neue", Font.PLAIN, (int)((25/864.0)*screen_height)));
        userid.setForeground(Color.WHITE);

        usertxt = new JTextField(30);
        usertxt.setFont(new Font("Helvetica Neue", Font.PLAIN, (int)((25/864.0)*screen_height)));

        passwd = new JLabel();
        passwd.setText("Password:*");
        passwd.setFont(new Font("Helvetica Neue", Font.PLAIN, (int)((25/864.0)*screen_height)));
        passwd.setForeground(Color.WHITE);

        passtxt = new JPasswordField(30);
        passtxt.setFont(new Font("Helvetica Neue", Font.PLAIN, (int)((25/864.0)*screen_height)));

        name = new JLabel();
        name.setText("Name:*");
        name.setFont(new Font("Helvetica Neue", Font.PLAIN, (int)((25/864.0)*screen_height)));
        name.setForeground(Color.WHITE);

        nametxt = new JTextField(30);
        nametxt.setFont(new Font("Helvetica Neue", Font.PLAIN, (int)((25/864.0)*screen_height)));

        emailid = new JLabel();
        emailid.setText("EmailId:*");
        emailid.setFont(new Font("Helvetica Neue", Font.PLAIN, (int)((25/864.0)*screen_height)));
        emailid.setForeground(Color.WHITE);

        emailidtxt = new JTextField(30);
        emailidtxt.setFont(new Font("Helvetica Neue", Font.PLAIN, (int)((25/864.0)*screen_height)));

        phoneno = new JLabel();
        phoneno.setText("Phone No:*");
        phoneno.setFont(new Font("Helvetica Neue", Font.PLAIN, (int)((25/864.0)*screen_height)));
        phoneno.setForeground(Color.WHITE);

        phonetxt = new JTextField(30);
        phonetxt.setFont(new Font("Helvetica Neue", Font.PLAIN, (int)((25/864.0)*screen_height)));


        mandate = new JLabel();
        mandate.setText("* marked fields are mandatory");
        mandate.setFont(new Font("Helvetica Neue", Font.PLAIN, (int)((18/864.0)*screen_height)));
        mandate.setForeground(Color.YELLOW);

        submit = new JButton("SUBMIT");
        cancel = new JButton("CANCEL");

        panel = new JPanel(null);
        panel.setBackground(new Color(0,35,102));

        name.setBounds((int)((950/1536.0)*screen_widht),(int)((250/864.0)*screen_height),(int)((150/1536.0)*screen_widht),(int)((50/864.0)*screen_height));
        emailid.setBounds((int)((950/1536.0)*screen_widht),(int)((320/864.0)*screen_height),(int)((150/1536.0)*screen_widht),(int)((50/864.0)*screen_height));
        phoneno.setBounds((int)((950/1536.0)*screen_widht),(int)((390/864.0)*screen_height),(int)((150/1536.0)*screen_widht),(int)((50/864.0)*screen_height));
        userid.setBounds((int)((950/1536.0)*screen_widht),(int)((460/864.0)*screen_height),(int)((150/1536.0)*screen_widht),(int)((50/864.0)*screen_height));
        passwd.setBounds((int)((950/1536.0)*screen_widht),(int)((530/864.0)*screen_height),(int)((150/1536.0)*screen_widht),(int)((50/864.0)*screen_height));
        nametxt.setBounds((int)((1100/1536.0)*screen_widht),(int)((250/864.0)*screen_height),(int)((350/1536.0)*screen_widht),(int)((50/864.0)*screen_height));
        emailidtxt.setBounds((int)((1100/1536.0)*screen_widht),(int)((320/864.0)*screen_height),(int)((350/1536.0)*screen_widht),(int)((50/864.0)*screen_height));
        phonetxt.setBounds((int)((1100/1536.0)*screen_widht),(int)((390/864.0)*screen_height),(int)((350/1536.0)*screen_widht),(int)((50/864.0)*screen_height));
        usertxt.setBounds((int)((1100/1536.0)*screen_widht),(int)((460/864.0)*screen_height),(int)((350/1536.0)*screen_widht),(int)((50/864.0)*screen_height));
        passtxt.setBounds((int)((1100/1536.0)*screen_widht),(int)((530/864.0)*screen_height),(int)((350/1536.0)*screen_widht),(int)((50/864.0)*screen_height));
        submit.setBounds((int)((1050/1536.0)*screen_widht),(int)((630/864.0)*screen_height),(int)((100/1536.0)*screen_widht),(int)((50/864.0)*screen_height));
        cancel.setBounds((int)((1180/1536.0)*screen_widht),(int)((630/864.0)*screen_height),(int)((100/1536.0)*screen_widht),(int)((50/864.0)*screen_height));
        mandate.setBounds((int)((970/1536.0)*screen_widht),(int)((690/864.0)*screen_height),(int)((430/1536.0)*screen_widht),(int)((50/864.0)*screen_height));

        panel.add(name);
        panel.add(nametxt);
        panel.add(emailid);
        panel.add(emailidtxt);
        panel.add(phoneno);
        panel.add(phonetxt);
        panel.add(userid);
        panel.add(usertxt);
        panel.add(passwd);
        panel.add(passtxt);

        panel.add(submit);
        panel.add(cancel);
        panel.add(cab);
        panel.add(mandate);

        frame.add(panel,BorderLayout.CENTER);
        frame.setContentPane(panel);
        frame.setVisible(true);


        submit.addActionListener(this);
        cancel.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("CANCEL")) {
            Login l = new Login(frame);
        }
        else {
            try {
                String uid, pass, usn, ph, email;
                uid = usertxt.getText();
                pass = passtxt.getText();
                usn = nametxt.getText();
                ph = phonetxt.getText();
                email = emailidtxt.getText();

                usertxt.setForeground(Color.black);
                passtxt.setForeground(Color.black);
                nametxt.setForeground(Color.black);
                phonetxt.setForeground(Color.black);
                emailidtxt.setForeground(Color.black);

                if(uid.equals("") ||ph.equals("") ||pass.equals("") ||usn.equals("") ||email.equals(""))
                {
                    JOptionPane.showMessageDialog(frame, "Mandatory fields are left empty", "Wrong Entries", JOptionPane.ERROR_MESSAGE);
                }
                else if(pass.length() < 8) {
                    JOptionPane.showMessageDialog(frame, "Password should be a minimum of length 8.", "Wrong Entries", JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    ArrayList<String> outputs = new ArrayList<>();

                    outputs.add("signup");
                    outputs.add(uid);
                    outputs.add(pass);
                    outputs.add(usn);
                    outputs.add(ph);
                    outputs.add(email);

                    LoginPage.sk = new Socket("192.168.43.208",8001);
                    ArrayList<String> inputs = Talk.send(LoginPage.sk, outputs);
                    String s = inputs.remove(0);
                    if (s.equals("successful")) {
                        UserUI ui = new UserUI(frame);
                    }
                    else
                    {
                        do{
                            if (s.equals("emailid invalid")) {
                                emailidtxt.setForeground(Color.RED);
                                emailidtxt.setToolTipText("emailid is invalid");
                            }
                            if (s.equals("phone number invalid")) {
                                phonetxt.setForeground(Color.RED);
                                phonetxt.setToolTipText("phone number is invalid");
                            }
                            if (s.equals("emailid taken")) {
                                emailidtxt.setForeground(Color.RED);
                                emailidtxt.setToolTipText("emailid already exists");
                            }
                            if (s.equals("phone number taken")) {
                                phonetxt.setForeground(Color.RED);
                                phonetxt.setToolTipText("Phone number already exists");
                            }
                            if (s.equals("userid taken")) {
                                usertxt.setForeground(Color.RED);
                                usertxt.setToolTipText("Username already exists");
                            }

                        }while ((s = inputs.remove(0)).equals("errors finished") == false);
                    }
                }
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
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
