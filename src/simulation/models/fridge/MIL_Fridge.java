package simulation.models.fridge;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import simulation.models.common.TicModel;

public class MIL_Fridge {
	
	    public static void	main(String[] args)
	    {
	        SimulationEngine se ;

	        try {
	            Architecture localArchitecture = FridgeCoupledModel.build() ;
	            se = localArchitecture.constructSimulator() ;
	            
	            //Run parameters
	            Map<String, Object> simParams = new HashMap<String, Object>() ;
	            simParams.put(TicModel.URI_FRIDGE + ":" + TicModel.DELAY_PARAMETER_NAME,
	                    new Duration(10.0, TimeUnit.SECONDS));

	            se.setSimulationRunParameters(simParams);
	            se.setDebugLevel(0) ;
	            System.out.println(se.simulatorAsString()) ;
	            SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L ;
	            se.doStandAloneSimulation(0.0, 500.0) ;
	        } catch (Exception e) {
	            throw new RuntimeException(e) ;
	        }
	    }
}
