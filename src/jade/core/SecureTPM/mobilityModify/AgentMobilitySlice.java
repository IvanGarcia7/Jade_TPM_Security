package jade.core.SecureTPM.mobilityModify;

import jade.core.Service;
import jade.core.Filter;
import jade.core.AID;
import jade.core.ContainerID;
import jade.core.Location;
import jade.core.IMTPException;
import jade.core.ServiceException;
import jade.core.NotFoundException;
import jade.core.NameClashException;

import jade.security.Credentials;
import jade.security.JADESecurityException;

import jade.util.leap.List;

public interface AgentMobilitySlice extends Service.Slice {

    public static final String NAME = "jade.core.mobility.AgentMobility";

    static final String H_CREATEAGENT = "1";
    static final String H_FETCHCLASSFILE = "2";
    static final String H_MOVEAGENT = "3";
    static final String H_COPYAGENT = "4";
    static final String H_PREPARE = "5";
    static final String H_TRANSFERIDENTITY = "6";
    static final String H_HANDLETRANSFERRESULT = "7";
    static final String H_CLONEDAGENT = "8";
	//#J2ME_EXCLUDE_BEGIN
    static final String H_CLONECODELOCATORENTRY = "9";
    static final String H_REMOVECODELOCATORENTRY = "10";
	//#J2ME_EXCLUDE_END

    void createAgent(AID agentID, byte[] serializedInstance, String classSiteName, boolean isCloned, boolean startIt) throws IMTPException, ServiceException, NotFoundException, NameClashException, JADESecurityException;
    byte[] fetchClassFile(String className, String agentName) throws IMTPException, ClassNotFoundException;

    void moveAgent(AID agentID, Location where) throws IMTPException, NotFoundException;
    void copyAgent(AID agentID, Location where, String newName) throws IMTPException, NotFoundException;

    boolean prepare() throws IMTPException;

    boolean transferIdentity(AID agentID, Location src, Location dest) throws IMTPException, NotFoundException;
    void handleTransferResult(AID agentID, boolean result, List messages) throws IMTPException, NotFoundException;
    void clonedAgent(AID agentID, ContainerID cid, Credentials creds) throws IMTPException, JADESecurityException, NotFoundException, NameClashException;
    void cloneCodeLocatorEntry(AID oldAgentID, AID newAgentID) throws IMTPException, NotFoundException;
    void removeCodeLocatorEntry(AID name) throws IMTPException, NotFoundException;


}
