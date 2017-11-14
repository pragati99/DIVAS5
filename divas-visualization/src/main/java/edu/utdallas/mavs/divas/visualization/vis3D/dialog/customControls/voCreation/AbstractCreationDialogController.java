package edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voCreation;

import java.util.ArrayList;
import java.util.List;

import org.bushe.swing.event.EventTopicSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetNotFoundException;

import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.controls.dynamic.CustomControlCreator;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.events.NiftyMouseEvent;
import edu.utdallas.mavs.divas.visualization.utils.VisResource;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.AbstractDialogController;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.utils.CommonBuilders;

/**
 * This class represents the controller for the abstract creation dialog.
 * 
 * @param <T>
 *        The object type to be added to the simulation.
 */
public abstract class AbstractCreationDialogController<T> extends AbstractDialogController
{
    private static Logger           logger        = LoggerFactory.getLogger(AbstractCreationDialogController.class);

    // Search components
    protected boolean               searchEnabled;
    protected TextField             searchTextfield;
    protected Element               searchButton;

    // Grid components
    protected Element               gridPanelElement;
    protected Element               scrollPanel;
    protected Element               lastUsedResourcePanel;

    protected List<Element>         listOfElement = new ArrayList<Element>();

    // private Queue<Element> lastUsedResource = new LinkedList<Element>();

    protected static CommonBuilders builders      = new CommonBuilders();

    /**
     * Sets the entity creation type in the simulation.
     * 
     * @param object
     *        The type of entity currently being allow to add to the simulation.
     */
    public abstract void createObject(Object object);

    /**
     * Searches the vis repository with the given text.
     * 
     * @param text
     *        The query string to search the vis repository
     */
    public abstract void search(String text);

    @Override
    public void bindNiftyElements()
    {
        searchTextfield = getNiftyControl(AbstractCreationDialogDefinition.SEARCH_TEXTFIELD, TextField.class);
        searchButton = getElement(AbstractCreationDialogDefinition.SEARCH_BUTTON);
        gridPanelElement = getElement(AbstractCreationDialogDefinition.GRID_PANEL);
        scrollPanel = getElement(AbstractCreationDialogDefinition.GRID_SCROLL_PANEL);
        lastUsedResourcePanel = getElement(AbstractCreationDialogDefinition.LAST_USED_RESOURCES_PANEL);
    }

    @Override
    public void populatePanel()
    {}

    @Override
    public void subscriptions()
    {
        nifty.subscribe(screen, getSearchButton().getId(), NiftyMouseEvent.class, new EventTopicSubscriber<NiftyMouseEvent>()
        {
            @Override
            public void onEvent(final String id, final NiftyMouseEvent event) throws AssetNotFoundException
            {
                if(event.isButton0Down())
                {
                    search(getSearchTextfield().getText());
                }
            }
        });
    }

    @Override
    public void updatePanel()
    {}

    /**
     * Resets the content panel
     */
    public void resetContentPanel()
    {
        // Unsubscribe all events;
        unsubscribeAll();

        // Clear the grid panel
        gridPanelElement.getElements().clear();
    }

    protected void updateContentPanel(final List<VisResource<T>> list)
    {
        for(VisResource<?> visResource : list)
        {
            add(visResource, gridPanelElement);
        }
    }

    /**
     * Adds nifty subscription
     * 
     * @param element
     *        the nifty element to be subscribed to mouse events
     * @param object
     *        the object to be created when the element is clicked.
     * @param customButtonControl
     *        the custom control creator.
     * @param visResource
     *        the visualization resource repository
     */
    public void addSubscription(final Element element, final Object object, final CustomControlCreator customButtonControl, final VisResource<?> visResource)
    {
        logger.debug("Adding subscription for element {}", element);

        nifty.subscribe(screen, element.getId(), NiftyMouseEvent.class, new EventTopicSubscriber<NiftyMouseEvent>()
        {
            @Override
            public void onEvent(final String id, final NiftyMouseEvent event)
            {
                if(event.isButton0Down())
                {
                    createObject(object);
                }
            }
        });

        listOfElement.add(element);
    }

    /**
     * Unsubscribe all nifty elements
     */
    private void unsubscribeAll()
    {
        for(Element element : listOfElement)
        {
            nifty.unsubscribe(element.getId(), NiftyMouseEvent.class);
            screen.unregisterElementId(element.getId());
        }
    }

    private void add(VisResource<?> visResource, Element parent)
    {
        if(hasAsset(visResource.getImage()))
        {
            // Attaching element to the nifty
            (builders.vspacer("20px")).build(nifty, screen, parent);
            CustomControlCreator customButtonControl = createCustomButton(parent, visResource.getName(), visResource.getImage());
            Element customButtonElement = customButtonControl.create(nifty, screen, parent);
            addSubscription(customButtonElement, visResource.getObject(), customButtonControl, visResource);
        }
    }

    protected boolean hasAsset(String image)
    {
        return app.getAssetManager().locateAsset(new AssetKey<>(image)) != null;
    }

    /*
     * private void addLastUsedResource(CustomControlCreator customControlCreator)
     * {
     * Element el = customControlCreator.create(nifty, screen, lastUsedResourcePanel);
     * screen.unregisterElementId(el.getId());
     * if(lastUsedResource.contains(el))
     * {
     * el.markForRemoval();
     * }
     * else
     * {
     * lastUsedResource.add(el);
     * }
     * if(lastUsedResource.size() >= 2)
     * {
     * Element element = lastUsedResource.poll();
     * element.markForRemoval();
     * }
     * }
     */

    protected boolean isSearchEnabled()
    {
        return searchEnabled;
    }

    protected TextField getSearchTextfield()
    {
        return searchTextfield;
    }

    protected Element getSearchButton()
    {
        return searchButton;
    }

    protected Element getScrollPanel()
    {
        return scrollPanel;
    }
}
