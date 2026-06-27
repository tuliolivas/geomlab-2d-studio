package com.geomlab.studio.geometry;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public abstract class Volume implements Colidivel {

    protected Vector2 posicao;
    protected Color corBorda;
    protected Color corPreenchimento;

    // Estado de colisão: resetado e recalculado a cada frame por Cena
    protected boolean emColisao = false;

    private static final float ALPHA_PREENCHIMENTO = 0.25f;

    // Cor de alerta universal, compartilhada por todas as subclasses
    protected static final Color COR_COLISAO_BORDA = new Color(1f, 0.15f, 0.15f, 1f);
    protected static final Color COR_COLISAO_PREENCHIMENTO = new Color(1f, 0.15f, 0.15f, 0.35f);

    public Volume(Vector2 posicao, Color corBase) {
        this.posicao = posicao;
        this.corBorda = new Color(corBase);
        this.corPreenchimento = new Color(corBase.r, corBase.g, corBase.b, ALPHA_PREENCHIMENTO);
    }

    public abstract void render(ShapeRenderer renderer);

    public abstract void renderBorda(ShapeRenderer renderer);

    public abstract boolean contemPonto(Vector2 ponto);

    protected void atualizarLimites() {
        // Hook para subclasses recalcularem estado interno após mudança de posição
    }

    public Vector2 getPosicao() {
        return posicao;
    }

    public void setPosicao(Vector2 posicao) {
        this.posicao = posicao;
        atualizarLimites();
    }

    public Color getCorBorda() {
        return corBorda;
    }

    public boolean isEmColisao() {
        return emColisao;
    }

    public void setEmColisao(boolean emColisao) {
        this.emColisao = emColisao;
    }

    /** Retorna a cor de preenchimento a usar neste frame: normal ou de alerta. */
    protected Color resolverCorPreenchimento() {
        return emColisao ? COR_COLISAO_PREENCHIMENTO : corPreenchimento;
    }

    /** Retorna a cor de borda a usar neste frame: normal ou de alerta. */
    protected Color resolverCorBorda() {
        return emColisao ? COR_COLISAO_BORDA : corBorda;
    }

    @Override
    public Vector2 getCentro() {
        return posicao;
    }
}