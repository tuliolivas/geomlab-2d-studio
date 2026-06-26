package com.geomlab.studio.geometry;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Circulo extends Volume {

    private float raio;

    public Circulo(Vector2 posicao, float raio) {
        super(posicao);
        this.raio = raio;
        this.cor = new Color(0.95f, 0.45f, 0.25f, 1f); // laranja
    }

    @Override
    public void render(ShapeRenderer renderer) {
        renderer.setColor(cor);
        renderer.circle(posicao.x, posicao.y, raio, 40);
    }

    public float getRaio() {
        return raio;
    }
}
