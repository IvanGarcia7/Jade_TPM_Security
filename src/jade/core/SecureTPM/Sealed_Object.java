package jade.core.SecureTPM;

import java.io.Serializable;

public class Sealed_Object implements Serializable {

    private static final long serialVersionUID = -346287437385146468L;
    private byte [] public_part;
    private byte [] private_part;
    private byte [] agent;

    public Sealed_Object(byte[] fileContentPublic, byte[] fileContentPrivate, byte[] agente) {
        public_part = fileContentPublic;
        private_part = fileContentPrivate;
        agent = agente;
    }

    public Sealed_Object(byte[] fileContentPublic, byte[] fileContentPrivate) {
        public_part=fileContentPublic;
        private_part=fileContentPrivate;
    }

    public byte[] get_public_part() {
        return public_part;
    }

    public byte[] get_private_part() {
        return private_part;
    }

    public byte[] get_agente() {
        return agent;
    }

}