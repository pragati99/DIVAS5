package edu.utdallas.mavs.evacuation.simulation.spec.env;

import org.apache.log4j.PropertyConfigurator;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.core.sim.env.CellBounds;
import edu.utdallas.mavs.divas.core.sim.env.CellID;
import edu.utdallas.mavs.divas.core.spec.env.CellStateSpec;
import edu.utdallas.mavs.divas.core.spec.env.EnvLoader;
import edu.utdallas.mavs.divas.core.spec.env.EnvSpec;

public class SimpleRoom
{
    public static void main(String[] args)
    {
        // configure logging properties
        PropertyConfigurator.configure("log4j.properties");

        EnvSpec envSpec = new EnvSpec(String.format("%s.xml", EvacuationEnvSpecEnum.SimpleRoom), new EnvLoader());
 
        CellStateSpec cell = new CellStateSpec(CellID.rootID(), new CellBounds(-400, 400, -400, 400, -400, 400));

        envSpec.setCellState(cell);

        EnvObjectState state;

        state = new EnvObjectState();
        state.setDescription("floor");
        state.setModelName("floor");
        state.setType("floor");
        state.setMaterial("cement");
        state.setPosition(new Vector3f(0, 0, 0));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(400, .1f, 400));
        state.setCollidable(false);
        cell.addEnvObject(state);

        //envSpec.saveToFile();
        envSpec.saveToXML();
    }
}
