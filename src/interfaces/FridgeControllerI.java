package interfaces;

import fr.sorbonne_u.components.interfaces.RequiredI;

public interface FridgeControllerI extends RequiredI {
    
    public void switchFridge() throws Exception ;
    public void switchFreezer() throws Exception ;
    public boolean isFridgeOn() throws Exception ;
    public boolean isFreezerOn() throws Exception ;
    public float getFridgeTemp() throws Exception ;
    public float getFreezerTemp() throws Exception ;
    public void setFridgeTemp(float t) throws Exception ;
    public void setFreezerTemp(float t) throws Exception ;
}
