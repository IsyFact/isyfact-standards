package de.bund.bva.isyfact.datetime.core;

import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import de.bund.bva.isyfact.datetime.format.InFormat;
import de.bund.bva.isyfact.datetime.format.OutFormat;
import de.bund.bva.isyfact.datetime.persistence.ZeitraumEntitaet;

/**
 * Ein Zeitraum bestehend aus zwei Datums- oder Zeitangaben, die den Start und das Ende eines Zeitraums
 * markieren. Die Dauer des Zeitraums ist die Differenz aus Ende und Start.
 *
 */
public class Zeitraum implements Serializable {

    private static final long serialVersionUID = -1694209697511614665L;

    private ZonedDateTime anfang;

    private ZonedDateTime ende;

    private boolean ohneDatum = false;

    private Zeitraum(ZonedDateTime anfang, ZonedDateTime ende) {
        this.anfang = anfang;
        this.ende = ende;
    }

    private static ZonedDateTime getLocalDateTimeInJvmTimeZone(LocalDateTime localDateTime) {
        return ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
    }

    /**
     * Erstellt einen {@link Zeitraum} unter Angabe von Start und Ende des Zeitraums.
     *
     * @param anfang
     *     der Anfang des Zeitraums inklusive, muss vor dem Ende liegen, nicht null
     * @param ende
     *     das Ende des Zeitraums exklusive, muss nach Anfang liegen, nicht null
     * @return ein {@link Zeitraum} mit Start und Ende gesetzt
     */
    public static Zeitraum of(ZonedDateTime anfang, ZonedDateTime ende) {
        Objects.requireNonNull(anfang);
        Objects.requireNonNull(ende);

        if (ende.isBefore(anfang)) {
            throw new DateTimeException("Der Anfang " + anfang + " liegt nach dem Ende " + ende + " des Zeitraums.");
        }

        return new Zeitraum(anfang, ende);
    }

    /**
     * Erstellt einen {@link Zeitraum} unter Angabe des Anfangs und der Dauer als {@link Duration} des
     * Zeitraums. Zur Bestimmung des Endes wird die Dauer zum Anfang hinzuaddiert.
     *
     * @param anfang
     *     der Anfang des Zeitraums inklusive, nicht null
     * @param dauer
     *     die Dauer des Zeitraums, nicht negativ, nicht null
     * @return ein {@link Zeitraum} mit Anfang und Dauer
     * @throws DateTimeException
     *     wenn die Dauer negativ ist
     */
    public static Zeitraum of(ZonedDateTime anfang, Duration dauer) {
        Objects.requireNonNull(anfang);
        Objects.requireNonNull(dauer);

        if (dauer.isNegative()) {
            throw new DateTimeException("Die Dauer " + dauer + " darf nicht negativ sein.");
        }

        return new Zeitraum(anfang, anfang.plus(dauer));
    }

    /**
     * Erstellt einen {@link Zeitraum} unter Angabe des Anfangs und der Dauer als {@link Period} des
     * Zeitraums. Zur Bestimmung des Endes wird die Dauer zum Anfang hinzuaddiert.
     *
     * @param anfang
     *     der Anfang des Zeitraums inklusive, nicht null
     * @param dauer
     *     die Dauer des Zeitraums, nicht negativ, nicht null
     * @return ein {@link Zeitraum} mit Anfang und Dauer
     * @throws DateTimeException
     *     wenn die Dauer negativ ist
     */
    public static Zeitraum of(ZonedDateTime anfang, Period dauer) {
        Objects.requireNonNull(anfang);
        Objects.requireNonNull(dauer);

        if (dauer.isNegative()) {
            throw new DateTimeException("Die Dauer " + dauer + " darf nicht negativ sein.");
        }

        return new Zeitraum(anfang, anfang.plus(dauer));
    }

    /**
     * Erstellt einen {@link Zeitraum} unter Angabe von Start und Ende des Zeitraums. Als Zeitzone für
     * Anfang und Ende wird Default-Zeitzone verwendet.
     *
     * @param anfang
     *     der Anfang des Zeitraums inklusive, nicht null
     * @param ende
     *     das Ende des Zeitraums exklusive, nicht null
     * @return ein {@link Zeitraum} mit Start und Ende gesetzt
     */
    public static Zeitraum of(LocalDateTime anfang, LocalDateTime ende) {
        Objects.requireNonNull(anfang);
        Objects.requireNonNull(ende);

        if (ende.isBefore(anfang)) {
            throw new DateTimeException("Der Anfang " + anfang + " liegt nach dem Ende " + ende + " des Zeitraums.");
        }

        return new Zeitraum(getLocalDateTimeInJvmTimeZone(anfang), getLocalDateTimeInJvmTimeZone(ende));
    }

    /**
     * Erstellt einen {@link} unter Angabe des Anfangs und der Dauer als {@link Duration} des
     * Zeitraums. Zur Bestimmung des Endes wird die Dauer zum Anfang hinzuaddiert. Als Zeitzone für
     * Anfang und Ende wird Default-Zeitzone verwendet.
     *
     * @param anfang
     *     der Anfang des Zeitraums inklusive, nicht null
     * @param dauer
     *     die Dauer des Zeitraums, nicht negativ, nicht null
     * @return ein {@link Zeitraum} mit Anfang und Dauer
     * @throws DateTimeException
     *     wenn die Dauer negativ ist
     */
    public static Zeitraum of(LocalDateTime anfang, Duration dauer) {
        Objects.requireNonNull(anfang);
        Objects.requireNonNull(dauer);

        if (dauer.isNegative()) {
            throw new DateTimeException("Die Dauer " + dauer + " darf nicht negativ sein.");
        }

        ZonedDateTime zonedAnfang = getLocalDateTimeInJvmTimeZone(anfang);
        return new Zeitraum(zonedAnfang, zonedAnfang.plus(dauer));
    }

    /**
     * Erstellt einen {@link Zeitraum} unter Angabe des Anfangs und der Dauer als {@link Period} des
     * Zeitraums. Zur Bestimmung des Endes wird die Dauer zum Anfang hinzuaddiert. Als Zeitzone für
     * Anfang und Ende wird Default-Zeitzone verwendet.
     *
     * @param anfang
     *     der Anfang des Zeitraums inklusive, nicht null
     * @param dauer
     *     die Dauer des Zeitraums, nicht negativ, nicht null
     * @return ein {@link Zeitraum} mit Anfang und Dauer
     * @throws DateTimeException
     *     wenn die Dauer negativ ist
     */
    public static Zeitraum of(LocalDateTime anfang, Period dauer) {
        Objects.requireNonNull(anfang);
        Objects.requireNonNull(dauer);

        if (dauer.isNegative()) {
            throw new DateTimeException("Die Dauer " + dauer + " darf nicht negativ sein.");
        }

        ZonedDateTime zonedAnfang = getLocalDateTimeInJvmTimeZone(anfang);
        return new Zeitraum(zonedAnfang, zonedAnfang.plus(dauer));
    }

    /**
     * Erstellt einen {@link Zeitraum} unter Angabe von Start und Ende des Zeitraums.
     *
     * @param anfang
     *     der Anfang des Zeitraums inklusive, nicht null
     * @param ende
     *     das Ende des Zeitraums exklusive, nicht null
     * @return ein {@link Zeitraum} mit Start und Ende gesetzt
     */
    public static Zeitraum of(LocalDate anfang, LocalDate ende) {
        Objects.requireNonNull(anfang);
        Objects.requireNonNull(ende);

        if (ende.isBefore(anfang)) {
            throw new DateTimeException("Der Anfang " + anfang + " liegt nach dem Ende " + ende + " des Zeitraums.");
        }

        ZonedDateTime zonedAnfang =
            getLocalDateTimeInJvmTimeZone(LocalDateTime.of(anfang, LocalTime.MIDNIGHT));
        ZonedDateTime zonedEnde = getLocalDateTimeInJvmTimeZone(LocalDateTime.of(ende, LocalTime.MIDNIGHT));
        return new Zeitraum(zonedAnfang, zonedEnde);
    }

    /**
     * Erstellt einen {@link Zeitraum} unter Angabe des Anfangs und der Dauer als {@link Period} des
     * Zeitraums. Zur Bestimmung des Endes wird die Dauer zum Anfang hinzuaddiert.
     *
     * @param anfang
     *     der Anfang des Zeitraums inklusive, nicht null
     * @param dauer
     *     die Dauer des Zeitraums, nicht negativ, nicht null
     * @return ein {@link Zeitraum} mit Anfang und Dauer
     * @throws DateTimeException
     *     wenn die Dauer negativ ist
     */
    public static Zeitraum of(LocalDate anfang, Period dauer) {
        Objects.requireNonNull(anfang);
        Objects.requireNonNull(dauer);

        if (dauer.isNegative()) {
            throw new DateTimeException("Die Dauer " + dauer + " darf nicht negativ sein.");
        }

        ZonedDateTime zonedAnfang =
            getLocalDateTimeInJvmTimeZone(LocalDateTime.of(anfang, LocalTime.MIDNIGHT));
        return new Zeitraum(zonedAnfang, zonedAnfang.plus(dauer));
    }

    /**
     * Erstellt einen {@link Zeitraum} unter Angabe des Anfangs und des Endes als {@link LocalTime}
     * des Zeitraums. Ein solcher Zeitraum enthält eine reine Zeitdauer und ist unabhängig von einem Datum.
     * Liegt die Anfangszeit nach der Endzeit, geht der Zeitraum über den Tageswechsel.
     *
     * @param anfang
     *     der Anfang des Zeitraums, nicht null
     * @param ende
     *     das Ende des Zeitraums, nicht null
     * @return ein {@link Zeitraum} mit Start und Ende gesetzt
     */
    public static Zeitraum of(LocalTime anfang, LocalTime ende) {
        Objects.requireNonNull(anfang);
        Objects.requireNonNull(ende);

        Zeitraum zeitraum;

        if (anfang.isBefore(ende) || anfang.equals(ende)) {
            ZonedDateTime anfangDate =
                ZonedDateTime.of(LocalDate.now(Clock.systemUTC()), anfang, ZoneOffset.UTC);
            ZonedDateTime endeDate = ZonedDateTime.of(LocalDate.now(Clock.systemUTC()), ende, ZoneOffset.UTC);

            zeitraum = new Zeitraum(anfangDate, endeDate);

            zeitraum.ohneDatum = true;
        } else {
            ZonedDateTime anfangDate =
                ZonedDateTime.of(LocalDate.now(Clock.systemUTC()), anfang, ZoneOffset.UTC);
            ZonedDateTime endeDate =
                ZonedDateTime.of(LocalDate.now(Clock.systemUTC()).plusDays(1), ende, ZoneOffset.UTC);

            zeitraum = new Zeitraum(anfangDate, endeDate);

            zeitraum.ohneDatum = true;
        }

        return zeitraum;
    }

    /**
     * Erstellt einen {@link Zeitraum} unter Angabe des Anfangs und der Dauer als {@link Duration}
     * des Zeitraums. Zur Bestimmung des Endes wird die Dauer zum Anfang hinzuaddiert. Ein solcher Zeitraum
     * enthält eine reine Zeitdauer und ist unabhängig von einem Datum.
     *
     * @param anfang
     *     der Anfang des Zeitraums inklusive, nicht null
     * @param dauer
     *     die Dauer des Zeitraums, nicht negativ, nicht null
     * @return ein {@link Zeitraum} mit Anfang und Dauer
     * @throws DateTimeException
     *     wenn die Dauer negativ ist
     */
    public static Zeitraum of(LocalTime anfang, Duration dauer) {
        Objects.requireNonNull(anfang);
        Objects.requireNonNull(dauer);

        if (dauer.isNegative()) {
            throw new DateTimeException("Die Dauer " + dauer + " darf nicht negativ sein.");
        } else if (dauer.compareTo(Duration.ofDays(1)) > 0) {
            throw new DateTimeException("Die Dauer " + dauer + " darf nicht länger als ein Tag sein.");
        }

        ZonedDateTime anfangDate = ZonedDateTime.of(LocalDate.now(Clock.systemUTC()), anfang, ZoneOffset.UTC);
        ZonedDateTime endeDate = anfangDate.plus(dauer);

        Zeitraum zeitraum = new Zeitraum(anfangDate, endeDate);

        zeitraum.ohneDatum = true;

        return zeitraum;
    }

    /**
     * Erstellt einen {@link Zeitraum} aus der dazugehörigen Persistenzklasse {@link ZeitraumEntitaet}.
     *
     * @param zeitraumEntitaet die {@link ZeitraumEntitaet}
     * @return ein {@link Zeitraum} mit den der {@link ZeitraumEntitaet}
     */
    public static Zeitraum of(ZeitraumEntitaet zeitraumEntitaet) {
        if (zeitraumEntitaet.isOhneDatum()) {
            return Zeitraum.of(zeitraumEntitaet.getAnfang().toLocalTime(), zeitraumEntitaet.getEnde().toLocalTime());
        } else {
            return Zeitraum.of(zeitraumEntitaet.getAnfang(), zeitraumEntitaet.getEnde());
        }
    }

    /**
     * Parst einen String und erstellt daraus einen {@link Zeitraum}.
     * <p>
     * Folgende Eingaben im Format {@code "<1. Wert>, <2. Wert>"} sind möglich:
     * <p>
     *
     * <table summary="Unterstütze Formate" border="1">
     * <tr><th>1. Wert</th><th>2. Wert</th><th>Beispiel</th></tr>
     * <tr><td>Datumswer</td><td>Datumswert</td><td>12.7.2017 14:00, 13.09.2018 19:00</td></tr>
     * <tr><td>Datumswert</td><td>Dauer</td><td>12.8.2013, 14 d</td></tr>
     * <tr><td>Zeitwert</td><td>Zeitwert</td><td>13:40:00, 15:45:12</td></tr>
     * <tr><td>Zeitwert</td><td>Dauer</td><td>12:00, 90min 15s</td></tr>
     * </table>
     *
     *
     * @param text
     *     der Text, der geparst werden soll, nicht null
     * @return der {@link Zeitraum}
     */
    public static Zeitraum parse(String text) {
        Objects.requireNonNull(text);

        String[] anfangUndEndeOderDauer = getZeitraumAnfangUndEndeOderDauerString(text);

        Object anfang = parseAnfang(anfangUndEndeOderDauer[0]);

        Object endeOderDauer = parseEnde(anfangUndEndeOderDauer[1]);

        if (anfang instanceof ZonedDateTime) {
            ZonedDateTime anfangZonedDateTime = (ZonedDateTime) anfang;

            if (endeOderDauer instanceof ZonedDateTime) {
                return Zeitraum.of(anfangZonedDateTime, (ZonedDateTime) endeOderDauer);
            } else if (endeOderDauer instanceof Duration) {
                return Zeitraum.of(anfangZonedDateTime, (Duration) endeOderDauer);
            } else if (endeOderDauer instanceof Period) {
                return Zeitraum.of(anfangZonedDateTime, (Period) endeOderDauer);
            }
        } else if (anfang instanceof LocalDateTime) {
            LocalDateTime anfangLocalDateTime = (LocalDateTime) anfang;

            if (endeOderDauer instanceof LocalDateTime) {
                return Zeitraum.of(anfangLocalDateTime, (LocalDateTime) endeOderDauer);
            } else if (endeOderDauer instanceof Duration) {
                return Zeitraum.of(anfangLocalDateTime, (Duration) endeOderDauer);
            } else if (endeOderDauer instanceof Period) {
                return Zeitraum.of(anfangLocalDateTime, (Period) endeOderDauer);
            }
        } else if (anfang instanceof LocalDate) {
            LocalDate anfangLocalDate = (LocalDate) anfang;

            if (endeOderDauer instanceof LocalDate) {
                return Zeitraum.of(anfangLocalDate, (LocalDate) endeOderDauer);
            } else if (endeOderDauer instanceof Period) {
                return Zeitraum.of(anfangLocalDate, (Period) endeOderDauer);
            }
        } else if (anfang instanceof LocalTime) {
            LocalTime anfangLocalTime = (LocalTime) anfang;

            if (endeOderDauer instanceof LocalTime) {
                return Zeitraum.of(anfangLocalTime, (LocalTime) endeOderDauer);
            } else if (endeOderDauer instanceof Duration) {
                return Zeitraum.of(anfangLocalTime, (Duration) endeOderDauer);
            }
        }

        throw new DateTimeParseException("Beim Parsen des Strings ist ein Fehler aufgetreten.", text, 0);
    }

    private static Object parseAnfang(String textAnfang) {
        List<Object> anfang = new ArrayList<>();

        anfang.add(tryParse(InFormat::parseToZonedDateTime, textAnfang));
        anfang.add(tryParse(InFormat::parseToOffsetDateTime, textAnfang));
        anfang.add(tryParse(InFormat::parseToLocalDateTime, textAnfang));
        anfang.add(tryParse(InFormat::parseToLocalDate, textAnfang));
        anfang.add(tryParse(InFormat::parseToOffsetTime, textAnfang));
        anfang.add(tryParse(InFormat::parseToLocalTime, textAnfang));

        return anfang.stream().filter(Objects::nonNull).findFirst()
            .orElseThrow(() -> new DateTimeParseException("Der Anfang des Zeitraums konnte nicht ermittelt werden.", textAnfang, 0));
    }

    private static Object parseEnde(String textEndeOderDauer) {
        List<Object> endeOderDauer = new ArrayList<>();

        endeOderDauer.add(tryParse(InFormat::parseToZonedDateTime, textEndeOderDauer));
        endeOderDauer.add(tryParse(InFormat::parseToOffsetDateTime, textEndeOderDauer));
        endeOderDauer.add(tryParse(InFormat::parseToLocalDateTime, textEndeOderDauer));
        endeOderDauer.add(tryParse(InFormat::parseToLocalDate, textEndeOderDauer));
        endeOderDauer.add(tryParse(InFormat::parseToOffsetTime, textEndeOderDauer));
        endeOderDauer.add(tryParse(InFormat::parseToLocalTime, textEndeOderDauer));
        endeOderDauer.add(tryParse(InFormat::parseToDuration, textEndeOderDauer));
        endeOderDauer.add(tryParse(InFormat::parseToPeriod, textEndeOderDauer));

        return endeOderDauer.stream().filter(Objects::nonNull).findFirst()
            .orElseThrow(() -> new DateTimeParseException("Das Ende oder die Dauer des Zeitraums konnte nicht ermittelt werden.", textEndeOderDauer, 0));
    }

    private static <T> T tryParse(Function<String, T> parseFunction, String text) {
        try {
            return parseFunction.apply(text);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static String[] getZeitraumAnfangUndEndeOderDauerString(String text) {
        if (text.isEmpty()) {
            throw new DateTimeParseException("Der String war leer.", text, 0);
        }

        String teile[] = new String[2];

        try {
            teile[0] = text.split(", ")[0].trim();
            teile[1] = text.split(", ")[1].trim();
        } catch (Exception e) {
            throw new DateTimeParseException("Der String entspricht nicht dem Format \"<Anfang>, <Ende> | <Dauer>\"", text, 0);
        }

        return teile;
    }

    /**
     * Gibt die Dauer des Zeitraums in der angegebenen Zeiteinheit ({@link TemporalUnit}) zurück.
     *
     * @param unit
     *     die Zeiteinheit für die Dauer
     * @return die Dauer des Zeitraums in der angegeben Zeiteinheit
     */
    public long dauer(TemporalUnit unit) {
        Objects.requireNonNull(unit);

        return anfang.until(ende, unit);
    }

    /**
     * Prüft, ob ein Datums-/Zeitwert innerhalb dieses {@link Zeitraum} liegt.
     *
     * @param dateTime
     *     der Datums-/Zeitwert
     * @return true wenn der Wert innerhalb des Zeitraums liegt.
     */
    public boolean isInZeitraum(ZonedDateTime dateTime) {
        if (ohneDatum) {
            return false;
        } else {
            return (dateTime.isAfter(anfang) || dateTime.isEqual(anfang)) && dateTime.isBefore(ende);
        }
    }

    /**
     * Prüft, ob ein Datums-/Zeitwert innerhalb dieses {@link Zeitraum} liegt. Als Zeitzone wird die
     * Default-Zeitzone verwendet.
     *
     * @param dateTime
     *     der Datums-/Zeitwert
     * @return true wenn der Wert innerhalb des Zeitraums liegt.
     */
    public boolean isInZeitraum(LocalDateTime dateTime) {
        if (ohneDatum) {
            return false;
        } else {
            ZonedDateTime zonedDateTime = getLocalDateTimeInJvmTimeZone(dateTime);
            return isInZeitraum(zonedDateTime);
        }
    }

    /**
     * Prüft, ob ein Datumswert innerhalb dieses {@link Zeitraum} liegt.
     * Als Zeit für das Datum wird 00:00 in der Default-Zeitzone angenommen.
     *
     * @param date
     *     der Datumswert
     * @return true wenn der Wert innerhalb des Zeitraums liegt.
     */
    public boolean isInZeitraum(LocalDate date) {
        if (ohneDatum) {
            return false;
        } else {
            ZonedDateTime zonedDateTime = ZonedDateTime.of(date, LocalTime.of(0, 0), ZoneId.systemDefault());
            return isInZeitraum(zonedDateTime);
        }
    }

    /**
     * Prüft, ob ein Zeitwert innerhalb dieses {@link Zeitraum} liegt.
     *
     * @param localTime
     *     der Datumswert
     * @return true wenn der Wert innerhalb des Zeitraums liegt.
     */
    public boolean isInZeitraum(LocalTime localTime) {
        if (ohneDatum) {
            ZonedDateTime localTimeMitDatum =
                ZonedDateTime.of(anfang.toLocalDate(), localTime, ZoneId.of("UTC"));
            return (localTimeMitDatum.isAfter(anfang) || localTimeMitDatum.isEqual(anfang))
                && localTimeMitDatum.isBefore(ende);
        } else {
            return isInZeitraum(ZonedDateTime.of(anfang.toLocalDate(), localTime, ZoneId.systemDefault()));
        }
    }

    /**
     * Prüft, ob sich zwei Zeiträume überschneiden. Ist einer der beiden Zeiträume ohne Datum, ist das
     * Ergebnis immer false.
     *
     * @param zeitraum
     *     der andere Zeitraum
     * @return true wenn sich die Zeiträume überschneiden, false wenn nicht ohne einer der beiden
     * Zeiträume ohne Datum ist
     */
    public boolean ueberschneidetSichMit(Zeitraum zeitraum) {
        if (zeitraum != null && ohneDatum == zeitraum.ohneDatum) {
            return teilweiseUeberschneidung(this, zeitraum) || teilweiseUeberschneidung(zeitraum, this)
                || kompletteUeberschneidung(this, zeitraum) || kompletteUeberschneidung(zeitraum, this)
                || this.equals(zeitraum);
        } else {
            return false;
        }
    }

    private boolean teilweiseUeberschneidung(Zeitraum z1, Zeitraum z2) {
        return (z2.anfang.isBefore(z1.anfang) || z2.anfang.isEqual(z1.anfang)) && (z1.anfang.isBefore(z2.ende)
            || z1.ende.isEqual(z2.ende));
    }

    private boolean kompletteUeberschneidung(Zeitraum z1, Zeitraum z2) {
        return z1.anfang.isBefore(z2.anfang) && z1.ende.isAfter(z2.ende);
    }

    /**
     * Gibt true zurück, wenn dieser Zeitraum nur eine Zeit enthält und unabhängig von einem Datum ist.
     *
     * @return true, wenn der Zeitraum ohne Datum ist
     */
    public boolean isOhneDatum() {
        return ohneDatum;
    }

    /**
     * Gibt den Anfang dieses Zeitraums zurück. Handelt es sich um einen Zeitraum ohne Datum, wird
     * {@code null} zurückgegeben. Für Zeiträume ohne Datum ist die Methode {@link Zeitraum#getAnfangszeit()}
     * zu verwenden.
     *
     * @return der Anfang des Zeitraums als {@link ZonedDateTime},
     */
    public ZonedDateTime getAnfangsdatumzeit() {
        return ohneDatum ? null : anfang;
    }

    /**
     * Gibt das Ende dieses Zeitraums (exklusive) zurück. Handelt es sich um einen Zeitraum ohne Datum, wird
     * {@code null} zurückgegeben. Für Zeiträume ohne Datum ist die Methode {@link Zeitraum#getEndzeit()}
     * zu verwenden.
     *
     * @return das Ende dieses Zeitraums (exklusive)
     */
    public ZonedDateTime getEndedatumzeit() {
        return ohneDatum ? null : ende;
    }

    /**
     * Gibt die Anfangszeit des Zeitraums zurück.
     *
     * @return die Anfangszeit des Zeitraums.
     */
    public LocalTime getAnfangszeit() {
        return anfang.toLocalTime();
    }

    /**
     * Gibt die Endzeit des Zeitraums zurück.
     *
     * @return die Endzeit des Zeitraums.
     */
    public LocalTime getEndzeit() {
        return ende.toLocalTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Zeitraum)) {
            return false;
        }
        Zeitraum zeitraum = (Zeitraum) o;
        return Objects.equals(anfang, zeitraum.anfang) && Objects.equals(ende, zeitraum.ende);
    }

    @Override
    public int hashCode() {
        return Objects.hash(anfang, ende);
    }

    /**
     * Gibt eine String-Repräsentation der Form "&lt;Anfang&gt; - &lt;Ende&gt;" des Zeitraums zurück.
     *
     * @return eine String-Repräsentation des Zeitraums
     */
    @Override
    public String toString() {
        if (ohneDatum) {
            if (getAnfangszeit().getSecond() == 0 && getEndzeit().getSecond() == 0) {
                return OutFormat.ZEIT_KURZ.format(getAnfangszeit()) + " - " + OutFormat.ZEIT_KURZ
                    .format(getEndzeit());
            } else {
                return OutFormat.ZEIT.format(getAnfangszeit()) + " - " + OutFormat.ZEIT.format(getEndzeit());
            }
        } else {
            return OutFormat.DATUM_ZEIT_ZONE.format(anfang) + " - " + OutFormat.DATUM_ZEIT_ZONE.format(ende);
        }
    }
}
