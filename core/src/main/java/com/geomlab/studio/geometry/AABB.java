package com.geomlab.studio.geometry;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class AABB extends Volume {

    private float largura;
    private float altura;

    public AABB(Vector2 posicao, float largura, float altura) {
        super(posicao, new Color(0.25f, 0.55f, 0.95f, 1f)); // azul
        this.largura = largura;
        this.altura = altura;
    }

    @Override
    public void render(ShapeRenderer renderer) {
        renderer.setColor(corPreenchimento);
        renderer.rect(posicao.x - largura / 2f, posicao.y - altura / 2f, largura, altura);
    }

    @Override
    public void renderBorda(ShapeRenderer renderer) {
        renderer.setColor(corBorda);
        renderer.rect(posicao.x - largura / 2f, posicao.y - altura / 2f, largura, altura);
    }

    @Override
    public boolean contemPonto(Vector2 ponto) {
        float minX = posicao.x - largura / 2f;
        float maxX = posicao.x + largura / 2f;
        float minY = posicao.y - altura / 2f;
        float maxY = posicao.y + altura / 2f;
        return ponto.x >= minX && ponto.x <= maxX && ponto.y >= minY && ponto.y <= maxY;
    }

    public float getLargura() { return largura; }
    public float getAltura() { return altura; }
}