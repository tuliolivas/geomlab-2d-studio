package com.geomlab.studio.geometry;

import com.badlogic.gdx.math.Vector2;

// contrato que toda forma geométrica deve cumprir pra participar do sistema de colisão
public interface Colidivel {

    // retorna true se esta forma esta tocando "outro"
    boolean colidirCom(Colidivel outro);

    // retorna o centro geométrico da forma, usado nos cálculos de distância
    Vector2 getCentro();
}