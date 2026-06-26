package com.geomlab.studio.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
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
    private Table canvasPlaceholder;
    private ShapeRenderer shapeRenderer;

    // Agregação: Cena mantém os Volumes, mas eles podem ser
    // criados/transferidos externamente (pelo PainelInspetor)
    private List<Volume> volumes;

    private static final float PROPORCAO_PAINEL = 0.30f;

    public void construir() {
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        this.volumes = new ArrayList<>();
        this.shapeRenderer = new ShapeRenderer();

        // Associação Simples: PainelInspetor recebe referência a esta Cena
        this.painel = new PainelInspetor(this);

        this.canvasPlaceholder = new Table();
        this.canvasPlaceholder.setBackground(criarFundoCanvas());

        this.layoutRaiz = new Table();
        layoutRaiz.setFillParent(true);

        layoutRaiz.add(painel.getRaiz()).width(Gdx.graphics.getWidth() * PROPORCAO_PAINEL).growY();
        layoutRaiz.add(canvasPlaceholder).width(Gdx.graphics.getWidth() * (1 - PROPORCAO_PAINEL)).growY();

        stage.addActor(layoutRaiz);
    }

    /** Ponto de entrada usado pelo PainelInspetor para popular a cena (Agregação). */
    public void adicionarVolume(Volume v) {
        volumes.add(v);
    }

    /** Gera uma posição aleatória dentro da área visível do canvas (70% direito). */
    public Vector2 gerarPosicaoAleatoriaNoCanvas() {
        float larguraTela = Gdx.graphics.getWidth();
        float alturaTela = Gdx.graphics.getHeight();
        float inicioCanvasX = larguraTela * PROPORCAO_PAINEL;

        float x = MathUtils.random(inicioCanvasX + 60, larguraTela - 60);
        float y = MathUtils.random(60, alturaTela - 60);
        return new Vector2(x, y);
    }

    public void render(float delta) {
        stage.act(delta);
        stage.draw();
        desenharVolumes();
    }

    private void desenharVolumes() {
        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Volume v : volumes) {
            v.render(shapeRenderer); // chamada polimórfica: AABB, Circulo ou OBB
        }
        shapeRenderer.end();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        ajustarProporcoes(width);
    }

    protected void ajustarProporcoes(int largura) {
        layoutRaiz.getCells().get(0).width(largura * PROPORCAO_PAINEL);
        layoutRaiz.getCells().get(1).width(largura * (1 - PROPORCAO_PAINEL));
        layoutRaiz.invalidate();
    }

    private Drawable criarFundoCanvas() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0.95f, 0.95f, 0.97f, 1f);
        pixmap.fill();
        Texture tex = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(tex);
    }

    public void dispose() {
        stage.dispose();
        shapeRenderer.dispose();
    }
}