# Checkpoint

作業の進捗スナップショットを作成・検証する。

## 使い方

`/checkpoint [create|verify|list] [name]`

## Create

チェックポイント作成時:

1. `/verify quick` で現在の状態が正常か確認
2. 現在のgit状態を記録
3. 以下を報告:
   - テスト状態（成功/失敗件数）
   - ビルド状態
   - 前回チェックポイントからの変更（git diff --stat）
   - 完了タスクと残りタスク
   - ブロッカーがあれば報告
   - 次のステップ

## Verify

チェックポイントとの比較:

1. 指定チェックポイントからの変更ファイル数
2. テスト結果の差分
3. ビルド状態

```
CHECKPOINT: $NAME
==================
変更ファイル: X
テスト: +Y成功 / -Z失敗
ビルド: [PASS/FAIL]
```

## ワークフロー例

```
[開始] --> /checkpoint create "feature-start"
   |
[実装] --> /checkpoint create "core-done"
   |
[テスト] --> /checkpoint verify "core-done"
   |
[リファクタ] --> /checkpoint create "refactor-done"
   |
[PR] --> /checkpoint verify "feature-start"
```
