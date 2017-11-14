package edu.utdallas.mavs.divas.core.sim.env;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.utdallas.mavs.divas.core.config.SimConfig;
import edu.utdallas.mavs.divas.core.msg.Workload;
import edu.utdallas.mavs.divas.utils.Multithreader;
import edu.utdallas.mavs.divas.utils.Multithreader.ThreadPoolType;
import edu.utdallas.mavs.divas.utils.StatsHelper;

/**
 * This class describes an environment coordinator.
 * <p>
 * Its goals include improving the efficiency, scalability and stability of the simulation.
 * 
 * @param <CC> the CC's type
 */
public class Coordinator<CC extends SelfOrganizingCellController<?>> implements Serializable
{
    private static final long                                        serialVersionUID          = 1L;

    private final static Logger                                      logger                    = LoggerFactory.getLogger(Coordinator.class);

    /**
     * The time between each reorganization request execution time.
     */
    protected static final long                                      REORGANIZATION_SLEEP_TIME = 100;

    /**
     * Self-organized environment
     */
    protected SelfOrganizingEnvironment<CC>                          environment;

    /**
     * A map to store cells work load alerts.
     */
    protected transient Map<CellID, Workload>                        assistanceRequests;

    /**
     * A map for storing the reorganization strategy along with the
     * implementation for each type of strategies.
     */
    protected transient Map<ReorganizationStrategy, CellReorganizer> reorganizationStrategies;

    /**
     * Periodic reorganization thread
     */
    protected transient Multithreader                                reorganizationService;

    public Coordinator(SelfOrganizingEnvironment<CC> environment)
    {
        this.environment = environment;
        assistanceRequests = Collections.synchronizedMap(new HashMap<CellID, Workload>());
        reorganizationStrategies = new HashMap<ReorganizationStrategy, CellReorganizer>();
        loadStrategies();
        startReorganizationService();
    }

    /**
     * Creates the daemon multithreader and start it to work at periodic times
     * to execute the reorganization requests.
     */
    public void startReorganizationService()
    {
        reorganizationService = new Multithreader("ReorganizationService", ThreadPoolType.FIXED, 2, true);
        reorganizationService.executePeriodicTask(createReorganizationTask(), REORGANIZATION_SLEEP_TIME);
    }

    /**
     * Creates the reorganization task
     * 
     * @return the reorganization task
     */
    private Runnable createReorganizationTask()
    {
        Runnable task = new Runnable()
        {
            @Override
            public void run()
            {
                reorganize();
            }
        };
        return task;
    }

    /**
     * Load strategies for this coordinator
     */
    private void loadStrategies()
    {
        reorganizationStrategies.put(ReorganizationStrategy.No_Reorganization, new NoReorganizationCellReorganizer());
        reorganizationStrategies.put(ReorganizationStrategy.One_Cell, new oneCellReorganizer());
        reorganizationStrategies.put(ReorganizationStrategy.Basic_Agent_Population_Balance, new BasicCellReorganizer());
        reorganizationStrategies.put(ReorganizationStrategy.Basic_Agent_and_EnvObject_Population_Balance, new BasicEOCellReorganizer());
        reorganizationStrategies.put(ReorganizationStrategy.Greedy_Agent_Population_Balance, new GreedyCellReorganizer());
        reorganizationStrategies.put(ReorganizationStrategy.Greedy_Agent_and_EnvObject_Population_Balance, new GreedyEOCellReorganizer());
        reorganizationStrategies.put(ReorganizationStrategy.Fair_Agent_Population_Balance, new FairCellReorganizer());
        reorganizationStrategies.put(ReorganizationStrategy.Fair_Agent_and_EnvObjects_Population_Balance, new FairEOCellReorganizer());
        reorganizationStrategies.put(ReorganizationStrategy.Self_Organizing, new SelfOrganizingReorganizer());
        reorganizationStrategies.put(ReorganizationStrategy.Autonomic, new AutonomicReorganizer());
    }

    /**
     * Reorganizes the environment cell controllers based on the current
     * reorganization strategy.
     */
    protected void reorganize()
    {
        reorganizationStrategies.get(environment.getStrategy()).reorganize();
        assistanceRequests.clear();
    }

    /**
     * Enqueues a assistance request to this coordinator
     * 
     * @param workload the workload message
     */
    public void requestAssistance(Workload workload)
    {
        assistanceRequests.put(workload.getCellID(), workload);
    }

    /**
     * Computes the urgency of a cell controller
     * 
     * @param cell the cell controller
     * @return the calculated urgency
     */
    private Float computeUrgency(CC cell)
    {
        return computeUrgency(cell.getCellID(), cell.getWorkload());
    }

    /**
     * Computes the urgency of a cell controller
     * 
     * @param cellID the cell controller's ID
     * @param workload the cell controller's workload
     * @return the calculated urgency
     */
    private Float computeUrgency(CellID cellID, float workload)
    {
        float neighborsWorkload = computeNeighborsWorkload(cellID);
        return workload + SimConfig.getInstance().neighbors_urgency_weight * neighborsWorkload;
    }

    /**
     * Computes the total workload of the given cell controller
     * 
     * @param cellID the id of the cell controller
     * @return workload the total workload of the neighboring cells
     */
    private float computeNeighborsWorkload(CellID cellID)
    {
        List<SelfOrganizingCellController<?>> neighbors = getNeighbors(cellID);

        float neighborsWorkload = 0;
        for(SelfOrganizingCellController<?> cc : neighbors)
        {
            neighborsWorkload += cc.getWorkload();
        }
        return neighborsWorkload;
    }

    /**
     * Retrieves the list of neighboring cell controllers of a given cell controller.
     * 
     * @param cellID the ID of the cell controller
     */
    private List<SelfOrganizingCellController<?>> getNeighbors(CellID cellID)
    {
        List<SelfOrganizingCellController<?>> neighbors = new ArrayList<>();

        CC cc = environment.getCellController(cellID);
        if(cc != null)
        {
            CellBounds ccBounds = cc.getCellState().getBounds();
            int cushion = 1;
            Rectangle2D adjacencyBounds = new Rectangle2D.Float(-cushion + ccBounds.x, -cushion + ccBounds.y, ccBounds.width + 2 * cushion, ccBounds.height + 2 * cushion);
            for(CC cell : environment.getCellControllers())
            {
                if(cell.getCellID().equals(cellID))
                    continue;

                if(environment.getCellMap().isLeaf(cell.getCellID()))
                {
                    if(adjacencyBounds.intersects(cell.getCellState().getBounds()))
                    {
                        neighbors.add(cell);
                    }
                }
            }
        }
        return neighbors;
    }

    /*
     * Concrete Reorganization Strategies
     */

    /**
     * No reorganization. Multiple cells are possible.
     */
    class NoReorganizationCellReorganizer implements CellReorganizer
    {
        @Override
        public void reorganize()
        {
            // no reorganization
        }
    }

    /**
     * No reorganization. If multiple cells exist, try to merge them all into
     * one cell.
     */
    class oneCellReorganizer implements CellReorganizer
    {
        @Override
        public void reorganize()
        {
            // no reorganization
            // Merge cells if there are more than one cell
            for(CC cell : environment.getCellControllers())
            {
                if(environment.getCellMap().isLeaf(cell.getCellID()))
                {
                    if(!environment.getCellMap().isRoot(cell.getCellID()))
                    {
                        // get the sibling cell controller
                        int siblingIndex = (cell.getCellID().getLastPart() + 1) % 2;
                        CellID siblingID = cell.getCellID().getParentID().createChild(siblingIndex);

                        if(environment.getCellMap().isLeaf(siblingID))
                        {
                            environment.enqueueCellMerge(cell.getCellID());
                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * Basic strategy for reorganization applying naive population balance based
     * on a threshold
     */
    class BasicCellReorganizer implements CellReorganizer
    {
        @Override
        public void reorganize()
        {
            for(CC cell : environment.getCellControllers())
            {
                if(environment.getCellMap().isLeaf(cell.getCellID()))
                {
                    int cellAgentsCount = cell.getAgentsCount();
                    CellID cellID = cell.getCellID();
                    if(cellAgentsCount > SimConfig.getInstance().max_workload_threshold)
                    {
                        environment.enqueueCellSplit(cellID);
                        return;
                    }
                    else if(!environment.getCellMap().isRoot(cell.getCellID()))
                    {
                        // get the sibling cell controller
                        int siblingIndex = (cell.getCellID().getLastPart() + 1) % 2;
                        CellID siblingID = cell.getCellID().getParentID().createChild(siblingIndex);

                        if(environment.getCellMap().isLeaf(siblingID))
                        {
                            CC sibling = environment.getCellController(siblingID);
                            int siblingAgentsCount = sibling.getAgentsCount();
                            if((cellAgentsCount + siblingAgentsCount) <= SimConfig.getInstance().max_workload_threshold)
                            {
                                environment.enqueueCellMerge(cellID);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Basic strategy for reorganization applying naive population balance based
     * on a threshold of number of agents and number of environment objects.
     */
    class BasicEOCellReorganizer implements CellReorganizer
    {
        @Override
        public void reorganize()
        {
            // System.out.println(getPrinterFriendlySortedList(environment.getCellControllers()));

            for(CC cell : environment.getCellControllers())
            {
                if(environment.getCellMap().isLeaf(cell.getCellID()))
                {
                    int cellEntitiesCount = cell.getAgentsCount() + cell.getEnvObjectsCount();
                    CellID cellID = cell.getCellID();

                    if(cellEntitiesCount > SimConfig.getInstance().max_workload_threshold)
                    {
                        environment.enqueueCellSplit(cellID);
                        return;
                    }
                    else if(!environment.getCellMap().isRoot(cell.getCellID()))
                    {
                        // get the sibling cell controller
                        int siblingIndex = (cell.getCellID().getLastPart() + 1) % 2;
                        CellID siblingID = cell.getCellID().getParentID().createChild(siblingIndex);

                        if(environment.getCellMap().isLeaf(siblingID))
                        {
                            CC sibling = environment.getCellController(siblingID);
                            int siblingEntitiesCount = sibling.getAgentsCount() + sibling.getEnvObjectsCount();

                            if((cellEntitiesCount + siblingEntitiesCount) <= SimConfig.getInstance().max_workload_threshold)
                            {
                                environment.enqueueCellMerge(cellID);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Greedy strategy for reorganization applying naive population balance
     * based on a threshold and prioritization
     */
    class GreedyCellReorganizer implements CellReorganizer
    {
        @Override
        public void reorganize()
        {
            List<CC> sortedCells = environment.getCellControllers();
            Collections.sort(sortedCells, new PopulationDescendingComparator());

            for(CC cell : sortedCells)
            {
                if(environment.getCellMap().isLeaf(cell.getCellID()))
                {
                    int cellAgentsCount = cell.getAgentsCount();
                    CellID cellID = cell.getCellID();
                    if(cellAgentsCount > SimConfig.getInstance().max_workload_threshold)
                    {
                        environment.enqueueCellSplit(cellID);
                        return;
                    }
                    else if(!environment.getCellMap().isRoot(cell.getCellID()))
                    {
                        // get the sibling cell controller
                        int siblingIndex = (cell.getCellID().getLastPart() + 1) % 2;
                        CellID siblingID = cell.getCellID().getParentID().createChild(siblingIndex);

                        if(environment.getCellMap().isLeaf(siblingID))
                        {
                            CC sibling = environment.getCellController(siblingID);
                            int siblingAgentsCount = sibling.getAgentsCount();
                            if((cellAgentsCount + siblingAgentsCount) <= SimConfig.getInstance().max_workload_threshold)
                            {
                                environment.enqueueCellMerge(cellID);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Greedy strategy for reorganization applying naive population balance
     * based on a threshold of agents and environment objects and prioritization
     */
    class GreedyEOCellReorganizer implements CellReorganizer
    {
        @Override
        public void reorganize()
        {
            List<CC> sortedCells = environment.getCellControllers();
            Collections.sort(sortedCells, new PopulationDescendingComparator()
            {
                @Override
                public int compare(CC o1, CC o2)
                {
                    Integer i1 = o1.getAgentsCount() + o1.getEnvObjectsCount();
                    Integer i2 = o2.getAgentsCount() + o2.getEnvObjectsCount();
                    return -i1.compareTo(i2);
                }
            });

            for(CC cell : sortedCells)
            {
                if(environment.getCellMap().isLeaf(cell.getCellID()))
                {
                    int cellEntitiesCount = cell.getAgentsCount() + cell.getEnvObjectsCount();

                    CellID cellID = cell.getCellID();
                    if(cellEntitiesCount > SimConfig.getInstance().max_workload_threshold)
                    {
                        environment.enqueueCellSplit(cellID);
                        return;
                    }
                    else if(!environment.getCellMap().isRoot(cell.getCellID()))
                    {
                        // get the sibling cell controller
                        int siblingIndex = (cell.getCellID().getLastPart() + 1) % 2;
                        CellID siblingID = cell.getCellID().getParentID().createChild(siblingIndex);

                        if(environment.getCellMap().isLeaf(siblingID))
                        {
                            CC sibling = environment.getCellController(siblingID);
                            int siblingEntitiesCount = sibling.getAgentsCount() + sibling.getEnvObjectsCount();
                            if((cellEntitiesCount + siblingEntitiesCount) <= SimConfig.getInstance().max_workload_threshold)
                            {
                                environment.enqueueCellMerge(cellID);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Fair strategy for reorganization applying naive population balance based
     * on a threshold, prioritization, and number of cell controllers
     */
    class FairCellReorganizer implements CellReorganizer
    {
        private StatsHelper criticalAgentCount = new StatsHelper(10);

        @Override
        public void reorganize()
        {
            List<CC> sortedArray = environment.getCellControllers();
            Collections.sort(sortedArray, new PopulationAscendingComparator());

            criticalAgentCount.add((float) sortedArray.get(sortedArray.size() - 1).getAgentsCount());

            if(environment.getCellControllers().size() < SimConfig.getInstance().cc_capacity)
            {
                for(int i = sortedArray.size() - 1; i >= 0; i--)
                {
                    CC splitCell = sortedArray.get(i);
                    if(splitCell.getAgentsCount() > SimConfig.getInstance().max_workload_threshold)
                    {
                        if(environment.getCellMap().isLeaf(splitCell.getCellID()) && environment.getCellMap().getCellDepth(splitCell.getCellID()) < SimConfig.getInstance().max_cc_depth)
                        {
                            environment.enqueueCellSplit(splitCell.getCellID());
                            return;
                        }
                    }
                    else
                    {
                        break;
                    }
                }
            }

            for(int i = 0; i < sortedArray.size(); i++)
            {
                CC mergeCell = sortedArray.get(i);
                if(mergeCell.getAgentsCount() < SimConfig.getInstance().max_workload_threshold)
                {
                    if(!environment.getCellMap().isRoot(mergeCell.getCellID()))
                    {
                        // get the sibling cell controller
                        int siblingIndex = (mergeCell.getCellID().getLastPart() + 1) % 2;
                        CellID siblingID = mergeCell.getCellID().getParentID().createChild(siblingIndex);

                        if(environment.getCellMap().isLeaf(siblingID))
                        {
                            CC sibling = environment.getCellController(siblingID);
                            int siblingAgentsCount = sibling.getAgentsCount();
                            if((mergeCell.getAgentsCount() + siblingAgentsCount) <= SimConfig.getInstance().max_workload_threshold)
                            {
                                environment.enqueueCellMerge(mergeCell.getCellID());
                                return;
                            }
                        }
                    }
                }
                else
                {
                    if(!environment.getCellMap().isRoot(mergeCell.getCellID()))
                    {
                        // get the sibling cell controller
                        int siblingIndex = (mergeCell.getCellID().getLastPart() + 1) % 2;
                        CellID siblingID = mergeCell.getCellID().getParentID().createChild(siblingIndex);

                        if(environment.getCellMap().isLeaf(siblingID))
                        {
                            CC sibling = environment.getCellController(siblingID);
                            int siblingAgentsCount = sibling.getAgentsCount();

                            if((mergeCell.getAgentsCount() + siblingAgentsCount) <= criticalAgentCount.getAverage() / 2)
                            {
                                environment.enqueueCellMerge(mergeCell.getCellID());
                                return;
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * Fair strategy for reorganization applying naive population balance of
     * agents and environment objects based on a threshold, ranking, and number
     * of cell controllers
     * For experiment evaluation in AAMAS2013 paper
     */
    class FairEOCellReorganizer implements CellReorganizer
    {
        /**
         * Windowed average of largest population in a cell in the past 10
         * executions of this coordinator
         */
        private StatsHelper criticalUrgency = new StatsHelper(10);

        @Override
        public void reorganize()
        {
            List<CC> sortedArray = environment.getCellControllers();
            Collections.sort(sortedArray, new UrgencyAscendingComparatorComparator());
            criticalUrgency.add(computeUrgency(sortedArray.get(sortedArray.size() - 1)));

            if(environment.getCellControllers().size() < SimConfig.getInstance().cc_capacity)
            {
                for(int i = sortedArray.size() - 1; i >= 0; i--)
                {
                    CC splitCell = sortedArray.get(i);
                    float splitCellUrgency = computeUrgency(splitCell);

                    if(splitCellUrgency > SimConfig.getInstance().urgency_threshold)
                    {
                        if(environment.getCellMap().isLeaf(splitCell.getCellID()) && environment.getCellMap().getCellDepth(splitCell.getCellID()) < SimConfig.getInstance().max_cc_depth)
                        {
                            environment.enqueueCellSplit(splitCell.getCellID());
                            return;
                        }
                    }
                    else
                    {
                        // Do not need to continue searching for cells to split
                        break;
                    }
                }
            }

            for(int i = 0; i < sortedArray.size(); i++)
            {
                CC mergeCell = sortedArray.get(i);
                float mergeCellUrgency = computeUrgency(mergeCell);

                if(mergeCell.getAgentsCount() < SimConfig.getInstance().urgency_threshold)
                {
                    if(!environment.getCellMap().isRoot(mergeCell.getCellID()))
                    {
                        // get the sibling cell controller
                        int siblingIndex = (mergeCell.getCellID().getLastPart() + 1) % 2;
                        CellID siblingID = mergeCell.getCellID().getParentID().createChild(siblingIndex);

                        if(environment.getCellMap().isLeaf(siblingID))
                        {
                            CC sibling = environment.getCellController(siblingID);
                            int siblingUrgency = sibling.getAgentsCount() + sibling.getEnvObjectsCount();

                            if((mergeCellUrgency + siblingUrgency) <= SimConfig.getInstance().urgency_threshold)
                            {
                                environment.enqueueCellMerge(mergeCell.getCellID());
                                return;
                            }
                        }
                    }
                }
                else
                {
                    if(!environment.getCellMap().isRoot(mergeCell.getCellID()))
                    {
                        // get the sibling cell controller
                        int siblingIndex = (mergeCell.getCellID().getLastPart() + 1) % 2;
                        CellID siblingID = mergeCell.getCellID().getParentID().createChild(siblingIndex);

                        if(environment.getCellMap().isLeaf(siblingID))
                        {
                            CC sibling = environment.getCellController(siblingID);
                            float siblingUrgency = computeUrgency(sibling);

                            if((mergeCellUrgency + siblingUrgency) <= criticalUrgency.getAverage() / 2)
                            {
                                environment.enqueueCellMerge(mergeCell.getCellID());
                                return;
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * Self-organizing reorganization strategy, based on bottom-up interactions
     * For experiment evaluation in AAMAS2013 paper
     */
    class SelfOrganizingReorganizer implements CellReorganizer
    {
        @Override
        public void reorganize()
        {
            try
            {
                List<Workload> sortedWorkLoadAlerts = new ArrayList<>(assistanceRequests.values());
                Workload ccMax = null;
                if(sortedWorkLoadAlerts.size() > 0)
                {
                    if(sortedWorkLoadAlerts.size() > 1)
                    {
                        Collections.sort(sortedWorkLoadAlerts, new WorkloadUrgencyAscendingComparatorComparator());
                    }
                    ccMax = sortedWorkLoadAlerts.get(sortedWorkLoadAlerts.size() - 1);
                }

                if(ccMax != null)
                    environment.enqueueCellSplit(ccMax.getCellID());
            }
            catch(Exception e)
            {
                logger.error("An error occurred while processing coordinator task", e);
            }
        }
    }

    /**
     * Autonomic reorganization strategy, based on bottom-up and top-down interactions
     * Algorithm 2 for AAMAS2013 paper
     */
    class AutonomicReorganizer implements CellReorganizer
    {
        /**
         * Windowed average of largest urgency in a cell in the past 10
         * executions of this coordinator
         */
        private StatsHelper criticalUrgency = new StatsHelper(10);

        @Override
        public void reorganize()
        {
            try
            {
                List<Workload> sortedWorkLoadAlerts = new ArrayList<>(assistanceRequests.values());
                Workload ccMax = null;
                if(sortedWorkLoadAlerts.size() > 0)
                {
                    if(sortedWorkLoadAlerts.size() > 1)
                    {
                        Collections.sort(sortedWorkLoadAlerts, new WorkloadUrgencyAscendingComparatorComparator());
                    }
                    ccMax = sortedWorkLoadAlerts.get(sortedWorkLoadAlerts.size() - 1);
                    criticalUrgency.add(computeUrgency(ccMax.getCellID(), ccMax.getWorkload()));
                }

                if(environment.getCellControllers().size() < SimConfig.getInstance().cc_capacity)
                {
                    for(int i = sortedWorkLoadAlerts.size() - 1; i >= 0; i--)
                    {
                        Workload ccSplit = sortedWorkLoadAlerts.get(i);

                        if(computeUrgency(ccSplit.getCellID(), ccSplit.getWorkload()) < SimConfig.getInstance().urgency_threshold)
                            break;

                        if(environment.getCellMap().isLeaf(ccSplit.getCellID()) && environment.getCellMap().getCellDepth(ccSplit.getCellID()) < SimConfig.getInstance().max_cc_depth)
                        {
                            environment.enqueueCellSplit(ccSplit.getCellID());
                            return;
                        }
                    }
                }

                List<CC> sortedArray = environment.getCellControllers();
                Collections.sort(sortedArray, new UrgencyAscendingComparatorComparator());

                for(int i = 0; i < sortedArray.size(); i++)
                {
                    CC mergeCellWorkload = sortedArray.get(i);

                    if(!environment.getCellMap().isRoot(mergeCellWorkload.getCellID()))
                    {
                        float mergeCellUrgency = computeUrgency(mergeCellWorkload);

                        if(mergeCellUrgency >= SimConfig.getInstance().urgency_threshold)
                        {
                            int siblingIndex = (mergeCellWorkload.getCellID().getLastPart() + 1) % 2;
                            CellID siblingID = mergeCellWorkload.getCellID().getParentID().createChild(siblingIndex);
                            if(environment.getCellMap().isLeaf(siblingID))
                            {
                                CC sibling = environment.getCellController(siblingID);
                                float siblingCellUrgency = computeUrgency(siblingID, sibling.getWorkload());

                                if((mergeCellUrgency + siblingCellUrgency) <= criticalUrgency.getAverage() / 2)
                                    environment.enqueueCellMerge(mergeCellWorkload.getCellID());
                                return;
                            }
                        }
                    }
                }
            }
            catch(Exception e)
            {
                logger.error("An error occurred while processing coordinator task", e);
            }
        }
    }

    /*
     * Printing utilities
     */

    @SuppressWarnings("unused")
    private StringBuilder getPrinterFriendlySortedList(List<CC> sortedCells)
    {
        StringBuilder sb = new StringBuilder();
        for(CC cc : sortedCells)
        {
            sb.append(String.format("%s[%s] ", cc.getCellID(), cc.getAgentsCount()));
        }

        return sb;
    }

    /*
     * Comparators
     */

    class WorkloadUrgencyAscendingComparatorComparator implements Comparator<Workload>
    {
        @Override
        public int compare(Workload o1, Workload o2)
        {
            Float u1 = computeUrgency(o1.getCellID(), o1.getWorkload());
            Float u2 = computeUrgency(o2.getCellID(), o2.getWorkload());
            return u1.compareTo(u2);
        }

    }

    class UrgencyAscendingComparatorComparator implements Comparator<CC>
    {
        @Override
        public int compare(CC o1, CC o2)
        {
            Float u1 = computeUrgency(o1);
            Float u2 = computeUrgency(o2);
            return u1.compareTo(u2);
        }
    }

    class PopulationDescendingComparator implements Comparator<CC>
    {
        @Override
        public int compare(CC o1, CC o2)
        {
            Integer i1 = o1.getAgentsCount();
            Integer i2 = o2.getAgentsCount();
            return -i1.compareTo(i2);
        }
    }

    class PopulationAscendingComparator implements Comparator<CC>
    {

        @Override
        public int compare(CC o1, CC o2)
        {
            Integer i1 = o1.getAgentsCount();
            Integer i2 = o2.getAgentsCount();
            return i1.compareTo(i2);
        }
    }

}
