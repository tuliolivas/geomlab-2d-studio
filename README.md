# GeomLab 2D Studio

> Software educacional desktop para visualização, manipulação e teste de colisão entre primitivas geométricas 2D (AABB, Círculo e OBB), desenvolvido como projeto prático de **Programação Orientada a Objetos**.

![Status](https://img.shields.io/badge/status-concluído-brightgreen)
![Java](https://img.shields.io/badge/Java-8%2B-orange)
![LibGDX](https://img.shields.io/badge/LibGDX-LWJGL3-blue)
![License](https://img.shields.io/badge/license-MIT-green)

---

## 📖 Sobre o Projeto

**GeomLab 2D Studio** é uma aplicação visual interativa onde o usuário cria, seleciona, edita, arrasta e remove formas geométricas (AABB, Círculo, OBB) através de um painel inspetor, observando em tempo real seu comportamento e colisão em um canvas cartesiano.

O projeto foi concebido como exercício prático de **Orientação a Objetos**, aplicando seus quatro pilares — Abstração, Encapsulamento, Herança e Polimorfismo — além das quatro relações estruturais entre classes (Associação, Agregação, Composição e Dependência).

A especificação completa da arquitetura e o diagrama de classes estão em [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md).

---

## Interface

A tela principal é dividida em duas regiões:

| Região | Proporção | Função |
|---|---|---|
| **Painel Inspetor** | 30% (esquerda) | Criação de formas, edição de propriedades, remoção e status da seleção atual |
| **Canvas Cartesiano** | 70% (direita) | Visualização, seleção, arraste e teste de colisão entre as formas |

### Funcionalidades principais

- **Criação** de AABB, Círculo e OBB com tamanho/ângulo aleatórios, via botões do Inspetor
- **Seleção persistente**: clicar numa forma a mantém selecionada até outra ação acontecer (clicar em outra forma ou em área vazia)
- **Desambiguação de sobreposição**: quando várias formas ocupam o mesmo ponto, **Ctrl+clique** alterna para a mais antiga entre as candidatas; clique simples nessa região limpa a seleção
- **Edição de propriedades em tempo real**: largura, altura, raio e ângulo são editáveis diretamente no painel, com efeito imediato no canvas
- **Arraste** livre da forma selecionada, com indicador visual (anel preto) de qual forma está ativa
- **Remoção** da forma selecionada via botão dedicado
- **Limite de canvas**: nenhuma forma pode ser arrastada, redimensionada ou criada além dos limites visíveis do canvas — inclusive ao redimensionar a janela
- **Detecção de colisão** entre todos os pares de formas presentes na cena, com feedback visual instantâneo (cor de alerta), suportando colisões simultâneas entre 3 ou mais formas

---

## Stack Tecnológica

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 8+ |
| Framework de aplicação/renderização | [LibGDX](https://libgdx.com/) (backend **LWJGL3**) |
| Biblioteca de UI | [VisUI](https://github.com/kotcrab/vis-ui) (Scene2D.UI) |
| Matemática vetorial | `com.badlogic.gdx.math` (Vector2, MathUtils) |
| Build | Gradle |
| IDE de referência | Eclipse (via *Buildship*) |

> **Decisão de arquitetura:** o projeto **não utiliza motores de física** (ex: Box2D). A detecção de colisão é implementada de forma própria na classe utilitária `GeometriaUtils`, preservando o valor didático do polimorfismo via interface `Colidivel`.

---

## Estrutura do Projeto

```
geomlab-2d-studio/
├── assets/
├── core/
│   └── src/main/java/com/geomlab/studio/
│       ├── GeomLab2DStudioApp.java
│       ├── scene/
│       │   └── Cena.java
│       ├── ui/
│       │   └── PainelInspetor.java
│       └── geometry/
│           ├── Volume.java          # classe abstrata: render, renderBorda, contemPonto,
│           │                        # colidirCom, getRaioEnvolvente + estado emColisao/idCriacao
│           ├── AABB.java
│           ├── Circulo.java
│           ├── OBB.java
│           ├── Colidivel.java       # interface
│           └── GeometriaUtils.java  # SAT, clamping, atributo/método estático
├── lwjgl3/
│   └── src/main/java/com/geomlab/studio/lwjgl3/
│       └── Lwjgl3Launcher.java
├── docs/
│   └── ARCHITECTURE.md
└── build.gradle
```

---

## Como Executar

### Pré-requisitos
- JDK 8 ou superior
- Eclipse com plugin **Buildship** (Gradle) instalado
- Conexão com a internet na primeira sincronização do Gradle

### Passos

1. Clone o repositório:
   ```bash
   git clone https://github.com/<seu-usuario>/geomlab-2d-studio.git
   ```
2. No Eclipse: `File > Import > Gradle > Existing Gradle Project` e selecione a pasta clonada.
3. Aguarde a sincronização do Gradle finalizar.
4. Execute a classe `Lwjgl3Launcher` (`Run As > Java Application`), localizada no módulo `lwjgl3`.

---

## Conceitos de POO Demonstrados

- **Abstração**: classe abstrata `Volume` e interface `Colidivel`
- **Encapsulamento**: uso correto dos modificadores `+ public`, `# protected`, `- private`; estado de arraste, seleção e colisão (`emColisao`) totalmente privados/protegidos
- **Herança**: `AABB`, `Circulo` e `OBB` especializando `Volume`
- **Polimorfismo**: 4 despachos dinâmicos no ciclo de `Cena` — `v.render(renderer)`, `v.renderBorda(renderer)`, `a.colidirCom(b)` e `v.getRaioEnvolvente()` — cada um resolvido em tempo de execução conforme o tipo concreto real do objeto
- **Relações estruturais**: Composição (`App *-- Cena`), Agregação (`Cena o-- Volume`), Associação (`PainelInspetor --> Cena`) e Dependência (`Volume ..> GeometriaUtils`)
- **Atributo e método estático**: `GeometriaUtils.EPSILON` (privado) e os 6 métodos de interseção; reforçado também por `Volume.totalCriados`/`getTotalCriados()`, usado como base do `idCriacao` imutável de cada forma

### Destaques do Motor de Colisão

- **6 pares de colisão** implementados cobrindo todas as combinações entre AABB, Círculo e OBB
- **SAT (Separating Axis Theorem)** para os pares que envolvem rotação (OBB×OBB, OBB×AABB)
- **Clamping** para os pares com Círculo (AABB×Círculo, OBB×Círculo — este último calculado no espaço local do OBB, após desfazer a rotação)
- **Estado de colisão por forma** (`emColisao`, booleano): suporta qualquer quantidade de colisões simultâneas (3+ formas sobrepostas), com feedback visual instantâneo (cor de alerta)

---

## Autores

Desenvolvido por **Túlio Vasconcelos** e **Lucas Ferreira** como projeto prático de estudo em Programação Orientada a Objetos.

## 📄 Licença

Este projeto é distribuído sob a licença MIT — veja o arquivo `LICENSE` para detalhes.
