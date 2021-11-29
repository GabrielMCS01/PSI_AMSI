package com.psi.ciclodias.model;

public class User {
    private long id;
    private String username, auth_key, password, email;

    private long user_info_id;
    private String primeiro_nome, ultimo_nome;
    private String data_nascimento;
    private long user_id;

    // User e Userinfo
    public User(String username, String password, String email, String primeiro_nome, String ultimo_nome) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.primeiro_nome = primeiro_nome;
        this.ultimo_nome = ultimo_nome;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuth_key() {
        return auth_key;
    }

    public void setAuth_key(String auth_key) {
        this.auth_key = auth_key;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getUser_info_id() {
        return user_info_id;
    }

    public void setUser_info_id(long user_info_id) {
        this.user_info_id = user_info_id;
    }

    public String getPrimeiro_nome() {
        return primeiro_nome;
    }

    public void setPrimeiro_nome(String primeiro_nome) {
        this.primeiro_nome = primeiro_nome;
    }

    public String getUltimo_nome() {
        return ultimo_nome;
    }

    public void setUltimo_nome(String ultimo_nome) {
        this.ultimo_nome = ultimo_nome;
    }

    public String getData_nascimento() {
        return data_nascimento;
    }

    public void setData_nascimento(String data_nascimento) {
        this.data_nascimento = data_nascimento;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", primeiro_nome='" + primeiro_nome + '\'' +
                ", ultimo_nome='" + ultimo_nome + '\'' +
                ", data_nascimento='" + data_nascimento + '\'' +
                '}';
    }
}
