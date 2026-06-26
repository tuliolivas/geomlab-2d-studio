package com.geomlab.studio.geometry;

import com.badlogic.gdx.math.Vector2;

/**
 * Contrato de colidibilidade. A implementação real do algoritmo
 * de interseção chega na Etapa 4, via GeometriaUtils.
 */
public interface Colidivel {
    boolean colidirCom(Colidivel outro);
    Vector2 getCentro();
}