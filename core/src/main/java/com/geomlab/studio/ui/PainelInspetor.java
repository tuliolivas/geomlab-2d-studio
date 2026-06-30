package com.geomlab.studio.ui;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.geomlab.studio.geometry.AABB;
import com.geomlab.studio.geometry.Circulo;
import com.geomlab.studio.geometry.OBB;
import com.geomlab.studio.geometry.Volume;
import com.geomlab.studio.scene.Cena;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;

import java.util.function.Consumer;

public class PainelInspetor {

    private VisTable raiz;
    private VisLabel status;
    private VisLabel totalLabel;
    private VisTable areaPropriedades;
    private Cena cena;
    private Volume volumeAtual; // forma sendo editada agora, usada pra reposicionar apos mudar tamanho

    private static final float TAMANHO_MINIMO = 10f;

    public PainelInspetor(Cena cena) {
        this.cena = cena;
        montar();
    }

    private void montar() {
        raiz = new VisTable();
        raiz.setBackground("window-bg");
        raiz.top().pad(10);

        raiz.add(new VisLabel("Inspetor de Volumes")).padBottom(15).row();

        VisTextButton bAABB = new VisTextButton("+ AABB");
        bAABB.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) {
                Vector2 p = cena.gerarPosicaoAleatoriaNoCanvas();
                cena.adicionarVolume(new AABB(p, MathUtils.random(40, 100), MathUtils.random(40, 100)));
            }
        });

        VisTextButton bCirculo = new VisTextButton("+ Circulo");
        bCirculo.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) {
                Vector2 p = cena.gerarPosicaoAleatoriaNoCanvas();
                cena.adicionarVolume(new Circulo(p, MathUtils.random(25, 55)));
            }
        });

        VisTextButton bOBB = new VisTextButton("+ OBB");
        bOBB.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) {
                Vector2 p = cena.gerarPosicaoAleatoriaNoCanvas();
                cena.adicionarVolume(new OBB(p, MathUtils.random(50, 100), MathUtils.random(30, 70), MathUtils.random(0, 360)));
            }
        });

        raiz.add(bAABB).width(220).padBottom(8).row();
        raiz.add(bCirculo).width(220).padBottom(8).row();
        raiz.add(bOBB).width(220).padBottom(15).row();

        VisTextButton bDeletar = new VisTextButton("Remover Selecionado");
        bDeletar.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) {
                cena.removerSelecionado();
            }
        });
        raiz.add(bDeletar).width(220).padBottom(15).row();

        totalLabel = new VisLabel("Formas criadas: 0");
        raiz.add(totalLabel).width(220).padBottom(15).row();

        status = new VisLabel("Nenhuma forma selecionada");
        status.setWrap(true);
        raiz.add(status).width(220).padBottom(10).row();

        areaPropriedades = new VisTable();
        raiz.add(areaPropriedades).width(220).row();
    }

    public void selecionar(Volume v) {
        totalLabel.setText("Formas criadas: " + Volume.getTotalCriados());
        areaPropriedades.clear();
        volumeAtual = v;

        if (v == null) {
            status.setText("Nenhuma forma selecionada");
            return;
        }

        atualizarPosicao(v);

        if (v instanceof AABB) {
            AABB a = (AABB) v;
            addCampoTamanho("Largura", a.getLargura(), a::setLargura);
            addCampoTamanho("Altura", a.getAltura(), a::setAltura);
        } else if (v instanceof Circulo) {
            Circulo c = (Circulo) v;
            addCampoTamanho("Raio", c.getRaio(), c::setRaio);
        } else if (v instanceof OBB) {
            OBB o = (OBB) v;
            addCampoTamanho("Largura", o.getLargura(), o::setLargura);
            addCampoTamanho("Altura", o.getAltura(), o::setAltura);
            addCampoAngulo("Angulo", o.getAnguloRotacao(), o::setAnguloRotacao);
        }
    }

    public void atualizarPosicao(Volume v) {
        if (v == null) return;
        status.setText("Selecionado: " + v.getClass().getSimpleName()
            + "\nPos: (" + (int) v.getPosicao().x + ", " + (int) v.getPosicao().y + ")");
    }

    /** Campo de largura/altura/raio -- limitado ao tamanho maximo que cabe no canvas. */
    private void addCampoTamanho(String nome, float valorInicial, Consumer<Float> aoMudar) {
        VisTable linha = new VisTable();
        linha.add(new VisLabel(nome)).width(70).left();

        VisTextField campo = new VisTextField(String.valueOf((int) valorInicial));
        campo.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    float valor = Float.parseFloat(campo.getText());
                    float max = cena.getTamanhoMaximoPermitido();
                    valor = MathUtils.clamp(valor, TAMANHO_MINIMO, max);
                    aoMudar.accept(valor);
                    cena.notificarMudancaTamanho(volumeAtual);
                } catch (NumberFormatException ignored) {
                    // usuario ainda digitando, ignora por enquanto
                }
            }
        });

        linha.add(campo).width(130);
        areaPropriedades.add(linha).padBottom(6).row();
    }

    /** Campo de angulo -- nao tem limite de tamanho, so normaliza 0-360. */
    private void addCampoAngulo(String nome, float valorInicial, Consumer<Float> aoMudar) {
        VisTable linha = new VisTable();
        linha.add(new VisLabel(nome)).width(70).left();

        VisTextField campo = new VisTextField(String.valueOf((int) valorInicial));
        campo.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    aoMudar.accept(Float.parseFloat(campo.getText()));
                } catch (NumberFormatException ignored) {
                }
            }
        });

        linha.add(campo).width(130);
        areaPropriedades.add(linha).padBottom(6).row();
    }

    public Table getRaiz() {
        return raiz;
    }
}