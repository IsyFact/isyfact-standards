package de.bund.bva.pliscommon.util.servlet.filter;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

/**
 * FilterConfig class that can be extended by additional parameters e.g. from Spring configuration or an
 * external property file. The resulting FilterConfig is based upon an given FilterConfig.
 */
public class ConfigurableFilterWrapperConfig implements FilterConfig {

    /** The parameters of the filter. */
    private Map<String, String> parameters;

    /** The given original filter. */
    private FilterConfig givenFilterConfig;

    /**
     * Creates a new instance.
     *
     * @param filterConfig
     *            The base FilterConfig.
     * @param additionalParameters
     *            The additional Parameters.
     */
    public ConfigurableFilterWrapperConfig(FilterConfig filterConfig, Map<String, String> additionalParameters) {
        this.parameters = new HashMap<>(50);
        this.givenFilterConfig = filterConfig;
        Enumeration<String> filterConfigParameterNames = filterConfig.getInitParameterNames();
        String parameterName = null;
        while (filterConfigParameterNames.hasMoreElements()) {
            parameterName = filterConfigParameterNames.nextElement();
            this.parameters.put(parameterName, filterConfig.getInitParameter(parameterName));
        }
        if (additionalParameters != null) {
            this.parameters.putAll(additionalParameters);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFilterName() {
        return this.givenFilterConfig.getFilterName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServletContext getServletContext() {
        return this.givenFilterConfig.getServletContext();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInitParameter(String name) {
        return this.parameters.get(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Enumeration getInitParameterNames() {
        return Collections.enumeration(this.parameters.keySet());
    }

}
