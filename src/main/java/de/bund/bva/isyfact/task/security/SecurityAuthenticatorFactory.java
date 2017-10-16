package de.bund.bva.isyfact.task.security;

public interface SecurityAuthenticatorFactory {
    SecurityAuthenticator getSecurityAuthenticator(String taskId);
}
