package com.geomlab.studio.geometry;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Classe utilitária stateless com algoritmos de detecção de colisão.
 * Todos os métodos são estáticos — não há instâncias desta classe.
 */
public final class GeometriaUtils {

	/**
     * Margem de tolerância para comparações de ponto flutuante,
     * evitando falsos negativos por erro de arredondamento.
     */
    private static final float EPSILON = 0.001f;
    
    // Construtor privado: reforça que a classe é apenas um agrupador
    // de funções estáticas, nunca deve ser instanciada.
    private GeometriaUtils() {
    }

    /**
     * Testa colisão entre dois círculos comparando a distância ao
     * quadrado entre os centros com a soma dos raios ao quadrado
     * (evita o custo de uma raiz quadrada).
     */
    public static boolean intersectaCirculoVsCirculo(Circulo a, Circulo b) {
        float somaRaios = a.getRaio() + b.getRaio();
        float distanciaQuadrada = a.getCentro().dst2(b.getCentro());
        return distanciaQuadrada <= (somaRaios * somaRaios) + EPSILON;
    }

    /**
     * Testa colisão entre dois AABBs por sobreposição de intervalos
     * (Axis-Aligned Overlap Test). Colide se houver interseção
     * simultânea nos eixos X e Y.
     */
    public static boolean intersectaAABBvsAABB(AABB a, AABB b) {
        boolean colideEixoX = a.getMinX() <= b.getMaxX() && a.getMaxX() >= b.getMinX();
        boolean colideEixoY = a.getMinY() <= b.getMaxY() && a.getMaxY() >= b.getMinY();
        return colideEixoX && colideEixoY;
    }
    
    /**
     * Testa colisão entre um AABB e um Círculo via técnica de clamping:
     * encontra o ponto do AABB mais próximo do centro do círculo e
     * compara a distância desse ponto ao raio.
     */
    public static boolean intersectaAABBvsCirculo(AABB caixa, Circulo circulo) {
        Vector2 centroCirculo = circulo.getCentro();

        float pontoMaisProximoX = MathUtils.clamp(centroCirculo.x, caixa.getMinX(), caixa.getMaxX());
        float pontoMaisProximoY = MathUtils.clamp(centroCirculo.y, caixa.getMinY(), caixa.getMaxY());

        float dx = centroCirculo.x - pontoMaisProximoX;
        float dy = centroCirculo.y - pontoMaisProximoY;
        float distanciaQuadrada = dx * dx + dy * dy;

        float raio = circulo.getRaio();
        return distanciaQuadrada <= (raio * raio) + EPSILON;
    }
    
 // ---------- OBB × OBB ----------

    public static boolean intersectaOBBvsOBB(OBB a, OBB b) {
        return sobreposicaoSAT(a.obterVertices(), a.obterEixos(), b.obterVertices(), b.obterEixos());
    }

    // ---------- OBB × AABB ----------

    public static boolean intersectaOBBvsAABB(OBB obb, AABB caixa) {
        Vector2[] eixosAABB = { new Vector2(1, 0), new Vector2(0, 1) };
        return sobreposicaoSAT(obb.obterVertices(), obb.obterEixos(), caixa.obterVertices(), eixosAABB);
    }

    // ---------- OBB × Círculo ----------

    public static boolean intersectaOBBvsCirculo(OBB obb, Circulo circulo) {
        // Traz o centro do círculo para o espaço local do OBB (desfaz a rotação)
        Vector2 relativo = new Vector2(circulo.getCentro()).sub(obb.getPosicao());
        relativo.rotateDeg(-obb.getAnguloRotacao());

        float meiaLargura = obb.getLargura() / 2f;
        float meiaAltura = obb.getAltura() / 2f;

        // Mesmo clamping da Etapa 4.3, agora no referencial local do OBB
        float pontoMaisProximoX = MathUtils.clamp(relativo.x, -meiaLargura, meiaLargura);
        float pontoMaisProximoY = MathUtils.clamp(relativo.y, -meiaAltura, meiaAltura);

        float dx = relativo.x - pontoMaisProximoX;
        float dy = relativo.y - pontoMaisProximoY;
        float distanciaQuadrada = dx * dx + dy * dy;

        float raio = circulo.getRaio();
        return distanciaQuadrada <= (raio * raio) + EPSILON;
    }

    // ---------- Implementação interna do SAT (privada — detalhe de implementação) ----------

    private static boolean sobreposicaoSAT(Vector2[] verticesA, Vector2[] eixosA,
                                             Vector2[] verticesB, Vector2[] eixosB) {
        for (Vector2 eixo : eixosA) {
            if (!seSobrepoeNoEixo(verticesA, verticesB, eixo)) {
                return false; // achamos um eixo separador — não colidem
            }
        }
        for (Vector2 eixo : eixosB) {
            if (!seSobrepoeNoEixo(verticesA, verticesB, eixo)) {
                return false;
            }
        }
        return true; // nenhum eixo separou — colidem
    }

    private static boolean seSobrepoeNoEixo(Vector2[] verticesA, Vector2[] verticesB, Vector2 eixo) {
        float minA = Float.MAX_VALUE, maxA = -Float.MAX_VALUE;
        for (Vector2 v : verticesA) {
            float projecao = v.dot(eixo);
            minA = Math.min(minA, projecao);
            maxA = Math.max(maxA, projecao);
        }

        float minB = Float.MAX_VALUE, maxB = -Float.MAX_VALUE;
        for (Vector2 v : verticesB) {
            float projecao = v.dot(eixo);
            minB = Math.min(minB, projecao);
            maxB = Math.max(maxB, projecao);
        }

        return minA <= maxB + EPSILON && maxA >= minB - EPSILON;
    }
}