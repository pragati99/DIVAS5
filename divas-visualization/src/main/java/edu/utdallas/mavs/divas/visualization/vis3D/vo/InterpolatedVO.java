package edu.utdallas.mavs.divas.visualization.vis3D.vo;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.utils.StatsHelper;

/**
 * This class describes an abstract interpolated VO.
 * <p>
 * It provides the basic functionalities for interpolating VOs in the 3D visualization. By interpolating the VOs position, rotation, and scale between simulation updates, the VOs are perceived as moving continuously and smoothly in the screen. The
 * interpolation algorithm used is basic, providing a linear time interpolation from the last updated position, rotation, and scale.
 */
public abstract class InterpolatedVO extends BaseVO
{
    /**
     * For how long, since the last received update, should VOs interpolate
     */
    private static final int INTERPOLATION_TIME   = 1000;

    /**
     * Global flag indicating if interpolation is enabled
     */
    private static boolean   interpolationEnabled = true;

    /**
     * Flag indicating if this VO is to be interpolated or not. A VO is interpolated when this flag is <code>true</code> and <code>INTERPOLATION_ENABLED</code> is <code>true</code>.
     */
    protected boolean        interpolated         = true;

    private boolean          firstUpdateReceived  = false;
    private long             updateInterval;
    private long             updateTime           = System.currentTimeMillis();

    // use these values to interpolate position, rotation, and scale between state updates
    private Vector3f         prevTranslation;
    private Quaternion       prevRotation;
    private Vector3f         prevScale;
    private Vector3f         nextTranslation;
    private Quaternion       nextRotation;
    private Vector3f         nextScale;

    private float            lastInterp           = 0f;
    private StatsHelper      avgUpdateInterval    = new StatsHelper(10);

    /**
     * Constructs a new interpolated VO
     * 
     * @param nodeName
     *        the name of the VO
     * @param cycle
     *        the current cycle of the simulation
     */
    public InterpolatedVO(String nodeName, long cycle)
    {
        super(nodeName, cycle);
    }

    /**
     * Constructs an empty interpolated VO
     */
    public InterpolatedVO()
    {
        super();
    }

    /**
     * Updates the state of this VO
     * 
     * @param translation
     *        the new translation of this VO
     * @param rotation
     *        the new rotation of this VO
     * @param scale
     *        the new scale of this VO
     */
    protected void updateState(Vector3f translation, Quaternion rotation, Vector3f scale)
    {
        if(isInterpolationEnabled())
        {
            long prevUpdateTime = updateTime;
            updateTime = System.currentTimeMillis();
            updateInterval = (updateTime - prevUpdateTime);
            avgUpdateInterval.add((float) updateInterval);

            // if this is the first update
            if(!firstUpdateReceived)
            {
                prevTranslation = translation.clone();
                prevRotation = rotation.clone();
                prevScale = scale.clone();
            }
            else
            {
                prevTranslation = nextTranslation;
                prevRotation = nextRotation;
                prevScale = nextScale;
            }
            nextTranslation = translation.clone();
            nextRotation = rotation.clone();
            nextScale = scale.clone();
            firstUpdateReceived = true;
        }
        else
        {
            setLocalTranslation(translation);
            setLocalRotation(rotation);
            setLocalScale(scale);

        }
        // Update with last updated time
        // lastUpdated = System.currentTimeMillis();
    }

    /**
     * Interpolates the position, rotation, and scale of this VO
     * 
     * @param now
     *        the time to which the VO should be interpolated
     */
    public void interpolate(long now)
    {
        if(firstUpdateReceived)
        {
            long interval = now - updateTime;
            if(isInterpolationEnabled() && (interval < INTERPOLATION_TIME))
            {
                float interp = interval / avgUpdateInterval.getAverage();

                interp = (Float.isNaN(interp) || Float.isInfinite(interp)) ? lastInterp : interp;
                interp = Math.min(interp, 1f);
                interp = Math.max(interp, 0f);

                getLocalTranslation().interpolate(prevTranslation, nextTranslation, interp);
                setLocalTranslation(getLocalTranslation());

                // FIXME: why does rotation not work for attached children when slerping
                getLocalRotation().slerp(prevRotation, nextRotation, interp);
                setLocalRotation(getLocalRotation());

                getLocalScale().interpolate(prevScale, nextScale, interp);
                setLocalScale(getLocalScale());
                lastInterp = interp;
            }
            updateSelfObjects();
        }

    }

    protected void updateSelfObjects()
    {
        // nothing to do
    }

    /**
     * Enables or disables interpolation of VOs
     * 
     * @param interpolationEnabled
     *        a flag indicating if the application should interpolate VOs
     */
    public static void setInterpolationEnabled(boolean interpolationEnabled)
    {
        InterpolatedVO.interpolationEnabled = interpolationEnabled;
    }

    /**
     * Checks if interpolation is enabled for this VO
     * 
     * @return a boolean flag indicating if interpolation is enabled for this VO
     */
    public boolean isInterpolationEnabled()
    {
        return interpolationEnabled && interpolated;
    }

    /**
     * Enables or disables interpolation for this VO
     * 
     * @param interpolated
     *        a flag indicating if the application should interpolate this VO
     */
    public void setInterpolated(boolean interpolated)
    {
        this.interpolated = interpolated;
    }
}
