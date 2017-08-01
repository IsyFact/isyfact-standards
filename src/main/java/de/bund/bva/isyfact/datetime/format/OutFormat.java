package de.bund.bva.isyfact.datetime.format;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author Björn Saxe, msg systems ag
 */
public abstract class OutFormat {
    // Montag, 17. Juli 2017 14:35:19 MESZ
    public static final DateTimeFormatter DATUM_ZEIT_LANG_TAG_ZONE =
        DateTimeFormatter.ofPattern("cccc, dd. MMMM yyyy HH:mm:ss z", Locale.GERMAN);

    // Montag, 17. Juli 2017 14:35:19
    public static final DateTimeFormatter DATUM_ZEIT_LANG_TAG =
        DateTimeFormatter.ofPattern("cccc, dd. MMMM yyyy HH:mm:ss z", Locale.GERMAN);

    // 17. Juli 2017 14:35:19 MESZ
    public static final DateTimeFormatter DATUM_ZEIT_LANG_ZONE =
        DateTimeFormatter.ofPattern("dd. MMMM yyyy HH:mm:ss z", Locale.GERMAN);

    // 17.07.2017 14:35:19 +02:00
    public static final DateTimeFormatter DATUM_ZEIT_ZONE =
        DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss xxx", Locale.GERMAN);

    // 17.07.2017 14:35:19
    public static final DateTimeFormatter DATUM_ZEIT =
        DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss", Locale.GERMAN);

    // 17.07.2017
    public static final DateTimeFormatter DATUM = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMAN);

    // 14:35:19
    public static final DateTimeFormatter ZEIT = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.GERMAN);
}
