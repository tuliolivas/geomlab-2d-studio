package com.geomlab.studio.geometry;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public abstract class Volume implements Colidivel {

    protected Vector2 posicao;
    protected Color corBorda;
    protected Color corPreenchimento;
    protected boolean emColisao = false;

    protected static int totalCriados = 0;
    protected final int idCriacao;

    protected static final Color COR_HALO = new Color(0f, 0f, 0f, 1f);
    protected static final Color COR_COLISAO_BORDA = new Color(1f, 0.15f, 0.15f, 1f);
    protected static final Color COR_COLISAO_PREENCHIMENTO = new Color(1f, 0.15f, 0.15f, 0.35f);

    public Volume(Vector2 posicao, Color corBase) {
        this.posicao = posicao;
        this.corBorda = new Color(corBase);
        this.corPreenchimento = new Color(corBase.r, corBase.g, corBase.b, 0.25f);
        totalCriados++;
        this.idCriacao = totalCriados;
    }

    public abstract void render(ShapeRenderer renderer);
    public abstract void renderHalo(ShapeRenderer renderer);
    public abstract void renderBorda(ShapeRenderer renderer);
    public abstract boolean contemPonto(Vector2 ponto);

    protected void atualizarLimites() {}

    public static int getTotalCriados() {
        return totalCriados;
    }

    public int getIdCriacao() {
        return idCriacao;
    }

    public Vector2 getPosicao() {
        return posicao;
    }

    public void setPosicao(Vector2 posicao) {
        this.posicao = posicao;
        atualizarLimites();
    }

    public boolean isEmColisao() {
        return emColisao;
    }

    public void setEmColisao(boolean emColisao) {
        this.emColisao = emColisao;
    }

    @Override
    public Vector2 getCentro() {
        return posicao;
    }
}