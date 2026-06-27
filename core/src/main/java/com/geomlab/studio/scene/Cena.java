package com.geomlab.studio.scene;

import com.badlogic.gdx.Gdx;
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
    private Table canvasPlaceholder;
    private ShapeRenderer shapeRenderer;

    private List<Volume> volumes;

    // Estado de arraste — inteiramente privado, encapsulado dentro de Cena
    private Volume volumeSelecionado;
    private boolean arrastando;
    private float inicioCanvasX;

    private static final float PROPORCAO_PAINEL = 0.30f;

    public void construir() {
        this.stage = new Stage(new ScreenViewport());
        this.volumes = new ArrayList<>();
        this.shapeRenderer = new ShapeRenderer();
        this.painel = new PainelInspetor(this);

        this.canvasPlaceholder = new Table();
        this.canvasPlaceholder.setBackground(criarFundoCanvas());

        this.layoutRaiz = new Table();
        layoutRaiz.setFillParent(true);

        layoutRaiz.add(painel.getRaiz()).width(Gdx.graphics.getWidth() * PROPORCAO_PAINEL).growY();
        layoutRaiz.add(canvasPlaceholder).width(Gdx.graphics.getWidth() * (1 - PROPORCAO_PAINEL)).growY();

        stage.addActor(layoutRaiz);
        inicioCanvasX = Gdx.graphics.getWidth() * PROPORCAO_PAINEL;

        // Multiplexer: a Stage (botões VisUI) tem prioridade; se ela não
        // consumir o evento, o ouvinte do canvas tenta tratar o arraste.
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(criarOuvinteCanvas());
        Gdx.input.setInputProcessor(multiplexer);
        
    }

    public void adicionarVolume(Volume v) {
        volumes.add(v);
    }

    public Vector2 gerarPosicaoAleatoriaNoCanvas() {
        float larguraTela = Gdx.graphics.getWidth();
        float alturaTela = Gdx.graphics.getHeight();

        float x = MathUtils.random(inicioCanvasX + 60, larguraTela - 60);
        float y = MathUtils.random(60, alturaTela - 60);
        return new Vector2(x, y);
    }

    public void render(float delta) {
        stage.act(delta);
        stage.draw();
        desenharVolumes();
        verificarColisoes();
    }
    
    /**
     * Verifica colisão entre todos os pares de volumes da cena.
     * Cada volume tem seu estado emColisao resetado e recalculado
     * a cada frame, suportando qualquer quantidade de colisões
     * simultâneas (ex: 3+ formas sobrepostas ao mesmo tempo).
     */
    protected void verificarColisoes() {
        for (Volume v : volumes) {
            v.setEmColisao(false);
        }

        for (int i = 0; i < volumes.size(); i++) {
            for (int j = i + 1; j < volumes.size(); j++) {
                Volume a = volumes.get(i);
                Volume b = volumes.get(j);
                if (a.colidirCom(b)) {        // (2) chamada polimórfica
                    a.setEmColisao(true);
                    b.setEmColisao(true);
                }
            }
        }
    }

    private void desenharVolumes() {
        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);

        // ---- AQUI ----
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Passada 1: preenchimentos translúcidos
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Volume v : volumes) {
            v.render(shapeRenderer);
        }
        shapeRenderer.end();

        // Passada 2: bordas sólidas, desenhadas por cima de tudo
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (Volume v : volumes) {
            v.renderBorda(shapeRenderer);
        }
        shapeRenderer.end();
    }

    // ---------- Lógica de arraste (privada) ----------

    private InputAdapter criarOuvinteCanvas() {
        return new InputAdapter() {
        	@Override
        	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        	    Vector2 mundo = converterParaCoordenadasDoMundo(screenX, screenY);
        	    if (mundo.x < inicioCanvasX) {
        	        return false;
        	    }
        	    Volume clicado = encontrarVolumeNoPonto(mundo);
        	    if (clicado != null) {
        	        trazerParaFrente(clicado);   // <-- novo
        	        volumeSelecionado = clicado;
        	        arrastando = true;
        	        painel.atualizarStatus(volumeSelecionado);
        	        return true;
        	    }
        	    return false;
        	}

        	/** Move o volume para o final da lista, fazendo-o desenhar por último (primeiro plano). */
        	private void trazerParaFrente(Volume v) {
        	    volumes.remove(v);
        	    volumes.add(v);
        	}

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (arrastando && volumeSelecionado != null) {
                    Vector2 mundo = converterParaCoordenadasDoMundo(screenX, screenY);
                    volumeSelecionado.setPosicao(mundo);
                    painel.atualizarStatus(volumeSelecionado);
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                boolean estavaArrastando = arrastando;
                arrastando = false;
                volumeSelecionado = null;
                if (estavaArrastando) {
                    painel.atualizarStatus(null);
                    return true;
                }
                return false;
            }
        };
    }

    private Vector2 converterParaCoordenadasDoMundo(int screenX, int screenY) {
        Vector3 coordenada = new Vector3(screenX, screenY, 0);
        stage.getCamera().unproject(coordenada);
        return new Vector2(coordenada.x, coordenada.y);
    }

    private Volume encontrarVolumeNoPonto(Vector2 ponto) {
        // Percorre do mais recente para o mais antigo: forma desenhada por
        // cima é selecionada primeiro em caso de sobreposição.
        for (int i = volumes.size() - 1; i >= 0; i--) {
            Volume v = volumes.get(i);
            if (v.contemPonto(ponto)) { // chamada polimórfica
                return v;
            }
        }
        return null;
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        ajustarProporcoes(width);
    }

    protected void ajustarProporcoes(int largura) {
        layoutRaiz.getCells().get(0).width(largura * PROPORCAO_PAINEL);
        layoutRaiz.getCells().get(1).width(largura * (1 - PROPORCAO_PAINEL));
        layoutRaiz.invalidate();
        inicioCanvasX = largura * PROPORCAO_PAINEL;
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