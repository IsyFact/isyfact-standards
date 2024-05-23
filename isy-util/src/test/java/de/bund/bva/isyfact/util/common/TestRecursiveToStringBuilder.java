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
package de.bund.bva.isyfact.util.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.CompositeName;
import javax.naming.InvalidNameException;

import org.junit.Test;

import de.bund.bva.isyfact.util.common.test.MyNode;
import de.bund.bva.isyfact.util.exception.MessageSourceFehlertextProvider;

public class TestRecursiveToStringBuilder {

    @Test
    public void testNullString() {
        String result = RecursiveToStringBuilder.recursiveToString(null);
        assertEquals("null\n", result);
    }

    @Test
    public void testPrimitiveArray() {
        int[] arr = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };

        String result = RecursiveToStringBuilder.recursiveToString(arr);
        assertEquals("[\n1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 ... 4 more\n]\n", result);

        arr = new int[] { 1, 2, 3, 4, 5, 6 };
        result = RecursiveToStringBuilder.recursiveToString(arr);
        assertEquals("[\n1 2 3 4 5 6 \n]\n", result);

        arr = new int[] {};
        result = RecursiveToStringBuilder.recursiveToString(arr);
        assertEquals("[\n]\n", result);
    }

    @Test
    public void testNonPrimitiveArray() throws InvalidNameException {
        Object[] arr = new Object[] { 1, "20", new String[] { "foo", "bar" }, new CompositeName() };

        String result = RecursiveToStringBuilder.recursiveToString(arr);
        assertEquals("[\n1\n20\n[\nfoo\nbar\n]\n\n]\n", result);
    }

    @Test
    public void testMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("key1", "value1");
        map.put("key2", "value2");

        String result = RecursiveToStringBuilder.recursiveToString(map);
        assertEquals("java.util.HashMap [\nkey1\n => value1\nkey2\n => value2\n]\n", result);
    }

    @Test
    public void testIterable() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        String result = RecursiveToStringBuilder.recursiveToString(list);
        assertTrue(result.endsWith("[\n1\n2\n3\n4\n5\n6\n]\n"));
    }

    @Test
    public void testEnum() {
        String result = RecursiveToStringBuilder.recursiveToString(Day.MONDAY);
        assertEquals("MONDAY\n", result);
    }

    @Test
    public void testGenericObject() {
        MessageSourceFehlertextProvider generic = new MessageSourceFehlertextProvider();
        String result = RecursiveToStringBuilder.recursiveToString(generic);
        assertTrue(
            result.startsWith("de.bund.bva.isyfact.util.exception.MessageSourceFehlertextProvider"));
    }

    @Test
    public void testSelfcontainingObject() {
        MyNode node = new MyNode();
        node.child = node;
        String result = RecursiveToStringBuilder.recursiveToString(node);
        assertTrue(result.startsWith("de.bund.bva.isyfact.util.common.test.MyNode"));
    }

    @Test
    public void testSelfcontainingObject2() {
        MyNode node1 = new MyNode();
        MyNode node2 = new MyNode();
        node1.child = node2;
        String str = "foo bar";
        node1.str = str;
        node2.str = str;
        String result = RecursiveToStringBuilder.recursiveToString(node1);
        assertTrue(result.startsWith("de.bund.bva.isyfact.util.common.test.MyNode"));
    }

    public enum Day {
        SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
    }
}