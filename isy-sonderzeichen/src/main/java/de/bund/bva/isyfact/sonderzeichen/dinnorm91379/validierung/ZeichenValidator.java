package de.bund.bva.isyfact.sonderzeichen.dinnorm91379.validierung;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.sonderzeichen.dinnorm91379.CharacterUtil;
import de.bund.bva.isyfact.sonderzeichen.dinnorm91379.konstanten.EreignisSchluessel;

/**
 * Provides methods for validating strings against DIN Norm 91379 data types.
 */
public class ZeichenValidator {

    /** Logger. */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(ZeichenValidator.class);

    /** A map that sorts valid characters by their data type. */
    private final Map<Datentyp, Set<String>> validCharactersByDatentyp = new HashMap<>();

    /**
     * Creates a new validator. Initializes the {@link #validCharactersByDatentyp} map.
     */
    public ZeichenValidator() {
        LOG.info(LogKategorie.JOURNAL, EreignisSchluessel.VALIDIERUNG, "Erstelle ZeichenValidator.");

        try {
            for (Datentyp datentyp : Datentyp.values()) {
                // add all character groups to the map for its data type
                for (Zeichengruppe zeichengruppe : datentyp.getZeichengruppen()) {
                    addCharactersFromZeichengruppeToDatentypMap(datentyp, zeichengruppe);
                }
            }
        } catch (IOException e) {
            LOG.error(EreignisSchluessel.VALIDIERUNG, "Fehler beim Laden der Kategorietabelle => Abbruch", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Check if the given string contains only characters from the specified data type.
     *
     * @param zeichenkette
     *         the string to check
     * @param datentyp
     *         data type as defined by DIN Norm 91379
     * @return {@code true} if the string only contains characters from the datatype, otherwise {@code false}
     */
    public boolean isGueltigerString(String zeichenkette, Datentyp datentyp) {
        Set<String> validCharacters = validCharactersByDatentyp.get(datentyp);
        return CharacterUtil.containsOnlyCharsFromSet(zeichenkette, validCharacters);
    }

    private void addCharactersFromZeichengruppeToDatentypMap(Datentyp datentyp, Zeichengruppe zeichengruppe) throws IOException {
        LOG.debug("Lade Kategorietabelle für Zeichengruppe: {}", zeichengruppe);

        try (InputStream inputStream = getClass().getResourceAsStream(zeichengruppe.getPfad())) {
            // Load file
            Properties properties = new Properties();
            properties.load(inputStream);

            addCharactersFromPropertiesToDatentypMap(datentyp, properties);
        }
    }

    private void addCharactersFromPropertiesToDatentypMap(Datentyp datentyp, Properties properties) {
        Set<String> zeichenketteSet = new HashSet<>();
        for (Object o : properties.keySet()) {
            String gueltigerCharacter = (String) o;

            // Parse the data
            char[] zeichen = CharacterUtil.parseString(gueltigerCharacter);
            if (zeichen != null) {
                String newString = new String(zeichen);
                zeichenketteSet.add(newString);

                LOG.debug("Zeichen: {} zu Datentyp {} zugeordnet.", newString, datentyp);
            }
        }

        // Add all characters to this datatype
        Set<String> gueltigeZeichenSet = validCharactersByDatentyp.computeIfAbsent(datentyp, k -> new HashSet<>());
        gueltigeZeichenSet.addAll(zeichenketteSet);
    }

}
