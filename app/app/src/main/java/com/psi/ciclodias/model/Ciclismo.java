package com.psi.ciclodias.model;

public class Ciclismo {
    private long id, user_id_ciclismo;
    private String nome_percurso, velocidade_grafico, rota;
    private float distancia, velocidade_media, velocidade_maxima;
    private String data_treino, duracao;

    public Ciclismo(String nome_percurso, String duracao, float distancia, float velocidade_media, float velocidade_maxima, String velocidade_grafico, String rota) {
        this.nome_percurso = nome_percurso;
        this.velocidade_grafico = velocidade_grafico;
        this.rota = rota;
        this.distancia = distancia;
        this.velocidade_media = velocidade_media;
        this.velocidade_maxima = velocidade_maxima;
        this.duracao = duracao;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUser_id_ciclismo() {
        return user_id_ciclismo;
    }

    public void setUser_id_ciclismo(long user_id_ciclismo) {
        this.user_id_ciclismo = user_id_ciclismo;
    }

    public String getNome_percurso() {
        return nome_percurso;
    }

    public void setNome_percurso(String nome_percurso) {
        this.nome_percurso = nome_percurso;
    }

    public String getVelocidade_grafico() {
        return velocidade_grafico;
    }

    public void setVelocidade_grafico(String velocidade_grafico) {
        this.velocidade_grafico = velocidade_grafico;
    }

    public String getRota() {
        return rota;
    }

    public void setRota(String rota) {
        this.rota = rota;
    }

    public float getDistancia() {
        return distancia;
    }

    public void setDistancia(float distancia) {
        this.distancia = distancia;
    }

    public float getVelocidade_media() {
        return velocidade_media;
    }

    public void setVelocidade_media(float velocidade_media) {
        this.velocidade_media = velocidade_media;
    }

    public float getVelocidade_maxima() {
        return velocidade_maxima;
    }

    public void setVelocidade_maxima(float velocidade_maxima) {
        this.velocidade_maxima = velocidade_maxima;
    }

    public String getData_treino() {
        return data_treino;
    }

    public void setData_treino(String data_treino) {
        this.data_treino = data_treino;
    }

    public String getDuracao() {
        return duracao;
    }

    public void setDuracao(String duracao) {
        this.duracao = duracao;
    }
}
