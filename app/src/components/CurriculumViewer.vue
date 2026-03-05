<script setup>
import { ref, computed, watch } from 'vue'
import { marked } from 'marked'

const modules = import.meta.glob('../../../docs/curriculum/*.md', { query: '?raw', eager: true })

// 学習順序の定義（易→難、前提→発展）
const grammarOrder = [
  'grammar-passive-voice',
  'grammar-conditionals',
  'grammar-relative-pronouns',
  'grammar-infinitives-gerunds',
  'grammar-comparatives',
  'grammar-colons-semicolons',
  'grammar-causative-perception',
  'grammar-inanimate-subject',
  'grammar-post-modifying-participles',
  'grammar-appositive-that',
  'grammar-with-opc',
  'grammar-participial-constructions',
  'grammar-preposition-relative',
  'grammar-inversion-emphasis',
]

const vocabularyOrder = [
  'vocabulary-it-basic',
  'vocabulary-abbreviations',
  'vocabulary-general-verbs',
  'vocabulary-general-adjectives',
  'vocabulary-general-connectors',
  'vocabulary-it-expressions',
]

function sortByOrder(items, order) {
  return items.sort((a, b) => {
    const ai = order.indexOf(a.filename)
    const bi = order.indexOf(b.filename)
    // 順序未定義のものは末尾へ
    return (ai === -1 ? Infinity : ai) - (bi === -1 ? Infinity : bi)
  })
}

const topics = computed(() => {
  const grammar = []
  const vocabulary = []

  for (const [path, mod] of Object.entries(modules)) {
    const filename = path.split('/').pop().replace('.md', '')
    const raw = mod.default
    const titleMatch = raw.match(/^#\s+(.+)/m)
    const title = titleMatch ? titleMatch[1] : filename

    const entry = { filename, title, raw }

    if (filename.startsWith('grammar-')) {
      grammar.push(entry)
    } else if (filename.startsWith('vocabulary-')) {
      vocabulary.push(entry)
    }
  }

  sortByOrder(grammar, grammarOrder)
  sortByOrder(vocabulary, vocabularyOrder)

  return { grammar, vocabulary }
})

const selectedTopic = ref(null)

const renderedHtml = computed(() => {
  if (!selectedTopic.value) return ''
  return marked(selectedTopic.value.raw)
})

function selectTopic(topic) {
  selectedTopic.value = topic
}
</script>

<template>
  <h1>カリキュラム教材</h1>
  <div class="curriculum-layout">
    <div class="topic-list">
      <h3 class="category-title">文法（{{ topics.grammar.length }}）</h3>
      <ul>
        <li
          v-for="(t, i) in topics.grammar"
          :key="t.filename"
          class="topic-item"
          :class="{ active: selectedTopic?.filename === t.filename }"
          @click="selectTopic(t)"
        >
          <span class="step-badge">{{ i + 1 }}</span>
          {{ t.title }}
        </li>
      </ul>

      <h3 class="category-title">語彙（{{ topics.vocabulary.length }}）</h3>
      <ul>
        <li
          v-for="(t, i) in topics.vocabulary"
          :key="t.filename"
          class="topic-item"
          :class="{ active: selectedTopic?.filename === t.filename }"
          @click="selectTopic(t)"
        >
          <span class="step-badge">{{ i + 1 }}</span>
          {{ t.title }}
        </li>
      </ul>
    </div>

    <div class="topic-content">
      <div v-if="renderedHtml" class="markdown-body" v-html="renderedHtml"></div>
      <div v-else class="placeholder">左のリストからトピックを選択してください</div>
    </div>
  </div>
</template>

<style scoped>
.curriculum-layout {
  display: flex;
  gap: 24px;
}

.topic-list {
  width: 280px;
  flex-shrink: 0;
}

.category-title {
  font-size: 0.85rem;
  font-weight: 600;
  color: #999;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin: 16px 0 8px;
}

.category-title:first-child {
  margin-top: 0;
}

ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.topic-item {
  padding: 8px 12px;
  cursor: pointer;
  color: #555;
  font-size: 0.9rem;
  border-radius: 6px;
  transition: background 0.15s, color 0.15s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.step-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: #e0e0e0;
  color: #666;
  font-size: 0.75rem;
  font-weight: 600;
  flex-shrink: 0;
}

.topic-item.active .step-badge {
  background: #1565c0;
  color: #fff;
}

.topic-item:hover {
  background: #f5f5f5;
  color: #333;
}

.topic-item.active {
  background: #e3f2fd;
  color: #1565c0;
  font-weight: 600;
}

.topic-content {
  flex: 1;
  min-width: 0;
}

.placeholder {
  color: #999;
  font-size: 0.95rem;
  padding: 40px 0;
  text-align: center;
}

.markdown-body :deep(h1) {
  font-size: 1.5rem;
  margin-top: 0;
  margin-bottom: 16px;
  color: #1a1a1a;
  border-bottom: 1px solid #e0e0e0;
  padding-bottom: 8px;
}

.markdown-body :deep(h2) {
  font-size: 1.2rem;
  margin-top: 24px;
  margin-bottom: 12px;
  color: #333;
}

.markdown-body :deep(h3) {
  font-size: 1.05rem;
  margin-top: 20px;
  margin-bottom: 8px;
  color: #444;
}

.markdown-body :deep(p) {
  margin: 8px 0;
  line-height: 1.7;
  color: #333;
}

.markdown-body :deep(ul),
.markdown-body :deep(ol) {
  margin: 8px 0;
  padding-left: 24px;
}

.markdown-body :deep(li) {
  margin: 4px 0;
  line-height: 1.6;
}

.markdown-body :deep(code) {
  background: #f5f5f5;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 0.9em;
  color: #d63384;
}

.markdown-body :deep(pre) {
  background: #f8f8f8;
  padding: 16px;
  border-radius: 8px;
  overflow-x: auto;
  border: 1px solid #e0e0e0;
}

.markdown-body :deep(pre code) {
  background: none;
  padding: 0;
  color: #333;
}

.markdown-body :deep(table) {
  border-collapse: collapse;
  width: 100%;
  margin: 12px 0;
}

.markdown-body :deep(th),
.markdown-body :deep(td) {
  border: 1px solid #e0e0e0;
  padding: 8px 12px;
  text-align: left;
}

.markdown-body :deep(th) {
  background: #f5f5f5;
  font-weight: 600;
}

.markdown-body :deep(blockquote) {
  border-left: 4px solid #e0e0e0;
  margin: 12px 0;
  padding: 8px 16px;
  color: #666;
  background: #fafafa;
}

.markdown-body :deep(strong) {
  color: #1a1a1a;
}

.markdown-body :deep(hr) {
  border: none;
  border-top: 1px solid #e0e0e0;
  margin: 20px 0;
}
</style>
