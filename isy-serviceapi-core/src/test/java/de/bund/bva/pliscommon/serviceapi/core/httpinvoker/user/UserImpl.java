package de.bund.bva.pliscommon.serviceapi.core.httpinvoker.user;

import java.io.Serializable;

public class UserImpl implements Serializable, User {

    private String name;

    public UserImpl() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
