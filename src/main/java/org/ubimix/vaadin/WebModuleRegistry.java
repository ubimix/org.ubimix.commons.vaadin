/**
 * 
 */
package org.ubimix.vaadin;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * @author kotelnikov
 */
public class WebModuleRegistry {

    private Collection<IWebModuleListener> fListeners = newListeners();

    private Map<String, IWebModule> fModules = newModules();

    private Collection<IWebResources> fResources = newResources();

    /**
     * 
     */
    public WebModuleRegistry() {
    }

    public synchronized void addListener(IWebModuleListener listener) {
        Collection<IWebModuleListener> listeners = newListeners();
        listeners.addAll(fListeners);
        listeners.add(listener);
        fListeners = listeners;
    }

    public IWebModule getModule(String id) {
        return fModules.get(id);
    }

    public Collection<IWebModule> getModules() {
        return fModules.values();
    }

    public URL getResource(String path) {
        if (path == null) {
            return null;
        }
        URL result = null;
        Collection<IWebResources> list = fResources;
        for (IWebResources resources : list) {
            result = resources.getResource(path);
            if (result != null) {
                break;
            }
        }
        return result;
    }

    public Collection<IWebResources> getResources() {
        return fResources;
    }

    private Collection<IWebModuleListener> newListeners() {
        return new LinkedHashSet<IWebModuleListener>();
    }

    private Map<String, IWebModule> newModules() {
        return new HashMap<String, IWebModule>();
    }

    private Collection<IWebResources> newResources() {
        return new ArrayList<IWebResources>();
    }

    public synchronized void registerModule(IWebModule module) {
        Map<String, IWebModule> map = newModules();
        map.putAll(fModules);
        String id = module.getModuleId();
        removeModule(map, id);
        map.put(id, module);
        for (IWebModuleListener listener : fListeners) {
            listener.onModuleRemove(module);
        }
        fModules = map;
    }

    public synchronized void registerResources(IWebResources resources) {
        Collection<IWebResources> list = newResources();
        list.addAll(fResources);
        list.add(resources);
        fResources = list;
    }

    public synchronized void removeListener(IWebModuleListener listener) {
        Collection<IWebModuleListener> listeners = newListeners();
        listeners.addAll(fListeners);
        listeners.remove(listener);
        fListeners = listeners;
    }

    private boolean removeModule(Map<String, IWebModule> map, String id) {
        boolean result = false;
        IWebModule m = map.remove(id);
        if (m != null) {
            for (IWebModuleListener listener : fListeners) {
                listener.onModuleRemove(m);
            }
            result = true;
        }
        return result;
    }

    public synchronized void unregisterModule(IWebModule module) {
        String id = module.getModuleId();
        unregisterModule(id);
    }

    public synchronized void unregisterModule(String id) {
        Map<String, IWebModule> map = newModules();
        map.putAll(fModules);
        if (removeModule(map, id)) {
            fModules = map;
        }
    }

    public synchronized void unregisterResources(IWebResources resources) {
        Collection<IWebResources> list = newResources();
        list.addAll(fResources);
        if (list.remove(resources)) {
            fResources = list;
        }
    }

}
