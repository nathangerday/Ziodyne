package simulation.models.battery;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;

public class MIL_Battery{
    public static void   main(String[] args){
        {
            SimulationEngine se ;

            try {
                Architecture localArchitecture = BatteryCoupledModel.build() ;
                se = localArchitecture.constructSimulator() ;

                se.setDebugLevel(0) ;
                System.out.println(se.simulatorAsString()) ;
                SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L ;
                se.doStandAloneSimulation(0.0, 500.0) ;
            } catch (Exception e) {
                throw new RuntimeException(e) ;
            }
        }
    }
}