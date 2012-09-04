/**
 * 
 */
package org.ubimix.vaadin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.AbstractApplicationServlet;

/**
 * @author kotelnikov
 */
public class WebApplicationServlet extends AbstractApplicationServlet {

    private static final String ENCODING = "UTF-8";

    private static final String MIME_TYPE = "application/javascript";

    private static final long serialVersionUID = -1;

    private WebModuleRegistry moduleService;

    /**
     * 
     */
    public WebApplicationServlet(WebModuleRegistry moduleRegistry) {
        this.moduleService = moduleRegistry;
    }

    protected void copy(InputStream input, OutputStream out) throws IOException {
        try {
            byte[] buf = new byte[1024];
            try {
                int len;
                while ((len = input.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            input.close();
        }
    }

    @Override
    protected Class<? extends Application> getApplicationClass() {
        return WebApplication.class;
    }

    @Override
    protected Application getNewApplication(HttpServletRequest request)
        throws ServletException {
        return new WebApplication(moduleService);
    }

    private String readString(InputStream input) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            byte[] buf = new byte[1024];
            try {
                int len;
                while ((len = input.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            input.close();
        }
        String str = new String(out.toByteArray(), ENCODING);
        return str;
    }

    public void sendContent(ServletRequest req, ServletResponse resp)
        throws ServletException,
        IOException {
        req.setCharacterEncoding(ENCODING);
        resp.setCharacterEncoding(ENCODING);
        resp.setContentType(MIME_TYPE);

        String str = readString(req.getInputStream());
        resp.getOutputStream().write(str.getBytes(ENCODING));
    }

    @Override
    protected void service(
        HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException {
        String method = request.getMethod();
        if ("GET".equals(method.toUpperCase())) {
            String path = request.getPathInfo();
            if (path != null && path.startsWith("/VAADIN/")) {
                URL url = moduleService.getResource(path);
                if (url != null) {
                    URLConnection connection = url.openConnection();
                    String type = connection.getContentType();
                    response.setContentType(type);
                    InputStream in = connection.getInputStream();
                    try {
                        ServletOutputStream out = response.getOutputStream();
                        copy(in, out);
                    } finally {
                        in.close();
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
                return;
            }
        }
        super.service(request, response);
    }

    @Override
    public void service(ServletRequest arg0, ServletResponse arg1)
        throws ServletException,
        IOException {
        // TODO Auto-generated method stub
        super.service(arg0, arg1);
    }
}
