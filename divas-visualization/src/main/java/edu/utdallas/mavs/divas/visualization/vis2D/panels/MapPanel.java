package edu.utdallas.mavs.divas.visualization.vis2D.panels;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.JToolTip;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.config.Config;
import edu.utdallas.mavs.divas.core.msg.RuntimeAgentCommandMsg.RuntimeAgentCommand;
import edu.utdallas.mavs.divas.core.sim.common.percept.VisionPerceptor;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.core.sim.env.CellBounds;
import edu.utdallas.mavs.divas.core.sim.env.CellID;
import edu.utdallas.mavs.divas.core.sim.env.CellMapNode;
import edu.utdallas.mavs.divas.utils.StatsHelper;
import edu.utdallas.mavs.divas.visualization.vis2D.Visualizer2DApplication;
import edu.utdallas.mavs.divas.visualization.vis2D.engine.Picker2D;
import edu.utdallas.mavs.divas.visualization.vis2D.engine.Picker2D.CurrentlySelectedType;
import edu.utdallas.mavs.divas.visualization.vis2D.panels.HeaderPanel.SimulationMode;
import edu.utdallas.mavs.divas.visualization.vis2D.spectator.PlayGround2D;
import edu.utdallas.mavs.divas.visualization.vis2D.spectator.VisualSpectator2D;
import edu.utdallas.mavs.divas.visualization.vis2D.utils.Interpolation;
import edu.utdallas.mavs.divas.visualization.vis2D.vo.ExplosionVO2D;
import edu.utdallas.mavs.divas.visualization.vis2D.vo.FireworkVO2D;

/**
 * This class is responsible for the 2D visualizing the simulation. It contains
 * the visualization area where each object in the simulation is visualized.
 */
public class MapPanel extends JPanel implements Observer
{
    private static final long  serialVersionUID          = 1L;
    private PopupFactory       popupFactory              = PopupFactory.getSharedInstance();
    private JToolTip           tooltip;
    private Popup              tooltipPopup;
    private static int         ENTITY_DOT_RADIUS         = 2;
    private static Color       AGENT_COLOR               = new Color(40, 40, 40, 80);

    private static Color       CELL_FILL_COLOR           = new Color(220, 220, 220, 80);
    // private static Color CELL_FILL_COLOR = new Color(79, 129, 189, 80);
    private static Color       AGENT_VISION_FILL_COLOR   = new Color(9, 20, 9, 14);         // new
                                                                                             // Color(79,
                                                                                             // 129,
                                                                                             // 79,
                                                                                             // 80);
    private static Color       NON_COLLIDABLE_FILL_COLOR = new Color(7, 14, 7, 10);
    private ToolBox            toolBox;
    private Map<String, Image> imageList                 = new HashMap<String, Image>();    // Cache
                                                                                             // envObject
                                                                                             // images,
                                                                                             // to
                                                                                             // save
                                                                                             // I/O

    private int                MAX_DIMENSION             = 1000;
    private Double             zoomFactor                = 1.0;
    private CellMapNode        mapRoot;
    private CellBounds         envBounds;
    private double             scaleFactor               = 1.0;

    StatsHelper                cycleAverage              = new StatsHelper(30);

    /**
     * Initialize the visualization panel for the 2D Visualizer. Setup the mouse
     * event listeners and get ready to start visualization.
     * 
     * @param toolBox
     */
    public MapPanel(final ToolBox toolBox)
    {
        super();
        this.toolBox = toolBox;
        setFocusable(true);
        requestFocus();
        VisualSpectator2D.getInstance().addObserver(this);
        tooltip = createToolTip();
        setPreferredSize(new Dimension(1000, 1000));

        addMouseMotionListener(new MouseMotionAdapter()
        {
            @Override
            public void mouseDragged(MouseEvent e)
            {
                if(tooltipPopup != null)
                {
                    Point2D envLocation = Interpolation.interpolate(getPanelBounds(), envBounds, e.getPoint());

                    CellMapNode cell = VisualSpectator2D.getPlayGround().getCellMap().findMapNode((float) envLocation.getX(), 0f, (float) envLocation.getY());

                    if(cell != null)
                    {
                        tooltipPopup.hide();
                        showToolTip(e.getX(), e.getY(), "<html>Cell ID: " + cell.getId() + "<br>Host ID: " + cell.getHostID() + "</html>");
                        MapPanel.this.repaint();
                    }
                }

                super.mouseDragged(e);
            }
        });

        addMouseMotionListener(
        // anonymous inner class
        new MouseMotionAdapter()
        {
            @Override
            public void mouseDragged(MouseEvent event)
            {
                if(Picker2D.getCurrentlySelectedID() != -1)
                {
                    Point2D aLoc = Interpolation.revereseInterpolate(envBounds, getPanelBounds(), new Point2D.Float(event.getX(), event.getY()));
                    EnvObjectState eo = VisualSpectator2D.getPlayGround().findEnvObject(Picker2D.getCurrentlySelectedID());
                    if(toolBox.getSimMode() == SimulationMode.EDIT_SCALE_X)
                    {
                        if(aLoc.getX() > eo.getPosition().getX())
                        {
                            eo.setScale(new Vector3f((float) (aLoc.getX() - eo.getPosition().getX()), eo.getScale().getY(), eo.getScale().getZ()));
                        }
                        else
                        {
                            eo.setScale(new Vector3f((float) (eo.getPosition().getX() - aLoc.getX()), eo.getScale().getY(), eo.getScale().getZ()));
                        }
                    }
                    else if(toolBox.getSimMode() == SimulationMode.EDIT_SCALE_Z)
                    {
                        if(aLoc.getY() > eo.getPosition().getZ())
                        {
                            eo.setScale(new Vector3f(eo.getScale().getX(), eo.getScale().getY(), (float) (aLoc.getY() - eo.getPosition().getZ())));
                        }
                        else
                        {
                            eo.setScale(new Vector3f(eo.getScale().getX(), eo.getScale().getY(), (float) (eo.getPosition().getZ() - aLoc.getY())));
                        }
                    }
                    else if(toolBox.getSimMode() == SimulationMode.EDIT_LOCATION)
                    {
                        eo.setPosition(new Vector3f((float) aLoc.getX(), eo.getPosition().getY(), (float) aLoc.getY()));
                    }
                    else if(toolBox.getSimMode() == SimulationMode.EDIT_ROTATION)
                    {
                        double angle = Math.atan2(aLoc.getY() - eo.getPosition().getY(), aLoc.getX() - eo.getPosition().getX()) / Math.PI;
                        Quaternion add = new Quaternion(0, (float) (-1 * angle), 0, 1);
                        eo.setRotation(add);
                    }

                    Visualizer2DApplication.getInstance().getSimCommander().sendStateUpdate(eo);
                }
            }
        } // end anonymous inner class

        ); // end call to addMouseMotionListener

        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseReleased(MouseEvent e)
            {
                if(e.getButton() == 2)
                {
                    if(tooltipPopup != null)
                        tooltipPopup.hide();
                    tooltipPopup = null;
                }
            }

            @Override
            public void mousePressed(MouseEvent e)
            {
                if(VisualSpectator2D.getPlayGround().getCellMap() != null)
                {
                    Point2D envLocation = Interpolation.interpolate(getPanelBounds(), envBounds, e.getPoint());
                    CellMapNode cell = VisualSpectator2D.getPlayGround().getCellMap().findMapNode((float) envLocation.getX(), 0f, (float) envLocation.getY());
                    Point2D aLoc = Interpolation.revereseInterpolate(envBounds, getPanelBounds(), new Point2D.Float(e.getX(), e.getY()));
                    if(cell != null)
                    {
                        if(e.getButton() == 2)
                        {
                            showToolTip(e.getX(), e.getY(), "<html>Cell ID: " + cell.getId() + "<br>Host ID:" + cell.getHostID() + "</html>");
                        }
                        else if(e.getButton() == 1)
                        {
                            if(toolBox.getSimMode() == SimulationMode.SELECTION_MODE)
                            {
                                Picker2D.pick(aLoc.getX(), aLoc.getY());
                            }
                            else if(toolBox.getSimMode() == SimulationMode.ADD_AGENT)
                            {
                                Random r = new Random();
                                for(int i = 0; i < 100; i++)
                                {
                                    int x = 25 - r.nextInt(100);
                                    int y = 25 - r.nextInt(100);
                                    Visualizer2DApplication.getInstance().getSimCommander().createAgent(new AgentState(), new Vector3f((float) aLoc.getX() + x, 0, (float) aLoc.getY() + y));
                                }
                                //Visualizer2DApplication.getInstance().getSimCommander().createAgent(new AgentState(), new Vector3f((float) aLoc.getX(), 0, (float) aLoc.getY()));
                            }
                            else if(toolBox.getSimMode() == SimulationMode.ADD_EXPLOSION)
                            {
                                Visualizer2DApplication.getInstance().getSimCommander().createExplosion(new Vector3f((float) aLoc.getX(), 0, (float) aLoc.getY()), 0.00000003f, true);
                            }
                            else if(toolBox.getSimMode() == SimulationMode.ADD_EXPLOSION_NO_SMOKE)
                            {
                                Visualizer2DApplication.getInstance().getSimCommander().createExplosion(new Vector3f((float) aLoc.getX(), 0, (float) aLoc.getY()), 0.00000003f, false);
                            }
                            else if(toolBox.getSimMode() == SimulationMode.ADD_FIREWORK)
                            {
                                Visualizer2DApplication.getInstance().getSimCommander().createFireworks(new Vector3f((float) aLoc.getX(), 0, (float) aLoc.getY()), 5, false);
                            }
                            else if(toolBox.getSimMode() == SimulationMode.SPLIT_CELL)
                            {
                                Visualizer2DApplication.getInstance().getSimCommander().splitCell(cell.getId());
                            }
                            else if(toolBox.getSimMode() == SimulationMode.MERGE_CELL)
                            {
                                Visualizer2DApplication.getInstance().getSimCommander().mergeCell(cell.getId());
                            }
                            else if(toolBox.getSimMode() == SimulationMode.ADD_ENV_OBJECT)
                            {
                                EnvObjectState candidateEnvObj = toolBox.getCandidateEnvObj();
                                candidateEnvObj.setPosition(new Vector3f((float) aLoc.getX(), 0, (float) aLoc.getY()));
                                Visualizer2DApplication.getInstance().getSimCommander().createEnvObject(candidateEnvObj);
                            }
                            else if(toolBox.getSimMode() == SimulationMode.CHANGE_AGENT_GOAL)
                            {
                                Random r = new Random();
                                for(AgentState a : VisualSpectator2D.getPlayGround().getAgents().values())
                                {
                                    int x = 50 - r.nextInt(100);
                                    int y = 50 - r.nextInt(100);
                                    Visualizer2DApplication.getInstance().getSimCommander().sendRuntimeAgentCommand(a.getID(), RuntimeAgentCommand.UPDATE_AGENT_GOAL, new Vector3f(x + (float) aLoc.getX(), 0, y + (float) aLoc.getY()));
                                }
                            }
                        }

                        repaint();
                    }

                }
            }

        });
    }

    private void showToolTip(int x, int y, String text)
    {
        tooltip.setTipText(text);
        tooltipPopup = popupFactory.getPopup(this, tooltip, getLocationOnScreen().x + x + 10, getLocationOnScreen().y + y + 10);
        tooltipPopup.show();
    }

    @Override
    public void paint(Graphics g)
    {
        final Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setPaint(Color.white);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setPaint(Color.black);

        if(VisualSpectator2D.getCycles() != -1)
        {
            draw(g2, mapRoot);
        }
    }

    private void draw(final Graphics2D g2, CellMapNode node)
    {
        PlayGround2D playGround2D = VisualSpectator2D.getPlayGround().clone();

        drawCellControllers(g2, playGround2D);

        drawEnvObjects(g2, playGround2D);

        drawAgents(g2, playGround2D);

        drawExplosions(g2, playGround2D);

        drawFireworks(g2, playGround2D);
    }

    private void drawFireworks(Graphics2D g2, PlayGround2D playGround2D)
    {
        for(FireworkVO2D f : playGround2D.getFireworks())
        {
            Point2D fLoc = Interpolation.interpolate(envBounds, getPanelBounds(), new Point2D.Float(f.getOrigin().getX(), f.getOrigin().getZ()));
            g2.translate(fLoc.getX(), fLoc.getY());

            for(int count = 1; count <= 20; count++)
            {
                g2.rotate(Math.PI / 10.0);
                // set random drawing color
                g2.setColor(new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256)));

                // draw filled star
                g2.fill(f.attachFirework());
                if(!f.isAttached())
                {
                    f.setAttached(true);
                }
            }
            g2.translate(-1 * fLoc.getX(), -1 * fLoc.getY());
        }
    }

    private void drawExplosions(Graphics2D g2, PlayGround2D playGround2D)
    {
        for(ExplosionVO2D e : playGround2D.getExplosions())
        {
            Paint origPaint = g2.getPaint();
            g2.setPaint(new Color(211 + e.getAge(), 211 + e.getAge(), 211 + e.getAge()));
            if(e.isSmoke())
            {
                g2.fill(e.attachExplosionSmoke(envBounds, getPanelBounds()));
            }
            for(int i = 0; i < Math.random() * 10; i++)
            {
                g2.setPaint(new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256)));
                g2.fill(e.attachExplosion(envBounds, getPanelBounds()));
            }
            if(!e.isAttached())
            {
                e.setAttached(true);
            }
            g2.setPaint(origPaint);
        }
    }

    private void drawAgents(Graphics2D g2, PlayGround2D playGround2D)
    {
        for(AgentState agent : playGround2D.getAgents().values())
        {
            // interpolate to find the agent's panel position
            Point2D aLoc = Interpolation.interpolate(envBounds, getPanelBounds(), new Point2D.Float(agent.getPosition().x, agent.getPosition().z));

            Paint origPaint = g2.getPaint();

            if(toolBox.agentIsTracked(agent.getID()))
            {
                g2.setPaint(Color.YELLOW);
                g2.fillOval((int) (aLoc.getX()) - ENTITY_DOT_RADIUS - 6, (int) (aLoc.getY()) - ENTITY_DOT_RADIUS - 6, 8 * ENTITY_DOT_RADIUS, 8 * ENTITY_DOT_RADIUS);
            }

            g2.setPaint(AGENT_VISION_FILL_COLOR);

            if(toolBox.isVisionCone())
            {
                if(agent instanceof VisionPerceptor)
                {
                    // interpolate to scale the arc to the panel
                    Arc2D arc = (Arc2D) ((VisionPerceptor) agent).calculateVisibleRegion();
                    arc.setFrame(Interpolation.interpolate(envBounds, getPanelBounds(), arc.getFrame()));
                    g2.fill(arc);
                }
            }

            // g2.setPaint(Color.black);
            g2.setPaint(AGENT_COLOR);

            if(toolBox.isAgentIdToolTip())
            {
                g2.drawString(String.format("%s", agent.getID()), (int) (aLoc.getX()) - ENTITY_DOT_RADIUS - 6, (int) (aLoc.getY()) - ENTITY_DOT_RADIUS - 6);
            }

            g2.fillOval((int) (aLoc.getX()) - ENTITY_DOT_RADIUS, (int) (aLoc.getY()) - ENTITY_DOT_RADIUS, 2 * ENTITY_DOT_RADIUS, 2 * ENTITY_DOT_RADIUS);

            g2.setPaint(origPaint);
        }
    }

    private void drawEnvObjects(Graphics2D g2, PlayGround2D playGround2D)
    {
        g2.setColor(Color.red);

        for(EnvObjectState eo : playGround2D.getEnvObjects())
        {
            Point2D eoLoc = Interpolation.interpolate(envBounds, getPanelBounds(), new Point2D.Float(eo.getPosition().x, eo.getPosition().z));

            if((eo.getID() == Picker2D.getCurrentlySelectedID()) && (Picker2D.getCurrentlySelectedType() == CurrentlySelectedType.ENVIRONMENT_OBJECT))
            {
                g2.setColor(Color.green);
            }
            else
            {
                if(eo.isCollidable())
                {
                    // g2.setColor(Color.red);
                    g2.setColor(new Color(100, 100, 100, 40));

                }
                else
                    g2.setColor(NON_COLLIDABLE_FILL_COLOR);
            }

            if(!eo.getType().equals("3DModel") || !toolBox.isShowEnvObjImage())
            {
                Shape transformed = Interpolation.getEnvObjectShape(eo, eoLoc, scaleFactor);
                g2.fill(transformed);
            }
            else if(eo.getType().equals("3DModel"))
            {
                Image icon;
                if(!imageList.containsKey(eo.getImage()))
                {
                    icon = Config.getIcon(eo.getImage());
                    imageList.put(eo.getImage(), icon);
                }
                else
                {
                    icon = imageList.get(eo.getImage());
                }

                AffineTransform transform = new AffineTransform();
                transform.rotate(-1 * eo.getRotation().getY() * Math.PI / 2, eoLoc.getX(), eoLoc.getY());
                g2.transform(transform);

                g2.drawImage(icon, (int) eoLoc.getX(), (int) eoLoc.getY(), (int) Math.ceil(eo.getScale().getX() * scaleFactor * 2) * 3, (int) Math.ceil(eo.getScale().getZ() * scaleFactor * 2) * 3, null);
                try
                {
                    g2.transform(transform.createInverse());
                }
                catch(NoninvertibleTransformException e)
                {
                    e.printStackTrace();
                }
            }

        }
        g2.setColor(Color.black);
    }

    private void drawCellControllers(Graphics2D g2, PlayGround2D playGround2D)
    {
        Rectangle2D bounds;
        Paint orig = g2.getPaint();

        VisualSpectator2D.getInstance();
        cycleAverage.add((float) VisualSpectator2D.getPeriod());
        int average = (int) cycleAverage.getAverage();

        g2.setPaint((average > 150) ? Color.red.darker() : Color.black.darker());

        String cycleTime = String.format("[%s: %s ms]", "Cycle", average);
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        g2.drawString(cycleTime, 5, 25);

        Map<CellID, CellBounds> leafBounds = playGround2D.getCellMap().getLeafBounds();
        for(CellID cellID : leafBounds.keySet())
        {
            CellBounds cBound = leafBounds.get(cellID);
            bounds = Interpolation.interpolate(envBounds, getPanelBounds(), cBound);

            g2.setPaint(CELL_FILL_COLOR);
            g2.fill(bounds);

            g2.setPaint(Color.black);
            bounds.setFrame(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
            Stroke s = new BasicStroke(2);
            g2.setStroke(s);
            g2.draw(bounds);

            // g2.setPaint((playGround2D.getAgentsCount(cellID) + playGround2D.getEnvObjCount(cellID) > 40) ? Color.red : Color.black);
            // String cellStats = String.format("[%s, %s]", playGround2D.getAgentsCount(cellID), playGround2D.getEnvObjCount(cellID));
            // g2.drawString(cellStats, (int) bounds.getMinX() + 5, (int) bounds.getMinY() + 15);
        }

        g2.setPaint(orig);
    }

    /**
     * Reloads the visualization area by adjusting to the new scale factor and
     * re drawing all the objects in the simulation on the visualization area.
     */
    public void reloadMap()
    {
        mapRoot = VisualSpectator2D.getPlayGround().getCellMap().getRoot();
        scaleFactor = (MAX_DIMENSION / (mapRoot.getBounds().getWidth())) * toolBox.getZoomFactor();
        envBounds = mapRoot.getBounds();

        if(!zoomFactor.equals(toolBox.getZoomFactor()))
        {
            if(zoomFactor >= 1.0)
            {
                setPreferredSize(new Dimension((int) (getWidth() * toolBox.getZoomFactor() / zoomFactor), (int) (getWidth() * toolBox.getZoomFactor() / zoomFactor)));
            }
            else
            {
                setPreferredSize(new Dimension((int) (getWidth() * toolBox.getZoomFactor() * zoomFactor), (int) (getWidth() * toolBox.getZoomFactor() * zoomFactor)));
            }
            zoomFactor = toolBox.getZoomFactor();
            scaleFactor = (MAX_DIMENSION / (mapRoot.getBounds().getWidth())) * toolBox.getZoomFactor();

            revalidate();
        }
        repaint();
    }

    private Rectangle2D getPanelBounds()
    {
        if(toolBox.getZoomFactor() >= 1.0)
            return new Rectangle2D.Double(0, 0, getWidth(), getHeight());
        return new Rectangle2D.Double(0, 0, getWidth() * toolBox.getZoomFactor(), getHeight() * toolBox.getZoomFactor());
    }

    @Override
    public void update(Observable o, Object arg)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                reloadMap();
            }
        });
    }
}
