# Project: english-for-it-engineers

## Overview
ITエンジニア向け英語学習プロジェクト。
公式リファレンス・GitHub・Claude指示文で使われる英語の読み書きスキルを体系的に習得する。

## 学習スコープ
1. **公式リファレンス英語** - API docs, README, CONTRIBUTING.md 等の読解
2. **GitHub英語** - Issues, PR, コードレビュー、commit message の読み書き
3. **Claude/LLM指示英語** - プロンプト、CLAUDE.md、system instruction の作成
4. **技術文書英語** - 設計書、ADR、RFC の読み書き

## 学習目標

現在の英語力: CEFR A2（初級）

### Phase 1: A2 → B1（読む力中心）

| 領域 | 目標 | 達成イメージ |
|------|------|-------------|
| 公式リファレンス英語 | B1 Reading | Next.js や React のドキュメントを、辞書なしで概要を掴める |
| GitHub英語 | B1 Reading | Issue/PRのやりとりを読んで、論点と結論を理解できる |
| Claude/LLM指示英語 | B1 Reading | CLAUDE.md や system instruction の構造と意図を理解できる |
| 技術文書英語 | ― | Phase 1ではスコープ外 |

### Phase 2: B1 → B2（書く力へ拡張）

| 領域 | 目標 | 達成イメージ |
|------|------|-------------|
| 公式リファレンス英語 | B2 Reading | 複雑なAPIリファレンスの条件分岐や注意書きまで正確に読める |
| GitHub英語 | B2 Reading+Writing | Issue報告やPRレビューコメントを英語で書ける |
| Claude/LLM指示英語 | B2 Writing | CLAUDE.md やプロンプトを英語で設計・記述できる |
| 技術文書英語 | B2 Reading+Writing | ADR/RFCを読み、簡潔な技術文書を英語で書ける |

## プロジェクト方針
- 武田塾メソッド「①単語・熟語・文法 → ②英文解釈 → ③長文読解」の段階的学習
- 現在は Step1（単語・文法）のみ実装。Step2/3 は後で追加
- 実際のOSSリポジトリや公式ドキュメントを教材として活用
- モノレポ: Vue 3 フロントエンド + Spring Boot バックエンド

## Directory Structure
- `frontend/` - フロントエンド（Vue 3 + Vite + Vue Router）
- `backend/` - バックエンド（Spring Boot + MyBatis + SQLite）
- `docs/plans/` - 設計書・学習プラン
- `docs/curriculum/` - カリキュラム・学習教材
  - `step1-grammar/` - 文法解説（01〜14、番号順）
  - `step1-vocabulary/` - 語彙解説
- `scripts/` - ユーティリティスクリプト

### ファイル命名規則
| ディレクトリ | 命名規則 | 例 |
|-------------|---------|-----|
| `docs/plans/` | `YYYY-MM-DD-<topic>-<type>.md` | `2026-03-13-project-restructure-design.md` |
| `docs/curriculum/step1-grammar/` | `NN-<topic>.md` | `01-passive-voice.md` |
| `docs/curriculum/step1-vocabulary/` | `vocabulary-<topic>.md` | `vocabulary-it-basic.md` |

## Development

### バックエンド起動
```bash
cd backend && ./gradlew bootRun
# → http://localhost:8080
```

### フロントエンド起動
```bash
cd frontend && npm run dev
# → http://100.124.149.51:5173/english-for-it-engineers/
```

### DB初期化
SQLiteのDB（backend/study.db）は Spring Boot 起動時に schema.sql + data.sql で自動初期化される。

## Workflow
1. Read existing docs before making changes
2. Use Conventional Commits format
3. ドキュメントは日本語で記述（学習対象の英語表現はそのまま記載）
4. カスタムコマンド活用:
   - `/explain` - 英語の技術文章の構文・語彙・表現パターンを解説
   - `/exercise` - 指定テーマで練習問題を生成
   - `/plan` - 学習プランを作成
