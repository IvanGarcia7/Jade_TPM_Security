package jade.core.GUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.JTextArea;

import jade.core.AID;
import jade.core.PlatformID;
import jade.core.SecureTPM.Pair;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ivan
 */
public class AgentGuiImpl extends javax.swing.JFrame implements AgentGui,Serializable  {

    private CAAgent myAgent;

    /**
     * Creates new form AgentGUI
     */
    public AgentGuiImpl() {
        initComponents();
    }


    public AgentGuiImpl(CAAgent a) {
        super();
        initComponents();
        myAgent =a;
        addWindowListener(new	WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                myAgent.doDelete();
            }
        });}


    //jPanel1
    //getClass().getResource("user.png"))); // NOI18N


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        PANELHORIZONTAL = new javax.swing.JPanel();
        USERIMAGE = new javax.swing.JLabel();
        SUITCASEIMAGE = new javax.swing.JLabel();
        PRINTERIMAGE = new javax.swing.JLabel();
        EXITIMAGE = new javax.swing.JLabel();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        STARTPANEL = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jTextField4 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField1 = new javax.swing.JTextField();
        START = new javax.swing.JButton();
        TELECOIMAGE = new javax.swing.JLabel();
        EK = new javax.swing.JLabel();
        ADDCA = new javax.swing.JLabel();
        AIK = new javax.swing.JLabel();
        AIDCA = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        STARTAREAS = new javax.swing.JTextArea();
        MIGRATIONPANEL = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        ADDBUTTON = new javax.swing.JButton();
        AIDPLATFORM = new javax.swing.JLabel();
        ADDPLATFORM = new javax.swing.JLabel();
        MIGRATEBUTTON = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        HOPSAERA = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        AIDPLATFORMTEXT = new javax.swing.JTextField();
        ADDPLATFORMTEXT = new javax.swing.JTextField();
        DELETEBUTTON = new javax.swing.JButton();
        SERVICELABEL = new javax.swing.JLabel();
        SERVICETEXT = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        STATUSAREA = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        PANELHORIZONTAL.setBackground(new java.awt.Color(51, 102, 255));

        USERIMAGE.setIcon(new javax.swing.ImageIcon(getClass().getResource("user.png"))); // NOI18N
        USERIMAGE.setPreferredSize(new java.awt.Dimension(113, 64));
        USERIMAGE.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                USERIMAGEMouseClicked(evt);
            }
        });

        SUITCASEIMAGE.setIcon(new javax.swing.ImageIcon(getClass().getResource("maleta.png"))); // NOI18N
        SUITCASEIMAGE.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                SUITCASEIMAGEMouseClicked(evt);
            }
        });

        PRINTERIMAGE.setIcon(new javax.swing.ImageIcon(getClass().getResource("printer.png"))); // NOI18N
        PRINTERIMAGE.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                PRINTERIMAGEMouseClicked(evt);
            }
        });

        EXITIMAGE.setIcon(new javax.swing.ImageIcon(getClass().getResource("exit.png"))); // NOI18N
        EXITIMAGE.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                EXITIMAGEMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout PANELHORIZONTALLayout = new javax.swing.GroupLayout(PANELHORIZONTAL);
        PANELHORIZONTAL.setLayout(PANELHORIZONTALLayout);
        PANELHORIZONTALLayout.setHorizontalGroup(
                PANELHORIZONTALLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(PANELHORIZONTALLayout.createSequentialGroup()
                                .addGap(100, 100, 100)
                                .addComponent(USERIMAGE, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(152, 152, 152)
                                .addComponent(SUITCASEIMAGE)
                                .addGap(152, 152, 152)
                                .addComponent(PRINTERIMAGE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(EXITIMAGE)
                                .addGap(100, 100, 100))
        );
        PANELHORIZONTALLayout.setVerticalGroup(
                PANELHORIZONTALLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(PANELHORIZONTALLayout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addGroup(PANELHORIZONTALLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(EXITIMAGE)
                                        .addComponent(PRINTERIMAGE)
                                        .addComponent(SUITCASEIMAGE)
                                        .addComponent(USERIMAGE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(19, Short.MAX_VALUE))
        );

        STARTPANEL.setPreferredSize(new java.awt.Dimension(900, 523));

        jPanel2.setBackground(new java.awt.Color(102, 153, 255));

        jTextField4.setFont(new java.awt.Font("SansSerif", 0, 16)); // NOI18N
        jTextField4.setHorizontalAlignment(javax.swing.JTextField.CENTER);


        jTextField3.setFont(new java.awt.Font("SansSerif", 0, 16)); // NOI18N
        jTextField3.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jTextField2.setFont(new java.awt.Font("SansSerif", 0, 16)); // NOI18N
        jTextField2.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jTextField1.setFont(new java.awt.Font("SansSerif", 0, 16)); // NOI18N
        jTextField1.setHorizontalAlignment(javax.swing.JTextField.CENTER);


        START.setText("START");

        START.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                STARTActionPerformed(evt);
            }
        });

        TELECOIMAGE.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        TELECOIMAGE.setIcon(new javax.swing.ImageIcon(getClass().getResource("antena.png"))); // NOI18N

        EK.setFont(new java.awt.Font("SansSerif", 0, 16)); // NOI18N
        EK.setText("CONTEXT EK:");

        ADDCA.setFont(new java.awt.Font("SansSerif", 0, 16)); // NOI18N
        ADDCA.setText("CA ADDRESS:");

        AIK.setFont(new java.awt.Font("SansSerif", 0, 16)); // NOI18N
        AIK.setText("CONTEXT AIK:");

        AIDCA.setFont(new java.awt.Font("SansSerif", 0, 16)); // NOI18N
        AIDCA.setText("CA AID:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGap(22, 22, 22)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(AIK)
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(AIDCA)
                                                                        .addComponent(ADDCA))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                        .addComponent(EK)))
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGap(101, 101, 101)
                                                .addComponent(START, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(21, 21, 21))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(TELECOIMAGE, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(109, 109, 109))
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addComponent(TELECOIMAGE, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(AIDCA))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(ADDCA)
                                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(49, 49, 49)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                                .addComponent(EK)
                                                .addGap(18, 18, 18))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(AIK, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jTextField4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(43, 43, 43)
                                .addComponent(START, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(56, 56, 56))
        );

        STARTAREAS.setColumns(20);
        STARTAREAS.setRows(5);
        jScrollPane1.setViewportView(STARTAREAS);

        javax.swing.GroupLayout STARTPANELLayout = new javax.swing.GroupLayout(STARTPANEL);
        STARTPANEL.setLayout(STARTPANELLayout);
        STARTPANELLayout.setHorizontalGroup(
                STARTPANELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(STARTPANELLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
                                .addContainerGap())
        );
        STARTPANELLayout.setVerticalGroup(
                STARTPANELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(STARTPANELLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(STARTPANELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jScrollPane1)))
        );

        jPanel3.setBackground(new java.awt.Color(102, 153, 255));

        ADDBUTTON.setText("ADD PLATFORM");
        ADDBUTTON.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ADDBUTTONActionPerformed(evt);
            }
        });

        AIDPLATFORM.setFont(new java.awt.Font("SansSerif", 0, 16)); // NOI18N
        AIDPLATFORM.setText("AID PLATFORM:");

        ADDPLATFORM.setFont(new java.awt.Font("SansSerif", 0, 16)); // NOI18N
        ADDPLATFORM.setText("ADDRESS PLATFORM:");

        MIGRATEBUTTON.setText("MIGRATE");
        MIGRATEBUTTON.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MIGRATEBUTTONActionPerformed(evt);
            }
        });

        HOPSAERA.setColumns(20);
        HOPSAERA.setRows(5);
        jScrollPane2.setViewportView(HOPSAERA);

        jLabel1.setFont(new java.awt.Font("SansSerif", 0, 16)); // NOI18N
        jLabel1.setText("SELECTED ROUTE");

        AIDPLATFORMTEXT.setFont(new java.awt.Font("SansSerif", 0, 16)); // NOI18N
        AIDPLATFORMTEXT.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        ADDPLATFORMTEXT.setFont(new java.awt.Font("SansSerif", 0, 16)); // NOI18N
        ADDPLATFORMTEXT.setHorizontalAlignment(javax.swing.JTextField.CENTER);


        DELETEBUTTON.setText("DELETE PLATFORM");
        DELETEBUTTON.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DELETEBUTTONActionPerformed(evt);
            }
        });

        SERVICELABEL.setFont(new java.awt.Font("SansSerif", 0, 16)); // NOI18N
        SERVICELABEL.setText("SERVICE:");

        SERVICETEXT.setFont(new java.awt.Font("SansSerif", 0, 16)); // NOI18N
        SERVICETEXT.setHorizontalAlignment(javax.swing.JTextField.CENTER);


        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addGap(23, 23, 23)
                                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(ADDPLATFORM)
                                                                        .addComponent(AIDPLATFORM)
                                                                        .addComponent(SERVICELABEL))
                                                                .addGap(18, 18, 18)
                                                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(ADDPLATFORMTEXT)
                                                                        .addComponent(AIDPLATFORMTEXT)
                                                                        .addComponent(SERVICETEXT)))
                                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
                                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                                .addComponent(ADDBUTTON)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(DELETEBUTTON)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(MIGRATEBUTTON))))
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addGap(172, 172, 172)
                                                .addComponent(jLabel1)))
                                .addGap(18, 18, 18))
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addGap(43, 43, 43)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(AIDPLATFORM)
                                        .addComponent(AIDPLATFORMTEXT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(10, 10, 10)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(ADDPLATFORM)
                                        .addComponent(ADDPLATFORMTEXT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(SERVICELABEL)
                                        .addComponent(SERVICETEXT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(10, 10, 10)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(ADDBUTTON, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(DELETEBUTTON, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(MIGRATEBUTTON, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(24, 24, 24))
        );

        STATUSAREA.setColumns(20);
        STATUSAREA.setRows(5);
        jScrollPane3.setViewportView(STATUSAREA);

        javax.swing.GroupLayout MIGRATIONPANELLayout = new javax.swing.GroupLayout(MIGRATIONPANEL);
        MIGRATIONPANEL.setLayout(MIGRATIONPANELLayout);
        MIGRATIONPANELLayout.setHorizontalGroup(
                MIGRATIONPANELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(MIGRATIONPANELLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                                .addContainerGap())
        );
        MIGRATIONPANELLayout.setVerticalGroup(
                MIGRATIONPANELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(MIGRATIONPANELLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(MIGRATIONPANELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jScrollPane3)))
        );

        jLayeredPane1.setLayer(STARTPANEL, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(MIGRATIONPANEL, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
                jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jLayeredPane1Layout.createSequentialGroup()
                                .addComponent(STARTPANEL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(13, 13, 13))
                        .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(MIGRATIONPANEL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jLayeredPane1Layout.setVerticalGroup(
                jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(STARTPANEL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(10, 10, 10))
                        .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                                        .addComponent(MIGRATIONPANEL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(0, 16, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(PANELHORIZONTAL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLayeredPane1)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(PANELHORIZONTAL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLayeredPane1)
                                .addContainerGap())
        );



        Font f = new Font("SansSerif", Font.BOLD, 10);
        STATUSAREA.setFont(f);
        STATUSAREA.setForeground(Color.BLACK);
        STARTAREAS.setFont(f);
        STARTAREAS.setForeground(Color.BLACK);
        HOPSAERA.setFont(f);
        HOPSAERA.setForeground(Color.BLACK);

        STARTPANEL.setVisible(true);
        MIGRATIONPANEL.setVisible(false);

        pack();
    }// </editor-fold>



    private void STARTActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        STARTAREAS.selectAll();
        STARTAREAS.replaceSelection("");
        STARTAREAS.setText("");
        String publicKeyGen = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxWn5INyIwdQlCFnw+45a91O3LrmRtWkj9mEXcXUViblgTrNEYpuY2HuU7wDn6tIs3WrZcxfNbw8vYKnYGmsCvyua2eqhYQ7AW31Itj+fsOy/XrX5a02aNrqwVOs+Evcx9d9Ap5gWU1XJ2Vl47wsCShxFFhadR2ILZNj5XhTeqMwEalsXcQ+D8DIIyy5eKrgZ1KP79s8Kf2UrVFMADsjt39hM4ajB2F9Pge5m5/tQmt3sBKnGFMf+kaIiHd6INZYJB+5+UdcFzBYzF2PMJpU54kywpIyjZ+xo6RLzMnmlP4lEJnPwKai94mUUV4K9V/fe17DPpEey1SKk7I2DMokNSwIDAQAB";
        byte[] publicBytes = Base64.getDecoder().decode(publicKeyGen);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory;
        PublicKey pubKey = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            pubKey = keyFactory.generatePublic(keySpec);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        System.out.println(jTextField1.getText()+" "+jTextField2.getText()+" "+jTextField3.getText()+" "+jTextField4.getText());

        if(jTextField1.getText().isEmpty()||jTextField2.getText().isEmpty()) {
            STARTAREAS.append("PLEASE INSERT THE AID AND THE ADDRESS OF THE SECURE CA\n");
        }else if(jTextField3.getText().isEmpty()||jTextField4.getText().isEmpty()){
            STARTAREAS.append("PLEASE INSERT A CONTEXT TO SAVE THE EK AND THE AIK INTO THE TPM\n");
        }else {
            AID remoteAMS = new AID(jTextField1.getText(), AID.ISGUID);
            remoteAMS.addAddresses(jTextField2.getText());
            PlatformID destination = new PlatformID(remoteAMS);
            myAgent.doInitializeAgent(destination,pubKey,jTextField3.getText(),jTextField4.getText(),STARTAREAS,HOPSAERA,STATUSAREA);
        }

    }

    private void ADDBUTTONActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        //System.out.println(AIDPLATFORMTEXT.getText()+" "+ADDPLATFORMTEXT.getText());
        if(AIDPLATFORMTEXT.getText().isEmpty() || ADDPLATFORMTEXT.getText().isEmpty()){
            STATUSAREA.append("PLEASE INSERT A VALID PLATFORM");
        }else{
            AID remoteAMSDestiny = new AID(AIDPLATFORMTEXT.getText(), AID.ISGUID);
            remoteAMSDestiny.addAddresses(ADDPLATFORMTEXT.getText());
            PlatformID destination2 = new PlatformID(remoteAMSDestiny);
            String service = "";
            if(!SERVICETEXT.getText().isEmpty()) {
                service = SERVICETEXT.getText();
            }
            Pair<PlatformID,String> hopsNew = new Pair<PlatformID,String>(destination2,service);
            hops.add(hopsNew);
            HOPSAERA.append("\nPLATFORM: "+destination2.getID()+" Service: "+service+"\n");
        }
    }



    private void DELETEBUTTONActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:

        if(AIDPLATFORMTEXT.getText().isEmpty() || ADDPLATFORMTEXT.getText().isEmpty()){
            STATUSAREA.append("PLEASE INSERT AN AID AND AN ADDRESS");
        }else{

            //RECORRO LA LISTA Y VEO SI ALGUNA TIENE EL AID IGUAL
            boolean progress = true;
            int index = 0;

            AID remoteAMSDestiny = new AID(AIDPLATFORMTEXT.getText(), AID.ISGUID);
            remoteAMSDestiny.addAddresses(ADDPLATFORMTEXT.getText());
            PlatformID destination2 = new PlatformID(remoteAMSDestiny);

            while(index<hops.size()&&progress){
                PlatformID selectedPlatform = hops.get(index).getKey();
                if(selectedPlatform.getAmsAID().equals(destination2.getAmsAID())){
                    hops.remove(index);
                    progress=false;
                }
                index++;
            }

            HOPSAERA.selectAll();
            HOPSAERA.replaceSelection("");
            HOPSAERA.setText("");

            for(int i=0;i<hops.size();i++){
                PlatformID destinationSelectedPrinter = hops.get(i).getKey();
                String service = hops.get(i).getValue();
                HOPSAERA.append("\nPLATFORM: "+destinationSelectedPrinter.getID()+" Service: "+service+"\n");
                HOPSAERA.append("*********************\n");
            }
        }

    }




    private void MIGRATEBUTTONActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        HOPSAERA.selectAll();
        HOPSAERA.replaceSelection("");
        HOPSAERA.setText("");

        STATUSAREA.selectAll();
        STATUSAREA.replaceSelection("");
        STATUSAREA.setText("");

        if(hops.size()==0){
            STATUSAREA.append("PLEASE INSERT ONE PLATFORM\n");
        }else {
            STATUSAREA.append("STARTING THE MIGRATION\n");
            PlatformID destiny = hops.get(0).getKey();
            //hops.remove(0);
            myAgent.setHops(hops);
            myAgent.doSecureMigration(destiny);
            //Platform.exit();
            //myAgent.dale(destiny);
            //myAgent.doMove(destiny);
            //Platform.exit();
        }

    }


    private void USERIMAGEMouseClicked(java.awt.event.MouseEvent evt) {
        // TODO add your handling code here:
        STARTPANEL.setVisible(true);
        MIGRATIONPANEL.setVisible(false);
    }

    private void SUITCASEIMAGEMouseClicked(java.awt.event.MouseEvent evt) {
        // TODO add your handling code here:
        STARTPANEL.setVisible(false);
        MIGRATIONPANEL.setVisible(true);
    }

    private void PRINTERIMAGEMouseClicked(java.awt.event.MouseEvent evt) {
        // TODO add your handling code here:
        if(STARTPANEL.isVisible()) {
            STARTAREAS.selectAll();
            STARTAREAS.replaceSelection("");
            STARTAREAS.setText("");
        }else {
            STATUSAREA.selectAll();
            STATUSAREA.replaceSelection("");
            STATUSAREA.setText("");
        }
    }

    private void EXITIMAGEMouseClicked(java.awt.event.MouseEvent evt) {
        // TODO add your handling code here:
        System.exit(0);
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AgentGuiImpl.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AgentGuiImpl.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AgentGuiImpl.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AgentGuiImpl.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AgentGuiImpl().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify
    private javax.swing.JButton ADDBUTTON;
    private javax.swing.JLabel ADDCA;
    private javax.swing.JLabel ADDPLATFORM;
    private javax.swing.JTextField ADDPLATFORMTEXT;
    private javax.swing.JLabel AIDCA;
    private javax.swing.JLabel AIDPLATFORM;
    private javax.swing.JTextField AIDPLATFORMTEXT;
    private javax.swing.JLabel AIK;
    private javax.swing.JButton DELETEBUTTON;
    private javax.swing.JLabel EK;
    private javax.swing.JLabel EXITIMAGE;
    private javax.swing.JTextArea HOPSAERA;
    private javax.swing.JButton MIGRATEBUTTON;
    private javax.swing.JPanel MIGRATIONPANEL;
    private javax.swing.JLabel PRINTERIMAGE;
    private javax.swing.JButton START;
    private javax.swing.JTextArea STARTAREAS;
    private javax.swing.JPanel STARTPANEL;
    private javax.swing.JTextArea STATUSAREA;
    private javax.swing.JLabel SUITCASEIMAGE;
    private javax.swing.JLabel TELECOIMAGE;
    private javax.swing.JLabel USERIMAGE;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel PANELHORIZONTAL;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JLabel SERVICELABEL;
    private javax.swing.JTextField SERVICETEXT;
    // End of variables declaration

    private List<Pair<PlatformID,String>> hops = new ArrayList<Pair<PlatformID,String>>();


    @Override
    public JTextArea getPrinterStart() {
        // TODO Auto-generated method stub
        return STARTAREAS;
    }


    @Override
    public JTextArea getPrinterHops() {
        // TODO Auto-generated method stub
        return HOPSAERA;
    }

    @Override
    public JTextArea getPrinterInformation() {
        // TODO Auto-generated method stub
        return STATUSAREA;
    }


}
