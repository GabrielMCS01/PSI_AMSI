package com.psi.ciclodias.listeners;

import java.util.Map;

public interface PerfilListener {
    void perfilDados(Map<String, String> dadosUser);
    void editUser(Boolean success);
    void removeUser(Boolean success);
}
