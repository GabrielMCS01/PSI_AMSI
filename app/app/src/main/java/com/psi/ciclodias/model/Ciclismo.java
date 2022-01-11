package com.psi.ciclodias.model;

public class Ciclismo {
    private long id, user_id_ciclismo;
    private String nome_percurso, velocidade_grafico, rota;
    private double velocidade_media, velocidade_maxima;
    private String data_treino;
    private int distancia, duracao;

    // Construtor para adicionar o ciclismo na DB Local
    public Ciclismo(String nome_percurso, int duracao, int distancia, double velocidade_media, double velocidade_maxima, String velocidade_grafico, String rota, String data_treino) {
        this.nome_percurso = nome_percurso;
        this.duracao = duracao;
        this.velocidade_grafico = velocidade_grafico;
        this.rota = rota;
        this.distancia = distancia;
        this.velocidade_media = velocidade_media;
        this.velocidade_maxima = velocidade_maxima;
        this.data_treino = data_treino;
    }

    // Construtor para Receber os treinos da API
    public Ciclismo(long id, String nome_percurso, int duracao, int distancia, double velocidade_media, double velocidade_maxima, String velocidade_grafico, String rota, String data_treino) {
        this.id = id;
        this.nome_percurso = nome_percurso;
        this.duracao = duracao;
        this.velocidade_grafico = velocidade_grafico;
        this.rota = rota;
        this.distancia = distancia;
        this.velocidade_media = velocidade_media;
        this.velocidade_maxima = velocidade_maxima;
        this.data_treino = data_treino;
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

    public double getVelocidade_media() {
        return velocidade_media;
    }

    public void setVelocidade_media(double velocidade_media) {
        this.velocidade_media = velocidade_media;
    }

    public double getVelocidade_maxima() {
        return velocidade_maxima;
    }

    public void setVelocidade_maxima(double velocidade_maxima) {
        this.velocidade_maxima = velocidade_maxima;
    }

    public String getData_treino() {
        return data_treino;
    }

    public void setData_treino(String data_treino) {
        this.data_treino = data_treino;
    }

    public int getDistancia() {
        return distancia;
    }

    public void setDistancia(int distancia) {
        this.distancia = distancia;
    }

    public int getDuracao() {
        return duracao;
    }

    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }
}
