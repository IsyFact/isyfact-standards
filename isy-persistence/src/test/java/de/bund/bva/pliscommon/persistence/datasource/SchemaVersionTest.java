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

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Test für die Überprüfung der DB-Schemaversion.
 *
 * Testvorbereitung: Es werden 2 Datenbankschemas benötigt, die in jpa.properties angegeben werden. Das erste
 * Datenbankschema muss die Tabelle m_schema_version enthalten. Das zweite Datenbankschema darf diese Tabelle
 * nicht enthalten.
 *
 * Die DDL für diese Tabelle steht unter src/test/skripte/sql/ddl-schema-version.sql
 *
 */
@Ignore("DB notwendig")
public class SchemaVersionTest {

    /**
     * Test 1: Testet, dass der Application-Context richtig hochfährt, wenn keine Versionsnummer angegeben
     * ist.
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
     * Test 2: Testet, dass der Application-Context richtig hochfährt, wenn die korrekte Versionsnummer
     * angegeben ist.
     * @throws SQLException
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
     * Test 3: Testet, dass der Application-Context richtig hochfährt, wenn die falsche Versionsnummer
     * angegeben ist und eine Warnung geloggt wird.
     * @throws SQLException
     */
    @Test
    public void testFalscheSchemaVersionWarn() throws SQLException {
        setzeSchemaVersion("3.2.1", "gueltig");
        ClassPathXmlApplicationContext applicationContext =
            new ClassPathXmlApplicationContext("resources/spring/application-test-3.xml");
        assertNotNull("appDataSource ist nicht im Application-Kontext",
            applicationContext.getBean("appDataSource", PlisDataSource.class));
        // Ausgabe des Logs manuell prüfen.
        // Erwartete Ausgabe:
        // WARN Die Version des Datenbankschemas entspricht nicht der erwarteten Version (1.2.3).
        applicationContext.close();
    }

    /**
     * Test 4: Testet, dass der Application-Context nicht hochfährt, wenn die falsche Versionsnummer angegeben
     * ist.
     * @throws SQLException
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
     * test 5: Testet, dass der Application-Context nicht hochfährt, wenn ein SQL-Fehler auftreten.
     * @throws SQLException
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
     * Test 6: Testet, dass der Application-Context richtig hochfährt, wenn die korrekte Versionsnummer
     * angegeben ist und ein Schemaname spezifiziert wurde.
     * @throws SQLException
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
     * Test 7: Testet, dass der Application-Context nicht hochfährt, wenn ein SQL-Fehler (falsches Schema)
     * auftreten.
     * @throws SQLException
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
     * Legt eine Schema-Version an.
     *
     * @param version
     *            Die Versionsnummer.
     * @param status
     *            Der Status der Version ("gueltig" oder "ungueltig")
     * @throws SQLException
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
