package interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;

public interface ElectricMeterI extends OfferedI{
    public double getAvailableEnergy() throws Exception;
    public double getProduction() throws Exception;
    public double getConsumption() throws Exception;
}
