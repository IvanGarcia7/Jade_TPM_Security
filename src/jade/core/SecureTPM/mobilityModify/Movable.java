package jade.core.SecureTPM.mobilityModify;

public interface Movable extends jade.util.leap.Serializable {
	void beforeMove();
	void afterMove();
	void beforeClone();
	void afterClone();
}
