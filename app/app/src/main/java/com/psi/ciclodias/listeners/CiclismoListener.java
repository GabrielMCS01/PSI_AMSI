package com.psi.ciclodias.listeners;

import java.util.Map;

public interface CiclismoListener {
    void ciclismoDados(Map<String, String> dadosUser);
    void editCiclismo(Boolean success);
}
