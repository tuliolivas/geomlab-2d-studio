package com.geomlab.studio.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.geomlab.studio.geometry.Volume;
import com.geomlab.studio.ui.PainelInspetor;

import java.util.ArrayList;
import java.util.List;

public class Cena {

    private Stage stage;
    private PainelInspetor painel;
    private Table layoutRaiz;
    private Table canvasArea;
    private ShapeRenderer sr;

    private List<Volume> volumes;

    private Volume selecionado;
    private boolean arrastando;
    private float limitePainel;

    private static final float PROP_PAINEL = 0.30f;

    public void construir() {
        stage = new Stage(new ScreenViewport());
        volumes = new ArrayList<>();
        sr = new ShapeRenderer();
        painel = new PainelInspetor(this);

        canvasArea = new Table();
        canvasArea.setBackground(criarFundoCanvas());

        layoutRaiz = new Table();
        layoutRaiz.setFillParent(true);
        layoutRaiz.add(painel.getRaiz()).width(Gdx.graphics.getWidth() * PROP_PAINEL).growY();
        layoutRaiz.add(canvasArea).width(Gdx.graphics.getWidth() * (1 - PROP_PAINEL)).growY();
        stage.addActor(layoutRaiz);

        limitePainel = Gdx.graphics.getWidth() * PROP_PAINEL;

        InputMultiplexer mux = new InputMultiplexer();
        mux.addProcessor(stage);
        mux.addProcessor(criarInputCanvas());
        Gdx.input.setInputProcessor(mux);
    }

    public void adicionarVolume(Volume v) {
        volumes.add(v);
        selecionado = v;
        painel.selecionar(v);
    }

    public void removerSelecionado() {
        if (selecionado != null) {
            volumes.remove(selecionado);
            selecionado = null;
            arrastando = false;
            painel.selecionar(null);
        }
    }

    public Vector2 gerarPosicaoAleatoriaNoCanvas() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        float x = MathUtils.random(limitePainel + 60, w - 60);
        float y = MathUtils.random(60, h - 60);
        return new Vector2(x, y);
    }

    public void render(float delta) {
        stage.act(delta);
        stage.draw();
        desenhar();
        verificarColisoes();
    }

    private void desenhar() {
        sr.setProjectionMatrix(stage.getCamera().combined);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (Volume v : volumes) {
            v.render(sr);
        }
        sr.end();
        
        sr.begin(ShapeRenderer.ShapeType.Line);
        if (selecionado != null) {
        	selecionado.renderHalo(sr);
        }

        for (Volume v : volumes) {
            v.renderBorda(sr);
        }
        sr.end();
    }

    protected void verificarColisoes() {
        for (Volume v : volumes) {
            v.setEmColisao(false);
        }
        for (int i = 0; i < volumes.size(); i++) {
            for (int j = i + 1; j < volumes.size(); j++) {
                Volume a = volumes.get(i);
                Volume b = volumes.get(j);
                if (a.colidirCom(b)) {
                    a.setEmColisao(true);
                    b.setEmColisao(true);
                }
            }
        }
    }

    private InputAdapter criarInputCanvas() {
        return new InputAdapter() {

            @Override
            public boolean touchDown(int x, int y, int pointer, int button) {
                Vector2 mundo = paraMundo(x, y);
                if (mundo.x < limitePainel) return false;

                List<Volume> candidatos = acharTodosNoPonto(mundo);

                if (candidatos.isEmpty()) {
                    selecionarNada();
                    return false;
                }

                if (candidatos.size() == 1) {
                    selecionarVolume(candidatos.get(0));
                    arrastando = true;
                    return true;
                }

                boolean ctrlPressionado = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)
                    || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);

                if (selecionado != null && candidatos.contains(selecionado)) {
                    if (ctrlPressionado) {
                        Volume maisAntigo = acharMaisAntigo(candidatos);
                        selecionarVolume(maisAntigo);
                        arrastando = true;
                    } else {
                        selecionarNada();
                        arrastando = false;
                    }
                    return true;
                }

                Volume maisRecente = acharMaisRecente(candidatos);
                selecionarVolume(maisRecente);
                arrastando = true;
                return true;
            }

            @Override
            public boolean touchDragged(int x, int y, int pointer) {
                if (arrastando && selecionado != null) {
                    selecionado.setPosicao(paraMundo(x, y));
                    painel.atualizarPosicao(selecionado);
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchUp(int x, int y, int pointer, int button) {
                arrastando = false;
                return false;
            }
        };
    }

    private void selecionarVolume(Volume v) {
        volumes.remove(v);
        volumes.add(v);
        selecionado = v;
        painel.selecionar(v);
    }

    private void selecionarNada() {
        selecionado = null;
        painel.selecionar(null);
    }

    private Volume acharMaisAntigo(List<Volume> lista) {
        Volume resultado = lista.get(0);
        for (Volume v : lista) {
            if (v.getIdCriacao() < resultado.getIdCriacao()) resultado = v;
        }
        return resultado;
    }

    private Volume acharMaisRecente(List<Volume> lista) {
        Volume resultado = lista.get(0);
        for (Volume v : lista) {
            if (v.getIdCriacao() > resultado.getIdCriacao()) resultado = v;
        }
        return resultado;
    }

    private Vector2 paraMundo(int screenX, int screenY) {
        Vector3 v = new Vector3(screenX, screenY, 0);
        stage.getCamera().unproject(v);
        return new Vector2(v.x, v.y);
    }

    private List<Volume> acharTodosNoPonto(Vector2 ponto) {
        List<Volume> achados = new ArrayList<>();
        for (Volume v : volumes) {
            if (v.contemPonto(ponto)) {
                achados.add(v);
            }
        }
        return achados;
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        limitePainel = width * PROP_PAINEL;
        layoutRaiz.getCells().get(0).width(limitePainel);
        layoutRaiz.getCells().get(1).width(width - limitePainel);
        layoutRaiz.invalidate();
    }

    private Drawable criarFundoCanvas() {
        Pixmap p = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        p.setColor(0.95f, 0.95f, 0.97f, 1f);
        p.fill();
        Texture t = new Texture(p);
        p.dispose();
        return new TextureRegionDrawable(t);
    }

    public void dispose() {
        stage.dispose();
        sr.dispose();
    }
}