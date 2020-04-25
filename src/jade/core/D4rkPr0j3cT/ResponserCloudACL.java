package jade.core.D4rkPr0j3cT;

import jade.core.Agent;
import jade.core.BaseService;
import jade.lang.acl.MessageTemplate;
import jade.proto.SimpleAchieveREResponder;
import jade.util.Logger;

public class ResponserCloudACL extends SimpleAchieveREResponder {

    private Logger logger = Logger.getMyLogger(getClass().getName());
    private BaseService myService;


    public ResponserCloudACL(Agent ams, MessageTemplate mt, SecureCloudTPMService secureCloudTPMService) {
        super(ams, mt);
        myService = secureCloudTPMService;
    }
}
