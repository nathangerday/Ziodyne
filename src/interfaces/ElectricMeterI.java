package interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;

public interface ElectricMeterI extends OfferedI{
    public int getConsommation() throws Exception;
}
