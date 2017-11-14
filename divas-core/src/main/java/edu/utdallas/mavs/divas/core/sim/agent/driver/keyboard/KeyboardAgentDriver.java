/*
 * File URL : $HeadURL:
 * https://mavs.utdallas.edu/svn/divas/branches/travis_diss/src/divas/sim/agent/driver/keyboard/KeyboardAgentDriver.java
 * $
 * Revision : $Rev: 584 $
 * Last modified at: $Date: 2010-10-03 01:07:32 -0500 (Sun, 03 Oct 2010) $
 * Last modified by: $Author: CAMPUS\tls022100 $
 */

package edu.utdallas.mavs.divas.core.sim.agent.driver.keyboard;

import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;

import javax.swing.JFrame;
import javax.swing.JLabel;

import edu.utdallas.mavs.divas.core.config.SimConfig;
import edu.utdallas.mavs.divas.core.sim.agent.driver.AgentDriver;

/**
 * This class is responsible for creating a {@link JFrame} for an agent and allowing the agent to be controlled via the
 * keyboard.
 * <p>
 * This class implements {@link AgentDriver} and {@link KeyListener}.
 */
public class KeyboardAgentDriver extends JFrame implements AgentDriver, KeyListener, Serializable
{
    private static final long serialVersionUID   = 1L;

    private final int         FORW               = 0;
    private final int         BACK               = 1;
    private final int         L_LAT              = 2;
    private final int         R_LAT              = 3;
    private final int         L_ROT              = 4;
    private final int         R_ROT              = 5;

    private float             forwardDelta       = 0;
    private float             rotationDelta      = 0;
    private float             lateralDelta       = 0;

    private float             forwardMultiplier  = SimConfig.getInstance().keyboard_Motion_Multiplier;
    private float             rotationMultiplier = SimConfig.getInstance().keyboard_Rotation_Multiplier;
    private float             lateralMultiplier  = SimConfig.getInstance().keyboard_Motion_Multiplier;

    // store when key press events occurred so we can determine how long they have been pressed
    // -1 denotes that the key is not currently down
    private long              pressTime[]        = { -1, -1, -1, -1, -1, -1 };

    /**
     * Creates a new keyboard agent driver instance for the given agent id.
     * 
     * @param agentID
     *        The id of the agent that will be controlled by the keyboard.
     */
    public KeyboardAgentDriver(int agentID)
    {
        super("DIVAs Keyboard Agent Driver");
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLayout(new GridLayout(0, 2));

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

        this.addKeyListener(this);

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
    public void keyPressed(KeyEvent e)
    {
        int keyIndex = getKeyIndex(e.getKeyCode());

        if(keyIndex >= 0)
        {
            // if the key is not down already (prevents corruption when repeated press events occur while key is held
            // down)
            if(pressTime[keyIndex] < 0)
            {
                // store the time this key was pressed in the pressTime array
                pressTime[keyIndex] = e.getWhen();
            }
        }
    }

    /**
     * Maps KeyEvent values to pressTime array index value
     * 
     * @param key
     *        - the KeyEvent's key code
     * @return the pressTime/delta array index value or -1 if the key does not have an index
     */
    public int getKeyIndex(int key)
    {
        int index = -1;

        switch(key)
        {
        case KeyEvent.VK_UP: // forwards
            index = FORW;
            break;
        case KeyEvent.VK_DOWN: // backwards
            index = BACK;
            break;
        case KeyEvent.VK_LEFT: // rotate left
            index = L_ROT;
            break;
        case KeyEvent.VK_RIGHT: // rotate right
            index = R_ROT;
            break;
        case KeyEvent.VK_COMMA: // lateral left
            index = L_LAT;
            break;
        case KeyEvent.VK_PERIOD: // lateral right
            index = R_LAT;
            break;
        }

        return index;
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        int pressTimeIndex = getKeyIndex(e.getKeyCode());

        if(pressTimeIndex >= 0)
        {
            // calculate the time that has passed since the key was pressed
            long duration = e.getWhen() - pressTime[pressTimeIndex];

            // reset the press time to -1 to indicate the key is no longer being pressed
            pressTime[pressTimeIndex] = -1;

            switch(pressTimeIndex)
            {
            case FORW: // forwards
                forwardDelta += duration * forwardMultiplier;
                break;
            case BACK: // backwards
                forwardDelta -= duration * forwardMultiplier;
                break;
            case L_ROT: // rotate left
                rotationDelta -= duration * rotationMultiplier;
                break;
            case R_ROT: // rotate right
                rotationDelta += duration * rotationMultiplier;
                break;
            case L_LAT: // lateral left
                lateralDelta -= duration * lateralMultiplier;
                break;
            case R_LAT: // lateral right
                lateralDelta += duration * lateralMultiplier;
                break;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
        // do nothing
    }

    @Override
    public void close()
    {
        this.dispose();
    }
}
