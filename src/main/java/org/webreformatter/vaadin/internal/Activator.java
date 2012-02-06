/*
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership. This file is licensed to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.webreformatter.vaadin.internal;

import java.net.URL;
import java.util.Properties;

import javax.servlet.ServletException;

import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.webreformatter.commons.osgi.ConfigurableMultiserviceActivator;
import org.webreformatter.commons.osgi.OSGIObjectActivator;
import org.webreformatter.commons.osgi.OSGIObjectDeactivator;
import org.webreformatter.commons.osgi.OSGIService;
import org.webreformatter.commons.osgi.OSGIServiceActivator;
import org.webreformatter.commons.osgi.OSGIServiceDeactivator;
import org.webreformatter.vaadin.IWebModule;
import org.webreformatter.vaadin.IWebResources;
import org.webreformatter.vaadin.WebApplicationServlet;
import org.webreformatter.vaadin.WebModuleRegistry;

/**
 * @author kotelnikov
 */
public class Activator extends ConfigurableMultiserviceActivator
    implements
    IWebResources {

    private WebModuleRegistry fRegistry = new WebModuleRegistry();

    private HttpService fService;

    private String fServletPrefix;

    public Activator() {
    }

    @OSGIObjectActivator
    public void activate() throws ServletException, NamespaceException {
        HttpContext context = fService.createDefaultHttpContext();
        WebApplicationServlet servlet = new WebApplicationServlet(fRegistry);
        Properties properties = new Properties();
        fServletPrefix = "/*";
        fService.registerServlet(fServletPrefix, servlet, properties, context);
    }

    @OSGIServiceActivator
    public void addModule(IWebModule module) {
        fRegistry.registerModule(module);
    }

    @OSGIServiceActivator(min = 0)
    public void addResources(IWebResources resources) {
        fRegistry.registerResources(resources);
    }

    @OSGIObjectDeactivator
    public void deactivate() {
        if (fServletPrefix != null) {
            fService.unregister(fServletPrefix);
            fServletPrefix = null;
        }
    }

    @OSGIService
    public IWebResources getBundleResources() {
        return this;
    }

    public URL getResource(String resource) {
        return fContext.getBundle().getResource(resource);
    }

    @OSGIServiceDeactivator
    public void removeModule(IWebModule module) {
        fRegistry.unregisterModule(module);
    }

    @OSGIServiceDeactivator
    public void removeResources(IWebResources resources) {
        fRegistry.unregisterResources(resources);
    }

    @OSGIServiceDeactivator
    public void removeService(HttpService service) {
        fService = null;
    }

    @OSGIServiceActivator
    public void setService(HttpService service) {
        fService = service;
    }

}
