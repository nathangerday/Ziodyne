package interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;

public interface LampI extends OfferedI{
	public boolean isOn() throws Exception;
	public void switchButton() throws Exception;
}
