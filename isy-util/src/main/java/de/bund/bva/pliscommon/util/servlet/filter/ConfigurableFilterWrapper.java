package de.bund.bva.pliscommon.util.servlet.filter;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * Wrapps a {@link Filter} for authentication purposes, so that this filter can be active or not active,
 * depending on the Spring configuration.
 *
 * This is especially useful for testing purposes or for working with different filters depending on the
 * spring profile.
 */
public class ConfigurableFilterWrapper implements Filter {

    /**
     * The real filter to call.
     */
    private Filter filter;

    /**
     * Flag if the filter is active. Will be set by Spring.
     */
    private boolean isFilterActive = true;

    /**
     * Additional parameters for the filter.
     */
    private Map<String, String> additionalFilterParameters;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (this.isFilterActive) {
            this.filter.init(new ConfigurableFilterWrapperConfig(filterConfig,
                this.additionalFilterParameters));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        if (this.isFilterActive) {
            if (this.filter != null) {
                this.filter.doFilter(request, response, chain);
            } else {
                ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        if (this.isFilterActive) {
            this.filter.destroy();
        }
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public void setIsFilterActive(boolean isFilterActive) {
        this.isFilterActive = isFilterActive;
    }

    public void setAdditionalFilterParameters(Map<String, String> additionalFilterParameters) {
        this.additionalFilterParameters = additionalFilterParameters;
    }
}
