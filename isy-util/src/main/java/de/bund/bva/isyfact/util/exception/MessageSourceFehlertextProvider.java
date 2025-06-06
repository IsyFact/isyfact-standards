package de.bund.bva.isyfact.util.exception;

import de.bund.bva.isyfact.exception.FehlertextProvider;
import de.bund.bva.isyfact.util.spring.MessageSourceHolder;

/**
 * This class implements a {@link FehlertextProvider} that uses the {@link MessageSourceHolder} to load the messages (must be configured as a Spring bean in the application).
 *
 * @deprecated as of IsyFact 3, due to the deprecation of {@link MessageSourceHolder}, an application must implement {@link FehlertextProvider} itself if required (without {@link  MessageSourceHolder}).
 * Class will be deleted in future version.
 */
@Deprecated
public class MessageSourceFehlertextProvider implements FehlertextProvider {

    @Override
    public String getMessage(String schluessel, String... parameter) {
        return MessageSourceHolder.getMessage(schluessel, parameter);
    }
}
