package edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voModification;

import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.AbstractDialogController;

/**
 * The EnvObjOptionsDialogController contains all the events that the EnvObjOptionsDialog element generates.
 * 
 * @param <E>
 *        The entity vo.
 */
public abstract class AbstractPropertyDialogController<E> extends AbstractDialogController
{
    protected E entityVO;

    /**
     * Constructs a new abstract property dialog controller
     */
    public AbstractPropertyDialogController()
    {}

    protected abstract void setEntity(E entity);
}
