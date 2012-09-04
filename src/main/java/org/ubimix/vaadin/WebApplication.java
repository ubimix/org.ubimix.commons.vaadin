/**
 * 
 */
package org.ubimix.vaadin;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import com.vaadin.Application;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Window;

/**
 * @author kotelnikov
 */
public class WebApplication extends Application {

    private static final long serialVersionUID = -5912759343477670326L;

    private Window fActiveWindow;

    private Set<IWebModule> fMainModules = new LinkedHashSet<IWebModule>();

    private WebModuleRegistry fRegistry;

    /**
     * 
     */
    public WebApplication(WebModuleRegistry registry) {
        fRegistry = registry;
        Collection<IWebModule> list = fRegistry.getModules();
        for (IWebModule module : list) {
            addMainModule(module);
        }
        fRegistry.addListener(new IWebModuleListener() {
            public void onModuleAdd(IWebModule module) {
                addMainModule(module);
            }

            public void onModuleRemove(IWebModule module) {
                removeMainModule(module);
            }
        });
    }

    private synchronized void addMainModule(IWebModule module) {
        String id = module.getModuleId();
        if (IWebModule.ID_MAIN.equals(id)) {
            IWebModule activeModule = getActiveModule();
            fMainModules.add(module);
            if (activeModule == null) {
                updateMainWindow();
            }
        }
    }

    private synchronized IWebModule getActiveModule() {
        Iterator<IWebModule> iterator = fMainModules.iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

    // Supports multiple browser windows
    @Override
    public Window getWindow(String name) {
        // If we already have the requested window, use it
        Window w = super.getWindow(name);
        if (w == null) {
            // If no window found, create it
            w = newWindow(name);
            if (w != null) {
                // set windows name to the one requested
                w.setName(name);
                // add it to this application
                addWindow(w);
                // ensure use of window specific url
                w.open(new ExternalResource(w.getURL().toString()));
            }
        }
        return w;
    }

    /**
     * @see com.vaadin.Application#init()
     */
    @Override
    public void init() {
        updateMainWindow();
    }

    protected Window newWindow(String name) {
        Window result = null;
        String id = name;
        int idx = name.lastIndexOf("_");
        if (idx > 0) {
            id = name.substring(0, idx);
        }
        IWebModule module = fRegistry.getModule(id);
        if (module != null) {
            result = module.newWindow();
        }
        return result;
    }

    private synchronized void removeMainModule(IWebModule module) {
        String id = module.getModuleId();
        if (IWebModule.ID_MAIN.equals(id)) {
            IWebModule activeModule = getActiveModule();
            fMainModules.remove(module);
            if (activeModule == module) {
                removeMainWindow();
                updateMainWindow();
            }
        }
    }

    private void removeMainWindow() {
        if (fActiveWindow != null) {
            fActiveWindow = null;
        }
    }

    private void updateMainWindow() {
        IWebModule first = getActiveModule();
        if (first != null && fActiveWindow == null) {
            fActiveWindow = first.newWindow();
            setMainWindow(fActiveWindow);
        }
    }

}
