package jade.core.SecureTPM.mobilityModify;

import jade.core.ServiceFinder;
import jade.core.IMTPException;
import jade.core.ServiceException;
import jade.core.mobility.AgentMobilityService;
import jade.core.mobility.AgentMobilitySlice;
import jade.util.Logger;

class MobileAgentClassLoader extends ClassLoader {

    private jade.core.mobility.AgentMobilitySlice classServer;
    private String agentName;
    private String sliceName;
    private ServiceFinder finder;
    private Logger myLogger = Logger.getMyLogger(AgentMobilityService.NAME);

    public MobileAgentClassLoader(String an, String sn, ServiceFinder sf, ClassLoader parent) throws IMTPException, ServiceException {
    	//#PJAVA_EXCLUDE_BEGIN
    	super(parent);
		//#PJAVA_EXCLUDE_END

		agentName = an;
	    sliceName = sn;
	    finder = sf;
	    classServer = (jade.core.mobility.AgentMobilitySlice)finder.findSlice(jade.core.mobility.AgentMobilitySlice.NAME, sliceName);
	    if (classServer == null) {
			throw new ServiceException("Code source container "+sliceName+" does not exist or does not support mobility");
	    }
    }

    protected Class findClass(String name) throws ClassNotFoundException {
    	byte[] classFile;

    	try {
    		if(myLogger.isLoggable(Logger.FINE)) {
    			myLogger.log(Logger.FINE,"Remote retrieval of code for class " + name);
    		}
    		try {
    			classFile = classServer.fetchClassFile(name, agentName);
    		}
    		catch(IMTPException imtpe) {
    			// Get a fresh slice and retry
    			classServer = (jade.core.mobility.AgentMobilitySlice)finder.findSlice(AgentMobilitySlice.NAME, sliceName);
    			classFile = classServer.fetchClassFile(name, agentName);
    		}
    	}
    	catch (IMTPException imtpe) {
    		imtpe.printStackTrace();
    		throw new ClassNotFoundException(name);
    	}
    	catch (ServiceException se) {
    		throw new ClassNotFoundException(name);
    	}

    	if (classFile != null) {
    		if(myLogger.isLoggable(Logger.FINE)) {
    			myLogger.log(Logger.FINE,"Code of class " + name + " retrieved. Length is " + classFile.length);
    		}
    		return defineClass(name, classFile, 0, classFile.length);
    	}
    	else {
    		throw new ClassNotFoundException(name);
    	}
    }
    
    protected Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
    	Class c = null;
    	if(myLogger.isLoggable(Logger.FINER)) {
    		myLogger.log(Logger.FINER,"Loading class " + name);
    	}
		c = super.loadClass(name, resolve);
		if(myLogger.isLoggable(Logger.FINER)) {
			myLogger.log(Logger.FINER,"Class " + name + " loaded" );
		}
	
	  	return c;
    }
}
