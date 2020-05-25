package jade.core.vomNew;


import jade.core.AID;
import jade.core.PlatformID;
import jade.core.vomNew.AgentGui;

import java.awt.*;
import java.awt.event.*;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import javax.swing.*;
import javax.swing.border.*;
import java.util.Base64;


/**
   J2SE (Swing-based) implementation of the GUI of the agent that 
   tries to buy books on behalf of its user
 */
public class AgentGuiImpl extends JFrame implements AgentGui {
private jade.core.vomNew.CAAgent myAgent;
	
	private JTextField SAIDP, AIDP, SADDP, ADDP, CEK, CAK;
	private JButton INIT, MIGRATE, resetB, exitB;
	private JTextArea logTA;
	
	public AgentGuiImpl(jade.core.vomNew.CAAgent a) {
		super();
		myAgent =a;
		addWindowListener(new	WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				myAgent.doDelete();
			}
		} );

	JPanel rootPanel = new JPanel();
	rootPanel.setLayout(new GridBagLayout());
    rootPanel.setMinimumSize(new Dimension(560, 180));
    rootPanel.setPreferredSize(new Dimension(560, 180));
		
    ///////////
    // Line 0
    ///////////
	JLabel l = new JLabel("SECURE AID PLATFORM:");
    l.setHorizontalAlignment(SwingConstants.LEFT);
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 3);
    rootPanel.add(l, gridBagConstraints);

    SAIDP = new JTextField(64);
    SAIDP.setMinimumSize(new Dimension(222, 20));
    SAIDP.setPreferredSize(new Dimension(222, 20));
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new Insets(5, 3, 0, 3);
    rootPanel.add(SAIDP, gridBagConstraints);
    
    
    
    l = new JLabel("SECURE ADDRESS PLATFORM:");
    l.setHorizontalAlignment(SwingConstants.LEFT);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 3);
    rootPanel.add(l, gridBagConstraints);

    SADDP = new JTextField(64);
    SADDP.setMinimumSize(new Dimension(222, 20));
    SADDP.setPreferredSize(new Dimension(222, 20));
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new Insets(5, 3, 0, 3);
    rootPanel.add(SADDP, gridBagConstraints);
    
    

    ///////////
    // Line 2
    ///////////
    l = new JLabel("AID PLATFORM:");
    l.setHorizontalAlignment(SwingConstants.LEFT);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 3);
    rootPanel.add(l, gridBagConstraints);

    AIDP = new JTextField(64);
    AIDP.setMinimumSize(new Dimension(222, 20));
    AIDP.setPreferredSize(new Dimension(222, 20));
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new Insets(5, 3, 0, 3);
    rootPanel.add(AIDP, gridBagConstraints);
    
    
    
    l = new JLabel("ADDRESS PLATFORM:");
    l.setHorizontalAlignment(SwingConstants.LEFT);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 3);
    rootPanel.add(l, gridBagConstraints);

    ADDP = new JTextField(64);
    ADDP.setMinimumSize(new Dimension(222, 20));
    ADDP.setPreferredSize(new Dimension(222, 20));
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new Insets(5, 3, 0, 3);
    rootPanel.add(ADDP, gridBagConstraints);
    
    
 
	///////////
	// Line 3
	///////////
	l = new JLabel("CONTEXT EK:");
	l.setHorizontalAlignment(SwingConstants.LEFT);
	gridBagConstraints = new GridBagConstraints();
	gridBagConstraints.gridx = 0;
	gridBagConstraints.gridy = 4;
	gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
	gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 3);
	rootPanel.add(l, gridBagConstraints);
	
	CEK = new JTextField(64);
	CEK.setMinimumSize(new Dimension(222, 20));
	CEK.setPreferredSize(new Dimension(222, 20));
	gridBagConstraints = new GridBagConstraints();
	gridBagConstraints.gridx = 1;
	gridBagConstraints.gridy = 4;
	gridBagConstraints.gridwidth = 3;
	gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
	gridBagConstraints.insets = new Insets(5, 3, 0, 3);
	rootPanel.add(CEK, gridBagConstraints);
	
	
	
	l = new JLabel("CONTEXT AK:");
	l.setHorizontalAlignment(SwingConstants.LEFT);
	gridBagConstraints = new GridBagConstraints();
	gridBagConstraints.gridx = 0;
	gridBagConstraints.gridy = 5;
	gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
	gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 3);
	rootPanel.add(l, gridBagConstraints);
	
	CAK = new JTextField(64);
	CAK.setMinimumSize(new Dimension(222, 20));
	CAK.setPreferredSize(new Dimension(222, 20));
	gridBagConstraints = new GridBagConstraints();
	gridBagConstraints.gridx = 1;
	gridBagConstraints.gridy = 5;
	gridBagConstraints.gridwidth = 3;
	gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
	gridBagConstraints.insets = new Insets(5, 3, 0, 3);
	rootPanel.add(CAK, gridBagConstraints);
    
    
    
    rootPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
    getContentPane().add(rootPanel, BorderLayout.NORTH);
    
    
    logTA = new JTextArea();
    logTA.setEnabled(false);
    JScrollPane jsp = new JScrollPane(logTA);
    jsp.setMinimumSize(new Dimension(500, 300));
    jsp.setPreferredSize(new Dimension(500, 300));
    JPanel p = new JPanel();
    p.setBorder(new BevelBorder(BevelBorder.LOWERED));
    p.add(jsp);
    getContentPane().add(p, BorderLayout.CENTER);
  
    
    p = new JPanel();
    INIT = new JButton("START");
    	INIT.addActionListener(new ActionListener(){
	  	public void actionPerformed(ActionEvent e) {
	  		logTA.selectAll();
	  		logTA.replaceSelection("");
	  		logTA.setText("");
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
			AID remoteAMS = new AID(SAIDP.getText(), AID.ISGUID);
			remoteAMS.addAddresses(SADDP.getText());
			PlatformID destination = new PlatformID(remoteAMS);
			//myAgent.doInitializeAgent(destination,pubKey,CEK.getText(),CAK.getText());	
	  	}
		} );
    	
    	
    	
    	
    	MIGRATE = new JButton("MIGRATE");
    	MIGRATE.addActionListener(new ActionListener(){
	  	public void actionPerformed(ActionEvent e) {
	  		logTA.selectAll();
	  		logTA.replaceSelection("");
	  		logTA.setText("");
	  		//SAIDP, AIDP, SADDP, ADDP, CEK, CAK
	  		//CREATE THE PLATFORM
	  		AID remoteAMSDestiny = new AID(AIDP.getText(), AID.ISGUID);
			remoteAMSDestiny.addAddresses(ADDP.getText());
			PlatformID destination2 = new PlatformID(remoteAMSDestiny);
			myAgent.doSecureMigration(destination2);
	  	}
		} );
    
    resetB = new JButton("CLEAR");
		resetB.addActionListener(new ActionListener(){
	  	public void actionPerformed(ActionEvent e) {
	  		logTA.selectAll();
	  		logTA.replaceSelection("");
	  		logTA.setText("");
	  		//SAIDP.setText("");
	  		//AIDP.setText("");
	  		//SADDP.setText("");
	  		//ADDP.setText("");
	  		//CEK.setText("");
	  		//CAK.setText("");
	  	}
		} );
		
		
    exitB = new JButton("EXIT");
		exitB.addActionListener(new ActionListener(){
	  	public void actionPerformed(ActionEvent e) {
	  		logTA.selectAll();
	  		logTA.replaceSelection("");
	  		logTA.setText("");
	  		myAgent.doDelete();
	  	}
		} );
		
    exitB.setPreferredSize(resetB.getPreferredSize());
    
    p.add(INIT);
    p.add(MIGRATE);
    p.add(resetB);
    p.add(exitB);
    
    p.setBorder(new BevelBorder(BevelBorder.LOWERED));
    getContentPane().add(p, BorderLayout.SOUTH);
    
    pack();
    
    setResizable(false);
	}
	

	public void notifyUser(String message) {
		logTA.append(message+"\n");
	}


	@Override
	public void setAgent(jade.core.vomNew.CAAgent a) {
		// TODO Auto-generated method stub
		myAgent = a;
	}


	@Override
	public JTextArea getPrinter() {
		// TODO Auto-generated method stub
		Font f = new Font("Courier New", Font.BOLD, 10);
		logTA.setFont(f);
		logTA.setForeground(Color.BLACK);
		return logTA;
	}
	
	
}