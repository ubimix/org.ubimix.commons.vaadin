package org.ubimix.vaadin;

import com.vaadin.ui.Window;

/**
 * @author kotelnikov
 */
public interface IWebModule {

    String ID_MAIN = "";

    String getModuleId();

    Window newWindow();

}
