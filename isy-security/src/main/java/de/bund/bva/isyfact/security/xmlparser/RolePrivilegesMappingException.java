package de.bund.bva.isyfact.security.xmlparser;

class RolePrivilegesMappingException extends RuntimeException {

    public RolePrivilegesMappingException(String message) {
        this("Fehler beim Parsen der Datei zum RollenRechteMapping: " + message, null);
    }

    public RolePrivilegesMappingException(String message, Throwable cause) {
        super(message, cause);
    }

}
