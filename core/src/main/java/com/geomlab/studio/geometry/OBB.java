package com.geomlab.studio.geometry;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class OBB extends Volume {

    private float largura;
    private float altura;
    private float anguloRotacao;

    public OBB(Vector2 posicao, float largura, float altura, float anguloRotacao) {
        super(posicao, new Color(0.55f, 0.85f, 0.35f, 1f)); // verde
        this.largura = largura;
        this.altura = altura;
        this.anguloRotacao = anguloRotacao;
    }

    @Override
    public void render(ShapeRenderer renderer) {
        renderer.setColor(corPreenchimento);
        desenharTriangulos(renderer);
    }

    @Override
    public void renderBorda(ShapeRenderer renderer) {
        renderer.setColor(corBorda);
        Vector2[] v = calcularVertices();
        renderer.line(v[0], v[1]);
        renderer.line(v[1], v[2]);
        renderer.line(v[2], v[3]);
        renderer.line(v[3], v[0]);
    }

    private void desenharTriangulos(ShapeRenderer renderer) {
        Vector2[] v = calcularVertices();
        renderer.triangle(v[0].x, v[0].y, v[1].x, v[1].y, v[2].x, v[2].y);
        renderer.triangle(v[0].x, v[0].y, v[2].x, v[2].y, v[3].x, v[3].y);
    }

    @Override
    public boolean contemPonto(Vector2 ponto) {
        Vector2 relativo = new Vector2(ponto).sub(posicao);
        relativo.rotateDeg(-anguloRotacao);
        return Math.abs(relativo.x) <= largura / 2f && Math.abs(relativo.y) <= altura / 2f;
    }

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

    public float getAnguloRotacao() { return anguloRotacao; }
    public void setAnguloRotacao(float anguloRotacao) { this.anguloRotacao = anguloRotacao; }
}