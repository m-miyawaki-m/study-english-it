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

## Coding Standards

### コード品質チェックリスト
作業完了前に必ず確認:
- [ ] 関数は50行以内
- [ ] ファイルは800行以内（200-400行を目安）
- [ ] ネストは4段以内
- [ ] エラーハンドリングが適切（エラーを握りつぶさない）
- [ ] ハードコードされた値がない（定数または設定を使う）
- [ ] 可読性の高い命名

### イミュータビリティ
オブジェクトや配列は直接変更せず、新しいコピーを作成する:
```javascript
// NG: 直接変更
user.name = 'New Name'
items.push(newItem)

// OK: 新しいオブジェクト/配列を作成
const updatedUser = { ...user, name: 'New Name' }
const updatedItems = [...items, newItem]
```

### エラーハンドリング
- UI側: ユーザーに分かりやすいエラーメッセージ
- サーバー側: 詳細なエラーログ
- エラーを黙って無視しない
- 外部データ（APIレスポンス、ユーザー入力）は信頼しない

### ファイル構成
- 多くの小さなファイル > 少数の大きなファイル
- 機能/ドメインでまとめる（技術レイヤーではなく）
- 高凝集・低結合

## Security

コミット前の必須チェック:
- [ ] シークレットのハードコードがない（APIキー、パスワード、トークン）
- [ ] ユーザー入力のバリデーション済み
- [ ] SQLインジェクション対策済み（パラメータ化クエリ）
- [ ] XSS対策済み（HTMLサニタイズ）
- [ ] エラーメッセージに機密情報が含まれない

## Development Workflow

### 機能実装の手順
1. **計画** - 複雑な機能は実装前に設計を整理し、方針をユーザーに確認する
2. **検証** - 大量データ生成や大規模変更の前に、小さなサンプルで検証する
3. **実装** - 小さな単位で段階的に実装。各ステップの完了後に結果を確認
4. **テスト** - 新機能にはテストを書く（バックエンドは JUnit、フロントエンドは手動確認）
5. **レビュー** - 実装後にコードの品質を確認
6. **コミット** - Conventional Commits 形式

### 大量データ生成・並列作業時の注意
- 生成方法（手動 vs 自動）、品質基準、リスクを事前にユーザーに確認
- DB参照データはIDハードコードではなくサブクエリ等の安定した参照方式を使う
- 並列エージェント使用前に重複リスク・出力形式・マージ方法を設計
- 「とりあえず全量」ではなく、小サンプルで検証してから本番規模に拡大

### コミットメッセージ形式
```
<type>: <description>
```
type: feat, fix, refactor, docs, test, chore, perf, ci

### PR作成時
1. ベースブランチからの全コミット履歴を分析
2. `git diff [base-branch]...HEAD` で全変更を確認
3. 包括的なPRサマリーを作成
4. テストプランを含める

## Context Management

### 長いセッションでの品質維持
- コンテキストが長くなったら、大規模な変更（複数ファイルのリファクタリング等）を避ける
- 複雑なタスクは新しいセッションで開始する
- `/checkpoint create` で進捗を保存してから `/clear` でリセット

### タスクの区切り方
- 1セッション = 1つの明確な目標（機能追加、バグ修正、リファクタリング等）
- 複数の無関係なタスクを1セッションに詰め込まない
- 大きなタスクは事前に分割してから各セッションで実行

## Workflow
1. Read existing docs before making changes
2. Use Conventional Commits format
3. ドキュメントは日本語で記述（学習対象の英語表現はそのまま記載）
4. カスタムコマンド活用:
   - `/explain` - 英語の技術文章の構文・語彙・表現パターンを解説
   - `/exercise` - 指定テーマで練習問題を生成
   - `/plan` - 学習プランを作成
   - `/code-review` - 未コミット変更のセキュリティ・品質レビュー
   - `/verify` - ビルド・テスト・品質の包括検証
   - `/checkpoint` - 作業進捗のスナップショット作成・比較
