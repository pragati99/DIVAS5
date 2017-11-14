package edu.utdallas.mavs.divas.visualization.vis3D.engine;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

/**
 * This class describes the Heads Up Display (HUD) of the visualizer.
 * <p>
 * It is used to display information messages to the user.
 */
public class HUDMessageArea extends Node
{
    private Queue<BitmapText> messages;

    private float             FADE_TIME_IN_SECONDS = 5;
    private int               MAX_MESSAGES         = 10;
    private float             LINE_SPACING         = 14;

    private BitmapFont        guiFont;

    /**
     * Creates a HUD instance for the visualizer
     * 
     * @param guiFont the bitmap of the text font used to display messages to the user
     */
    public HUDMessageArea(BitmapFont guiFont)
    {
        super("HUDMessage Node");

        this.guiFont = guiFont;

        messages = new LinkedList<BitmapText>();

        setQueueBucket(Bucket.Gui);

        addControl(new Control()
        {
            @Override
            public void write(JmeExporter arg0) throws IOException
            {}

            @Override
            public void read(JmeImporter arg0) throws IOException
            {}

            @Override
            public void update(float time)
            {
                // store the number of messages that have disappeared and can be removed
                int removeCount = 0;

                // update alpha of each message, removing hidden messages
                // from the queue
                for(BitmapText text : messages)
                {
                    float alpha = text.getColor().a;

                    alpha -= 1 / (FADE_TIME_IN_SECONDS / time);

                    if(alpha > 0f)
                    {
                        ColorRGBA textColor = text.getColor();
                        textColor.a = alpha;
                        text.setColor(textColor);
                    }
                    else
                    {
                        removeCount++;
                    }
                }

                for(int i = 0; i < removeCount; i++)
                {
                    detachChild(messages.poll());
                }

                float y = 80 + (messages.size() * LINE_SPACING);

                for(BitmapText text : messages)
                {
                    float x = text.getLocalTranslation().getX();
                    float z = text.getLocalTranslation().getZ();
                    text.setLocalTranslation(x, y, z);
                    y -= LINE_SPACING;
                }
            }

            @Override
            public void setSpatial(Spatial arg0)
            {}

            @Override
            public void render(RenderManager arg0, ViewPort arg1)
            {}

            @Override
            public Control cloneForSpatial(Spatial arg0)
            {
                return null;
            }
        });
    }

    /**
     * Adds a message to be displayed in the HUD
     * 
     * @param message the message to be displayed in the HUD
     */
    public void addMessage(String message)
    {
        BitmapText hudMsg = new BitmapText(guiFont, false);

        hudMsg.setName("HUDMessage");
        hudMsg.setText(message);
        hudMsg.setSize(guiFont.getCharSet().getRenderedSize() - 2f);
        hudMsg.setCullHint(Spatial.CullHint.Never);
        hudMsg.setLocalScale(1f);
        hudMsg.setColor(ColorRGBA.Orange.clone());
        hudMsg.setLocalTranslation(new Vector3f(10, 0, 0));
        attachChild(hudMsg);

        messages.add(hudMsg);

        // guarantee that we don't exceed the maximum number of
        // messages for the HUD
        if(messages.size() > MAX_MESSAGES)
            detachChild(messages.poll());
    }
}
