package com.geomlab.studio.geometry;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class AABB extends Volume {

    private float largura;
    private float altura;

    public AABB(Vector2 posicao, float largura, float altura) {
        super(posicao);
        this.largura = largura;
        this.altura = altura;
        this.cor = new Color(0.25f, 0.55f, 0.95f, 1f); // azul
    }

    @Override
    public void render(ShapeRenderer renderer) {
        renderer.setColor(cor);
        renderer.rect(
            posicao.x - largura / 2f,
            posicao.y - altura / 2f,
            largura,
            altura
        );
    }

    public float getLargura() {
        return largura;
    }

    public float getAltura() {
        return altura;
    }

    private void normalizarDimensoes() {
        // Exemplo de método privado: garante dimensões nunca negativas
        if (largura < 0) largura = Math.abs(largura);
        if (altura < 0) altura = Math.abs(altura);
    }
}
