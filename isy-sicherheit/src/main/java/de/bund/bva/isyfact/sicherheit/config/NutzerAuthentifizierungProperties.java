package de.bund.bva.isyfact.sicherheit.config;

import java.util.HashMap;
import java.util.Map;

public class NutzerAuthentifizierungProperties {

    private Map<String, Authentifizierung> benutzer = new HashMap<>();

    public Map<String, Authentifizierung> getBenutzer() {
        return benutzer;
    }

    public static class Authentifizierung {
        private String kennung;

        private String passwort;

        private String bhknz;

        public String getKennung() {
            return kennung;
        }

        public void setKennung(String kennung) {
            this.kennung = kennung;
        }

        public String getPasswort() {
            return passwort;
        }

        public void setPasswort(String passwort) {
            this.passwort = passwort;
        }

        public String getBhknz() {
            return bhknz;
        }

        public void setBhknz(String bhknz) {
            this.bhknz = bhknz;
        }
    }
}
