package de.bund.bva.isyfact.datetime.format;

import java.time.format.DateTimeFormatter;

/**
 * @author Björn Saxe, msg systems ag
 */
public abstract class InFormat {
    public static final DateTimeFormatter DATUM = DateTimeFormatter.ofPattern("d.MM.yyyy");

    public static final DateTimeFormatter DATUM_0 = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static final DateTimeFormatter ZEIT = DateTimeFormatter.ofPattern("HH:mm");

    public static final DateTimeFormatter ZEIT_SEKUNDE = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static final DateTimeFormatter ZEIT_SEKUNDE_BRUCHTEIL = DateTimeFormatter.ofPattern("HH:mm:ss.SS");

    public static final DateTimeFormatter ZEIT_ZONE = DateTimeFormatter.ofPattern("HH:mm z");

    public static final DateTimeFormatter ZEIT_SEKUNDE_ZONE = DateTimeFormatter.ofPattern("HH:mm:ss z");

    public static final DateTimeFormatter ZEIT_SEKUNDE_BRUCHTEIL_ZONE =
        DateTimeFormatter.ofPattern("HH:mm:ss.SS z");

    public static final DateTimeFormatter ZEIT_OFFSET = DateTimeFormatter.ofPattern("HH:mm xxx");

    public static final DateTimeFormatter ZEIT_SEKUNDE_OFFSET = DateTimeFormatter.ofPattern("HH:mm:ss xxx");

    public static final DateTimeFormatter ZEIT_SEKUNDE_BRUCHTEIL_OFFSET =
        DateTimeFormatter.ofPattern("HH:mm:ss.SS xxx");
}
