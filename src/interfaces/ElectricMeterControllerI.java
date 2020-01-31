package interfaces;

import fr.sorbonne_u.components.interfaces.RequiredI;

public interface ElectricMeterControllerI extends RequiredI{
    public double getAvailableEnergy() throws Exception;
    public double getProduction() throws Exception;
    public double getConsumption() throws Exception;
}
