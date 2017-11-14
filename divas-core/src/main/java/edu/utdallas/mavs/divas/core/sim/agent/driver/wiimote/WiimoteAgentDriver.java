package edu.utdallas.mavs.divas.core.sim.agent.driver.wiimote;

import java.awt.GridLayout;
import java.io.Serializable;

import javax.swing.JFrame;
import javax.swing.JLabel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wiiusej.WiiUseApiManager;
import wiiusej.Wiimote;
import wiiusej.wiiusejevents.physicalevents.ExpansionEvent;
import wiiusej.wiiusejevents.physicalevents.IREvent;
import wiiusej.wiiusejevents.physicalevents.MotionSensingEvent;
import wiiusej.wiiusejevents.physicalevents.WiimoteButtonsEvent;
import wiiusej.wiiusejevents.utils.WiimoteListener;
import wiiusej.wiiusejevents.wiiuseapievents.ClassicControllerInsertedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.ClassicControllerRemovedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.DisconnectionEvent;
import wiiusej.wiiusejevents.wiiuseapievents.GuitarHeroInsertedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.GuitarHeroRemovedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.NunchukInsertedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.NunchukRemovedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.StatusEvent;
import edu.utdallas.mavs.divas.core.config.SimConfig;
import edu.utdallas.mavs.divas.core.sim.agent.driver.AgentDriver;

/**
 * This class is responsible for creating a {@link JFrame} for an agent and allowing the agent to be controlled via the
 * Wiimote.
 * <p>
 * This class implements {@link AgentDriver} and {@link WiimoteListener}.
 */
public class WiimoteAgentDriver extends JFrame implements AgentDriver, WiimoteListener, Serializable
{
    private final static Logger logger             = LoggerFactory.getLogger(WiimoteAgentDriver.class);

    private static final long   serialVersionUID   = 1L;

    private final int           FORW               = 0;
    private final int           BACK               = 1;
    private final int           L_LAT              = 2;
    private final int           R_LAT              = 3;
    private final int           L_ROT              = 4;
    private final int           R_ROT              = 5;

    private float               forwardDelta       = 0;
    private float               rotationDelta      = 0;
    private float               lateralDelta       = 0;

    private float               forwardMultiplier  = SimConfig.getInstance().keyboard_Motion_Multiplier;
    private float               rotationMultiplier = SimConfig.getInstance().keyboard_Rotation_Multiplier;
    private float               lateralMultiplier  = SimConfig.getInstance().keyboard_Motion_Multiplier;

    // store when key press events occurred so we can determine how long they have been pressed
    // -1 denotes that the key is not currently down
    private long                pressTime[]        = { -1, -1, -1, -1, -1, -1 };

    /**
     * Creates a new Wiimote agent driver instance for the given agent id.
     * 
     * @param agentID
     *        The id of the agent that will be controlled by the Wiimote.
     */
    public WiimoteAgentDriver(int agentID)
    {
        super("DIVAs Wiimote Agent Driver");
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLayout(new GridLayout(0, 2));

        System.loadLibrary("wiiuse");

        Wiimote wiimote = WiiUseApiManager.getWiimotes(1, false)[0];
        wiimote.addWiiMoteEventListeners(this);
        wiimote.activateMotionSensing();

        this.add(new JLabel("Agent ID:"));
        this.add(new JLabel(String.valueOf(agentID)));
        this.add(new JLabel("Move Forward:"));
        this.add(new JLabel("Up"));
        this.add(new JLabel("Move Backward:"));
        this.add(new JLabel("Down"));
        this.add(new JLabel("Turn Left:"));
        this.add(new JLabel("Left"));
        this.add(new JLabel("Turn Right:"));
        this.add(new JLabel("Right"));
        this.add(new JLabel("Strafe Left:"));
        this.add(new JLabel("Comma"));
        this.add(new JLabel("Strafe Right:"));
        this.add(new JLabel("Period"));

        this.setLocationRelativeTo(null);
        this.setSize(300, 180);

        this.setVisible(true);
    }

    /**
     * Gets the current forward delta.
     * 
     * @return the forward delta.
     */
    public float getForwardDelta()
    {
        return forwardDelta;
    }

    /**
     * Gets the current rotation delta.
     * 
     * @return the rotation delta.
     */
    public float getRotationDelta()
    {
        return rotationDelta;
    }

    /**
     * Gets the current lateral delta.
     * 
     * @return the current lateral delta.
     */
    public float getLateralDelta()
    {
        return lateralDelta;
    }

    /*
     * (non-Javadoc) Get the current forward delta and then reset it to 0
     * @see divas.sim.agent.driver.IAgentDriver#grabForwardDelta()
     */
    @Override
    public float grabForwardDelta()
    {
        long now = System.currentTimeMillis();

        // if forward is still being pressed
        if(pressTime[FORW] >= 0)
        {
            // get the delta value according to the duration it has been pressed
            forwardDelta += (now - pressTime[FORW]) * forwardMultiplier;

            // reset the pressTime to now since we have already accounted for the time up to now
            pressTime[FORW] = now;
        }

        // if BACK is still being pressed
        if(pressTime[BACK] >= 0)
        {
            // get the delta value according to the duration it has been pressed
            forwardDelta -= (now - pressTime[BACK]) * forwardMultiplier;

            // reset the pressTime to now since we have already accounted for the time up to now
            pressTime[BACK] = now;
        }

        float delta = forwardDelta; // store return value
        forwardDelta = 0; // reset forward delta
        logger.debug("Forward Delta = " + delta);
        return delta;
    }

    /*
     * (non-Javadoc) Get the current rotation delta and then reset it to 0
     * @see divas.sim.agent.driver.IAgentDriver#grabRotationDelta()
     */
    @Override
    public float grabRotationDelta()
    {
        long now = System.currentTimeMillis();

        // if rotate right is still being pressed
        if(pressTime[R_ROT] >= 0)
        {
            // get the delta value according to the duration it has been pressed
            rotationDelta += (now - pressTime[R_ROT]) * rotationMultiplier;

            // reset the pressTime to now since we have already accounted for the time up to now
            pressTime[R_ROT] = now;
        }

        // if rotate left is still being pressed
        if(pressTime[L_ROT] >= 0)
        {
            // get the delta value according to the duration it has been pressed
            rotationDelta -= (now - pressTime[L_ROT]) * rotationMultiplier;

            // reset the pressTime to now since we have already accounted for the time up to now
            pressTime[L_ROT] = now;
        }

        float delta = rotationDelta; // store return value
        rotationDelta = 0; // reset rotation delta
        return delta;
    }

    /*
     * (non-Javadoc) Get the current lateral delta and then reset it to 0
     * @see divas.sim.agent.driver.IAgentDriver#grabLateralDelta()
     */
    @Override
    public float grabLateralDelta()
    {
        long now = System.currentTimeMillis();

        // if lateral right is still being pressed
        if(pressTime[R_LAT] >= 0)
        {
            // get the delta value according to the duration it has been pressed
            lateralDelta += (now - pressTime[R_LAT]) * lateralMultiplier;

            // reset the pressTime to now since we have already accounted for the time up to now
            pressTime[R_LAT] = now;
        }

        // if lateral left is still being pressed
        if(pressTime[L_LAT] >= 0)
        {
            // get the delta value according to the duration it has been pressed
            lateralDelta -= (now - pressTime[L_LAT]) * lateralMultiplier;

            // reset the pressTime to now since we have already accounted for the time up to now
            pressTime[L_LAT] = now;
        }

        float delta = lateralDelta; // store return value
        lateralDelta = 0; // reset lateral delta
        return delta;
    }

    @Override
    public void close()
    {
        this.dispose();
    }

    @Override
    public void onButtonsEvent(WiimoteButtonsEvent e)
    {
        long now = System.currentTimeMillis();

        if(e.isButtonUpJustPressed())
        {
            logger.debug("FORWARD START");
            pressTime[FORW] = now;
        }
        if(e.isButtonDownJustPressed())
        {
            logger.debug("BACKWARD START: " + now);
            pressTime[BACK] = now;
        }
        if(e.isButtonLeftJustPressed())
        {
            pressTime[L_LAT] = now;
        }
        if(e.isButtonRightJustPressed())
        {
            pressTime[R_LAT] = now;
        }

        if(e.isButtonUpJustReleased())
        {
            logger.debug("FORWARD END: " + now);
            forwardDelta += (now - pressTime[FORW]) * forwardMultiplier;
            pressTime[FORW] = -1;
        }
        if(e.isButtonDownJustReleased())
        {
            logger.debug("BACKWARD END");
            forwardDelta -= (now - pressTime[BACK]) * forwardMultiplier;
            pressTime[BACK] = -1;
        }
        if(e.isButtonLeftJustReleased())
        {
            lateralDelta -= (now - pressTime[L_LAT]) * lateralMultiplier;
            pressTime[L_LAT] = -1;
        }
        if(e.isButtonRightJustReleased())
        {
            lateralDelta += (now - pressTime[R_LAT]) * lateralMultiplier;
            pressTime[R_LAT] = -1;
        }
    }

    @Override
    public void onClassicControllerInsertedEvent(ClassicControllerInsertedEvent arg0)
    {}

    @Override
    public void onClassicControllerRemovedEvent(ClassicControllerRemovedEvent arg0)
    {}

    @Override
    public void onDisconnectionEvent(DisconnectionEvent arg0)
    {}

    @Override
    public void onExpansionEvent(ExpansionEvent arg0)
    {}

    @Override
    public void onGuitarHeroInsertedEvent(GuitarHeroInsertedEvent arg0)
    {}

    @Override
    public void onGuitarHeroRemovedEvent(GuitarHeroRemovedEvent arg0)
    {}

    @Override
    public void onIrEvent(IREvent arg0)
    {}

    @Override
    public void onMotionSensingEvent(MotionSensingEvent e)
    {
        long now = System.currentTimeMillis();

        if(e.getOrientation().getRoll() < -20)
        {
            if(pressTime[L_ROT] < 0)
                pressTime[L_ROT] = now;

            if(pressTime[R_ROT] >= 0)
            {
                rotationDelta += (now - pressTime[R_ROT]) * rotationMultiplier;
                pressTime[R_ROT] = -1;
            }
        }
        else if(e.getOrientation().getRoll() > 20)
        {
            if(pressTime[R_ROT] < 0)
                pressTime[R_ROT] = now;

            if(pressTime[L_ROT] >= 0)
            {
                rotationDelta -= (now - pressTime[L_ROT]) * rotationMultiplier;
                pressTime[L_ROT] = -1;
            }
        }
        else
        {
            if(pressTime[R_ROT] >= 0)
            {
                rotationDelta += (now - pressTime[R_ROT]) * rotationMultiplier;
                pressTime[R_ROT] = -1;
            }
            if(pressTime[L_ROT] >= 0)
            {
                rotationDelta -= (now - pressTime[L_ROT]) * rotationMultiplier;
                pressTime[L_ROT] = -1;
            }
        }
    }

    @Override
    public void onNunchukInsertedEvent(NunchukInsertedEvent arg0)
    {}

    @Override
    public void onNunchukRemovedEvent(NunchukRemovedEvent arg0)
    {}

    @Override
    public void onStatusEvent(StatusEvent arg0)
    {}
}
