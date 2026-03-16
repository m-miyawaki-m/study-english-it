# 語彙拡張 実装計画（2800語 + IPA + 関連語）

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** NGSL 2800語への拡張、IPA発音記号の追加、関連語（派生語・同義語・対義語）の実装

**Architecture:** DB スキーマに IPA カラムと word_relations テーブルを追加し、バックエンドAPI を拡張、フロントエンドのモック関連語を実データに差し替える。データ生成はセッション内で直接 data.sql に書き出す。

**Tech Stack:** Spring Boot + MyBatis + SQLite / Vue 3 / data.sql 直接生成

---

## Chunk 1: DB・モデル・データ生成

### Task 1: DB設計変更

**Files:**
- Modify: `backend/src/main/resources/schema.sql`

- [ ] **Step 1: schema.sql に pronunciation_ipa カラム追加**

```sql
CREATE TABLE IF NOT EXISTS words (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    word TEXT NOT NULL,
    meaning_general TEXT,
    meaning_it TEXT,
    part_of_speech TEXT,
    pronunciation_ipa TEXT,
    domain TEXT,
    source TEXT,
    level INTEGER NOT NULL DEFAULT 1,
    frequency INTEGER,
    example_en TEXT,
    example_ja TEXT,
    known INTEGER DEFAULT 0,
    last_reviewed TEXT,
    created_at TEXT DEFAULT (datetime('now'))
);
```

- [ ] **Step 2: word_relations テーブル追加**

```sql
CREATE TABLE IF NOT EXISTS word_relations (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    word_id INTEGER NOT NULL,
    related_word TEXT NOT NULL,
    relation_type TEXT NOT NULL CHECK(relation_type IN ('derivative', 'synonym', 'antonym')),
    FOREIGN KEY (word_id) REFERENCES words(id)
);

CREATE INDEX IF NOT EXISTS idx_word_relations_word_id ON word_relations(word_id);
```

- [ ] **Step 3: コミット**

```bash
git add backend/src/main/resources/schema.sql
git commit -m "feat: add pronunciation_ipa column and word_relations table"
```

---

### Task 2: Word モデル・Mapper 更新

**Files:**
- Modify: `backend/src/main/java/com/studyenglish/model/Word.java`
- Modify: `backend/src/main/resources/mapper/WordMapper.xml`（SELECT に pronunciation_ipa 追加 — ただし現在 `SELECT *` なのでXML変更不要の可能性あり）

- [ ] **Step 1: Word.java に pronunciationIpa フィールド追加**

```java
private String pronunciationIpa;

public String getPronunciationIpa() { return pronunciationIpa; }
public void setPronunciationIpa(String pronunciationIpa) { this.pronunciationIpa = pronunciationIpa; }
```

- [ ] **Step 2: バックエンド起動して API レスポンスに pronunciationIpa が含まれることを確認**

```bash
curl -s 'http://localhost:8080/api/words?size=1' | python3 -m json.tool
```

Expected: レスポンスの content[0] に `"pronunciationIpa": null` が含まれる

- [ ] **Step 3: コミット**

```bash
git add backend/src/main/java/com/studyenglish/model/Word.java
git commit -m "feat: add pronunciationIpa field to Word model"
```

---

### Task 3: 既存1000語に IPA 追加

**Files:**
- Modify: `backend/src/main/resources/data.sql`

既存の NGSL INSERT 文に `pronunciation_ipa` カラムを追加する。500語ずつ2回に分けて生成。

- [ ] **Step 1: NGSL 1〜500語の INSERT に IPA 追加**

既存:
```sql
INSERT INTO words (word, meaning_general, meaning_it, frequency, source, level, known) VALUES ('the', 'その、あの（定冠詞）', NULL, 1, 'NGSL', 1, 0);
```

変更後:
```sql
INSERT INTO words (word, meaning_general, meaning_it, pronunciation_ipa, frequency, source, level, known) VALUES ('the', 'その、あの（定冠詞）', NULL, '/ðə/', 1, 'NGSL', 1, 0);
```

- [ ] **Step 2: NGSL 501〜1000語の INSERT に IPA 追加**

- [ ] **Step 3: バックエンド再起動して IPA データ確認**

```bash
curl -s 'http://localhost:8080/api/words?keyword=run&size=1' | python3 -m json.tool
```

Expected: `"pronunciationIpa": "/rʌn/"` が含まれる

- [ ] **Step 4: コミット**

```bash
git add backend/src/main/resources/data.sql
git commit -m "feat: add IPA pronunciation to existing 1000 NGSL words"
```

---

### Task 4: NGSL 1001〜2800語データ生成

**Files:**
- Modify: `backend/src/main/resources/data.sql`

250語ずつ8回に分けて生成。各語に word, meaning_general, meaning_it（該当する場合）, pronunciation_ipa, frequency, source='NGSL', level=1 を含む。

**重要: 1回目（1001〜1250語）を生成した時点でユーザーに確認し、フォーマット・品質・方針を確定する。**

- [ ] **Step 1: NGSL 1001〜1250語を生成（250語）** → ユーザー確認ポイント
- [ ] **Step 2: NGSL 1251〜1500語を生成（250語）**
- [ ] **Step 3: NGSL 1501〜1750語を生成（250語）**
- [ ] **Step 4: NGSL 1751〜2000語を生成（250語）**
- [ ] **Step 5: 中間コミット**

```bash
git add backend/src/main/resources/data.sql
git commit -m "feat: add NGSL words 1001-2000 with IPA"
```

- [ ] **Step 6: NGSL 2001〜2250語を生成（250語）**
- [ ] **Step 7: NGSL 2251〜2500語を生成（250語）**
- [ ] **Step 8: NGSL 2501〜2800語を生成（300語）**
- [ ] **Step 9: バックエンド再起動して総語数確認**

```bash
curl -s 'http://localhost:8080/api/words/stats' | python3 -m json.tool
```

Expected: `"total": 2825`（NGSL 2800 + IT語彙 25）

- [ ] **Step 10: コミット**

```bash
git add backend/src/main/resources/data.sql
git commit -m "feat: add NGSL words 2001-2800 with IPA"
```

---

## Chunk 2: 関連語 API・データ・フロントエンド

### Task 5: 関連語 API 実装

**Files:**
- Create: `backend/src/main/java/com/studyenglish/model/WordRelation.java`
- Create: `backend/src/main/java/com/studyenglish/mapper/WordRelationMapper.java`
- Create: `backend/src/main/resources/mapper/WordRelationMapper.xml`
- Modify: `backend/src/main/java/com/studyenglish/controller/WordController.java`
- Modify: `backend/src/main/java/com/studyenglish/service/WordService.java`
- Create: `backend/src/test/java/com/studyenglish/controller/WordRelationControllerTest.java`

- [ ] **Step 1: WordRelation モデル作成**

```java
package com.studyenglish.model;

public class WordRelation {
    private Integer id;
    private Integer wordId;
    private String relatedWord;
    private String relationType; // "derivative", "synonym", "antonym"
    // getter/setter
}
```

- [ ] **Step 2: WordRelationMapper インターフェース作成**

```java
@Mapper
public interface WordRelationMapper {
    List<WordRelation> findByWordId(@Param("wordId") int wordId);
}
```

- [ ] **Step 3: WordRelationMapper.xml 作成**

```xml
<select id="findByWordId" resultType="com.studyenglish.model.WordRelation">
    SELECT * FROM word_relations WHERE word_id = #{wordId}
</select>
```

- [ ] **Step 4: WordService に getRelations メソッド追加**

```java
public List<WordRelation> getRelations(int wordId) {
    return wordRelationMapper.findByWordId(wordId);
}
```

- [ ] **Step 5: WordController に GET /api/words/{id}/relations エンドポイント追加**

```java
@GetMapping("/{id}/relations")
public List<WordRelation> getRelations(@PathVariable int id) {
    return wordService.getRelations(id);
}
```

- [ ] **Step 6: テスト作成・実行**

```bash
cd backend && ./gradlew test
```

- [ ] **Step 7: コミット**

```bash
git add backend/src/main/java/com/studyenglish/model/WordRelation.java \
        backend/src/main/java/com/studyenglish/mapper/WordRelationMapper.java \
        backend/src/main/resources/mapper/WordRelationMapper.xml \
        backend/src/main/java/com/studyenglish/service/WordService.java \
        backend/src/main/java/com/studyenglish/controller/WordController.java \
        backend/src/test/java/com/studyenglish/controller/WordRelationControllerTest.java
git commit -m "feat: add word relations API endpoint"
```

---

### Task 6: 関連語データ生成

**Files:**
- Modify: `backend/src/main/resources/data.sql`

主要な単語（頻出・IT関連）に対して派生語・同義語・対義語の INSERT 文を生成。
全2800語すべてに関連語を付けるのではなく、関連語が有意味な単語に絞る。

- [ ] **Step 1: 関連語データ生成（NGSL 上位500語の中から対象語を選定）**

```sql
-- word_id は words テーブルの id を参照
-- 例: "change" (id=X) の関連語
INSERT INTO word_relations (word_id, related_word, relation_type) VALUES (X, 'changeable', 'derivative');
INSERT INTO word_relations (word_id, related_word, relation_type) VALUES (X, 'unchanged', 'derivative');
INSERT INTO word_relations (word_id, related_word, relation_type) VALUES (X, 'modify', 'synonym');
INSERT INTO word_relations (word_id, related_word, relation_type) VALUES (X, 'alter', 'synonym');
INSERT INTO word_relations (word_id, related_word, relation_type) VALUES (X, 'keep', 'antonym');
INSERT INTO word_relations (word_id, related_word, relation_type) VALUES (X, 'maintain', 'antonym');
```

注意: word_id は DB 起動時に自動採番されるため、サブクエリで word 名から id を取得する形式にする。

```sql
INSERT INTO word_relations (word_id, related_word, relation_type)
SELECT id, 'changeable', 'derivative' FROM words WHERE word = 'change' AND source = 'NGSL';
```

- [ ] **Step 2: 残りの対象語の関連語データ生成**

- [ ] **Step 3: バックエンド再起動して API 確認**

```bash
curl -s 'http://localhost:8080/api/words?keyword=change&size=1' | python3 -m json.tool
# → id を確認
curl -s 'http://localhost:8080/api/words/{id}/relations' | python3 -m json.tool
```

- [ ] **Step 4: コミット**

```bash
git add backend/src/main/resources/data.sql
git commit -m "feat: add related words data (derivatives, synonyms, antonyms)"
```

---

### Task 7: フロントエンド対応

**Files:**
- Modify: `frontend/src/api/client.js`
- Modify: `frontend/src/views/VocabularyStep.vue`

- [ ] **Step 1: client.js に関連語 API メソッド追加**

```javascript
getRelations(id) {
    return api.get(`/words/${id}/relations`)
}
```

- [ ] **Step 2: VocabularyStep.vue のモック関連語を実 API に差し替え**

- 単語の「意味」ボタン押下時に `/api/words/{id}/relations` を呼び出し
- レスポンスを relation_type ごとにグループ化して表示
- mockRelated オブジェクトを削除

- [ ] **Step 3: IPA 発音記号の表示を追加**

単語名の横に IPA を表示:
```html
<span class="word-text">{{ word.word }}</span>
<span v-if="word.pronunciationIpa" class="word-ipa">{{ word.pronunciationIpa }}</span>
```

- [ ] **Step 4: ブラウザで動作確認**

確認項目:
- 検索窓でフィルタ動作
- 意味ボタンで IPA・和訳・IT意味・関連語が表示される
- 関連語が派生・同義・対義で改行表示される

- [ ] **Step 5: コミット**

```bash
git add frontend/src/api/client.js frontend/src/views/VocabularyStep.vue
git commit -m "feat: replace mock related words with API, add IPA display"
```

---

### Task 8: テスト・動作確認

**Files:**
- Modify: `backend/src/test/java/com/studyenglish/controller/WordControllerTest.java`

- [ ] **Step 1: 既存テストの totalElements を 2825 に更新**

- [ ] **Step 2: keyword 検索テスト追加**

```java
@Test
void getWords_withKeyword_returnsFilteredResults() throws Exception {
    mockMvc.perform(get("/api/words").param("keyword", "run"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].word").value("run"));
}
```

- [ ] **Step 3: 全テスト実行**

```bash
cd backend && ./gradlew test
```

Expected: 全テスト PASS

- [ ] **Step 4: コミット**

```bash
git add backend/src/test/java/com/studyenglish/controller/WordControllerTest.java
git commit -m "test: update word tests for 2800 vocabulary and keyword search"
```
