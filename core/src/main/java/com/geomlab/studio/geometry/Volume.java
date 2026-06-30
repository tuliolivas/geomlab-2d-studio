package com.geomlab.studio.geometry;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

//superclasse abstrata de todas as formas geométricas do sistema
//não pode ser instanciada diretamente -- só AABB, Circulo e OBB existem de verdade
public abstract class Volume implements Colidivel {

	// posicão e cores ficam aqui pra não repetir em cada subclasse
    protected Vector2 posicao;
    protected Color corBorda;
    protected Color corPreenchimento;

    // estado de colisão: resetado e recalculado por Cena a cada frame
    protected boolean emColisao = false;

 // contador estático: incrementado a cada new Volume(...), nunca decrementado
    protected static int totalCriados = 0;

    // id imutável, atribuído no construtor
    protected final int idCriacao;

    // cores
    protected static final Color COR_HALO = new Color(0f, 0f, 0f, 1f);
    protected static final Color COR_COLISAO_BORDA = new Color(1f, 0.15f, 0.15f, 1f);
    protected static final Color COR_COLISAO_PREENCHIMENTO = new Color(1f, 0.15f, 0.15f, 0.35f);

    public Volume(Vector2 posicao, Color corBase) {
        this.posicao = posicao;
        this.corBorda = new Color(corBase);
        this.corPreenchimento = new Color(corBase.r, corBase.g, corBase.b, 0.25f);
        totalCriados++;
        this.idCriacao = totalCriados;
    }

    // cada subclasse desenha do seu jeito
    public abstract void render(ShapeRenderer renderer);
    public abstract void renderHalo(ShapeRenderer renderer);
    public abstract void renderBorda(ShapeRenderer renderer);
    
    // cada subclasse testa a seu modo se um ponto está dentro dela
    public abstract boolean contemPonto(Vector2 ponto);
    
    // raio do menor círculo que cobre totalmente a forma
    public abstract float getRaioEnvolvente();

    //subclasses sobrescrevem se precisarem recalcular algo ao mover
    protected void atualizarLimites() {}

    public static int getTotalCriados() {
        return totalCriados;
    }

    public int getIdCriacao() {
        return idCriacao;
    }

    public Vector2 getPosicao() {
        return posicao;
    }
    
    @Override
    public Vector2 getCentro() {
        return posicao;
    }

    public void setPosicao(Vector2 posicao) {
        this.posicao = posicao;
        atualizarLimites();
    }

    public void setEmColisao(boolean emColisao) {
        this.emColisao = emColisao;
    }
    
    public boolean isEmColisao() {
        return emColisao;
    }
}