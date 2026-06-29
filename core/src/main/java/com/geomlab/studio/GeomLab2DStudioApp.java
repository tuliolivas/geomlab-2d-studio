package com.geomlab.studio;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.geomlab.studio.scene.Cena;
import com.kotcrab.vis.ui.VisUI;


//Classe raiz do ciclo de vida da aplicação.

public class GeomLab2DStudioApp extends ApplicationAdapter {

    // Composição: Cena é instanciada e destruída exclusivamente por esta classe
    private Cena cena;

    @Override
    public void create() {
        VisUI.load();
        this.cena = new Cena();
        this.cena.construir();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.18f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        cena.render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void resize(int width, int height) {
        cena.resize(width, height);
    }

    @Override
    public void dispose() {
        cena.dispose();
        VisUI.dispose();
    }
}