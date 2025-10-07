package de.bund.bva.isyfact.ueberwachung.service.loadbalancer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.Appender;

public class TestLoadbalancerServlet {

	private LoadbalancerServlet loadBalancer;
	private ServletConfig mockConfig;
	private Appender mockAppender;
	private ServletContext mockContext;
	
	@BeforeEach
	public void setUp(){
		loadBalancer = new LoadbalancerServlet();

		Logger logger = (Logger) LoggerFactory.getLogger(LoadbalancerServlet.class);
        mockAppender = mock(Appender.class);
		logger.addAppender(mockAppender);

		mockConfig = mock(ServletConfig.class);
		mockContext = mock(ServletContext.class);
		when(mockConfig.getServletContext()).thenReturn(mockContext);
	}
	
	@Test
	public void testInit() throws ServletException {
		when(mockContext.getRealPath("/WEB-INF/classes/config/isAlive")).thenReturn("/WEB-INF/classes/config/isAlive/isAlive");
		when(mockConfig.getInitParameter("isAliveFileLocation")).thenReturn("/WEB-INF/classes/config/isAlive");
		loadBalancer.init(mockConfig);	
		verify(mockAppender, times(2)).doAppend(any());
	}
	
	@Test
	public void testInitNull() throws ServletException {
		when(mockContext.getRealPath("/WEB-INF/classes/config/isAlive")).thenReturn("/WEB-INF/classes/config/isAlive/isAlive");
		when(mockConfig.getInitParameter("isAliveFileLocation")).thenReturn(null);
		loadBalancer.init(mockConfig);	
		verify(mockAppender, times(3)).doAppend(any());
	}
	
	
	@Test
	public void testDoGet() throws ServletException {
		when(mockContext.getRealPath("/src/test/resources")).thenReturn("/src/test/resources/isAlive");
		when(mockConfig.getInitParameter("isAliveFileLocation")).thenReturn("/src/test/resources");
		loadBalancer.init(mockConfig);	
		HttpServletResponse resp = mock(HttpServletResponse.class);
		loadBalancer.doGet(null, resp);
		verify(mockAppender, times(3)).doAppend(any());
		verify(resp, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
	}
	
	@Test
	public void testDoGetIsAlive() throws ServletException, IOException {
		when(mockContext.getRealPath("/src/test/resources")).thenReturn("src/test/resources/isAlive");
		when(mockConfig.getInitParameter("isAliveFileLocation")).thenReturn("/src/test/resources");
		File f = new File("src/test/resources/isAlive");
		assertTrue(f.createNewFile());
		PrintWriter writer = new PrintWriter(f);
		loadBalancer.init(mockConfig);	
		HttpServletResponse resp = mock(HttpServletResponse.class);
		when(resp.getWriter()).thenReturn(writer);
		loadBalancer.doGet(null, resp);
		verify(mockAppender, times(3)).doAppend(any());
		verify(resp, times(1)).setStatus(HttpServletResponse.SC_OK);

		writer.close();
		f.delete();
	}

}
