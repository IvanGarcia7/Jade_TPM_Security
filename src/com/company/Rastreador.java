package com.company;

import jade.core.AID;
import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.PlatformID;
import jade.core.SecureTPM.SecureAgent;


public class Rastreador extends SecureAgent{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2241822868465327314L;

	public void setup() {
		AID remoteAMS = new AID("ams@192.168.0.106:3333/JADE", AID.ISGUID);
		remoteAMS.addAddresses("http://192.168.0.106:7778/acc");
		//this.addBehaviour(new Beh1(this));
		PlatformID destination = new PlatformID(remoteAMS);
		
		//doSecureMove(destination);
	}
	
	
	
	public void beforeMove() {
		System.out.println("SECURE SERVICE EXECUTE CORRECTLY ;)");
	}
	
	public void afterMove() {
		//System.out.println("adios");
	}
	
	
	
	
		
	
}
