package com.geomlab.studio.geometry;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class AABB extends Volume {

    private float largura;
    private float altura;

    public AABB(Vector2 posicao, float largura, float altura) {
        super(posicao, new Color(0.25f, 0.55f, 0.95f, 1f));
        this.largura = largura;
        this.altura = altura;
    }

    @Override
    public void render(ShapeRenderer renderer) {
        renderer.setColor(resolverCorPreenchimento());
        renderer.rect(getMinX(), getMinY(), largura, altura);
    }

    @Override
    public void renderBorda(ShapeRenderer renderer) {
        renderer.setColor(resolverCorBorda());
        renderer.rect(getMinX(), getMinY(), largura, altura);
    }

    @Override
    public boolean contemPonto(Vector2 ponto) {
        return ponto.x >= getMinX() && ponto.x <= getMaxX()
            && ponto.y >= getMinY() && ponto.y <= getMaxY();
    }

    public Vector2[] obterVertices() {
        return new Vector2[] {
            new Vector2(getMinX(), getMinY()),
            new Vector2(getMaxX(), getMinY()),
            new Vector2(getMaxX(), getMaxY()),
            new Vector2(getMinX(), getMaxY())
        };
    }
    
    @Override
    public boolean colidirCom(Colidivel outro) {
        if (outro instanceof AABB) {
            return GeometriaUtils.intersectaAABBvsAABB(this, (AABB) outro);
        }
        if (outro instanceof Circulo) {
            return GeometriaUtils.intersectaAABBvsCirculo(this, (Circulo) outro);
        }
        if (outro instanceof OBB) {
            return GeometriaUtils.intersectaOBBvsAABB((OBB) outro, this);
        }
        return false;
    }
    // ---- Novos getters de limites, usados por GeometriaUtils e contemPonto ----

    public float getMinX() {
        return posicao.x - largura / 2f;
    }

    public float getMaxX() {
        return posicao.x + largura / 2f;
    }

    public float getMinY() {
        return posicao.y - altura / 2f;
    }

    public float getMaxY() {
        return posicao.y + altura / 2f;
    }

    public float getLargura() {
        return largura;
    }

    public float getAltura() {
        return altura;
    }
}