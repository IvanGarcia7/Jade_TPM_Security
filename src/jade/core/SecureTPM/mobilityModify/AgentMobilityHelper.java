

package jade.core.SecureTPM.mobilityModify;




import jade.core.ServiceHelper;
import jade.core.AID;
import jade.core.Location;
import jade.core.IMTPException;
import jade.core.ServiceException;
import jade.core.NotFoundException;
import jade.core.NameClashException;

import jade.core.mobility.Movable;
import jade.security.JADESecurityException;

import jade.util.leap.List;


public interface AgentMobilityHelper extends ServiceHelper {


	void move(Location destination);
	void clone(Location destination, String newName);

	ClassLoader getContainerClassLoader(String containerName, ClassLoader parent) throws ServiceException;

	
    /**
       The name of this service.
    */
    public static final String NAME = "jade.core.mobility.AgentMobility";


    /**
       This command name represents the <code>move-agent</code>
       action.
       This command object represents only the <i>first half</i> of
       the complete agent migration process. Even if this command is
       accepted by the kernel, there is no guarantee that the
       requested migration will ever happen. Only when the
       <code>InformMoved</code> command is issued can one assume that
       the agent migration has taken place.
    */
    static final String REQUEST_MOVE = "Request-Move";

    /**
       This command name represents the <code>clone-agent</code>
       action.
       This command object represents only the <i>first half</i> of
       the complete agent clonation process. Even if this command is
       accepted by the kernel, there is no guarantee that the
       requested clonation will ever happen. Only when the
       <code>InformCloned</code> command is issued can one assume that
       the agent clonation has taken place.
    */
    static final String REQUEST_CLONE = "Request-Clone";

    /**
       This command is issued by an agent that has just migrated.
       The agent migration can either be an autonomous move of the
       agent or the outcome of a previously issued
       <code>RequestMove</code> command. In the second case, this
       command represents only the <i>second half</i> of the complete
       agent migration process.
    */
    static final String INFORM_MOVED = "Inform-Moved";

    /**
       This command is issued by an agent that has just cloned itself.
       The agent clonation can either be an autonomous move of the
       agent or the outcome of a previously issued
       <code>RequestClone</code> command. In the second case, this
       command represents only the <i>second half</i> of the complete
       agent clonation process.
    */
    static final String INFORM_CLONED = "Inform-Cloned";

    void registerMovable(jade.core.SecureTPM.mobilityModify.Movable movable);


    //void informCloned(AID agentID, Location where, String newName) throws ServiceException, JADESecurityException, IMTPException, NotFoundException, NameClashException;

}
