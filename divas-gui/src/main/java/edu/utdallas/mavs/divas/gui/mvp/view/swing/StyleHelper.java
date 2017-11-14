package edu.utdallas.mavs.divas.gui.mvp.view.swing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StyleHelper
{
    private final static Logger logger = LoggerFactory.getLogger(StyleHelper.class);

    public static final int     WIDTH  = 600;
    public static final int     HEIGHT = 600;
    public static final String  TITLE  = "Divas 4.0 Control Panel";

    public static void setNimbusLookAndFeel()
    {
        try
        {
            for(javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
            {
                if("Nimbus".equals(info.getName()))
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch(Exception e)
        {
            logger.error("An error has occurred while loading nibus style", e);
        }
    }
}
