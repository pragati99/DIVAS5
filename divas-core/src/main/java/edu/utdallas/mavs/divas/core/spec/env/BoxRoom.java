package edu.utdallas.mavs.divas.core.spec.env;

import org.apache.log4j.PropertyConfigurator;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.core.sim.env.CellBounds;
import edu.utdallas.mavs.divas.core.sim.env.CellID;

public class BoxRoom
{
    public static void main(String[] args)
    {
        // configure logging properties
        PropertyConfigurator.configure("log4j.properties");
              
        EnvSpec envSpec = new EnvSpec(String.format("%s.xml", EnvSpecEnum.BoxRoom), new EnvLoader());

        CellStateSpec cell = new CellStateSpec(CellID.rootID(), new CellBounds(-200, 200, -200, 200, -200, 200));

        envSpec.setCellState(cell);

        EnvObjectState state;

        state = new EnvObjectState();
        state.setDescription("floor");
        state.setModelName("floor");
        state.setType("floor");
        state.setMaterial("cement");
        state.setPosition(new Vector3f(0, 0, 0));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(200, .1f, 200));
        state.setCollidable(false);
        cell.addEnvObject(state);

        state = new EnvObjectState();
        state.setDescription("box");
        state.setModelName("box");
        state.setType("box");
        state.setMaterial("brick");
        state.setPosition(new Vector3f(0, 0, 0));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(4, 3, 4));
        cell.addEnvObject(state);

        state = new EnvObjectState();
        state.setDescription("box");
        state.setModelName("box");
        state.setType("box");
        state.setMaterial("brick");
        state.setPosition(new Vector3f(15, 0, 0));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(4, 3, 4));
        cell.addEnvObject(state);

        state = new EnvObjectState();
        state.setDescription("box");
        state.setModelName("box");
        state.setType("box");
        state.setMaterial("brick");
        state.setPosition(new Vector3f(0, 0, 15));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(4, 3, 4));
        cell.addEnvObject(state);

        state = new EnvObjectState();
        state.setDescription("box");
        state.setModelName("box");
        state.setType("box");
        state.setMaterial("brick");
        state.setPosition(new Vector3f(15, 0, 15));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(4, 3, 4));
        cell.addEnvObject(state);

        //envSpec.saveToFile();
        envSpec.saveToXML();
    }
}
