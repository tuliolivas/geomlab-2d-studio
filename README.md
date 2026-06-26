# 🧪 GeomLab 2D Studio

> Software educacional desktop para visualização, manipulação e teste de colisão entre primitivas geométricas 2D (AABB, Círculo e OBB), desenvolvido como projeto prático de **Programação Orientada a Objetos**.

![Status](https://img.shields.io/badge/status-em%20desenvolvimento-yellow)
![Java](https://img.shields.io/badge/Java-8%2B-orange)
![LibGDX](https://img.shields.io/badge/LibGDX-LWJGL3-blue)
![License](https://img.shields.io/badge/license-MIT-green)

---

## 📖 Sobre o Projeto

**GeomLab 2D Studio** é um laboratório visual interativo onde o usuário configura nuvens de pontos e formas geométricas envoltórias (*bounding volumes*) através de um painel inspetor, observando em tempo real seu comportamento e colisão em um canvas cartesiano.

O projeto foi concebido como exercício prático de **Orientação a Objetos**, aplicando seus quatro pilares (Abstração, Encapsulamento, Herança e Polimorfismo), além das quatro relações estruturais entre classes (Associação, Agregação, Composição e Dependência).

📐 A especificação completa da arquitetura e o diagrama de classes estão em [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md).

---

## 🖥️ Interface

A tela principal é dividida em duas regiões:

| Região | Proporção | Função |
|---|---|---|
| **Painel Inspetor** | 30% (esquerda) | Configuração de nuvens de pontos e criação de volumes (AABB, Círculo, OBB) |
| **Canvas Cartesiano** | 70% (direita) | Visualização, manipulação e teste de colisão entre as formas |

---

## 🛠️ Stack Tecnológica

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 8+ |
| Framework de aplicação/renderização | [LibGDX](https://libgdx.com/) (backend **LWJGL3**) |
| Biblioteca de UI | [VisUI](https://github.com/kotcrab/vis-ui) (Scene2D.UI) |
| Matemática vetorial | `com.badlogic.gdx.math` (Vector2, MathUtils) |
| Build | Gradle |
| IDE de referência | Eclipse (via *Buildship*) |

> ⚠️ **Decisão de arquitetura:** o projeto **não utiliza motores de física** (ex: Box2D). A detecção de colisão é implementada de forma própria na classe utilitária `GeometriaUtils`, preservando o valor didático do polimorfismo via interface `Colidivel`.

---

## 📦 Estrutura do Projeto

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
│       ├── geometria/          # (a partir da Etapa 2)
│       │   ├── Volume.java
│       │   ├── AABB.java
│       │   ├── Circulo.java
│       │   ├── OBB.java
│       │   └── Colidivel.java
│       └── util/               # (a partir da Etapa 4)
│           └── GeometriaUtils.java
├── lwjgl3/
│   └── src/main/java/com/geomlab/studio/lwjgl3/
│       └── Lwjgl3Launcher.java
├── docs/
│   └── ARCHITECTURE.md
└── build.gradle
```

---

## ▶️ Como Executar

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

## 🗺️ Roadmap de Desenvolvimento

O desenvolvimento é dividido em 5 etapas incrementais, cada uma testável e apresentável isoladamente:

| Etapa | Nome | Status |
|---|---|---|
| 1 | Fundação e Esqueleto Visual | ✅ Concluída |
| 2 | Domínio Geométrico (Volume, AABB, Círculo, OBB) | ✅ Concluída |
| 3 | Interatividade e Manipulação | ⏳ Planejada |
| 4 | Motor de Colisão (GeometriaUtils + Colidivel) | ⏳ Planejada |
| 5 | Polimento e Persistência | ⏳ Planejada |

Detalhes de cada etapa estão documentados em [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md).

---

## 🎯 Conceitos de POO Demonstrados

- **Abstração**: classe abstrata `Volume` e interface `Colidivel`
- **Encapsulamento**: uso correto dos modificadores `+ public`, `# protected`, `- private`
- **Herança**: `AABB`, `Circulo` e `OBB` especializando `Volume`
- **Polimorfismo**: despacho dinâmico de `render()` e `colidirCom()` no laço principal da `Cena`
- **Relações estruturais**: Composição (`App *-- Cena`), Agregação (`Cena o-- Volume`), Associação (`PainelInspetor --> Cena`) e Dependência (`Volume ..> GeometriaUtils`)

---

## 📄 Licença

Este projeto é distribuído sob a licença MIT — veja o arquivo `LICENSE` para detalhes.

---

## ✍️ Autor

Desenvolvido por **Túlio... e Lucas...** como projeto prático de estudo em Programação Orientada a Objetos.
