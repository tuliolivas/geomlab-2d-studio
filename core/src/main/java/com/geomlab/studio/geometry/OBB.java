package com.geomlab.studio.geometry;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Oriented Bounding Box: retângulo rotacionado.
 * Desenhado como dois triângulos (ShapeRenderer não desenha
 * retângulos rotacionados preenchidos diretamente).
 */
public class OBB extends Volume {

    private float largura;
    private float altura;
    private float anguloRotacao; // em graus

    public OBB(Vector2 posicao, float largura, float altura, float anguloRotacao) {
        super(posicao);
        this.largura = largura;
        this.altura = altura;
        this.anguloRotacao = anguloRotacao;
        this.cor = new Color(0.55f, 0.85f, 0.35f, 1f); // verde
    }

    @Override
    public void render(ShapeRenderer renderer) {
        renderer.setColor(cor);
        Vector2[] vertices = calcularVertices();

        renderer.triangle(
            vertices[0].x, vertices[0].y,
            vertices[1].x, vertices[1].y,
            vertices[2].x, vertices[2].y
        );
        renderer.triangle(
            vertices[0].x, vertices[0].y,
            vertices[2].x, vertices[2].y,
            vertices[3].x, vertices[3].y
        );
    }

    /** Calcula os 4 vértices do retângulo já rotacionados em torno da posição. */
    private Vector2[] calcularVertices() {
        float meiaLargura = largura / 2f;
        float meiaAltura = altura / 2f;

        Vector2[] locais = {
            new Vector2(-meiaLargura, -meiaAltura),
            new Vector2(meiaLargura, -meiaAltura),
            new Vector2(meiaLargura, meiaAltura),
            new Vector2(-meiaLargura, meiaAltura)
        };

        for (Vector2 v : locais) {
            v.rotateDeg(anguloRotacao);
            v.add(posicao);
        }
        return locais;
    }

    public float getAnguloRotacao() {
        return anguloRotacao;
    }

    public void setAnguloRotacao(float anguloRotacao) {
        this.anguloRotacao = anguloRotacao;
    }
}
