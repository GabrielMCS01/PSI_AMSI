package com.psi.ciclodias.listeners;

import java.util.Map;

public interface LoginListener {
    void onValidateLogin(final Map<String, String> dadosUser, final String username);
}
