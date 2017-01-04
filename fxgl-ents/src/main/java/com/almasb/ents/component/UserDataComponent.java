package com.almasb.ents.component;

/**
 * Can be used to store user specific data to add as component
 * to an entity.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class UserDataComponent extends ObjectComponent<Object> {
    public UserDataComponent(Object data) {
        super(data);
    }
}
