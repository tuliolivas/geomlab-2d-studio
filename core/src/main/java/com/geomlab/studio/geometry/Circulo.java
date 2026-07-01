package com.geomlab.studio.geometry;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

//círculo definido por centro (posicão herdada de Vólume) e raio
public class Circulo extends Volume {

    private float raio;

    public Circulo(Vector2 posicao, float raio) {
        super(posicao, new Color(0.95f, 0.45f, 0.25f, 1f));
        this.raio = raio;
    }

    @Override
    public void render(ShapeRenderer renderer) {
        renderer.setColor(emColisao ? COR_COLISAO_PREENCHIMENTO : corPreenchimento);
        renderer.circle(posicao.x, posicao.y, raio, 40);
    }
    
    @Override
    public void renderHalo(ShapeRenderer renderer) {
        renderer.setColor(COR_HALO);
        renderer.circle(posicao.x, posicao.y, raio + 5f, 40);
    }

    @Override
    public void renderBorda(ShapeRenderer renderer) {
        renderer.setColor(emColisao ? COR_COLISAO_BORDA : corBorda);
        renderer.circle(posicao.x, posicao.y, raio, 40);
    }

    // usa distância ao quadrado pra evitar sqrt
    @Override
    public boolean contemPonto(Vector2 ponto) {
        return posicao.dst2(ponto) <= raio * raio;
    }

    @Override
    public boolean colidirCom(Colidivel outro) {
        if (outro instanceof Circulo) return GeometriaUtils.intersectaCirculoVsCirculo(this, (Circulo) outro);
        if (outro instanceof AABB)    return GeometriaUtils.intersectaAABBvsCirculo((AABB) outro, this);
        if (outro instanceof OBB)     return GeometriaUtils.intersectaOBBvsCirculo((OBB) outro, this);
        return false;
    }

    @Override
    public float getRaioEnvolvente() {
        return raio;
    }

    public float getRaio() {
        return raio;
    }

    public void setRaio(float raio) {
        this.raio = raio;
    }
}