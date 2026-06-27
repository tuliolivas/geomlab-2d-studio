package com.geomlab.studio.ui;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.geomlab.studio.geometry.AABB;
import com.geomlab.studio.geometry.Circulo;
import com.geomlab.studio.geometry.OBB;
import com.geomlab.studio.geometry.Volume;
import com.geomlab.studio.scene.Cena;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class PainelInspetor {

    private VisTable raiz;
    private VisLabel labelStatus;
    private final Cena cena;

    private static final String SEM_SELECAO = "Nenhuma forma selecionada";

    public PainelInspetor(Cena cena) {
        this.cena = cena;
        construirUI();
    }

    private void construirUI() {
        raiz = new VisTable();
        raiz.setBackground("window-bg");
        raiz.top().pad(10);

        raiz.add(new VisLabel("Inspetor de Volumes")).padBottom(15).row();

        VisTextButton btnAABB = new VisTextButton("+ Adicionar AABB");
        VisTextButton btnCirculo = new VisTextButton("+ Adicionar Círculo");
        VisTextButton btnOBB = new VisTextButton("+ Adicionar OBB");

        btnAABB.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                aoClicarAdicionarAABB();
            }
        });
        btnCirculo.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                aoClicarAdicionarCirculo();
            }
        });
        btnOBB.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                aoClicarAdicionarOBB();
            }
        });

        raiz.add(btnAABB).width(220).padBottom(8).row();
        raiz.add(btnCirculo).width(220).padBottom(8).row();
        raiz.add(btnOBB).width(220).padBottom(20).row();

        labelStatus = new VisLabel(SEM_SELECAO);
        labelStatus.setWrap(true);
        raiz.add(labelStatus).width(220).row();
    }

    /** Chamado por Cena a cada seleção/arraste/soltura — mantém o painel sincronizado. */
    public void atualizarStatus(Volume v) {
        if (v == null) {
            labelStatus.setText(SEM_SELECAO);
        } else {
            labelStatus.setText(String.format(
                "Selecionado: %s%nPos: (%.0f, %.0f)",
                v.getClass().getSimpleName(),
                v.getPosicao().x,
                v.getPosicao().y
            ));
        }
    }

    private void aoClicarAdicionarAABB() {
        Vector2 pos = cena.gerarPosicaoAleatoriaNoCanvas();
        float largura = MathUtils.random(40, 100);
        float altura = MathUtils.random(40, 100);
        cena.adicionarVolume(new AABB(pos, largura, altura));
    }

    private void aoClicarAdicionarCirculo() {
        Vector2 pos = cena.gerarPosicaoAleatoriaNoCanvas();
        float raio = MathUtils.random(25, 55);
        cena.adicionarVolume(new Circulo(pos, raio));
    }

    private void aoClicarAdicionarOBB() {
        Vector2 pos = cena.gerarPosicaoAleatoriaNoCanvas();
        float largura = MathUtils.random(50, 100);
        float altura = MathUtils.random(30, 70);
        float angulo = MathUtils.random(0, 360);
        cena.adicionarVolume(new OBB(pos, largura, altura, angulo));
    }

    public Table getRaiz() {
        return raiz;
    }
}