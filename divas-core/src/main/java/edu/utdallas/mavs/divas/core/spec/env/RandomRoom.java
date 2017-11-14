package edu.utdallas.mavs.divas.core.spec.env;

import java.util.HashMap;
import java.util.Random;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.core.sim.env.CellBounds;
import edu.utdallas.mavs.divas.core.sim.env.CellID;

public class RandomRoom
{
    private static final Logger      logger        = LoggerFactory.getLogger(RandomRoom.class);

    private static final int         SEED          = 4447;

    static CellStateSpec             cell;
    static int                       wallSize      = 300;
    static Random                    r;

    static float                     doorChance    = .30f;
    static float                     blockChance   = .20f;

    static int                       roomsize      = 30;

    static HashMap<Integer, Boolean> slotv         = new HashMap<Integer, Boolean>();

    static HashMap<Integer, Boolean> sloth         = new HashMap<Integer, Boolean>();

    static int                       doorcount     = 0;
    static int                       obstaclecount = 0;

    public static void main(String[] args)
    {
        // configure logging properties
        PropertyConfigurator.configure("log4j.properties");

        for(int i = -wallSize; i < wallSize; i++)
        {
            slotv.put(i, false);
        }

        for(int i = -wallSize; i < wallSize; i++)
        {
            sloth.put(i, false);
        }

        r = new Random(SEED);

        EnvSpec envSpec = new EnvSpec(String.format("%s.xml", EnvSpecEnum.RandomRoom), new EnvLoader());

        cell = new CellStateSpec(CellID.rootID(), new CellBounds(-wallSize * 2, wallSize * 2, -wallSize * 2, wallSize * 2, -wallSize * 2, wallSize * 2));

        envSpec.setCellState(cell);

        makeFloor();

        makeRoom();

        //envSpec.saveToFile();
        envSpec.saveToXML();

        logger.info("Created environment with {} doors, {} obstacles", doorcount, obstaclecount);
    }

    private static void makeVDooredSection(float location, float location2)
    {
        EnvObjectState state;

        state = new EnvObjectState();
        state.setDescription("box");
        state.setModelName("box");
        state.setType("wall");
        state.setMaterial("cut20L");
        state.setPosition(new Vector3f(location, 0, location2 - 8.5f));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(.5f, 8, 6.5f));
        cell.addEnvObject(state);

        state = new EnvObjectState();
        state.setDescription("door");
        state.setModelName("door");
        state.setType("door");
        state.setMaterial("door");
        state.setPosition(new Vector3f(location, 0, location2));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(.5f, 8, 2));
        state.setCollidable(false);
        cell.addEnvObject(state);
        doorcount++;

        state = new EnvObjectState();
        state.setDescription("box");
        state.setModelName("box");
        state.setType("wall");
        state.setMaterial("cut20L");
        state.setPosition(new Vector3f(location, 0, location2 + 8.5f));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(.5f, 8, 6.5f));
        cell.addEnvObject(state);

    }

    private static void makeVWalledSection(float location, float location2)
    {
        EnvObjectState state;

        state = new EnvObjectState();
        state.setDescription("box");
        state.setModelName("box");
        state.setType("wall");
        state.setMaterial("cut20L");
        state.setPosition(new Vector3f(location, 0, location2));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(.5f, 8, 15));
        cell.addEnvObject(state);
    }

    private static void makeHWalledSection(float location, float location2)
    {
        EnvObjectState state;

        state = new EnvObjectState();
        state.setDescription("box");
        state.setModelName("box");
        state.setType("wall");
        state.setMaterial("cut20L");
        state.setPosition(new Vector3f(location2, 0, location));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(15, 8, .5f));
        cell.addEnvObject(state);
    }

    private static void makeHDooredSection(float location, float location2)
    {
        EnvObjectState state;

        state = new EnvObjectState();
        state.setDescription("box");
        state.setModelName("box");
        state.setType("wall");
        state.setMaterial("cut20L");
        state.setPosition(new Vector3f(location2 - 8.5f, 0, location));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(6.5f, 8, .5f));
        cell.addEnvObject(state);

        state = new EnvObjectState();
        state.setDescription("door");
        state.setModelName("door");
        state.setType("door");
        state.setMaterial("door");
        state.setPosition(new Vector3f(location2, 0, location));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(2, 8, .5f));
        state.setCollidable(false);
        cell.addEnvObject(state);
        doorcount++;

        state = new EnvObjectState();
        state.setDescription("box");
        state.setModelName("box");
        state.setType("wall");
        state.setMaterial("cut20L");
        state.setPosition(new Vector3f(location2 + 8.5f, 0, location));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(6.5f, 8, .5f));
        cell.addEnvObject(state);

    }

    private static void makeRoom()
    {

        for(int i = -wallSize + roomsize; i < wallSize; i = i + roomsize)
        {
            if(r.nextFloat() > .3)
            {
                makeVWall(i);
            }
            if(r.nextFloat() > .3)
            {
                makeHWall(i);
            }
        }
        makeOutsideWalls();

        makeBlocks();

    }

    private static void makeBlocks()
    {
        EnvObjectState state;
        for(int i = -wallSize + 15; i < wallSize; i = i + roomsize)
        {
            for(int j = -wallSize + 15; j < wallSize; j = j + roomsize)
            {
                if(r.nextFloat() < blockChance)
                {
                    state = new EnvObjectState();
                    state.setDescription("box");
                    state.setModelName("box");
                    state.setType("box");
                    state.setMaterial("brick");
                    state.setPosition(new Vector3f(i, 0, j));
                    state.setRotation(new Quaternion(0, 0, 0, 0));
                    state.setScale(new Vector3f(r.nextInt(3) + 1, 3, r.nextInt(3) + 1));
                    cell.addEnvObject(state);
                    obstaclecount++;
                }
            }
        }
    }

    private static void makeOutsideWalls()
    {
        makeOutsideVWall(wallSize);
        makeOutsideVWall(-wallSize);
        makeOutsideHWall(wallSize);
        makeOutsideHWall(-wallSize);
    }

    private static void makeVWall(int location)
    {

        for(int i = -wallSize + 15; i < wallSize; i = i + roomsize)
        {

            if(i + roomsize > wallSize)
            {
                slotv.put(i, false);
            }

            if(slotv.get(i) == false)
            {
                makeVDooredSection(location, i);
                slotv.put(i, true);
            }
            else
            {
                if(r.nextFloat() > doorChance)
                {
                    makeVWalledSection(location, i);
                    slotv.put(i, false);
                }
                else
                {
                    makeVDooredSection(location, i);
                    slotv.put(i, true);
                }
            }
        }

    }

    private static void makeHWall(int location)
    {

        for(int i = -wallSize + 15; i < wallSize; i = i + roomsize)
        {
            if(i + roomsize > wallSize)
            {
                sloth.put(i, false);
            }

            if(sloth.get(i) == false)
            {
                makeHDooredSection(location, i);
                sloth.put(i, true);
            }
            else
            {
                if(r.nextFloat() > doorChance)
                {
                    makeHWalledSection(location, i);
                    sloth.put(i, false);
                }
                else
                {

                    makeHDooredSection(location, i);
                    sloth.put(i, true);
                }
            }
        }

    }

    private static void makeOutsideVWall(float location)
    {
        EnvObjectState state;
        state = new EnvObjectState();
        state.setDescription("box");
        state.setModelName("box");
        state.setType("wall");
        state.setMaterial("cut20L");
        state.setPosition(new Vector3f(location, 0, 0));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(.5f, 8, wallSize));
        cell.addEnvObject(state);
    }

    private static void makeOutsideHWall(float location)
    {
        EnvObjectState state;
        state = new EnvObjectState();
        state.setDescription("box");
        state.setModelName("box");
        state.setType("wall");
        state.setMaterial("cut20L");
        state.setPosition(new Vector3f(0, 0, -location));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(wallSize, 8, .5f));
        cell.addEnvObject(state);
    }

    private static void makeFloor()
    {

        EnvObjectState state;
        state = new EnvObjectState();
        state.setDescription("floor");
        state.setModelName("floor");
        state.setType("floor");
        state.setMaterial("cement");
        state.setPosition(new Vector3f(0, 0, 0));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(wallSize, .1f, wallSize));
        state.setCollidable(false);
        cell.addEnvObject(state);

        state = new EnvObjectState();
        state.setDescription("grass");
        state.setModelName("grass");
        state.setType("floor");
        state.setMaterial("grass");
        state.setPosition(new Vector3f(0, -.2f, 0));
        state.setRotation(new Quaternion(0, 0, 0, 0));
        state.setScale(new Vector3f(wallSize + 100, .1f, wallSize + 100));
        state.setCollidable(false);
        cell.addEnvObject(state);
    }
}
