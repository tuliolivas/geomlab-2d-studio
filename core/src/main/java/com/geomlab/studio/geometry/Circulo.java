package com.geomlab.studio.geometry;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Circulo extends Volume {

    private float raio;

    public Circulo(Vector2 posicao, float raio) {
        super(posicao, new Color(0.95f, 0.45f, 0.25f, 1f)); // laranja
        this.raio = raio;
    }

    @Override
    public void render(ShapeRenderer renderer) {
        renderer.setColor(corPreenchimento);
        renderer.circle(posicao.x, posicao.y, raio, 40);
    }

    @Override
    public void renderBorda(ShapeRenderer renderer) {
        renderer.setColor(corBorda);
        renderer.circle(posicao.x, posicao.y, raio, 40);
    }

    @Override
    public boolean contemPonto(Vector2 ponto) {
        return posicao.dst2(ponto) <= raio * raio;
    }

    public float getRaio() { return raio; }
}