package org.ubimix.vaadin;

/**
 * @author kotelnikov
 */
public interface IWebModuleListener {

    void onModuleAdd(IWebModule module);

    void onModuleRemove(IWebModule module);

}