package interfaces;

import fr.sorbonne_u.components.interfaces.RequiredI;

public interface ElectricMeterControllerI extends RequiredI{
    public int getConsommation() throws Exception;
}
