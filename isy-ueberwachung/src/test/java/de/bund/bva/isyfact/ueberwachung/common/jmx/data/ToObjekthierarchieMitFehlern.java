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
package de.bund.bva.isyfact.ueberwachung.common.jmx.data;

public class ToObjekthierarchieMitFehlern {

    private int testPrimitive;

    private String testString;

    private ToMitFehlerFeld to;

    private ToMitFehlerCollection toColl;

    public int getTestPrimitive() {
        return this.testPrimitive;
    }

    public void setTestPrimitive(int testPrimitive) {
        this.testPrimitive = testPrimitive;
    }

    public String getTestString() {
        return this.testString;
    }

    public void setTestString(String testString) {
        this.testString = testString;
    }

    public ToMitFehlerFeld getTo() {
        return this.to;
    }

    public void setTo(ToMitFehlerFeld to) {
        this.to = to;
    }

    public ToMitFehlerCollection getToColl() {
        return this.toColl;
    }

    public void setToColl(ToMitFehlerCollection toColl) {
        this.toColl = toColl;
    }

}
