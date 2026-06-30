# Especificação Arquitetural — GeomLab 2D Studio

## 1. Visão Geral

O sistema segue uma arquitetura de **Composição de Tela** típica do LibGDX, separando claramente:

- **Camada de Apresentação** — `GeomLab2DStudioApp` (ciclo de vida da aplicação)
- **Camada de Cena** — `Cena` (orquestra o estado do mundo geométrico, seleção, arraste e limites de canvas)
- **Camada de UI** — `PainelInspetor` (VisUI / Scene2D)
- **Camada de Domínio Geométrico** (pacote `com.geomlab.studio.geometry`) — `Volume` (abstração polimórfica), `AABB`, `Circulo`, `OBB`
- **Camada de Contrato** — `Colidivel` (interface)
- **Camada Utilitária** — `GeometriaUtils` (funções matemáticas puras, *stateless*)

> **Decisão de arquitetura:** o projeto não utiliza motores de física (ex: Box2D). Usa apenas as primitivas matemáticas do `com.badlogic.gdx.math` (`Vector2`, `MathUtils`) como base aritmética, implementando seus próprios algoritmos de detecção de colisão em `GeometriaUtils`. Isso preserva a clareza didática do polimorfismo via `Colidivel` e a genuinidade da classe utilitária exigida pelo projeto.

> **Decisão de renderização:** `Volume.render()` usa `ShapeRenderer` (geometria vetorial), não `SpriteBatch` (sprites/texturas). O desenho é feito em **duas passadas por frame**: `render()` (preenchimento translúcido) e `renderBorda()` (halo e contorno sólido, desenhado por cima de todos os preenchimentos) — isso evita que formas sobrepostas se ocultem totalmente umas às outras.

---

## 2. Justificativa do Polimorfismo (`Volume`)

A superclasse abstrata `Volume` permite que `Cena` mantenha uma única coleção `List<Volume>` contendo `AABB`, `Circulo` e `OBB` simultaneamente, sem precisar de `if/else instanceof` espalhado pelo código. Cinco métodos são chamados dinamicamente (*dynamic dispatch*): `render(ShapeRenderer)`, `renderHalo(ShapeRenderer)`, `renderBorda(ShapeRenderer)`, `colidirCom(Colidivel)` e `getRaioEnvolvente()` — cada subclasse decide como se desenha, como testa colisão e qual seu alcance espacial máximo, mas a `Cena` chama todos de forma uniforme:

```java
for (Volume v : volumes) v.render(shapeRenderer);
```

Isso aplica o **Princípio Aberto/Fechado**: novas formas (ex: `Poligono`) podem ser adicionadas sem alterar `Cena`.

---

## 3. Diagrama de Classes UML

```mermaid
classDiagram
    direction LR

    class GeomLab2DStudioApp {
        -Cena cena
        +void create()
        +void render()
        #void dispose()
    }

    class Cena {
        -List~Volume~ volumes
        -PainelInspetor painel
        -Volume selecionado
        -boolean arrastando
        -float limitePainel
        +void adicionarVolume(Volume v)
        +void removerSelecionado()
        +void render(float delta)
        +float getTamanhoMaximoPermitido()
        +void notificarMudancaTamanho(Volume v)
        #void verificarColisoes()
        -Vector2 limitarAoCanvas(Vector2 pos, float raio)
    }

    class PainelInspetor {
        -VisTable raiz
        -Cena cena
        -Volume volumeAtual
        +void construirUI()
        +void selecionar(Volume v)
        +void atualizarPosicao(Volume v)
        -void addCampoTamanho(...)
    }

    class Colidivel {
        <<interface>>
        +boolean colidirCom(Colidivel outro)
        +Vector2 getCentro()
    }

    class Volume {
        <<abstract>>
        #Vector2 posicao
        #Color corBorda
        #Color corPreenchimento
        #boolean emColisao
        #static int totalCriados
        #final int idCriacao
        +Volume(Vector2 pos, Color corBase)
        +void render(ShapeRenderer r)*
        +void renderHalo(ShapeRenderer)*
        +void renderBorda(ShapeRenderer r)*
        +boolean contemPonto(Vector2 p)*
        +boolean colidirCom(Colidivel outro)*
        +float getRaioEnvolvente()*
        +static int getTotalCriados()
        #void atualizarLimites()
    }

    class AABB {
        -float largura
        -float altura
        +void render(ShapeRenderer r)
        +void renderHalo(ShapeRenderer r)
        +void renderBorda(ShapeRenderer r)
        +boolean contemPonto(Vector2 p)
        +boolean colidirCom(Colidivel outro)
        +float getRaioEnvolvente()
    }

    class Circulo {
        -float raio
        +void render(ShapeRenderer r)
        +void renderHalo(ShapeRenderer r)
        +void renderBorda(ShapeRenderer r)
        +boolean contemPonto(Vector2 p)
        +boolean colidirCom(Colidivel outro)
        +float getRaioEnvolvente()
    }

    class OBB {
        -float largura
        -float altura
        -float anguloRotacao
        +void render(ShapeRenderer r)
        +void renderHalo(ShapeRenderer r)
        +void renderBorda(ShapeRenderer r)
        +boolean contemPonto(Vector2 p)
        +boolean colidirCom(Colidivel outro)
        +float getRaioEnvolvente()
        +Vector2[] obterVertices()
        +Vector2[] obterEixos()
    }

    class GeometriaUtils {
        -static float EPSILON
        +static boolean intersectaCirculoVsCirculo(Circulo a, Circulo b)
        +static boolean intersectaAABBvsAABB(AABB a, AABB b)
        +static boolean intersectaAABBvsCirculo(AABB caixa, Circulo c)
        +static boolean intersectaOBBvsOBB(OBB a, OBB b)
        +static boolean intersectaOBBvsAABB(OBB obb, AABB caixa)
        +static boolean intersectaOBBvsCirculo(OBB obb, Circulo c)
    }

    GeomLab2DStudioApp *-- Cena : Composição
    PainelInspetor --> Cena : Associação Simples
    Cena o-- Volume : Agregação
    Volume ..> GeometriaUtils : Dependência
    Volume ..|> Colidivel : Realização (Interface)
    AABB --|> Volume : Herança
    Circulo --|> Volume : Herança
    OBB --|> Volume : Herança
```

---

## 4. Chamadas Polimórficas no Ciclo de Vida do `render()`

Dentro de `Cena.render(float delta)`:

```java
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
            if (a.colidirCom(b)) {  // (3) Despacho dinâmico via interface Colidivel
                a.setEmColisao(true);
                b.setEmColisao(true);
            }
        }
    }
}
```

Uma quinta chamada dinâmico ocorre fora do ciclo de render, mas igualmente relevante para a rubrica de polimorfismo, em `Cena.limitarAoCanvas`:

```java
Vector2 limitado = limitarAoCanvas(mundo, selecionado.getRaioEnvolvente());
```

Cada uma dessas cinco chamadas é resolvida **em tempo de execução**, conforme o tipo concreto real do objeto (`AABB`, `Circulo` ou `OBB`) — sem nenhum `instanceof` no lado de `Cena` para decidir o comportamento.

---

## 5. Motor de Colisão — 6 Pares Implementados em `GeometriaUtils`

| Par | Técnica | Resumo |
|---|---|---|
| Círculo × Círculo | Distância ao quadrado | `distância² ≤ (somaRaios)²` |
| AABB × AABB | Sobreposição de intervalos | Colide se há overlap simultâneo nos eixos X e Y |
| AABB × Círculo | Clamping | Centro do círculo é restringido aos limites do AABB; testa distância do ponto resultante ao raio |
| OBB × OBB | SAT (Separating Axis Theorem) | Projeta vértices nos 4 eixos (2 de cada OBB); se algum eixo separa as sombras, não colide |
| OBB × AABB | SAT | Mesma lógica, tratando o AABB como um "OBB" de eixos fixos (1,0) e (0,1) |
| OBB × Círculo | Clamping no espaço local | Centro do círculo é rotacionado para o referencial local do OBB, então clampado como um AABB comum |

---

## 6. Matriz de Auditoria POO

| # | Regra | Classe(s) Exata(s) no Diagrama | Evidência |
|---|---|---|---|
| 1 | ≥ 7 classes no ecossistema | `GeomLab2DStudioApp`, `Cena`, `PainelInspetor`, `Colidivel`, `Volume`, `AABB`, `Circulo`, `OBB`, `GeometriaUtils` | 9 classes/tipos declarados |
| 2 | Superclasse abstrata + 3 heranças genuínas | `Volume` ← `AABB`, `Circulo`, `OBB` | Setas `--\|>` no diagrama |
| 3a | Associação Simples | `PainelInspetor --> Cena` | `PainelInspetor` chama métodos de `Cena` sem possuí-la |
| 3b | Dependência | `Volume ..> GeometriaUtils` | `AABB`/`Circulo`/`OBB.colidirCom()` usam métodos utilitários sem manter referência |
| 3c | Agregação | `Cena o-- Volume` | `Cena` mantém `List<Volume>`, mas os volumes podem existir independentemente |
| 3d | Composição | `GeomLab2DStudioApp *-- Cena` | Ciclo de vida da `Cena` é totalmente dependente de `GeomLab2DStudioApp` |
| 4a | Interface (abstração 1) | `Colidivel` | `<<interface>>` com `colidirCom()` e `getCentro()` |
| 4b | Classe Abstrata (abstração 2) | `Volume` | `<<abstract>>` com `render`, `renderBorda`, `contemPonto`, `colidirCom`, `getRaioEnvolvente` marcados `*` |
| 5 | 4 chamadas polimórficas no render() | `Cena` (métodos `desenhar` e `verificarColisoes`) | `v.render(sr)`, `v.renderHalo(sr)`, `v.renderBorda(sr)`, `a.colidirCom(b)` (um 5º chamada extra, `getRaioEnvolvente()`, ocorre fora do render, em `limitarAoCanvas`) |
| 6 | Modificadores `+` / `#` / `-` | `Volume` (`#posicao`, `#emColisao`), `GeomLab2DStudioApp` (`#dispose`), `Volume` (`+render`), `AABB` (`-largura`) | Presentes em todas as classes do domínio |
| 7 | 1 atributo estático + 1 método estático | `GeometriaUtils` | `-static float EPSILON` / 6 métodos `+static boolean intersecta...(...)`; reforçado por `Volume.totalCriados`/`getTotalCriados()` |

---

## 7. Ambiente de Desenvolvimento

| Item | Configuração |
|---|---|
| Ferramenta de setup | LibGDX Setup UI (`gdx-setup.jar`) |
| Package raiz | `com.geomlab.studio` |
| Pacote do domínio geométrico | `com.geomlab.studio.geometry` |
| Game class | `GeomLab2DStudioApp` |
| Sub-projetos | `core` + `lwjgl3` (Desktop) |
| Extensão de terceiros | VisUI (Kotcrab) — `com.kotcrab.vis:vis-ui:1.5.3` |
| IDE | Eclipse (via plugin Buildship/Gradle) |
