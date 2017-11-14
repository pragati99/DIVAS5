package edu.utdallas.mavs.divas.core.spec.env;

import org.apache.log4j.PropertyConfigurator;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.core.sim.env.CellBounds;
import edu.utdallas.mavs.divas.core.sim.env.CellID;

public class ComplexRoom
{
    public static void main(String[] args)
    {
        // configure logging properties
        PropertyConfigurator.configure("log4j.properties");
        
        // configure logging properties
        PropertyConfigurator.configure("log4j.properties");
       
        EnvSpec envSpec = new EnvSpec(String.format("%s.xml", EnvSpecEnum.ComplexRoom), new EnvLoader());

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
        state.setDescription("grass");
        state.setModelName("grass");
        state.setType("floor");
        state.setMaterial("grass");
        state.setPosition(new Vector3f(0, .2f, 150));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(100, .1f, 25));
        state.setCollidable(false);
        cell.addEnvObject(state);

        state = new EnvObjectState();
        state.setDescription("box");
        state.setModelName("box");
        state.setType("box");
        state.setMaterial("brick");
        state.setPosition(new Vector3f(0, 0, 20));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(4, 3, 4));
        cell.addEnvObject(state);

        state = new EnvObjectState();
        state.setDescription("box");
        state.setModelName("box");
        state.setType("wall");
        state.setMaterial("cut20L");
        state.setPosition(new Vector3f(-50, 0, 0));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(.5f, 8, 50));
        cell.addEnvObject(state);

        state = new EnvObjectState();
        state.setDescription("box");
        state.setModelName("box");
        state.setType("wall");
        state.setMaterial("cut20L");
        state.setPosition(new Vector3f(50, 0, 0));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(.5f, 8, 50));
        cell.addEnvObject(state);

        state = new EnvObjectState();
        state.setDescription("box");
        state.setModelName("box");
        state.setType("wall");
        state.setMaterial("cut20L");
        state.setPosition(new Vector3f(0, 0, -50));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(50, 8, .5f));
        cell.addEnvObject(state);

        state = new EnvObjectState();
        state.setDescription("box");
        state.setModelName("box");
        state.setType("wall");
        state.setMaterial("cut20L");
        state.setPosition(new Vector3f(-25.5f, 0, 50));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(24, 8, .5f));
        cell.addEnvObject(state);

        state = new EnvObjectState();
        state.setDescription("box");
        state.setModelName("box");
        state.setType("wall");
        state.setMaterial("cut20L");
        state.setPosition(new Vector3f(25.5f, 0, 50));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(24, 8, .5f));
        cell.addEnvObject(state);

        state = new EnvObjectState();
        state.setDescription("door");
        state.setModelName("door");
        state.setType("door");
        state.setMaterial("door");
        state.setPosition(new Vector3f(0, 0, 50));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(2, 20, 2));
        state.setCollidable(false);
        cell.addEnvObject(state);

        state = new EnvObjectState();
        state.setDescription("box");
        state.setModelName("box");
        state.setType("wall");
        state.setMaterial("cut20L");
        state.setPosition(new Vector3f(-25.5f, 0, -20));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(24, 8, .5f));
        cell.addEnvObject(state);

        state = new EnvObjectState();
        state.setDescription("box");
        state.setModelName("box");
        state.setType("wall");
        state.setMaterial("cut20L");
        state.setPosition(new Vector3f(25.5f, 0, -20));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(24, 8, .5f));
        cell.addEnvObject(state);

        state = new EnvObjectState();
        state.setDescription("door");
        state.setModelName("door");
        state.setType("door");
        state.setMaterial("door");
        state.setPosition(new Vector3f(0, 0, -20));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(2, 20, 2));
        state.setCollidable(false);
        cell.addEnvObject(state);

        state = new EnvObjectState();
        state.setDescription("box");
        state.setModelName("box");
        state.setType("wall");
        state.setMaterial("cut20L");
        state.setPosition(new Vector3f(-20, 0, -25.5f));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(.5f, 8, 24));
        cell.addEnvObject(state);

        state = new EnvObjectState();
        state.setDescription("box");
        state.setModelName("box");
        state.setType("wall");
        state.setMaterial("cut20L");
        state.setPosition(new Vector3f(-20, 0, 25.5f));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(.5f, 8, 24));
        cell.addEnvObject(state);

        state = new EnvObjectState();
        state.setDescription("door");
        state.setModelName("door");
        state.setType("door");
        state.setMaterial("door");
        state.setPosition(new Vector3f(-20, 0, 0));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(2, 20, 2));
        state.setCollidable(false);
        cell.addEnvObject(state);

        state = new EnvObjectState();
        state.setDescription("box");
        state.setModelName("box");
        state.setType("wall");
        state.setMaterial("cut20L");
        state.setPosition(new Vector3f(20, 0, -25.5f));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(.5f, 8, 24));
        cell.addEnvObject(state);

        state = new EnvObjectState();
        state.setDescription("box");
        state.setModelName("box");
        state.setType("wall");
        state.setMaterial("cut20L");
        state.setPosition(new Vector3f(20, 0, 25.5f));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(.5f, 8, 24));
        cell.addEnvObject(state);

        state = new EnvObjectState();
        state.setDescription("door");
        state.setModelName("door");
        state.setType("door");
        state.setMaterial("door");
        state.setPosition(new Vector3f(20, 0, 0));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(2, 20, 2));
        state.setCollidable(false);
        cell.addEnvObject(state);

        state = new EnvObjectState();
        state.setDescription("table");
        state.setModelName("table");
        state.setType("table");
        state.setMaterial("");
        state.setPosition(new Vector3f(40, 0, 40));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(2, 2, 2));
        cell.addEnvObject(state);

        //envSpec.saveToFile();
        envSpec.saveToXML();
    }
}
