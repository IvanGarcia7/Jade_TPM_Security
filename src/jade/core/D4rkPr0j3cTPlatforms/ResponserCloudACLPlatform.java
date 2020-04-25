package jade.core.D4rkPr0j3cTPlatforms;

import jade.core.Agent;
import jade.core.BaseService;
import jade.lang.acl.MessageTemplate;
import jade.proto.SimpleAchieveREResponder;
import jade.util.Logger;

public class ResponserCloudACLPlatform extends SimpleAchieveREResponder {

    private Logger logger = Logger.getMyLogger(getClass().getName());
    private BaseService myService;


    public ResponserCloudACLPlatform(Agent ams, MessageTemplate mt, SecureCloudTPMServicePlatform secureCloudTPMService) {
        super(ams, mt);
        myService = secureCloudTPMService;
    }
}
