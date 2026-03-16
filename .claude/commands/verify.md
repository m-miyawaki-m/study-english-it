# Verify

プロジェクトの現在の状態を包括的に検証する。

## 引数

$ARGUMENTS:
- `quick` - ビルドのみ
- `full` - 全チェック（デフォルト）
- `pre-commit` - コミット前チェック

## 手順

以下の順序で検証を実行:

1. **バックエンドビルド**
   - `cd backend && ./gradlew build` を実行
   - 失敗したらエラーを報告して停止

2. **バックエンドテスト**
   - `cd backend && ./gradlew test` を実行
   - 成功/失敗件数を報告

3. **フロントエンドビルド**
   - `cd frontend && npm run build` を実行
   - 失敗したらエラーを報告

4. **console.log 監査**
   - frontend/src/ 内の console.log を検索
   - backend/src/main/ 内の System.out.println を検索
   - 該当箇所を報告

5. **Git状態**
   - 未コミットの変更を表示
   - 最終コミットからの変更ファイルを表示

## 出力フォーマット

```
VERIFICATION: [PASS/FAIL]

Backend Build:  [OK/FAIL]
Backend Tests:  [X/Y passed]
Frontend Build: [OK/FAIL]
Debug Logs:     [OK/X箇所]

Ready for commit: [YES/NO]
```

問題がある場合は修正提案を含める。
