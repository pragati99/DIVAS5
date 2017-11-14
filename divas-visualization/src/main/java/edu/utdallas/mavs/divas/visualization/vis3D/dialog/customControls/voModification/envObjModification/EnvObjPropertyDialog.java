package edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voModification.envObjModification;

import java.util.HashMap;
import java.util.Map;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voModification.AbstractPropertyDialog;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.EnvObjectVO;

/**
 * This class describes a property dialog for an environment object.
 */
public class EnvObjPropertyDialog extends AbstractPropertyDialog<EnvObjectVO, EnvObjPropertyDialogController>
{
    /**
     * The {@link EnvObjPropertyDialog} constructor.
     * 
     * @param parentElement
     *        The parent element for the dialog.
     */
    public EnvObjPropertyDialog(Element parentElement)
    {
        super(parentElement);
    }

    @Override
    public boolean isContextSelected(Object object)
    {
        if(object instanceof EnvObjectVO)
        {
            return ((EnvObjectVO) object).isContextSelected();
        }
        else
        {
            return false;
        }
    }
  
    @Override
    public String getWidth()
    {
        return "375px";
    }

    @Override
    public String getHeight()
    {
        return "260px";
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
    public Class<EnvObjPropertyDialogDefinition> getDefinitionClass()
    {
        return EnvObjPropertyDialogDefinition.class;
    }

    @Override
    public Class<EnvObjPropertyDialogController> getControllerClass()
    {
        return EnvObjPropertyDialogController.class;
    }

    @Override
    public void registerNiftyDefinition(Nifty nifty)
    {
        EnvObjPropertyDialogDefinition.register(nifty);
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
        return (new EnvObjPropertyDialogController()).getClass().getName();
    }

    @Override
    public String getPositionX()
    {
        return "80";
    }

    @Override
    public String getPositionY()
    {
        return "80";
    }

    @Override
    public String getContentHeight()
    {
        return "240px";
    }

    @Override
    public String getContentWidth()
    {
        return "100%";
    }
    
    @Override
    public String getEntityId(Object entity)
    {
        return String.valueOf(((EnvObjectVO) entity).getState().getID());
    }
    
    @Override
    public String createDialogId(String id)
    {
        return String.format("EnvObjPropertyWindow%s", id);
    }

    @Override
    public String createDialogTitle(String id)
    {
        return String.format("%s %s", "Env Object ", id);
    }
}
