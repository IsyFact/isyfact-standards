package de.bund.bva.pliscommon.serviceapi.core.httpinvoker.user;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class UserInvocationHandler implements InvocationHandler, Serializable {

    private User user;

    public UserInvocationHandler(User user) {
        this.user = user;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(user, args);
    }

}
