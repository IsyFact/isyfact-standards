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
package de.bund.bva.pliscommon.persistence.datasource;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

import de.bund.bva.pliscommon.persistence.exception.PersistenzException;

public class TestPlisDataSource {

	private DataSource source;
	private Connection conn;
	private ResultSet rs;
	private PlisDataSource plisSource;
	private PreparedStatement stmt;
	
	@Before
	public void setUp(){
		
		source = mock(DataSource.class);
		conn = mock(Connection.class);
		rs = mock(ResultSet.class);
		stmt = mock(PreparedStatement.class);
		plisSource = new PlisDataSource();
		plisSource.setTargetDataSource(source);
	}
	
	@Test(expected = PersistenzException.class)
	public void testGetConnectionNull() throws SQLException {
		when(source.getConnection()).thenReturn(null);
		plisSource.getConnection();
	}
	
	@Test
	public void testGetConnection() throws SQLException {
		when(source.getConnection()).thenReturn(conn);
		plisSource.setInvalidSchemaVersionAction(null);
		plisSource.afterPropertiesSet();		
		Connection ref = plisSource.getConnection();
		assertEquals(conn, ref);
		
	}
	
	@Test(expected = PersistenzException.class)
	public void testGetConnectionUserPasswordNull() throws SQLException {
		when(source.getConnection("user", "password")).thenReturn(null);
		plisSource.getConnection("user", "password");
	}
	
	@Test
	public void testGetConnectionUserPassword() throws SQLException {
		when(source.getConnection("user", "password")).thenReturn(conn);
		plisSource.setSchemaVersion(" ");
		plisSource.setInvalidSchemaVersionAction("action");
		plisSource.afterPropertiesSet();
		Connection ref = plisSource.getConnection("user", "password");
		assertEquals(conn, ref);
	}
	
	@Test
	public void testAfterPropertiesSet() throws SQLException{
		String query = "select version_nummer from m_schema_version where version_nummer = ? and status = 'gueltig'";
		when(source.getConnection()).thenReturn(conn);
		when(conn.prepareStatement(query)).thenReturn(stmt);
		when(stmt.executeQuery()).thenReturn(rs);
		when(rs.next()).thenReturn(true);
		when(rs.getString(1)).thenReturn("version");
		plisSource.setSchemaVersion("version");
		plisSource.afterPropertiesSet();
	}
	
	@Test
	public void testAfterPropertiesSetResultSetIsNullActionWarn() throws SQLException{
		String query = "select version_nummer from m_schema_version where version_nummer = ? and status = 'gueltig'";
		when(source.getConnection()).thenReturn(conn);
		when(conn.prepareStatement(query)).thenReturn(stmt);
		when(stmt.executeQuery()).thenReturn(null);
		doThrow(SQLException.class).when(conn).close();
		plisSource.setSchemaVersion("version");
		plisSource.setInvalidSchemaVersionAction("warn");
		plisSource.afterPropertiesSet();
	}
	
	@Test(expected = PersistenzException.class)
	public void testAfterPropertiesSetResultSetIsNull() throws SQLException{
		String query = "select version_nummer from m_schema_version where version_nummer = ? and status = 'gueltig'";
		when(source.getConnection()).thenReturn(conn);
		when(conn.prepareStatement(query)).thenReturn(stmt);
		when(stmt.executeQuery()).thenReturn(null);
		plisSource.setSchemaVersion("version");
		plisSource.afterPropertiesSet();
	}
	
	@Test(expected = PersistenzException.class)
	public void testAfterPropertiesSetResultSetNextIsFalse() throws SQLException{
		String query = "select version_nummer from m_schema_version where version_nummer = ? and status = 'gueltig'";
		when(source.getConnection()).thenReturn(conn);
		when(conn.prepareStatement(query)).thenReturn(stmt);
		when(stmt.executeQuery()).thenReturn(rs);
		when(rs.next()).thenReturn(false);
		plisSource.setSchemaVersion("version");
		plisSource.afterPropertiesSet();
	}

	@Test(expected = PersistenzException.class)
	public void testAfterPropertiesSetConnIsNull() throws SQLException{
		when(source.getConnection()).thenThrow(SQLException.class);
		plisSource.setSchemaVersion("version");
		plisSource.afterPropertiesSet();
	}
	
}
