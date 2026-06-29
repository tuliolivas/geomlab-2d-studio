package com.geomlab.studio.geometry;

import com.badlogic.gdx.math.Vector2;

public interface Colidivel {
    boolean colidirCom(Colidivel outro);
    Vector2 getCentro();
}