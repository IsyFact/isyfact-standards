package de.bund.bva.isyfact.persistence.datasource;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

import de.bund.bva.isyfact.persistence.exception.PersistenzException;

public class TestIsyDataSource {

	private DataSource dataSource;
	private Connection connection;
	private ResultSet resultSet;
	private IsyDataSource isyDataSource;
	private PreparedStatement statement;

	private static final String query = "select version_nummer from m_schema_version where version_nummer = ? and status = 'gueltig'";
	
	@Before
	public void setUp(){
		
		dataSource = mock(DataSource.class);
		connection = mock(Connection.class);
		resultSet = mock(ResultSet.class);
		statement = mock(PreparedStatement.class);
		isyDataSource = new IsyDataSource();
		isyDataSource.setTargetDataSource(dataSource);
	}
	
	@Test(expected = PersistenzException.class)
	public void testGetConnectionNull() throws SQLException {
		when(dataSource.getConnection()).thenReturn(null);
		isyDataSource.getConnection();
	}
	
	@Test
	public void testGetConnection() throws SQLException {
		when(dataSource.getConnection()).thenReturn(connection);
		isyDataSource.setInvalidSchemaVersionAction(null);
		isyDataSource.afterPropertiesSet();
		Connection ref = isyDataSource.getConnection();
		assertEquals(connection, ref);
		
	}
	
	@Test(expected = PersistenzException.class)
	public void testGetConnectionUserPasswordNull() throws SQLException {
		when(dataSource.getConnection("user", "password")).thenReturn(null);
		isyDataSource.getConnection("user", "password");
	}
	
	@Test
	public void testGetConnectionUserPassword() throws SQLException {
		when(dataSource.getConnection("user", "password")).thenReturn(connection);
		isyDataSource.setSchemaVersion(" ");
		isyDataSource.setInvalidSchemaVersionAction("action");
		isyDataSource.afterPropertiesSet();
		Connection ref = isyDataSource.getConnection("user", "password");
		assertEquals(connection, ref);
	}
	
	@Test
	public void testAfterPropertiesSet() throws SQLException {
		when(dataSource.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(query)).thenReturn(statement);
		when(statement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true);
		when(resultSet.getString(1)).thenReturn("version");
		isyDataSource.setSchemaVersion("version");
		isyDataSource.afterPropertiesSet();
	}
	
	@Test
	public void testAfterPropertiesSetResultSetIsNullActionWarn() throws SQLException {
		when(dataSource.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(query)).thenReturn(statement);
		when(statement.executeQuery()).thenReturn(null);
		doThrow(SQLException.class).when(connection).close();
		isyDataSource.setSchemaVersion("version");
		isyDataSource.setInvalidSchemaVersionAction("warn");
		isyDataSource.afterPropertiesSet();
	}
	
	@Test(expected = PersistenzException.class)
	public void testAfterPropertiesSetResultSetIsNull() throws SQLException {

		when(dataSource.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(query)).thenReturn(statement);
		when(statement.executeQuery()).thenReturn(null);
		isyDataSource.setSchemaVersion("version");
		isyDataSource.afterPropertiesSet();
	}
	
	@Test(expected = PersistenzException.class)
	public void testAfterPropertiesSetResultSetNextIsFalse() throws SQLException{
		when(dataSource.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(query)).thenReturn(statement);
		when(statement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(false);
		isyDataSource.setSchemaVersion("version");
		isyDataSource.afterPropertiesSet();
	}

	@Test(expected = PersistenzException.class)
	public void testAfterPropertiesSetConnIsNull() throws SQLException{
		when(dataSource.getConnection()).thenThrow(SQLException.class);
		isyDataSource.setSchemaVersion("version");
		isyDataSource.afterPropertiesSet();
	}
	
}
