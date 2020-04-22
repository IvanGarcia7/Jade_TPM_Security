package jade.core.SecureTPM;

import jade.core.*;
import jade.core.SecureInterTPM.SecureInterTPMHelper;
import jade.core.SecureIntraTPM.SecureIntraTPMHelper;
import jade.core.SecureOnionTPM.SecureOnionTPMHelper;
import jade.core.mobility.Movable;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SecureAgent extends Agent {
    private static final long serialVersionUID = 9058618378207435614L;

    /**
     * SERVICE CREATION, INTRA E INTER PLATFORM. SERVICES ARE INSTALLED WHEN THE PROGRAM REQUIRES IT
     */
    private transient SecureIntraTPMHelper mobHelperIntra;
    private transient SecureInterTPMHelper mobHelperInter;
    private transient SecureOnionTPMHelper mobHelperOnion;


    /**
     * THIS FUNCTION, INITIATES THE ATTESTATION PROTOCOL DESIGNED FOR THIS TFG,
     * FIRST OF ALL, CARRY OUT THE ATTESTATION, AND LATER, COMMUNICATE WITH THE
     * INTRAPLATFORM MOBILITY SERVICE ENCRYPTED TO MOVE THE AGENT.
     * TAKES ONLY THE LOCATION AS A PARAM TO KNOW THE SERVICE THAT HAS TO INSTANTIATE.
     * @param destiny
     */
    public void doSecureMove(Location destiny) {
        System.out.println("THE SECURE MOVE PROCESS HAS JUST STARTED CORRECTLY");
        StringBuilder sb = new StringBuilder();
        sb.append("A REQUEST TO MOVE THE AGENT WITH THE FOLLOWING DATA HAS BEEN REGISTERED:\n")
                .append("AGENT: "+this.getAID()+"\n")
                .append("ACTUAL AGENCY "+destiny.getName()+" WITH DESTINY "+destiny.getAddress());
        System.out.println(sb.toString());
        try {
            if(destiny instanceof ContainerID){
                System.out.println("THERE ARE A MOVE INTRA-PLATFORM REQUEST:");
                initmobHelperIntra();
                Agencia.printLog("THE SERVICE HAS BEGUN TO RUN",
                        Level.INFO,SecureIntraTPMHelper.DEBUG,this.getClass().getName());
                System.out.println("THE SERVICE HAS STARTED WITHOUT ERRORS, PROCEEDING TO ITS IMPLEMENTATION");
                mobHelperIntra.doMoveTPM(this,destiny);
            }else{
                System.out.println("THERE ARE A MOVE INTER-PLATFORM REQUEST:");
                initmobHelperInter();
                Agencia.printLog("THE SERVICE HAS BEGUN TO RUN",
                        Level.INFO,SecureInterTPMHelper.DEBUG,this.getClass().getName());
                System.out.println("THE SERVICE HAS STARTED WITHOUT ERRORS, PROCEEDING TO ITS IMPLEMENTATION");
                mobHelperInter.doMoveTPM(this,destiny);
            }
        }
        catch(ServiceException se) {
            System.out.println("FATAL ERROR, THE MOVEMENT PROCESS HAS NOT BEEN POSSIBLE, PLEASE RUN THE PROCESS AGAIN");
            se.printStackTrace();
            return;
        }
    }


    /**
     * THIS FUNCTION EXECUTE THE MOVE SERVICE, THAT HAS BEEN MODIFIED TO IMPLEMENT SECURITY
     * WITH THE TPM, IN ORDER TO CIPHER THE AGENT PREVIOUSLY.
     * TAKES ONLY THE LOCATION AS A PARAM.
     * @param destiny
     */
    public final void doMoveOld(Location destiny){
        super.doMove(destiny);
    }


    /**
     * THIS FUNCTION, INITIATES THE ATTESTATION PROTOCOL DESIGNED FOR THIS TFG,
     * FIRST OF ALL, CARRY OUT THE ATTESTATION, AND LATER, COMMUNICATE WITH THE
     * INTRAPLATFORM MOBILITY SERVICE ENCRYPTED TO CLONE THE AGENT.
     * TAKES ONLY THE LOCATION AS A PARAM TO KNOW THE SERVICE THAT HAS TO INSTANTIATE.
     * @param destiny
     */
    public void doSecureClone(Location destiny, String new_name_agent) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("A REQUEST TO MOVE THE AGENT WITH THE FOLLOWING DATA HAS BEEN REGISTERED:\n")
                    .append("AGENT: "+this.getAID()+"\n")
                    .append("ACTUAL AGENCY "+destiny.getName()+" WITH DESTINY "+destiny.getAddress());
            sb.toString();
            if(destiny instanceof ContainerID){
                System.out.println("THERE ARE A CLONE INTRA-PLATFORM REQUEST:");
                initmobHelperIntra();
                Agencia.printLog("THE SERVICE HAS BEGUN TO RUN",
                        Level.INFO,SecureIntraTPMHelper.DEBUG,this.getClass().getName());
                System.out.println("THE SERVICE HAS STARTED WITHOUT ERRORS, PROCEEDING TO ITS IMPLEMENTATION");
                mobHelperIntra.doCloneTPM(this, destiny, new_name_agent);
            }else{
                System.out.println("THERE ARE A CLONE INTER-PLATFORM REQUEST:");
                initmobHelperInter();
                Agencia.printLog("COMIENZO DE LA EJECUCION DEL SERVICIO", Level.INFO,
                        SecureIntraTPMHelper.DEBUG,this.getClass().getName());
                System.out.println("THE SERVICE HAS STARTED WITHOUT ERRORS, PROCEEDING TO ITS IMPLEMENTATION");
                mobHelperInter.doCloneTPM(this, destiny, new_name_agent);
            }
        }
        catch(ServiceException se) {
            System.out.println("FATAL ERROR, THE CLONING PROCESS HAS NOT BEEN POSSIBLE, PLEASE RUN THE PROCESS AGAIN");
            se.printStackTrace();
            return;
        }
    }

    /**
     * THIS FUNCTION EXECUTE THE CLONE SERVICE, THAT HAS BEEN MODIFIED TO IMPLEMENT SECURITY
     * WITH THE TPM, IN ORDER TO CIPHER THE AGENT PREVIOUSLY.
     * TAKES ONLY THE LOCATION AS A PARAM.
     * @param destiny
     */
    public final void doCloneOld(Location destiny, String name){
        super.doClone(destiny, name);
    }


    /**
     * THIS FUNCTION IS A TEST, TO SEND A MS TO A REMOTE PLATFORM,
     * USING ACL MESSAGES.
     */
    public void doSecureSend(){
        AMSAgentDescription[] agents = null;
        List<AID> confirmagents = new ArrayList<AID>();
        try {
            SearchConstraints c = new SearchConstraints();
            c.setMaxResults ( new Long(-1) );
            agents = AMSService.search( this, new AMSAgentDescription (), c );
        }catch(Exception e) {
            e.printStackTrace();
        }

        AID myID = getAID();
        for (int i=0; i<agents.length;i++)
        {
            AID agentID = agents[i].getName();
            System.out.println(( agentID.equals( myID ) ? "*** " : "    ")+ i + ": " + agentID.getName());
            if(agentID.getName().contains("Server")) {
                confirmagents.add(agentID);
            }
        }

        for(int l=0; l<confirmagents.size();l++) {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setContent( "HELLO SERVER" );
            msg.addReceiver(confirmagents.get(l));
            send(msg);
            System.out.println("ACK SENDED TO THE SERVER CORRECTLY, WAITING THE KEY");

            ACLMessage mensaje = blockingReceive();
            System.out.println("I HAVE RECEIVED FROM THE TRACKING NODE: "+mensaje.getContent());
            System.out.println("KEY FROM THE SERVER OBTAINED, ENCRYPTING THE MESSAGE AND SEND TO THE SERVER");

            ACLMessage reply = mensaje.createReply();
            reply.setPerformative( ACLMessage.INFORM );
            double publicKey = Math.random()*6 ;
            reply.setContent(Double.toString(publicKey));
            send(reply);
        }
    }


    public void doSecureOnionTransfer(Location destiny_platform, List<KeyStorage> devices_list) throws ServiceException {
        System.out.println("THERE ARE A ONION SECURE REQUEST:");
        initmobHelperOnion();
        Agencia.printLog("THE SERVICE HAS BEGUN TO RUN",
                Level.INFO,SecureInterTPMHelper.DEBUG,this.getClass().getName());
        System.out.println("THE SERVICE HAS STARTED WITHOUT ERRORS, PROCEEDING TO ITS IMPLEMENTATION");
        mobHelperOnion.sendBroadcastACL(this,destiny_platform,devices_list);
    }


    /**
     * FUNCTION TO PRINT IF APPEAR ONE ERROR IN THE SECURE MOVE
     * @param ms
     */
    public void doSecureMoveError(String ms){
        System.out.println(ms);
    }


    /**
     * FUNCTION TO PRINT IF APPEAR ONE ERROR IN THE SECURE CLONATION
     * @param ms
     */
    public void doSecureCloneError(String ms){
        System.out.println(ms);
    }


    /**
     * CALL THIS FUNCTION TO INICALIZE THE INTRA-PLATFORM SERVICE
     * @throws ServiceException
     */
    private void initmobHelperIntra() throws ServiceException {
        if(mobHelperIntra == null){
            mobHelperIntra = (SecureIntraTPMHelper) getHelper(SecureIntraTPMHelper.NAME);
            System.out.println(mobHelperIntra);
            mobHelperIntra.registerMovable(new Movable() {
                public void beforeMove() {
                    SecureAgent.this.beforeMove();
                }

                public void afterMove() {
                    System.out.println("GALLETITAS SALADAS2");
                    SecureAgent.this.afterMove();
                }

                public void beforeClone() {
                    SecureAgent.this.beforeClone();
                }

                public void afterClone() {
                    SecureAgent.this.afterClone();
                }
            } );
        }
    }


    /**
     * CALL THIS FUNCTION TO INICALIZE THE INTER-PLATFORM SERVICE
     * @throws ServiceException
     */
    private void initmobHelperInter() throws ServiceException {
        if(mobHelperInter == null){
            mobHelperInter = (SecureInterTPMHelper) getHelper(SecureInterTPMHelper.NAME);
            System.out.println(mobHelperInter);
            mobHelperInter.registerMovable(new Movable() {
                public void beforeMove() {

                    SecureAgent.this.beforeMove();
                }

                public void afterMove() {
                    System.out.println("GALLETITAS SALADAS");
                    SecureAgent.this.afterMove(); }

                public void beforeClone() {
                    SecureAgent.this.beforeClone();
                }

                public void afterClone() {
                    SecureAgent.this.afterClone();
                }
            } );
        }
    }


    /**
     * CALL THIS FUNCTION TO INICALIZE THE ONION SERVICE
     * @throws ServiceException
     */
    private void initmobHelperOnion() throws ServiceException {
        if(mobHelperOnion == null){
            mobHelperOnion = (SecureOnionTPMHelper) getHelper(SecureOnionTPMHelper.NAME);
            mobHelperOnion.registerMovable(new Movable() {
                public void beforeMove() { SecureAgent.this.beforeMove(); }
                public void afterMove() { SecureAgent.this.afterMove(); }
                public void beforeClone() { SecureAgent.this.beforeClone(); }
                public void afterClone() { SecureAgent.this.afterClone(); }
            } );
        }
    }
}
