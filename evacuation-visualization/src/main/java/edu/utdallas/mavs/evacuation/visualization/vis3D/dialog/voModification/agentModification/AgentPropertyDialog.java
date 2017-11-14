package edu.utdallas.mavs.evacuation.visualization.vis3D.dialog.voModification.agentModification;

import java.util.HashMap;
import java.util.Map;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voModification.AbstractPropertyDialog;
import edu.utdallas.mavs.evacuation.visualization.vis3D.vo.EvacuationHumanAgentVO;

/**
 * This class describes a property dialog for an agent.
 */
public class AgentPropertyDialog extends AbstractPropertyDialog<EvacuationHumanAgentVO, AgentPropertyDialogController>
{
    /**
     * The {@link AgentPropertyDialog} constructor.
     * 
     * @param parentElement
     *        The parent element for the dialog.
     */
    public AgentPropertyDialog(Element parentElement)
    {
        super(parentElement);
    }

    @Override
    public boolean isContextSelected(Object object)
    {
        if(object instanceof EvacuationHumanAgentVO)
        {
            return ((EvacuationHumanAgentVO) object).isContextSelected();
        }
        else
        {
            return false;
        }
    }

    @Override
    public String getWidth()
    {
        return "275px";
    }

    @Override
    public String getHeight()
    {
        return "610px";
    }

    @Override
    public String getAlignment()
    {
        return "center";
    }

    @Override
    public Map<String, String> getParameters()
    {
        return new HashMap<String, String>();
    }

    @Override
    public Class<AgentPropertyDialogDefinition> getDefinitionClass()
    {
        return AgentPropertyDialogDefinition.class;
    }

    @Override
    public Class<AgentPropertyDialogController> getControllerClass()
    {
        return AgentPropertyDialogController.class;
    }

    @Override
    public void registerNiftyDefinition(Nifty nifty)
    {
        AgentPropertyDialogDefinition.register(nifty);
    }

    @Override
    public void updateDialog()
    {
        if(content != null)
        {
            try
            {
                content.getControl(getControllerClass()).updatePanel();
            }
            catch(Exception e)
            {
                entity.setContextSelected(false);
                super.removeDialog();
            }
        }
    }

    @Override
    public String getControllerName()
    {
        return (new AgentPropertyDialogController()).getClass().getName();
    }

    @Override
    public String getPositionX()
    {
        return "40";
    }

    @Override
    public String getPositionY()
    {
        return "40";
    }

    @Override
    public String getContentHeight()
    {
        return "590px";
    }

    @Override
    public String getContentWidth()
    {
        return "100%";
    }

    @Override
    public String createDialogId(String id)
    {
        return String.format("#AgentPropertyWindow%s", id);
    }

    @Override
    public String createDialogTitle(String id)
    {
        return String.format("%s %s", "Agent", id);
    }

    @Override
    public String getEntityId(Object entity)
    {
        return String.valueOf(((EvacuationHumanAgentVO) entity).getState().getID());
    }
}
