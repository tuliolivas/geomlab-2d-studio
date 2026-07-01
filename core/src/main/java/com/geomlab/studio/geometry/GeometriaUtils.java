package com.geomlab.studio.geometry;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

//classe utilitária: so métodos estáticos, nunca instânciada
//centraliza todos os algoritmos de detecção de colisão do sistema
public final class GeometriaUtils {

	// margem de tolerância pra comparações de ponto flutuante
    // evita falso negativo quando duas formas estão exatamente se tocando
    private static final float EPSILON = 0.001f;

    // construtor privado já que essa classe nunca deve ser instanciada
    private GeometriaUtils() {
    }

    // círculo vs círculo
    // colide se a distância entre centros e menor ou igual a soma dos raios
    // usa distancia ao quadrado pra evitar sqrt
    public static boolean intersectaCirculoVsCirculo(Circulo a, Circulo b) {
        float somaRaios = a.getRaio() + b.getRaio();
        float distanciaQuadrada = a.getCentro().dst2(b.getCentro());
        return distanciaQuadrada <= (somaRaios * somaRaios) + EPSILON;
    }

    // aabb vs aabb
    // colide se houver sobreposição simultânea nos eixos X e Y
    // basta um eixo sem sobreposição pra garantir que não colidem
    public static boolean intersectaAABBvsAABB(AABB a, AABB b) {
        boolean colideEixoX = a.getMinX() <= b.getMaxX() && a.getMaxX() >= b.getMinX();
        boolean colideEixoY = a.getMinY() <= b.getMaxY() && a.getMaxY() >= b.getMinY();
        return colideEixoX && colideEixoY;
    }

    // aabb vs circulo
    // técnica de clamping: encontra o ponto do AABB mais próximo do centro do círculo
    // se a distância desse ponto ao centro for menor que o raio, colide
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

    // obb vs obb
    // usa o SAT com os 4 eixos (2 de cada OBB)
    public static boolean intersectaOBBvsOBB(OBB a, OBB b) {
        return sobreposicaoSAT(a.obterVertices(), a.obterEixos(), b.obterVertices(), b.obterEixos());
    }

    // obb vs aabb
    // mesma lógica do OBB vs OBB, tratando a AABB como uma OBB de eixos fixos (1,0) e (0,1)
    public static boolean intersectaOBBvsAABB(OBB obb, AABB caixa) {
        Vector2[] eixosAABB = { new Vector2(1, 0), new Vector2(0, 1) };
        return sobreposicaoSAT(obb.obterVertices(), obb.obterEixos(), caixa.obterVertices(), eixosAABB);
    }

    // obb vs círculo
    // traz o círculo pro espaco local da OBB (desfaz a rotação) e aplica o mesmo
    // clamping do AABB vs Círculo, a OBB é uma AABB nesse referêncial
    public static boolean intersectaOBBvsCirculo(OBB obb, Circulo circulo) {
        Vector2 relativo = new Vector2(circulo.getCentro()).sub(obb.getPosicao());
        relativo.rotateDeg(-obb.getAnguloRotacao());

        float meiaLargura = obb.getLargura() / 2f;
        float meiaAltura = obb.getAltura() / 2f;

        float pontoMaisProximoX = MathUtils.clamp(relativo.x, -meiaLargura, meiaLargura);
        float pontoMaisProximoY = MathUtils.clamp(relativo.y, -meiaAltura, meiaAltura);

        float dx = relativo.x - pontoMaisProximoX;
        float dy = relativo.y - pontoMaisProximoY;
        float distanciaQuadrada = dx * dx + dy * dy;

        float raio = circulo.getRaio();
        return distanciaQuadrada <= (raio * raio) + EPSILON;
    }
    
    // implementação interna do SAT
    // testa todos os eixos de A e de B; se qualquer um separar as projeções, não ha colisão

    private static boolean sobreposicaoSAT(Vector2[] verticesA, Vector2[] eixosA,
                                             Vector2[] verticesB, Vector2[] eixosB) {
        for (Vector2 eixo : eixosA) {
            if (!seSobrepoeNoEixo(verticesA, verticesB, eixo)) {
                return false;
            }
        }
        for (Vector2 eixo : eixosB) {
            if (!seSobrepoeNoEixo(verticesA, verticesB, eixo)) {
                return false;
            }
        }
        return true;
    }

    // projeta todos os vértices de A e B no eixo e verifica se as sombras se sobrepõem
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