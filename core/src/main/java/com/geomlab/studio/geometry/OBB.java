package com.geomlab.studio.geometry;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class OBB extends Volume {

    private float largura;
    private float altura;
    private float anguloRotacao;

    public OBB(Vector2 posicao, float largura, float altura, float anguloRotacao) {
        super(posicao, new Color(0.55f, 0.85f, 0.35f, 1f));
        this.largura = largura;
        this.altura = altura;
        this.anguloRotacao = anguloRotacao;
    }

    @Override
    public void render(ShapeRenderer renderer) {
        renderer.setColor(emColisao ? COR_COLISAO_PREENCHIMENTO : corPreenchimento);
        desenharTriangulos(renderer);
    }

    @Override
    public void renderHalo(ShapeRenderer renderer) {
        renderer.setColor(COR_HALO);
        
        float folga = 5f;
        float wExpandido = largura + (folga * 2);
        float hExpandido = altura + (folga * 2);
        
        float originX = wExpandido / 2f;
        float originY = hExpandido / 2f;
        
        float x = posicao.x - originX;
        float y = posicao.y - originY;
        
        renderer.rect(x, y, originX, originY, wExpandido, hExpandido, 1f, 1f, anguloRotacao);
    }
    
    @Override
    public void renderBorda(ShapeRenderer renderer) {
        renderer.setColor(emColisao ? COR_COLISAO_BORDA : corBorda);
        Vector2[] v = obterVertices();
        renderer.line(v[0], v[1]);
        renderer.line(v[1], v[2]);
        renderer.line(v[2], v[3]);
        renderer.line(v[3], v[0]);
    }

    @Override
    public boolean contemPonto(Vector2 ponto) {
        Vector2 relativo = new Vector2(ponto).sub(posicao);
        relativo.rotateDeg(-anguloRotacao);
        return Math.abs(relativo.x) <= largura / 2f && Math.abs(relativo.y) <= altura / 2f;
    }
    
    @Override
    public float getRaioEnvolvente() {
        // rotacao nao muda a diagonal, entao a formula e igual a do AABB
        return (float) Math.sqrt(largura * largura + altura * altura) / 2f;
    }
    
    private void desenharTriangulos(ShapeRenderer renderer) {
        Vector2[] v = obterVertices();
        renderer.triangle(v[0].x, v[0].y, v[1].x, v[1].y, v[2].x, v[2].y);
        renderer.triangle(v[0].x, v[0].y, v[2].x, v[2].y, v[3].x, v[3].y);
    }

    @Override
    public boolean colidirCom(Colidivel outro) {
        if (outro instanceof OBB) {
            return GeometriaUtils.intersectaOBBvsOBB(this, (OBB) outro);
        }
        if (outro instanceof AABB) {
            return GeometriaUtils.intersectaOBBvsAABB(this, (AABB) outro);
        }
        if (outro instanceof Circulo) {
            return GeometriaUtils.intersectaOBBvsCirculo(this, (Circulo) outro);
        }
        return false;
    }

    public Vector2[] obterVertices() {
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

    public Vector2[] obterEixos() {
        Vector2 eixoX = new Vector2(1, 0).rotateDeg(anguloRotacao);
        Vector2 eixoY = new Vector2(0, 1).rotateDeg(anguloRotacao);
        return new Vector2[] { eixoX, eixoY };
    }

    public float getLargura() {
        return largura;
    }
    
    public float getAltura() {
        return altura;
    }

    public float getAnguloRotacao() {
        return anguloRotacao;
    }
    
    public void setLargura(float largura) {
        this.largura = largura;
    }

    public void setAltura(float altura) {
        this.altura = altura;
    }    

    public void setAnguloRotacao(float anguloRotacao) {
        this.anguloRotacao = anguloRotacao;
    }
}