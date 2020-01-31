package interfaces;

import fr.sorbonne_u.components.interfaces.RequiredI;

public interface FridgeControllerI extends RequiredI {
    public void switchFridgeBreak() throws Exception ;
    public boolean isFridgeOnBreak() throws Exception ;
    public void switchFreezerBreak() throws Exception ;
    public boolean isFreezerOnBreak() throws Exception ;
    public boolean isFridgeOn() throws Exception ;
    public boolean isFreezerOn() throws Exception ;
}
