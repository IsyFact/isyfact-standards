package de.bund.bva.isyfact.task.security;

/**
 * @author Björn Saxe, msg systems ag
 */
public interface SecurityAuthenticatorFactory {
    SecurityAuthenticator getSecurityAuthenticator(String taskId);
}
