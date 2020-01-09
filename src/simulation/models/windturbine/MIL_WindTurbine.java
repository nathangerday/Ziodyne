package simulation.models.windturbine;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.examples.molene.SimulationMain;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.utils.PlotterDescription;
import simulation.models.common.TicModel;

public class MIL_WindTurbine {
    public static void   main(String[] args){
        SimulationEngine se ;

        try {
            Architecture localArchitecture = WindTurbineCoupledModel.build() ;
            se = localArchitecture.constructSimulator() ;
            se.setDebugLevel(0) ;
            System.out.println(se.simulatorAsString()) ;

            //**************
            //Run parameters
            //**************

            Map<String, Object> simParams = new HashMap<String, Object>() ;
            //Parameter of TicModel
            simParams.put(TicModel.URI_WINDTURBINE + ":" + TicModel.DELAY_PARAMETER_NAME,
                    new Duration(10.0, TimeUnit.SECONDS));
            //Parameters of WindModel
            simParams.put(WindModel.URI + ":" + WindModel.MAX_WIND,15.0);
            simParams.put(WindModel.URI + ":" + WindModel.WIS,10.0);
            simParams.put(WindModel.URI + ":" + WindModel.WMASSF,3.5);
            simParams.put(
                    WindModel.URI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
                    new PlotterDescription(
                            "Wind Speed Model",
                            "Time (sec)",
                            "Speed (m/s)",
                            SimulationMain.ORIGIN_X,
                            SimulationMain.ORIGIN_Y + 2*SimulationMain.getPlotterHeight(),
                            SimulationMain.getPlotterWidth(),
                            SimulationMain.getPlotterHeight())) ;
            //Parameters of WindSendsorModel
            simParams.put(
                    WindSensorModel.URI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
                    new PlotterDescription(
                            "Wind Sensor Speed Model",
                            "Time (sec)",
                            "Speed (m/s)",
                            SimulationMain.ORIGIN_X,
                            SimulationMain.ORIGIN_Y + 3*SimulationMain.getPlotterHeight(),
                            SimulationMain.getPlotterWidth(),
                            SimulationMain.getPlotterHeight())) ;
            //Parameters of WindTurbineControllerModel
            simParams.put(WindTurbineControllerModel.URI + ":" + WindTurbineControllerModel.MAX_SPEED,10.0);
            simParams.put(WindTurbineControllerModel.URI + ":" + WindTurbineControllerModel.MIN_SPEED,3.0);

            se.setSimulationRunParameters(simParams);
            SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L ;
            se.doStandAloneSimulation(0.0, 500.0) ;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
