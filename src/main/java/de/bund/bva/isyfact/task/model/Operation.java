package de.bund.bva.isyfact.task.model;

import java.util.concurrent.Callable;

/**
 * Eine Operation enthält die Anweisungen, die erledigt werden sollen.
 * Wenn die Anweisungen erfolgreich durchlaufen wurden, gibt hasBeenExecutedSuccessfully true zurück.
 * Sollte der Durchlauf unterbrochen werden, wird die ErrorMessage notiert.
 *
 * @author Alexander Salvanos, msg systems ag
 *
 */
public interface Operation extends Runnable {
    String get();

    void set(String result);

    boolean getHasBeenExecutedSuccessfully();

    void setHasBeenExecutedSuccessfully(boolean hasBeenExecutedSuccessfully);

    String getErrorMessage();

    void setErrorMessage(String errorMessage);

    FixedRate getFixedRate();

    void setFixedRate(FixedRate fixedRate);
}
