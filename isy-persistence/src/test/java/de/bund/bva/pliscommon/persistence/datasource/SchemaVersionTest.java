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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.bund.bva.pliscommon.persistence.KomponentenIntegrationsTest;

/**
 * Test to verify the database schema version
 *
 * Test preparation: Two database schemas need to be specified in the jpa.properties. The first database
 * schema must contain the table m_schema_version, the second database schema must not contain this table.
 *
 * The DDL for this table is located here: src/test/skripte/sql/ddl-schema-version.sql
 */
@Category(KomponentenIntegrationsTest.class)
public class SchemaVersionTest {

    /**
     * Test 1: Tests if the application context starts correctly when no version number is defined.
     */
    @Test
    public void testKeineAngabeSchemaVersion() {
        ClassPathXmlApplicationContext applicationContext =
            new ClassPathXmlApplicationContext("resources/spring/application-test-1.xml");
        assertNotNull("appDataSource ist nicht im Application-Kontext",
            applicationContext.getBean("appDataSource", PlisDataSource.class));
        applicationContext.close();

        applicationContext = new ClassPathXmlApplicationContext("resources/spring/application-test-1a.xml");
        assertNotNull("appDataSource ist nicht im Application-Kontext",
            applicationContext.getBean("appDataSource", PlisDataSource.class));
        applicationContext.close();
    }

    /**
     * Test 2: Tests if the application context starts correctly when the version number is specified
     * correctly.
     * @throws SQLException Exception when setting the schema version fails
     */
    @Test
    public void testKorrekteSchemaVersion() throws SQLException {
        setzeSchemaVersion("1.2.3", "gueltig");
        ClassPathXmlApplicationContext applicationContext =
            new ClassPathXmlApplicationContext("resources/spring/application-test-2.xml");
        assertNotNull("appDataSource ist nicht im Application-Kontext",
            applicationContext.getBean("appDataSource", PlisDataSource.class));
        applicationContext.close();
    }

    /**
     * Test 3: Tests if the application context starts correctly when a wrong version number is specified
     * and that a warning is logged in that case.
     * @throws SQLException Exception when setting the schema version fails
     */
    @Test
    public void testFalscheSchemaVersionWarn() throws SQLException {
        setzeSchemaVersion("3.2.1", "gueltig");
        ClassPathXmlApplicationContext applicationContext =
            new ClassPathXmlApplicationContext("resources/spring/application-test-3.xml");
        assertNotNull("appDataSource ist nicht im Application-Kontext",
            applicationContext.getBean("appDataSource", PlisDataSource.class));
        // Output of the log must be checked manually.
        // Expected output:
        // WARN Die Version des Datenbankschemas entspricht nicht der erwarteten Version (1.2.3).
        applicationContext.close();
    }

    /**
     * Test 4: Tests if the application context fails to start when a wrong version number is specified.
     * @throws SQLException Exception when setting the schema version fails
     */
    @Test
    public void testFalscheSchemaVersionFail() throws SQLException {
        setzeSchemaVersion("1.2.3", "ungueltig");
        try {
            ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("resources/spring/application-test-4.xml");
            applicationContext.close();
        } catch (BeanCreationException e) {
            assertTrue("Unerwarteter Fehler", e.getCause().getMessage().contains("PERSI00005"));
            return;
        }
        fail("Es wurde keine Exception geworfen.");
    }

    /**
     * Test 5: Tests if the application context fails to start when an SQL error occurs.
     * @throws SQLException Exception when setting the schema version fails
     */
    @Test
    public void testSchemaVersionSQLError() throws SQLException {
        setzeSchemaVersion("1.2.3", "gueltig");
        try {
            ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("resources/spring/application-test-5.xml");
            applicationContext.close();
        } catch (BeanCreationException e) {
            assertTrue("Unerwarteter Fehler", e.getCause().getMessage().contains("PERSI00006"));
            return;
        }
        fail("Es wurde keine Exception geworfen.");
    }

    /**
     * Test 6: Tests if the application context starts correctly when the version number is specified
     * correctly and and a schema name has been specified.
     * @throws SQLException Exception when setting the schema version fails
     */
    @Test
    public void testKorrekteSchemaVersionUndSchema() throws SQLException {
        setzeSchemaVersion("1.2.3", "gueltig");
        ClassPathXmlApplicationContext applicationContext =
            new ClassPathXmlApplicationContext("resources/spring/application-test-6.xml");
        assertNotNull("appDataSource ist nicht im Application-Kontext",
            applicationContext.getBean("appDataSource", PlisDataSource.class));
        applicationContext.close();
    }

    /**
     * Test 7: Tests if the application context fails to start when an SQL error (invalid schema) occurs.
     * @throws SQLException Exception when setting the schema version fails
     */
    @Test
    public void testSchemaVersionSQLErrorFalschesSchema() throws SQLException {
        setzeSchemaVersion("1.2.3", "gueltig");
        try {
            ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("resources/spring/application-test-7.xml");
            applicationContext.close();
        } catch (BeanCreationException e) {
            assertTrue("Unerwarteter Fehler", e.getCause().getMessage().contains("PERSI00006"));
            return;
        }
        fail("Es wurde keine Exception geworfen.");
    }

    /**
     * Creates a schema version.
     *
     * @param version
     *            The version number.
     * @param status
     *            Status of the version ("gueltig" or "ungueltig": 'valid' or 'invalid')
     * @throws SQLException Exception when setting the schema version fails
     */
    protected void setzeSchemaVersion(String version, String status) throws SQLException {
        ClassPathXmlApplicationContext applicationContext =
            new ClassPathXmlApplicationContext("resources/spring/application-test-1.xml");
        PlisDataSource dataSource = applicationContext.getBean("appDataSource", PlisDataSource.class);
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("delete from m_schema_version");
        stmt.executeUpdate("insert into m_schema_version (version_nummer, status) values ('" + version
            + "', '" + status + "')");
        stmt.close();
        conn.commit();
        conn.close();

        applicationContext.close();
    }

}
