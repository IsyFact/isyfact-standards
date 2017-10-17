package de.bund.bva.isyfact.task.sicherheit;

public interface AuthenticatorFactory {
    Authenticator getSecurityAuthenticator(String taskId);
}
