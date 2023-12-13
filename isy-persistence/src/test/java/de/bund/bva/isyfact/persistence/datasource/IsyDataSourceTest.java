/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * License). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
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

public class IsyDataSourceTest {

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
	
	@Test
	public void testGetConnection() throws SQLException {
		when(dataSource.getConnection()).thenReturn(connection);
		isyDataSource.setInvalidSchemaVersionAction(null);
		isyDataSource.afterPropertiesSet();
		Connection ref = isyDataSource.getConnection();
		assertEquals(connection, ref);
		
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
		assertNotNull(isyDataSource.getConnection());
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
		assertNotNull(isyDataSource.getConnection());
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

	@Test(expected = SQLException.class)
	public void getConnectionErrorState() throws SQLException {
		when(dataSource.getConnection()).thenThrow(SQLException.class);
		isyDataSource.setNonCriticalDataSource(true);
		isyDataSource.setSchemaVersion("version");
		isyDataSource.afterPropertiesSet();

		isyDataSource.getConnection();
	}

	@Test(expected = SQLException.class)
	public void getConnectionUsernamePasswordErrorState() throws SQLException {
		when(dataSource.getConnection()).thenThrow(SQLException.class);
		isyDataSource.setNonCriticalDataSource(true);
		isyDataSource.setSchemaVersion("version");
		isyDataSource.afterPropertiesSet();

		isyDataSource.getConnection("test", "test");
	}
}
