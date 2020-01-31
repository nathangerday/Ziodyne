package interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;

public interface FridgeI extends OfferedI {
    public void switchFridgeBreak() throws Exception ;
    public boolean isFridgeOnBreak() throws Exception ;
    public void switchFreezerBreak() throws Exception ;
    public boolean isFreezerOnBreak() throws Exception ;
    public boolean isFridgeOn() throws Exception ;
    public boolean isFreezerOn() throws Exception ;
}
