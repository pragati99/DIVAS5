package edu.utdallas.mavs.divas.core.sim.agent.interaction.perception;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.data.CombinedReasonedData;
import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.data.EventData;
import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.data.ReasonedData;
import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.data.SensedData;
import edu.utdallas.mavs.divas.core.sim.agent.knowledge.KnowledgeModule;
import edu.utdallas.mavs.divas.core.sim.agent.knowledge.external.EventKnowledge;
import edu.utdallas.mavs.divas.core.sim.agent.knowledge.external.EventPropertyKnowledge;

/**
 * This classs describes an agent's perception combination module.
 * <p>
 * This module is responsible for combining raw sensed data (acquired from the agent's sensors) resulting in knowledge to be memorized by the agent.
 * 
 * @param <KM>
 *        the agent's KnowledgeModule type
 */
public class PerceptionCombinationModule<KM extends KnowledgeModule<?>> implements Serializable
{
    private static final long            serialVersionUID = 1L;

    private static final Logger          logger           = LoggerFactory.getLogger(PerceptionCombinationModule.class);

    /**
     * The agent's KnowledgeModule
     */
    protected KM                         knowledgeModule;

    /**
     * All raw perceived data from this stick
     */
    protected List<SensedData>           perceivedThisTick;

    /**
     * All reasoned data from this stick
     */
    protected List<ReasonedData>         reasonedThisTick;

    /**
     * All combined data from this simulation cycle
     */
    protected List<CombinedReasonedData> combinedThisTick;

    /**
     * The number of events
     */
    protected int                        eventNumber;

    /**
     * Creates a new combination module.
     * 
     * @param knowledgeModule
     *        the agent's KnowledgeModule
     */
    public PerceptionCombinationModule(KM knowledgeModule)
    {
        super();
        this.knowledgeModule = knowledgeModule;
        perceivedThisTick = new ArrayList<SensedData>();
        reasonedThisTick = new ArrayList<ReasonedData>();
        combinedThisTick = new ArrayList<CombinedReasonedData>();
        eventNumber = 0;
    }

    /**
     * Combines agent perceptions into knowledge
     */
    public void combinePerceptions()
    {

        // Combination level 1 is done individually by each sense. Raw sense
        // data is recorded.

        // START
        // Combination level 2. Combine event knowledge rules with perceived
        // single sense raw data from this tick. (Basic Combination)
        combinationLevel2();

        // START
        // Combination level 3. Combine reasoned event knowledge with other
        // reasoned event knowledge and knowledge event data from the same tick.
        // (Current Combination)
        combinationLevel3();

        // START
        // Combination level X. Combine reasoned event knowledge with other
        // reasoned event knowledge and knowledge event data from the different
        // ticks. (Past Event Combination)
        // not implemented yet
        combinationLevelX();

        // select the most likely events - LEVEL 4
        combinationLevel4();

        // logging, for fine debugging
        if(logger.isTraceEnabled())
        {
            printCombinationResults();
        }
    }

    /**
     * Reset the combination data from this cycle
     */
    public void clearCombinationData()
    {
        perceivedThisTick.clear();
        reasonedThisTick.clear();
        combinedThisTick.clear();
        eventNumber = 0;
    }

    /**
     * Add some raw sensed data perceived this tick
     * 
     * @param sd
     *        the raw sense data
     */
    public void addPerceivedData(SensedData sd)
    {
        perceivedThisTick.add(sd);
    }

    /**
     * Count the estimated number of events perceived
     */
    private void eventCounter()
    {
        List<SensedData> diff = new ArrayList<SensedData>();

        if(perceivedThisTick.size() > 0)
        {
            diff.add(perceivedThisTick.get(0));
        }

        for(int i = 0; i < perceivedThisTick.size(); i++)
        {
            boolean newval = true;

            for(int j = 0; j < diff.size(); j++)
            {
                if(SourceComparison(perceivedThisTick.get(i), diff.get(j)))
                {
                    newval = false;
                }
            }
            if(newval == true)
            {
                diff.add(perceivedThisTick.get(i));
            }
        }

        eventNumber = diff.size();
    }

    /**
     * Level 2 of the combination algorithm
     */
    private void combinationLevel2()
    {
        eventCounter();

        for(int i = 0; i < perceivedThisTick.size(); i++)
        {

            SensedData tempSD = perceivedThisTick.get(i);
            ReasonedData tempRD = null;
            String reasonedName = null;
            String reasonedName2 = null;
            String reasonedName3 = null;
            float certaintyPercent = 0;
            float certaintyPercent2 = 0;
            float certaintyPercent3 = 0;

            String reasonedType = null;
            String reasonedType2 = null;
            String reasonedType3 = null;

            List<EventPropertyKnowledge> epk = knowledgeModule.getEventKnowledgeFromType(tempSD.getType());

            for(int j = 0; j < epk.size(); j++)
            {
                if(epk.get(j).getSense().equals(tempSD.getSense()))
                {
                    if(epk.get(j).getType().equals(tempSD.getType()))
                    {
                        float tempCP = -5;
                        float alpha = epk.get(j).getAlpha();
                        float R = epk.get(j).getR();
                        float epsilon = epk.get(j).getEpsilon();
                        float v = tempSD.getValue();

                        if((epsilon < .00000001f) && (v == epk.get(j).getMin()) && (v == epk.get(j).getMax())) // if range = 0
                        {
                            tempCP = 100;
                        }
                        else if(FastMath.abs(alpha - v) >= R) // ALG 7.1 from AAMAS 2011 submission
                        {
                            tempCP = 0;
                        }
                        else if(FastMath.abs(alpha - v) >= epsilon)
                        {
                            tempCP = 70 - (FastMath.abs(alpha - v) - epsilon) / epsilon * 70;
                        }
                        else if(FastMath.abs(alpha - v) < epsilon)
                        {
                            tempCP = 100 - FastMath.abs(alpha - v) / epsilon * 30;
                        }

                        tempCP = tempCP * tempSD.getTrustConstant(); // modify by trust constant for sense

                        if(tempCP > certaintyPercent)
                        {
                            certaintyPercent3 = certaintyPercent2;// two to three
                            reasonedName3 = reasonedName2;
                            reasonedType3 = reasonedType2;

                            certaintyPercent2 = certaintyPercent;// one to two
                            reasonedName2 = reasonedName;
                            reasonedType2 = reasonedType;

                            certaintyPercent = tempCP;
                            reasonedName = epk.get(j).getEventName();
                            reasonedType = epk.get(j).getType();

                        }
                        else if(tempCP > certaintyPercent2)
                        {
                            certaintyPercent3 = certaintyPercent2;// two to three
                            reasonedName3 = reasonedName2;
                            reasonedType3 = reasonedType2;

                            certaintyPercent2 = tempCP;
                            reasonedName2 = epk.get(j).getEventName();
                            reasonedType2 = epk.get(j).getType();
                        }
                        else if(tempCP > certaintyPercent3)
                        {
                            certaintyPercent3 = tempCP;
                            reasonedName3 = epk.get(j).getEventName();
                            reasonedType3 = epk.get(j).getType();
                        }

                    }
                }
            }

            if(reasonedName != null)
            {
                tempRD = new ReasonedData(knowledgeModule.getTime(), certaintyPercent, reasonedName, reasonedType);
            }

            if(reasonedName2 != null)
            {
                tempRD.setCertaintyPercent2(certaintyPercent2);
                tempRD.setPredicatedName2(reasonedName2);
                tempRD.setReasonedType2(reasonedType2);
            }

            if(reasonedName3 != null)
            {
                tempRD.setCertaintyPercent3(certaintyPercent3);
                tempRD.setPredicatedName3(reasonedName3);
                tempRD.setReasonedType3(reasonedType3);
            }

            if(tempRD != null)
            {
                tempRD.setIntensity(tempSD.getIntensity());
                if(tempSD.hasOrigin())
                {
                    tempRD.setOrigin(tempSD.getOrigin());
                }
                if(tempSD.hasDirection())
                {
                    tempRD.setDirection(tempSD.getDirection());
                }

                reasonedThisTick.add(tempRD);
            }
        }
    }

    /**
     * Combination Level 3
     */
    private void combinationLevel3()
    {
        List<EventKnowledge> evks = new ArrayList<EventKnowledge>();

        List<EventData> evksData = new ArrayList<EventData>();
        for(int i = 0; i < reasonedThisTick.size(); i++) // create list of possible events from reasoned data
        {
            EventKnowledge tempevk;
            EventData tempevkData = new EventData();
            ReasonedData tempRD = reasonedThisTick.get(i);
            tempevkData.setIntensity(tempRD.getIntensity());
            tempevk = knowledgeModule.getEventKnowledgeByName(tempRD.getPredicatedName());

            if(tempRD.hasDirection())
            {
                tempevkData.setDirection(tempRD.getDirection());
            }
            if(tempRD.hasOrigin())
            {
                tempevkData.setOrigin(tempRD.getOrigin());
            }

            evks.add(tempevk);
            evksData.add(tempevkData);

            if(tempRD.hasPredicatedName2())
            {
                tempevk = knowledgeModule.getEventKnowledgeByName(tempRD.getPredicatedName2());
                tempevkData = new EventData();
                if(tempRD.hasDirection())
                {
                    tempevkData.setDirection(tempRD.getDirection());
                }
                if(tempRD.hasOrigin())
                {
                    tempevkData.setOrigin(tempRD.getOrigin());
                }

                evks.add(tempevk);
                evksData.add(tempevkData);
            }

            if(tempRD.hasPredicatedName3())
            {
                tempevk = knowledgeModule.getEventKnowledgeByName(tempRD.getPredicatedName3());
                tempevkData = new EventData();
                if(tempRD.hasDirection())
                {
                    tempevkData.setDirection(tempRD.getDirection());
                }
                if(tempRD.hasOrigin())
                {
                    tempevkData.setOrigin(tempRD.getOrigin());
                }

                evks.add(tempevk);
                evksData.add(tempevkData);

            }

        }

        for(int i = 0; i < evks.size(); i++) // compare possible event rules with perceived event data
        {

            CombinedReasonedData crd;
            float combinedCertainity = 0;
            EventKnowledge tempEVK = evks.get(i);
            HashMap<String, EventPropertyKnowledge> epks = tempEVK.getEventPropertyKnowledges();
            Iterator<EventPropertyKnowledge> epkiter = epks.values().iterator();
            int counter = 0;

            while(epkiter.hasNext())
            {
                EventPropertyKnowledge epk = epkiter.next();
                for(int j = 0; j < reasonedThisTick.size(); j++)
                {
                    if(tempEVK.getName().equals(reasonedThisTick.get(j).getPredicatedName()))
                    {
                        if(epk.getType().equals(reasonedThisTick.get(j).getReasonedType()))
                        {
                            if(SourceComparison(reasonedThisTick.get(j), evksData.get(i)))
                            {
                                counter++;
                                combinedCertainity = combinedCertainity + reasonedThisTick.get(j).getCertaintyPercent();
                                break;
                            }
                        }
                    }
                    else if(tempEVK.getName().equals(reasonedThisTick.get(j).getPredicatedName2()))
                    {
                        if(epk.getType().equals(reasonedThisTick.get(j).getReasonedType2()))
                        {
                            if(SourceComparison(reasonedThisTick.get(j), evksData.get(i)))
                            {
                                counter++;
                                combinedCertainity = combinedCertainity + reasonedThisTick.get(j).getCertaintyPercent2();
                                break;
                            }
                        }
                    }
                    else if(tempEVK.getName().equals(reasonedThisTick.get(j).getPredicatedName3()))
                    {
                        if(epk.getType().equals(reasonedThisTick.get(j).getReasonedType3()))
                        {
                            if(SourceComparison(reasonedThisTick.get(j), evksData.get(i)))
                            {
                                counter++;
                                combinedCertainity = combinedCertainity + reasonedThisTick.get(j).getCertaintyPercent3();
                                break;
                            }
                        }
                    }
                }
            }

            // create combined data
            crd = new CombinedReasonedData(knowledgeModule.getTime(), combinedCertainity, tempEVK.getName());
            crd.setMaxEvents(tempEVK.getEventPropertyKnowledges().size());
            crd.setEventCounter(counter);
            if(evksData.get(i).hasDirection())
            {
                crd.setDirection(evksData.get(i).getDirection());
            }
            if(evksData.get(i).hasOrigin())
            {
                crd.setOrigin(evksData.get(i).getOrigin());
            }

            if(crd.getEventCounter() < crd.getMaxEvents())
            {
                crd.setCertaintyPercent(crd.getCertaintyPercent() * crd.getEventCounter() / crd.getMaxEvents());
            }

            crd.setIntensity(evksData.get(i).getIntensity());
            combinedThisTick.add(crd); // add combined data
        }
    }

    /**
     * Source Comparison of events
     * 
     * @param curItem
     *        Item 1 being compared
     * @param otherItem
     *        Item 2 being compared
     * @return whether they are close enough to be considered to be the same event
     */
    private boolean SourceComparison(ReasonedData curItem, EventData otherItem)
    {
        boolean rv = false;

        if(curItem.hasOrigin() && otherItem.hasOrigin())
        {
            if(curItem.getOrigin().distance(otherItem.getOrigin()) < 5)
            {
                rv = true;
            }
        }
        else if(curItem.hasDirection() && otherItem.hasDirection())
        {
            if(curItem.getDirection().normalize().angleBetween(otherItem.getDirection().normalize()) * FastMath.RAD_TO_DEG < 5)
            {
                rv = true;
            }
        }
        else if(curItem.hasOrigin() && otherItem.hasDirection())
        {
            Vector3f curDir = curItem.getOrigin().subtract(knowledgeModule.getSelf().getPosition());
            curDir.normalize();

            if(curDir.angleBetween(otherItem.getDirection().normalize()) * FastMath.RAD_TO_DEG < 5)
            {
                rv = true;
            }

        }
        else if(curItem.hasDirection() && otherItem.hasOrigin())
        {
            Vector3f otherDir = otherItem.getOrigin().subtract(knowledgeModule.getSelf().getPosition());
            otherDir.normalize();

            if(curItem.getDirection().normalize().angleBetween(otherDir) * FastMath.RAD_TO_DEG < 5)
            {
                rv = true;
            }

        }

        return rv;

    }

    /**
     * Source Comparison of events
     * 
     * @param curItem
     *        Item 1 being compared
     * @param otherItem
     *        Item 2 being compared
     * @return whether they are close enough to be considered to be the same event
     */
    private boolean SourceComparison(SensedData curItem, SensedData otherItem)
    {
        boolean rv = false;

        if(curItem.hasOrigin() && otherItem.hasOrigin())
        {
            if(curItem.getOrigin().distance(otherItem.getOrigin()) < 5)
            {
                rv = true;
            }
        }
        else if(curItem.hasDirection() && otherItem.hasDirection())
        {
            if(curItem.getDirection().normalize().angleBetween(otherItem.getDirection().normalize()) * FastMath.RAD_TO_DEG < 5)
            {
                rv = true;
            }
        }
        else if(curItem.hasOrigin() && otherItem.hasDirection())
        {
            Vector3f curDir = curItem.getOrigin().subtract(knowledgeModule.getSelf().getPosition());
            curDir.normalize();

            if(curDir.angleBetween(otherItem.getDirection().normalize()) * FastMath.RAD_TO_DEG < 5)
            {
                rv = true;
            }

        }
        else if(curItem.hasDirection() && otherItem.hasOrigin())
        {
            Vector3f otherDir = otherItem.getOrigin().subtract(knowledgeModule.getSelf().getPosition());
            otherDir.normalize();

            if(curItem.getDirection().normalize().angleBetween(otherDir) * FastMath.RAD_TO_DEG < 5)
            {
                rv = true;
            }
        }

        return rv;

    }

    /**
     * Source Comparison of events
     * 
     * @param curItem
     *        Item 1 being compared
     * @param otherItem
     *        Item 2 being compared
     * @return whether they are close enough to be considered to be the same event
     */
    private boolean SourceComparison(CombinedReasonedData curItem, CombinedReasonedData otherItem)
    {
        boolean rv = false;

        if(curItem.hasOrigin() && otherItem.hasOrigin())
        {
            if(curItem.getOrigin().distance(otherItem.getOrigin()) < 5)
            {
                rv = true;
            }
        }
        else if(curItem.hasDirection() && otherItem.hasDirection())
        {
            if(curItem.getDirection().normalize().angleBetween(otherItem.getDirection().normalize()) * FastMath.RAD_TO_DEG < 5)
            {
                rv = true;
            }
        }
        else if(curItem.hasOrigin() && otherItem.hasDirection())
        {
            Vector3f curDir = curItem.getOrigin().subtract(knowledgeModule.getSelf().getPosition());
            curDir.normalize();

            if(curDir.angleBetween(otherItem.getDirection().normalize()) * FastMath.RAD_TO_DEG < 5)
            {
                rv = true;
            }

        }
        else if(curItem.hasDirection() && otherItem.hasOrigin())
        {
            Vector3f otherDir = otherItem.getOrigin().subtract(knowledgeModule.getSelf().getPosition());
            otherDir.normalize();

            if(curItem.getDirection().normalize().angleBetween(otherDir) * FastMath.RAD_TO_DEG < 5)
            {
                rv = true;
            }

        }

        return rv;

    }

    /**
     * Future more advanced combination
     */
    private void combinationLevelX()
    {

    }

    /**
     * Level 4 of the combination algorithm.
     */
    private void combinationLevel4()
    {
        selectLikelyEvents();
    }

    /**
     * Select the most likely events after combination
     */
    private void selectLikelyEvents()
    {
        List<CombinedReasonedData> combined = new ArrayList<CombinedReasonedData>();

        for(int i = 0; i < combinedThisTick.size(); i++)
        {
            combined.add(combinedThisTick.get(i));
        }

        for(int z = 0; z < eventNumber; z++)
        {
            if(combined.size() > 0)
            {
                int highest = 0;
                float highestCert = 0;
                for(int i = 0; i < combined.size(); i++) // find highest
                {
                    if(combined.get(i).getCertaintyPercent() > highestCert)
                    {
                        highest = i;
                        highestCert = combined.get(i).getCertaintyPercent();
                    }
                }

                CombinedReasonedData highCRD = combined.get(highest);
                knowledgeModule.addEventsThisTick(highCRD); // add highest

                // next remove all events from combined with the same location
                List<CombinedReasonedData> toBeRemoved = new ArrayList<CombinedReasonedData>();
                for(int i = 0; i < combined.size(); i++)
                {
                    if(SourceComparison(highCRD, combined.get(i)))
                    {
                        toBeRemoved.add(combined.get(i));
                    }
                }

                for(int i = 0; i < toBeRemoved.size(); i++)
                {
                    combined.remove(toBeRemoved.get(i));
                }
            }
        }
    }

    /**
     * Prints out the results of the agent sense combination from the current tick.
     */
    public void printCombinationResults()
    {
        System.out.println("HI");
        String DASHES = "-----------------------------";
        List<SensedData> dat = perceivedThisTick;

        logger.trace(DASHES);

        if(dat.size() > 0)
        {
            for(int i = 0; i < dat.size(); i++)
            {
                logger.trace("{}: Perceived: {} Sense: {} Intensity: {}", new Object[] { dat.get(i).getEventOccurredTime(), dat.get(i).getType(), dat.get(i).getSense().toString(), dat.get(i).getValue() });
            }

            logger.trace(DASHES);
            logger.trace("tick # {}", knowledgeModule.getTime());

            logger.trace("Reasoned: ");
            logger.trace(DASHES);

            List<ReasonedData> datt = reasonedThisTick;

            for(int i = 0; i < datt.size(); i++)
            {
                datt.get(i).print();
            }

            logger.trace("Combined: ");
            logger.trace(DASHES);

            List<CombinedReasonedData> dat2 = combinedThisTick;

            for(int i = 0; i < dat2.size(); i++)
            {
                dat2.get(i).print();
            }

            logger.trace("Final Events: ");
            logger.trace(DASHES);

            List<CombinedReasonedData> dat3 = knowledgeModule.getEventsThisTick();

            for(int i = 0; i < dat3.size(); i++)
            {
                dat3.get(i).print();
            }
        }
    }
}
