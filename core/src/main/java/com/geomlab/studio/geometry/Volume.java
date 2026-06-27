package com.geomlab.studio.geometry;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public abstract class Volume implements Colidivel {

    protected Vector2 posicao;
    protected Color corBorda;
    protected Color corPreenchimento;

    private static final float ALPHA_PREENCHIMENTO = 0.25f;

    public Volume(Vector2 posicao, Color corBase) {
        this.posicao = posicao;
        this.corBorda = new Color(corBase);
        this.corPreenchimento = new Color(corBase.r, corBase.g, corBase.b, ALPHA_PREENCHIMENTO);
    }

    /** Passada 1: preenchimento translúcido. */
    public abstract void render(ShapeRenderer renderer);

    /** Passada 2: contorno sólido — desenhado por cima de todos os preenchimentos. */
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

    @Override
    public Vector2 getCentro() {
        return posicao;
    }

    @Override
    public boolean colidirCom(Colidivel outro) {
        return false; // Implementação real chega na Etapa 4
    }
}