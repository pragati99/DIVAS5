package edu.utdallas.mavs.divas.visualization.vis3D.dialog.utils;

import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;

/**
 * This is a helper class of reusable Nifty Element Builders.
 */
public class CommonBuilders
{
    /**
     * Creates an empty panel that has 9px of height. This is used as a vertical spacer.
     * 
     * @return the newly-created {@link PanelBuilder} object.
     */
    public PanelBuilder vspacer()
    {
        return new PanelBuilder()
        {
            {
                childLayoutHorizontal();
                height("9px");
                width("0px");
            }
        };
    }

    /**
     * Creates an empty panel that has the given height. This is used as a vertical spacer.
     * 
     * @param height
     *        The height of the panel.
     * @return the newly-created {@link PanelBuilder} object.
     */
    public PanelBuilder vspacer(final String height)
    {
        return new PanelBuilder()
        {
            {
                childLayoutHorizontal();
                height(height);
                width("100%");
            }
        };
    }

    /**
     * Creates an empty panel that has the given width. This is used as a horizontal spacer.
     * 
     * @param width
     * @return the newly-created {@link PanelBuilder} object.
     */
    public PanelBuilder hspacer(final String width)
    {
        return new PanelBuilder()
        {
            {
                width(width);
                height("0px");
            }
        };
    }

    /**
     * Creates a label with the given text.
     * 
     * @param text
     *        The text of the label.
     * @return
     *         the newly-created {@link LabelBuilder} object.
     */
    public LabelBuilder createLabel(final String text)
    {
        return createLabel(text, "100px");
    }

    /**
     * Creates a label with the given text and width.
     * 
     * @param text
     *        The text of the label.
     * @param width
     *        The width of the label.
     * @return
     *         the newly-created {@link LabelBuilder} object.
     */
    public LabelBuilder createLabel(final String text, final String width)
    {
        return new LabelBuilder()
        {
            {
                text(text);
                width(width);
                alignLeft();
                textVAlignCenter();
                textHAlignLeft();
            }
        };
    }

    /**
     * Creates a label with the given id, text and width.
     * 
     * @param id
     *        The id of the label.
     * @param text
     *        The text of the label.
     * @param width
     *        The width of the label.
     * @return
     *         the newly-created {@link LabelBuilder} object.
     */
    public LabelBuilder createLabel(final String id, final String text, final String width)
    {
        return new LabelBuilder(id, text)
        {
            {
                width(width);
                alignLeft();
                textVAlignCenter();
                textHAlignLeft();
            }
        };
    }
}
