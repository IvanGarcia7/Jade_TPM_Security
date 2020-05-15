
package jade.core.vomNew;


import jade.core.AID;
import jade.core.PlatformID;
import java.awt.*;
import java.awt.event.*;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.swing.*;
import javax.swing.border.*;
import java.util.Base64;


/**
 J2SE (Swing-based) implementation of the GUI of the agent that
 tries to buy books on behalf of its user
 */
public class SecureCAGuiImpl extends JFrame implements SecureCAGui{
	private CAPlatform myAgent;

	private JTextField INDEX;
	private JButton INIT, LIST, LISTA, ACCEPT, CLEAR, LOAD, exitB;
	private JTextArea logTA;

	public SecureCAGuiImpl(CAPlatform a) {
		super();
		myAgent =a;
		addWindowListener(new	WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				myAgent.doDelete();
			}
		} );

		JPanel rootPanel = new JPanel();
		rootPanel.setLayout(new GridBagLayout());
		rootPanel.setMinimumSize(new Dimension(560, 50));
		rootPanel.setPreferredSize(new Dimension(560, 50));

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

		INDEX = new JTextField(64);
		INDEX.setMinimumSize(new Dimension(222, 20));
		INDEX.setPreferredSize(new Dimension(222, 20));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(5, 3, 0, 3);
		rootPanel.add(INDEX, gridBagConstraints);

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
				try {
					String publicKeyGen = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxWn5INyIwdQlCFnw+45a91O3LrmRtWkj9mEXcXUViblgTrNEYpuY2HuU7wDn6tIs3WrZcxfNbw8vYKnYGmsCvyua2eqhYQ7AW31Itj+fsOy/XrX5a02aNrqwVOs+Evcx9d9Ap5gWU1XJ2Vl47wsCShxFFhadR2ILZNj5XhTeqMwEalsXcQ+D8DIIyy5eKrgZ1KP79s8Kf2UrVFMADsjt39hM4ajB2F9Pge5m5/tQmt3sBKnGFMf+kaIiHd6INZYJB+5+UdcFzBYzF2PMJpU54kywpIyjZ+xo6RLzMnmlP4lEJnPwKai94mUUV4K9V/fe17DPpEey1SKk7I2DMokNSwIDAQAB";
					String privateKeyGen = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDFafkg3IjB1CUIWfD7jlr3U7cuuZG1aSP2YRdxdRWJuWBOs0Rim5jYe5TvAOfq0izdatlzF81vDy9gqdgaawK/K5rZ6qFhDsBbfUi2P5+w7L9etflrTZo2urBU6z4S9zH130CnmBZTVcnZWXjvCwJKHEUWFp1HYgtk2PleFN6ozARqWxdxD4PwMgjLLl4quBnUo/v2zwp/ZStUUwAOyO3f2EzhqMHYX0+B7mbn+1Ca3ewEqcYUx/6RoiId3og1lgkH7n5R1wXMFjMXY8wmlTniTLCkjKNn7GjpEvMyeaU/iUQmc/ApqL3iZRRXgr1X997XsM+kR7LVIqTsjYMyiQ1LAgMBAAECggEBALmHEYmvmJrInBEQLejL7XOY6oPwBB8Ac7q9noGTLq2jWP49fZxKbMeuxNYk/M/zvBROsZN3oCqqk7T9icmyHf+5pCr+VbHYYjDZOjeE7bghluwUixYr9S1QIB1+g3ThecN/j8yxovGooy0v5/aHWxx5GvhaQm9ulhSt0RV4+ZSZmSW0T6zj+ukmRIQlBNnEssRG37Pvjm8aWCkYk6MkeOBau6z+MtglRlmYHDtvRFTaGRVMM+nn4G/v3TyLZjsFzDRUol45kRFCHl01lgpZhfocVGGq9rtuhORdX0x1YZ8lzdKQZdhuNsfWynIqP839b/XqTFZg0k6b1IYrAKrsI4kCgYEA54Io9AvEaugranylZWbMoUtOzW1XXZEbpcrKjZpF+WDoQqxKrSK406zC52mQFr22XGs62jKhtkg4n71+t5L/nxKwpDQaI8K0sBm41D6kluX0vlKBzi1PUdVAhW/mTbQQVAT7HIIEkvSk2n7iVoL8OuQuxA0eD4Hv2X3QS9F8NM8CgYEA2kxykvuryTXXeA/CCBH75zodzjMVN0irjwqM4yVgXF/mHkkHLbFMS4O1uccIvWkyMYMkG8tbPyGBOrvyUpBj91KQHARs4Nxwycg+xD/5tobW4e59Xc7tX/P2MTfHzvt+zYoLEM828H4xibd9CvpAu7uiyet+Ate1rTYFrBnTdsUCgYEAtUw2PCA2+q3EykjA0e82Ux4BoVh+cZTgvO5Zv5jQMaJVYVlBxeKKMaJ6o/UEVPrOpAOJfDTodTKLvXUNlj91FaLyWDVBPz4MeFg8aWKBTHbbOmysoMTU+DXzqEvgZHudyd54tHORl3Ak5cM2Bx/e3VOy1++Z2fUc2wrvI2DzTvsCgYEAymMF9m+OHMXmNlGluoWx3fZrm7iQeujM5ZkKda05YWsnlfxuw1YVPg2mdc6ps7HL70t+NqaaujT98s49I37qr2nEYbVYnEDD8M/OIXSOt0LcBitt34LxYHx2S7tDbAqOzVCmYbc7YEwe8WdEbuEFEwgrLLk/4rLKpQPozl5DR2ECgYA57KTO9XCAng3LkDKVZAam9k5xqFjy4fwj/GRM+1X4+cpzhLYM4Qini65jSxeWs07Z6fA2EwDW2PiL2xrDWuQNc/Iq370X/bOYRsm7313pslTcFvzqt0f0LW0C2A5DK3oS65GDU9TMivfroA0Ix7fquxDAnrRY8jqAHtt/BMNiJA==";
					byte[] publicBytes = Base64.getDecoder().decode(publicKeyGen);
					X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
					KeyFactory keyFactory = KeyFactory.getInstance("RSA");
					PublicKey pubKey = keyFactory.generatePublic(keySpec);
					byte[] privateBytes = Base64.getDecoder().decode(privateKeyGen);
					PKCS8EncodedKeySpec keySpecPriv = new PKCS8EncodedKeySpec(privateBytes);
					KeyFactory keyFactoryPriv = KeyFactory.getInstance("RSA");
					PrivateKey pubKeyPriv = keyFactoryPriv.generatePrivate(keySpecPriv);
					myAgent.doInitializeCA(pubKeyPriv,pubKey);
					//doListCA();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		} );


		LOAD = new JButton("LOAD");
		LOAD.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				logTA.selectAll();
				logTA.replaceSelection("");
				logTA.setText("");
				myAgent.doAcceptListCA(INDEX.getText());
			}
		} );

		LIST = new JButton("REQUEST LIST");
		LIST.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				logTA.selectAll();
				logTA.replaceSelection("");
				logTA.setText("");
				myAgent.doListCA();
			}
		} );

		LISTA = new JButton("ACCEPTED LIST");
		LISTA.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				logTA.selectAll();
				logTA.replaceSelection("");
				logTA.setText("");
				myAgent.doListACA();
			}
		} );

		ACCEPT = new JButton("ACCEPT");
		ACCEPT.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				logTA.selectAll();
				logTA.replaceSelection("");
				logTA.setText("");
				myAgent.doAcceptListCA(INDEX.getText());
			}
		} );

		CLEAR = new JButton("CLEAR");
		CLEAR.addActionListener(new ActionListener(){
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


		p.add(INIT);
		p.add(LOAD);
		p.add(LIST);
		p.add(LISTA);
		p.add(ACCEPT);
		p.add(CLEAR);
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
	public void setAgent(CAPlatform a) {
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