package edu.utdallas.mavs.evacuation.simulation.sim.agent.planning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.EvacuationHumanKnowledgeModule;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.external.NeighborKnowledgeStorageObject;

public class RVOPathFinder implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	static float                   RVO_EPSILON           = 0.00001f;
    EvacuationHumanKnowledgeModule knowledgeModule;
    /**
     * Stores the orcalines for calculation
     */
    List<OrcaLine>                 orcaLines;

    private static final float     TIMESTEP              = 1.0f;
    public static float            TIME_HORIZON          = 8.0f;
    public static float            TIME_HORIZON_OBSTACLE = 5.0f;

    public RVOPathFinder(EvacuationHumanKnowledgeModule knowledgeModule)
    {
        orcaLines = new ArrayList<>();
        this.knowledgeModule = knowledgeModule;
    }

    public Vector3f getGoodHeading(Vector2f waypoint)
    {

        // prepare env
        final float invTimeHorizonObst = 1.0f / TIME_HORIZON_OBSTACLE;
        final float invTimeHorizon = 1.0f / TIME_HORIZON;

        Vector2f preferredVelocity = new Vector2f(waypoint.x, waypoint.y);
        preferredVelocity.subtractLocal(knowledgeModule.getSelf().getPosition2D());
        Vector2f newVelocity = new Vector2f(preferredVelocity);
        preferredVelocity.normalizeLocal();
        preferredVelocity.multLocal(knowledgeModule.getSelf().getDesiredSpeed());

        Collection<NeighborKnowledgeStorageObject> neighbors = knowledgeModule.getNearbyAgents().values();
        List<RVOObstacle> obstacles = knowledgeModule.getPerceivedRVOObstacles();
        orcaLines.clear();

        // printAllObstacles(obstacles);

        /* Create obstacle ORCA lines. */
        processObstacles(obstacles, invTimeHorizonObst);

        final int numObstLines = orcaLines.size();

        // System.out.println("obsLines: " + numObstLines);

        /* Create agent ORCA lines. */
        processAgents(neighbors, invTimeHorizon);
        // These function should return the new velocity based on linear programming solution

        // System.out.println("AgentID: " + knowledgeModule.getSelf().getID() + " orig vel: " + newVelocity);

        return runLinearPrograms(preferredVelocity, newVelocity, numObstLines);

    }

    protected Vector3f runLinearPrograms(Vector2f preferredVelocity, Vector2f newVelocity, final int numObstLines)
    {
        int lineFail = linearProgram2(orcaLines, knowledgeModule.getSelf().getDesiredSpeed(), preferredVelocity, false, newVelocity);
        // System.out.println("AgentID: " + knowledgeModule.getSelf().getID() + " new vel: " + newVelocity + " #fail: " + lineFail + " orca lines: " + orcaLines.size());

        if(lineFail < orcaLines.size())
        {

            linearProgram3(orcaLines, numObstLines, lineFail, knowledgeModule.getSelf().getDesiredSpeed(), newVelocity);
        }
        // System.out.println("AgentID: " + knowledgeModule.getSelf().getID() + " final vel: " + newVelocity);
        // if(!newVelocity.equals(preferredVelocity))
        // {
        // System.out.println("AgentID: " + knowledgeModule.getSelf().getID() + " VELOCTY CHANGED");
        // }
        Vector3f vel = new Vector3f(newVelocity.x, 0, newVelocity.y);
        return vel;
    }

    protected void processAgents(Collection<NeighborKnowledgeStorageObject> neighbors, final float invTimeHorizon)
    {
        for(NeighborKnowledgeStorageObject otherAgent : neighbors)
        {

            if(otherAgent.getID() == knowledgeModule.getSelf().getID())
            {
                continue;
            }

            Vector2f relativePosition = new Vector2f(otherAgent.getPosition2D());
            relativePosition.subtractLocal(knowledgeModule.getSelf().getPosition2D());

            Vector2f relativeVelocity = new Vector2f(knowledgeModule.getSelf().getVelocity2D());
            relativeVelocity.subtractLocal(otherAgent.getVelocity2D());

            float distSq = relativePosition.dot(relativePosition);
            float combinedRadius = knowledgeModule.getSelf().getRadius() + otherAgent.getRadius();

            float combinedRadiusSq = FastMath.pow(combinedRadius, 2.0f);

            OrcaLine line = new OrcaLine();
            Vector2f u;

            if(distSq > combinedRadiusSq)
            {
                /* No collision. */
                Vector2f w = new Vector2f(relativePosition);
                w.multLocal(invTimeHorizon);
                w.subtractLocal(relativeVelocity);
                w.negateLocal();

                /* Vector from cutoff center to relative velocity. */
                final float wLengthSq = w.dot(w);

                final float dotProduct1 = w.dot(relativePosition);

                if(dotProduct1 < 0.0f && FastMath.pow(dotProduct1, 2.0f) > combinedRadiusSq * wLengthSq)
                {
                    /* Project on cut-off circle. */
                    final float wLength = FastMath.sqrt(wLengthSq);
                    Vector2f unitW = new Vector2f(w);
                    unitW.multLocal(1.0f / wLength);

                    line.direction = new Vector2f(unitW.getY(), -unitW.getX());
                    u = new Vector2f(unitW);
                    u.multLocal((combinedRadius * invTimeHorizon) - wLength);
                }
                else
                {
                    /* Project on legs. */

                    // final float LEG = ((distSq - combinedRadiusSq) > 0) ? Math.sqrt(distSq - combinedRadiusSq) : 0;
                    final float LEG = FastMath.sqrt(Math.abs(distSq - combinedRadiusSq));

                    if(RVO_Utils.det(relativePosition, w) > 0.0f)
                    {
                        /* Project on left LEG. */

                        line.direction = new Vector2f(relativePosition.getX() * LEG - relativePosition.getY() * combinedRadius, relativePosition.getX() * combinedRadius + relativePosition.getY() * LEG);
                        line.direction.multLocal(1.0f / distSq);
                    }
                    else
                    {
                        /* Project on right LEG. */

                        line.direction = new Vector2f(relativePosition.getX() * LEG + relativePosition.getY() * combinedRadius, -relativePosition.getX() * combinedRadius + relativePosition.getY() * LEG);
                        line.direction.multLocal(-1.0f / distSq);
                    }

                    final float dotProduct2 = relativeVelocity.dot(line.direction);
                    u = new Vector2f(line.direction);
                    u.multLocal(dotProduct2);
                    u.subtractLocal(relativeVelocity);

                }
            }
            else
            {
                /* Collision. */
                // //System.out.println("Collision!!!");

                final float invTimeStep = 1.0f / TIMESTEP;

                Vector2f w = new Vector2f(relativePosition);
                w.multLocal(invTimeStep);
                w.subtractLocal(relativeVelocity);

                w.negateLocal();

                float wLength = w.length();

                Vector2f unitW = new Vector2f(w);
                unitW.multLocal(1.0f / wLength);

                line.direction = new Vector2f(unitW.getY(), -unitW.getX());
                u = new Vector2f(unitW);
                u.multLocal((combinedRadius * invTimeStep) - wLength);

            }
            Vector2f newU = new Vector2f(u);
            newU.multLocal(0.5f);
            newU.addLocal(knowledgeModule.getSelf().getVelocity2D());

            line.point = new Vector2f(newU);
            assert Math.abs(line.direction.length() - 1.0) < RVO_EPSILON;
            orcaLines.add(line);

        }
    }

    protected void processObstacles(List<RVOObstacle> obstacles, final float invTimeHorizonObst)
    {
        for(RVOObstacle obstacleFromList : obstacles)
        {

            RVOObstacle obstacle1 = obstacleFromList;
            RVOObstacle obstacle2 = obstacleFromList.getNextObstacle();

            // System.out.println("obs1dist: " + obstacle1.getPoint().distance(knowledgeModule.getSelf().getPosition2D()));
            // System.out.println("obs2dist: " + obstacle2.getPoint().distance(knowledgeModule.getSelf().getPosition2D()));

            // System.out.println("for agent at " + knowledgeModule.getSelf().getPosition2D() + " Avoiding obstacle from " + obstacle1 + " to " + obstacle2);

            Vector2f relativePosition1 = new Vector2f(obstacle1.getPoint());
            relativePosition1.subtractLocal(knowledgeModule.getSelf().getPosition2D());

            Vector2f relativePosition2 = new Vector2f(obstacle2.getPoint());
            relativePosition2.subtractLocal(knowledgeModule.getSelf().getPosition2D());

            Vector2f obstacleVector = new Vector2f(obstacle2.getPoint());
            obstacleVector.subtractLocal(obstacle1.getPoint());

            /*
             * Check if velocity obstacle of obstacle is already taken care of by
             * previously constructed obstacle ORCA lines.
             */
            boolean alreadyCovered = false;

            for(int j = 0; j < orcaLines.size(); ++j)
            {
                if(checkIfSafe(invTimeHorizonObst, relativePosition1, relativePosition2, orcaLines.get(j), knowledgeModule.getSelf().getRadius()))
                {
                    alreadyCovered = true;
                    // System.out.println("Covered");
                    break;
                }
            }

            if(alreadyCovered)
            {
                continue;
            }

            /* Not yet covered. Check for collisions. */

            float distSq1 = relativePosition1.dot(relativePosition1);

            float distSq2 = relativePosition2.dot(relativePosition2);

            float radiusSq = knowledgeModule.getSelf().getRadius() * knowledgeModule.getSelf().getRadius();

            Vector2f leftLegDirection, rightLegDirection;

            Vector2f negRelativePosition1 = new Vector2f(relativePosition1);
            negRelativePosition1.negateLocal();
            float s = (negRelativePosition1.dot(obstacleVector) / obstacleVector.dot(obstacleVector));
            Vector2f distSq = new Vector2f(obstacleVector);
            distSq.multLocal(-s);
            distSq.addLocal(negRelativePosition1);
            float distSqLine = distSq.dot(distSq);

            Vector2f negRelativePosition2 = new Vector2f(relativePosition2);
            negRelativePosition2.negateLocal();

            OrcaLine line = new OrcaLine();

            // System.out.println("s: " + s);
            // System.out.println("distSq1: " + distSq1);
            // System.out.println("distSq2: " + distSq2);
            // System.out.println("distSqLine: " + distSqLine);

            if(s < 0 && distSq1 <= radiusSq)
            {
                /* Collision with left vertex. Ignore if non-convex. */
                // System.out.println("Left Vertex Collision");

                line.point = new Vector2f(0, 0);

                line.direction = new Vector2f(-relativePosition1.y, relativePosition1.x);
                line.direction.normalizeLocal();
                addOrcaLine(obstacle1, obstacle2, line, "Adding from first");

                continue;

            }
            else if(s > 1 && distSq2 <= radiusSq)
            {
                /* Collision with right vertex. Ignore if non-convex */

                if(RVO_Utils.det(relativePosition2, obstacle2.getUnitdir()) >= 0)
                {

                    line.point = new Vector2f(0, 0);

                    line.direction = new Vector2f(-relativePosition2.y, relativePosition2.x);
                    line.direction.normalizeLocal();
                    addOrcaLine(obstacle1, obstacle2, line, "Adding from second");

                }
                continue;
            }
            else if(s >= 0 && s < 1 && distSqLine <= radiusSq)
            {
                /* Collision with obstacle segment. */

                // System.out.println("Collision with a segment");
                line.point = new Vector2f(0, 0);

                line.direction = new Vector2f(obstacle1.getUnitdir());
                line.direction.negateLocal();
                addOrcaLine(obstacle1, obstacle2, line, "Adding from third");
                continue;
            }
            /*
             * No collision
             * Compute legs. When obliquely viewed, both legs can come from a single
             * vertex. Legs extend cut-off line when nonconvex vertex.
             */
            // System.out.println("No collision");
            if(s < 0 && distSqLine <= radiusSq)
            {
                // System.out.println("oblique view from left");

                obstacle2 = obstacle1;

                // final float LEG1 = ((distSq1 - radiusSq) < 0) ? 0 : Math.sqrt(distSq1 - radiusSq);
                final float LEG1 = FastMath.sqrt(Math.abs(distSq1 - radiusSq));

                leftLegDirection = new Vector2f(relativePosition1.getX() * LEG1 - relativePosition1.getY() * knowledgeModule.getSelf().getRadius(), relativePosition1.getX() * knowledgeModule.getSelf().getRadius() + relativePosition1.getY() * LEG1);
                rightLegDirection = new Vector2f(relativePosition1.getX() * LEG1 + relativePosition1.getY() * knowledgeModule.getSelf().getRadius(), negRelativePosition1.getX() * knowledgeModule.getSelf().getRadius() + relativePosition1.getY()
                        * LEG1);
                leftLegDirection.multLocal(1.0f / distSq1);
                rightLegDirection.multLocal(1.0f / distSq1);
            }
            else if(s > 1 && distSqLine <= radiusSq)
            {
                /*
                 * RVO2Obstacle viewed obliquely so that
                 * right vertex defines velocity obstacle.
                 */
                // System.out.println("oblique view from right");

                obstacle1 = obstacle2;

                // final float LEG2 = ((distSq2 - radiusSq) < 0) ? 0 : Math.sqrt(distSq2 - radiusSq);
                final float LEG2 = FastMath.sqrt(Math.abs(distSq2 - radiusSq));
                leftLegDirection = new Vector2f(relativePosition2.getX() * LEG2 - relativePosition2.getY() * knowledgeModule.getSelf().getRadius(), relativePosition2.getX() * knowledgeModule.getSelf().getRadius() + relativePosition2.getY() * LEG2);
                rightLegDirection = new Vector2f(relativePosition2.getX() * LEG2 + relativePosition2.getY() * knowledgeModule.getSelf().getRadius(), negRelativePosition2.getX() * knowledgeModule.getSelf().getRadius() + relativePosition2.getY()
                        * LEG2);
                leftLegDirection.multLocal(1.0f / distSq2);
                rightLegDirection.multLocal(1.0f / distSq2);
            }
            else
            {
                /* Usual situation. */

                // System.out.println("The usual");

                final float LEG1 = FastMath.sqrt(FastMath.abs(distSq1 - radiusSq));
                leftLegDirection = new Vector2f(relativePosition1.getX() * LEG1 - relativePosition1.getY() * knowledgeModule.getSelf().getRadius(), relativePosition1.getX() * knowledgeModule.getSelf().getRadius() + relativePosition1.getY() * LEG1);
                leftLegDirection.multLocal(1.0f / distSq1);

                final float LEG2 = FastMath.sqrt(Math.abs(distSq2 - radiusSq));
                rightLegDirection = new Vector2f(relativePosition2.getX() * LEG2 + relativePosition2.getY() * knowledgeModule.getSelf().getRadius(), negRelativePosition2.getX() * knowledgeModule.getSelf().getRadius() + relativePosition2.getY()
                        * LEG2);
                rightLegDirection.multLocal(1.0f / distSq2);

            }

            /*
             * Legs can never point into neighboring edge when convex vertex,
             * take cutoff-line of neighboring edge instead. If velocity projected on
             * "foreign" LEG, no constraint is added.
             */

            final RVOObstacle leftNeighbor = obstacle1.getPrevObstacle();

            boolean isLeftLegForeign = false;
            boolean isRightLegForeign = false;

            if(RVO_Utils.det(leftLegDirection, leftNeighbor.getUnitdir().negate()) >= 0.0f)
            {
                /* Left LEG points into obstacle. */
                // System.out.println("left leg into obstacle");

                leftLegDirection = new Vector2f(leftNeighbor.getUnitdir().negate());
                isLeftLegForeign = true;
            }

            if(RVO_Utils.det(rightLegDirection, obstacle2.getUnitdir()) <= 0.0f)
            {
                /* Right LEG points into obstacle. */
                // System.out.println("right leg into obstacle");
                rightLegDirection = new Vector2f(obstacle2.getUnitdir());
                isRightLegForeign = true;
            }

            /* Compute cut-off centers. */
            Vector2f vectorToObstacle1 = new Vector2f(obstacle1.getPoint());
            vectorToObstacle1.subtractLocal(knowledgeModule.getSelf().getPosition2D());
            vectorToObstacle1.multLocal(invTimeHorizonObst);
            final Vector2f LEFTCUTOFF = new Vector2f(vectorToObstacle1);

            Vector2f vectorToObstacle2 = new Vector2f(obstacle2.getPoint());
            vectorToObstacle2.subtractLocal(knowledgeModule.getSelf().getPosition2D());
            vectorToObstacle2.multLocal(invTimeHorizonObst);
            final Vector2f RIGHTCUTOFF = new Vector2f(vectorToObstacle2);

            Vector2f cutOffVecTemp = new Vector2f(RIGHTCUTOFF);
            cutOffVecTemp.subtractLocal(LEFTCUTOFF);
            final Vector2f CUTOFFVEC = new Vector2f(cutOffVecTemp);

            /* Project current velocity on velocity obstacle. */

            /* Check if current velocity is projected on cutoff circles. */

            Vector2f velocityMinusLeft = new Vector2f(knowledgeModule.getSelf().getVelocity2D());
            velocityMinusLeft.subtractLocal(LEFTCUTOFF);

            Vector2f velocityMinusRight = new Vector2f(knowledgeModule.getSelf().getVelocity2D());
            velocityMinusRight.subtractLocal(RIGHTCUTOFF);

            final float T = ((obstacle1.equals(obstacle2)) ? 0.5f : (velocityMinusLeft.dot(CUTOFFVEC) / CUTOFFVEC.dot(CUTOFFVEC)));
            final float TLEFT = (velocityMinusLeft.dot(leftLegDirection));
            final float TRIGHT = (velocityMinusRight.dot(rightLegDirection));

            // System.out.println("T: " + T);
            // System.out.println("TLEFT: " + TLEFT);
            // System.out.println("TRIGHT: " + TRIGHT);
            if((T < 0.0f && TLEFT < 0.0f) || (obstacle1.equals(obstacle2) && TLEFT < 0.0f && TRIGHT < 0.0f))
            {
                /* Project on left cut-off circle. */

                // System.out.println("Project on left cut off");

                Vector2f unitW = new Vector2f(velocityMinusLeft);
                unitW.normalizeLocal();

                line.direction = new Vector2f(unitW.getY(), -unitW.getX());
                unitW.multLocal(invTimeHorizonObst);
                unitW.multLocal(knowledgeModule.getSelf().getRadius());
                unitW.addLocal(LEFTCUTOFF);
                line.point = new Vector2f(unitW);
                addOrcaLine(obstacle1, obstacle2, line, "Adding from fourth");
                continue;
            }
            else if(T > 1.0f && TRIGHT < 0.0f)
            {
                /* Project on right cut-off circle. */
                // System.out.println("Project on righ cut off");
                Vector2f unitW = new Vector2f(velocityMinusRight);
                unitW.normalizeLocal();

                line.direction = new Vector2f(unitW.getY(), -unitW.getX());
                unitW.multLocal(invTimeHorizonObst);
                unitW.multLocal(knowledgeModule.getSelf().getRadius());
                unitW.addLocal(RIGHTCUTOFF);
                line.point = new Vector2f(unitW);
                addOrcaLine(obstacle1, obstacle2, line, "Adding from fifth");
                continue;

            }

            /*
             * Project on left LEG, right LEG, or cut-off line, whichever is closest
             * to velocity.
             */
            Vector2f vectorForCutOff = new Vector2f(CUTOFFVEC);
            vectorForCutOff.multLocal(T);
            vectorForCutOff.addLocal(LEFTCUTOFF);
            vectorForCutOff.negateLocal();
            vectorForCutOff.addLocal(knowledgeModule.getSelf().getVelocity2D());

            final float DISTSQCUTOFF = ((T < 0.0f || T > 1.0f || obstacle1.equals(obstacle2)) ? Float.MAX_VALUE : vectorForCutOff.dot(vectorForCutOff));

            Vector2f vectorForLeftCutOff = new Vector2f(leftLegDirection);
            vectorForLeftCutOff.multLocal(TLEFT);
            vectorForLeftCutOff.addLocal(LEFTCUTOFF);
            vectorForLeftCutOff.negateLocal();
            vectorForLeftCutOff.addLocal(knowledgeModule.getSelf().getVelocity2D());

            final float DISTSQLEFT = ((TLEFT < 0.0f) ? Float.MAX_VALUE : vectorForLeftCutOff.dot(vectorForLeftCutOff));

            Vector2f vectorForRightCutOff = new Vector2f(rightLegDirection);
            vectorForRightCutOff.multLocal(TRIGHT);
            vectorForRightCutOff.addLocal(RIGHTCUTOFF);
            vectorForRightCutOff.negateLocal();
            vectorForRightCutOff.addLocal(knowledgeModule.getSelf().getVelocity2D());

            final float DISTSQRIGHT = ((TRIGHT < 0.0f) ? Float.MAX_VALUE : vectorForRightCutOff.dot(vectorForRightCutOff));

            if(DISTSQCUTOFF <= DISTSQLEFT && DISTSQCUTOFF <= DISTSQRIGHT)
            {
                /* Project on cut-off line. */
                // System.out.println("Project on cut off");

                line.direction = new Vector2f(obstacle1.getUnitdir());

                line.direction.negateLocal();

                Vector2f vectorForPoint = new Vector2f(-line.direction.getY(), line.direction.getX());
                vectorForPoint.multLocal(invTimeHorizonObst);
                vectorForPoint.multLocal(knowledgeModule.getSelf().getRadius());
                vectorForPoint.addLocal(LEFTCUTOFF);
                line.point = new Vector2f(vectorForPoint);
                assert Math.abs(line.direction.length() - 1.0) < RVO_EPSILON;
                addOrcaLine(obstacle1, obstacle2, line, "Adding from sixth");
                continue;

            }
            else if(DISTSQLEFT <= DISTSQRIGHT)
            { /* Project on left LEG. */

                // System.out.println("Project on left leg");

                if(isLeftLegForeign)
                {
                    continue;
                }

                line.direction = new Vector2f(leftLegDirection);

                Vector2f vectorForPoint = new Vector2f(-line.direction.getY(), line.direction.getX());
                vectorForPoint.multLocal(invTimeHorizonObst);
                vectorForPoint.multLocal(knowledgeModule.getSelf().getRadius());
                vectorForPoint.addLocal(LEFTCUTOFF);
                line.point = new Vector2f(vectorForPoint);
                addOrcaLine(obstacle1, obstacle2, line, "Adding from seventh");
                continue;
            }
            else
            { /* Project on right LEG. */

                // System.out.println("Project on right leg");
                if(isRightLegForeign)
                {
                    continue;
                }

                line.direction = new Vector2f(rightLegDirection);
                line.direction.negateLocal();

                Vector2f vectorForPoint = new Vector2f(-line.direction.getY(), line.direction.getX());
                vectorForPoint.multLocal(invTimeHorizonObst);
                vectorForPoint.multLocal(knowledgeModule.getSelf().getRadius());
                vectorForPoint.addLocal(RIGHTCUTOFF);
                line.point = new Vector2f(vectorForPoint);
                addOrcaLine(obstacle1, obstacle2, line, "Adding from eigth");
                continue;
            }

        }
    }

    protected void printAllObstacles(List<RVOObstacle> obstacles)
    {
        int count = 1;
        for(RVOObstacle curObs : obstacles)
        {
            RVOObstacle temp = curObs;
            System.out.println("Obstacle: " + count);
            while(!temp.getNextObstacle().equals(curObs))
            {
                System.out.println(temp.getPoint());
                temp = temp.getNextObstacle();
            }
            System.out.println(temp.getPoint());
            count++;
        }

        count = 1;
    }

    protected void addOrcaLine(RVOObstacle obstacle1, RVOObstacle obstacle2, OrcaLine line, String out)
    {
        orcaLines.add(line);
        // System.out.println("obs1 point: " + obstacle1.getPoint());
        // System.out.println("obs2 point: " + obstacle2.getPoint());
        // System.out.println("obsID point: " + obstacle2.getID());
        // System.out.println(out);
        // System.out.println("orcaline point: " + line.point);
        // System.out.println("orcaline dir: " + line.direction);
    }

    private boolean checkIfSafe(float invTimeHorizonObst, Vector2f relativePosition1, Vector2f relativePosition2, OrcaLine line, float radius)
    {

        Vector2f a = new Vector2f(relativePosition1);
        a.multLocal(invTimeHorizonObst);
        a.subtractLocal(line.point);

        Vector2f b = new Vector2f(relativePosition2);
        b.multLocal(invTimeHorizonObst);
        b.subtractLocal(line.point);

        return((Float.compare((RVO_Utils.det(a, line.direction) - invTimeHorizonObst * radius), -RVO_EPSILON) >= 0) && (Float.compare((RVO_Utils.det(b, line.direction) - invTimeHorizonObst * radius), -RVO_EPSILON) >= 0));

    }

    boolean linearProgram1(List<OrcaLine> lines, int lineNo, float radius, Vector2f optVelocity, boolean directionOpt, Vector2f result)
    {

        Vector2f lineNoPoint = new Vector2f(lines.get(lineNo).point);
        Vector2f lineNoDirection = new Vector2f(lines.get(lineNo).direction);
        float dotProduct = lineNoPoint.dot(lineNoDirection);

        // final float detProduct = det(lines.get(lineNo).direction, lineNoPoint);
        // final float detProduct2 = lineNoPoint.dot(lineNoPoint);
        final float discriminant = FastMath.pow(dotProduct, 2.0f) + FastMath.pow(radius, 2.0f) - lineNoPoint.dot(lineNoPoint);

        if(Float.compare(discriminant, RVO_EPSILON) < 0)
        {
            /* Max speed circle fully invalidates line lineNo. */
            return false;
        }

        final float sqrtDiscriminant = FastMath.sqrt(discriminant);
        float tLeft = -(dotProduct) - sqrtDiscriminant;
        float tRight = -(dotProduct) + sqrtDiscriminant;

        for(int i = 0; i < lineNo; ++i)
        {
            final float denominator = RVO_Utils.det(lineNoDirection, lines.get(i).direction);
            Vector2f tempVector = new Vector2f(lineNoPoint);
            tempVector.subtractLocal(new Vector2f(lines.get(i).point));
            final float numerator = RVO_Utils.det(lines.get(i).direction, tempVector);

            if(Float.compare(Math.abs(denominator), RVO_EPSILON) <= 0)
            {
                /* Lines lineNo and i are (almost) parallel. */

                if(Float.compare(numerator, RVO_EPSILON) < 0)
                {
                    /* Line i fully invalidates line lineNo. */
                    return false;
                }
                else
                {
                    /* Line i does not impose constraint on line lineNo. */
                    continue;
                }
            }

            final float t = numerator / denominator;
            if(denominator >= 0)
            {
                /* Line i bounds line lineNo on the right. */
                tRight = Math.min(tRight, t);
            }
            else
            {
                /* Line i bounds line lineNo on the left. */
                tLeft = Math.max(tLeft, t);
            }

            if(tLeft > tRight)
            {
                return false;
            }
        }

        if(directionOpt)
        {
            /* Optimize direction. */
            Vector2f tempLineNoDirection = new Vector2f(lineNoDirection);
            if(Float.compare(optVelocity.dot(tempLineNoDirection), -RVO_EPSILON) > 0)
            {
                /* Take right extreme. */
                tempLineNoDirection.multLocal(tRight);
            }
            else
            {
                /* Take left extreme. */
                tempLineNoDirection.multLocal(tLeft);
            }
            tempLineNoDirection.addLocal(new Vector2f(lineNoPoint));
            result.x = tempLineNoDirection.x;
            result.y = tempLineNoDirection.y;
        }
        else
        {
            /* Optimize closest point. */
            Vector2f tempOptVector = new Vector2f(optVelocity);
            tempOptVector.subtractLocal(lineNoPoint);
            final float t = lineNoDirection.dot(tempOptVector);
            Vector2f tempLineNoDirection = new Vector2f(lineNoDirection);
            if(Float.compare(t, tLeft) < 0)
            {
                tempLineNoDirection.multLocal(tLeft);
            }
            else if(Float.compare(t, tRight) > 0)
            {
                tempLineNoDirection.multLocal(tRight);
            }
            else
            {
                tempLineNoDirection.multLocal(t);
            }
            tempLineNoDirection.addLocal(new Vector2f(lineNoPoint));
            result.x = tempLineNoDirection.x;
            result.y = tempLineNoDirection.y;

        }

        return true;
    }

    int linearProgram2(List<OrcaLine> lines, float radius, Vector2f optVelocity, boolean directionOpt, Vector2f result)
    {

        if(directionOpt)
        {
            /*
             * Optimize direction. Note that the optimization velocity is of unit
             * length in this case.
             */
            if(Float.compare(Math.abs(optVelocity.length() - 1), RVO_EPSILON) > 0)
            {
                // System.out.println("??" + optVelocity.length());
            }
            Vector2f tempOpt = new Vector2f(optVelocity);

            result.x = tempOpt.x;
            result.y = tempOpt.y;
            result.multLocal(radius);
        }
        else if(optVelocity.dot(optVelocity) > Math.pow(radius, 2.0f))
        {
            /* Optimize closest point and outside circle. */

            result.x = optVelocity.x;
            result.y = optVelocity.y;
            result.normalizeLocal();
            result.multLocal(radius);
        }
        else
        {
            /* Optimize closest point and inside circle. */

            result.x = optVelocity.x;
            result.y = optVelocity.y;
        }

        for(int i = 0; i < lines.size(); ++i)
        {

            Vector2f tempPoint = new Vector2f(lines.get(i).point);
            tempPoint.subtractLocal(new Vector2f(result));

            if(Float.compare(RVO_Utils.det(lines.get(i).direction, tempPoint), 0) > 0)
            {
                /* Result does not satisfy constraint i. Compute new optimal result. */
                Vector2f tempResult = new Vector2f(result);
                if(!linearProgram1(lines, i, radius, optVelocity, directionOpt, result))
                {
                    result.x = tempResult.x;
                    result.y = tempResult.y;
                    return i;
                }
            }
        }

        return lines.size();
    }

    void linearProgram3(List<OrcaLine> lines, int numObstLines, int beginLine, float radius, Vector2f result)
    {

        float distance = 0.0f;

        for(int i = beginLine; i < lines.size(); i++)
        {
            Vector2f tempPoint = new Vector2f(lines.get(i).point);
            tempPoint.subtractLocal(result);

            if(RVO_Utils.det(lines.get(i).direction, tempPoint) > distance)
            {
                /* Result does not satisfy constraint of line i. */
                List<OrcaLine> projLines = new ArrayList<>();
                for(int j = 0; j < numObstLines; j++)
                {
                    projLines.add(new OrcaLine(lines.get(j)));

                }

                for(int j = numObstLines; j < i; j++)
                {
                    OrcaLine line = new OrcaLine();

                    float determinant = RVO_Utils.det(lines.get(i).direction, lines.get(j).direction);
                    if(Float.compare(Math.abs(determinant), RVO_EPSILON) <= 0)
                    {
                        /* Line i and line j are (almost) parallel. */
                        if(Float.compare(lines.get(i).direction.dot(lines.get(j).direction), -RVO_EPSILON) > 0)
                        {
                            /* Line i and line j point in the same direction. */
                            continue;
                        }
                        else
                        {
                            /* Line i and line j point in opposite direction. */
                            line.point = new Vector2f(lines.get(j).point);
                            line.point.addLocal(lines.get(i).point);
                            line.point.multLocal(0.5f);

                        }
                    }
                    else
                    {

                        Vector2f tempVector = new Vector2f(lines.get(i).point);
                        tempVector.subtractLocal(new Vector2f(lines.get(j).point));
                        Vector2f newTempVector = new Vector2f(lines.get(i).direction);
                        newTempVector.multLocal(RVO_Utils.det(lines.get(j).direction, tempVector) / determinant);

                        line.point = new Vector2f(lines.get(i).point);
                        line.point.addLocal(newTempVector);

                    }
                    line.direction = new Vector2f(lines.get(j).direction);
                    line.direction.subtractLocal(lines.get(i).direction);
                    line.direction.normalizeLocal();

                    projLines.add(line);
                }

                final Vector2f tempResult = new Vector2f(result);

                if(linearProgram2(projLines, radius, new Vector2f(-lines.get(i).direction.y, lines.get(i).direction.x), true, result) < projLines.size())
                {
                    /*
                     * This should in principle not happen. The result is by definition
                     * already in the feasible region of this linear program. If it fails,
                     * it is due to small floating point error, and the current result is
                     * kept.
                     */
                    //
                    result.x = tempResult.x;
                    result.y = tempResult.y;

                    // result.x = 0.0f;
                    // result.y = 0.0f;

                }

                Vector2f tempVector = new Vector2f(lines.get(i).point);
                tempVector.subtractLocal(result);
                distance = RVO_Utils.det(lines.get(i).direction, tempVector);
            }
        }
    }
}