package com.geomlab.studio.ui;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.geomlab.studio.geometry.AABB;
import com.geomlab.studio.geometry.Circulo;
import com.geomlab.studio.geometry.OBB;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.geomlab.studio.scene.Cena;

/**
 * Painel lateral (30%). Associação Simples: mantém referência
 * à Cena para delegar a criação de novos Volumes.
 */
public class PainelInspetor {

    private VisTable raiz;
    private final Cena cena; // Associação: Painel "usa" Cena, mas não a possui

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
        raiz.add(btnOBB).width(220).padBottom(8).row();
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