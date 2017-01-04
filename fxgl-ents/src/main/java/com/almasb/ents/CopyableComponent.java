package com.almasb.ents;

/**
 * Marks a component as copyable.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface CopyableComponent<T extends Component> {

    /**
     * Copies this component.
     * The general contract should be similar to {@link Object#clone()}.
     * The 'depth' of the copy should be determined by the user.
     *
     * @return new instance (copy) of the component with copied values
     */
    T copy();
}
