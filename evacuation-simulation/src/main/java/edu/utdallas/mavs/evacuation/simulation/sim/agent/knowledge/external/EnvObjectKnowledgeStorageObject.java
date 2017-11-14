package edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.external;

import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Vector3f;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.planning.PathFindingNode;
import edu.utdallas.mavs.evacuation.simulation.sim.common.state.DoorObjectState;

/**
 * Store critial information about environment objects
 */
public class EnvObjectKnowledgeStorageObject extends VirtualKnowledgeStorageObject
{
    private static final long serialVersionUID = 1L;
    /**
     * The EnvObj's description
     */
    protected String          description      = "";
    /**
     * The EnvObj's Type
     */
    protected String          type             = "";

    protected boolean         isOpen           = true;


    /**
     * Create an KSO from an environment object State
     * 
     * @param envObj
     *        the env object state
     */
    public EnvObjectKnowledgeStorageObject(EnvObjectState envObj)
    {
        
        
        super(envObj.getID(), envObj.getScale(), envObj.getPosition(), envObj.isCollidable(), envObj.getBoundingArea());
        this.description = envObj.getDescription();
        this.type = envObj.getType();
        if(envObj instanceof DoorObjectState)
        {
            this.isOpen = ((DoorObjectState) envObj).isOpen();
        }
       
    }

    public boolean updateValues(EnvObjectState envObj)
    {
        boolean changed = false;
        if(!this.scale.equals(envObj.getScale()))
        {
            this.scale = envObj.getScale();
            changed = true;
        }

        if(!this.position.equals(envObj.getPosition()))
        {
            this.position = envObj.getPosition();
            changed = true;
        }

        if(this.isCollidable != envObj.isCollidable())
        {
            this.isCollidable = envObj.isCollidable();
            changed = true;
        }
        if(!this.boundingArea.equals(envObj.getBoundingArea()))
        {
            this.boundingArea = envObj.getBoundingArea();
            changed = true;
        }

        if(envObj instanceof DoorObjectState && this.isOpen != ((DoorObjectState) envObj).isOpen())
        {
            this.isOpen = ((DoorObjectState) envObj).isOpen();
            changed = true;
        }

        return changed;
    }

    /**
     * Get the KSO's description
     * 
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Set the KSO's description
     * 
     * @param description
     *        the description
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Get the KSO's type
     * 
     * @return the type
     */
    public String getType()
    {
        return type;
    }

    /**
     * Set the KSO's type
     * 
     * @param type
     *        the type
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * Calculate the four corner points of an env object.
     * 
     * @param points
     *        passed in empty ponts reference
     * @param space
     *        space away from object to buffer space with
     * @return the points list filled
     */
    public List<Vector3f> calcPoints(float space)
    {
        List<Vector3f> points = new ArrayList<Vector3f>();
        points.add(new Vector3f((position.x - (scale.x + space)), 0, (position.z - (scale.z + space))));
        points.add(new Vector3f((position.x - (scale.x + space)), 0, (position.z + (scale.z + space))));
        points.add(new Vector3f((position.x + (scale.x + space)), 0, (position.z - (scale.z + space))));
        points.add(new Vector3f((position.x + (scale.x + space)), 0, (position.z + (scale.z + space))));
        points.add(position);
        return points;
    }

    /**
     * Calculate the four corner points of an env object.
     * 
     * @param points
     *        passed in empty ponts reference
     * @param space
     *        space away from object to buffer space with
     * @param agentSize 
     * @return the points list filled
     */
    public List<PathFindingNode> calcNodesNoCenter(float space, float agentSize)
    {
        List<PathFindingNode> points = new ArrayList<PathFindingNode>();
        points.add(new PathFindingNode(agentSize, new Vector3f((position.x - (scale.x + space)), 0, (position.z - (scale.z + space))), id));
        points.add(new PathFindingNode(agentSize, new Vector3f((position.x - (scale.x + space)), 0, (position.z + (scale.z + space))), id));
        points.add(new PathFindingNode(agentSize, new Vector3f((position.x + (scale.x + space)), 0, (position.z - (scale.z + space))), id));
        points.add(new PathFindingNode(agentSize, new Vector3f((position.x + (scale.x + space)), 0, (position.z + (scale.z + space))), id));
        return points;
    }

    /**
     * Calculate the nodes of a door.
     * @param space 
     * @param agentSize 
     * 
     * @return the points list filled
     */
    public List<PathFindingNode> calcNodesForDoor(float space, float agentSize)
    {
        List<PathFindingNode> points = new ArrayList<PathFindingNode>();
        points.add(new PathFindingNode(agentSize, new Vector3f((position.x - (scale.x + space)), 0, position.z), id));
        points.add(new PathFindingNode(agentSize, new Vector3f((position.x + (scale.x + space)), 0, position.z), id));
        points.add(new PathFindingNode(agentSize, new Vector3f(position.x, 0, (position.z - (scale.z + space))), id));
        points.add(new PathFindingNode(agentSize, new Vector3f(position.x, 0, (position.z + (scale.z + space))), id));
        return points;
    }

    /**
     * Checks if this object is in between two points
     * 
     * @param startPoint
     *        start point
     * @param endPoint
     *        end point
     * @param space
     *        space away from object to buffer space with
     * @return true if object is in between. Otherwise, false.
     */
    public boolean isInBetween(Vector3f startPoint, Vector3f endPoint, float space)
    {
        List<Vector3f> points = calcPoints(space);

        for(Vector3f point : points)
        {
            if(((point.x > startPoint.x) && (point.x < endPoint.x)) || ((point.x < startPoint.x) && (point.x > endPoint.x)))
            {
                return true;
            }
            else if(((point.z > startPoint.z) && (point.z < endPoint.z)) || ((point.z < startPoint.z) && (point.z > endPoint.z)))
            {
                return true;
            }
        }
        return false;
    }

    public boolean agentPathIntersectsObj2D(Vector3f agentPosition, Vector3f goalPoint, float agentSize)
    {

        if(intersects2DLine(goalPoint.add(1, 0, 1), agentPosition.add(1, 0, 1)))
        {
            return true;
        }
        if(intersects2DLine(goalPoint.add(-1, 0, 1), agentPosition.add(-1, 0, 1)))
        {
            return true;
        }
        if(intersects2DLine(goalPoint.add(1, 0, -1), agentPosition.add(1, 0, -1)))
        {
            return true;
        }
        if(intersects2DLine(goalPoint.add(-1, 0, -1), agentPosition.add(-1, 0, -1)))
        {
            return true;
        }
        if(intersects2DLine(goalPoint, agentPosition))
        {
            return true;
        }

        // Vector3f agentHeading = goalPoint.subtract(agentPosition);
        // agentHeading.normalizeLocal();
        // if(intersects2DLine(goalPoint, agentPosition))
        // {
        // return true;
        // }
        // Vector3f headingNorm = new Vector3f(agentHeading.z, 0, -agentHeading.x);
        // headingNorm.multLocal(agentSize);
        // if(intersects2DLine(goalPoint.add(headingNorm).add(agentHeading), agentPosition.add(headingNorm).subtract(agentHeading)))
        // {
        // return true;
        // }
        // headingNorm = new Vector3f(-agentHeading.z, 0, agentHeading.x);
        // headingNorm.multLocal(agentSize);
        // if(intersects2DLine(goalPoint.add(headingNorm).add(agentHeading), agentPosition.add(headingNorm).subtract(agentHeading)))
        // {
        // return true;
        // }

        return false;
    }

    public boolean isOpen()
    {
        return isOpen;
    }

    public void setOpen(boolean isOpen)
    {
        this.isOpen = isOpen;
    }
    
    /**
     * Checks if its mini bounding area intersects the line determined by the given start and end points
     * 
     * @param endPoint
     *        end point of the line
     * @param startPoint
     *        start point of the line
     * @return true if it intersects. Otherwise, false.
     */
    public boolean miniBAIntersects2DLine(Vector3f endPoint, Vector3f startPoint)
    {
        return boundingArea.intersects(new GeometryFactory().createLineString(new Coordinate[] {new Coordinate(endPoint.x, endPoint.z), new Coordinate(startPoint.x, startPoint.z)}));
    }


}
