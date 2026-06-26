package com.geomlab.studio.geometry;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/**
 * Superclasse abstrata de todo volume envoltório do GeomLab.
 *
 * Justificativa de polimorfismo: Cena mantém apenas List<Volume> e
 * chama v.render(renderer) uniformemente — cada subclasse decide
 * COMO se desenha, sem que Cena precise conhecer AABB, Circulo ou OBB.
 */
public abstract class Volume implements Colidivel {

    protected Vector2 posicao;
    protected Color cor;

    public Volume(Vector2 posicao) {
        this.posicao = posicao;
        this.cor = new Color(Color.WHITE);
    }

    /** Método polimórfico-chave: cada subclasse desenha sua própria forma. */
    public abstract void render(ShapeRenderer renderer);

    /** Hook protegido para subclasses recalcularem limites internos. */
    protected void atualizarLimites() {
        // Implementação default vazia; subclasses sobrescrevem se necessário
    }

    public Vector2 getPosicao() {
        return posicao;
    }

    public void setPosicao(Vector2 posicao) {
        this.posicao = posicao;
        atualizarLimites();
    }

    public Color getCor() {
        return cor;
    }

    public void setCor(Color cor) {
        this.cor = cor;
    }

    @Override
    public Vector2 getCentro() {
        return posicao;
    }

    @Override
    public boolean colidirCom(Colidivel outro) {
        // Implementação real via GeometriaUtils chega na Etapa 4
        return false;
    }
}