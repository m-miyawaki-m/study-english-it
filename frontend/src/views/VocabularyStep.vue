<template>
  <div class="vocabulary-page">
    <!-- フィルタバー -->
    <div class="filter-bar">
      <div class="filter-group">
        <button
          v-for="opt in knownOptions"
          :key="opt.value"
          :class="['filter-btn', { active: filters.known === opt.value }]"
          @click="filters.known = opt.value"
        >
          {{ opt.label }}
        </button>
      </div>
      <div class="filter-group">
        <button
          v-for="opt in levelOptions"
          :key="opt.value"
          :class="['filter-btn', { active: filters.level === opt.value }]"
          @click="filters.level = opt.value"
        >
          {{ opt.label }}
        </button>
      </div>
      <div class="filter-group">
        <button
          v-for="opt in sourceOptions"
          :key="opt.value"
          :class="['filter-btn', { active: filters.source === opt.value }]"
          @click="filters.source = opt.value"
        >
          {{ opt.label }}
        </button>
      </div>
    </div>

    <!-- 単語リスト -->
    <div class="word-list">
      <div v-for="word in words" :key="word.id" class="word-row">
        <div class="word-info">
          <span class="word-text">{{ word.word }}</span>
          <span class="word-meaning">{{ word.meaningGeneral }}</span>
          <span v-if="word.meaningIt" class="word-meaning-it">{{ word.meaningIt }}</span>
        </div>
        <div class="word-actions">
          <button
            :class="['known-btn', 'unknown', { active: word.known === 0 }]"
            @click="setKnown(word, 0)"
          >✗</button>
          <button
            :class="['known-btn', 'learning', { active: word.known === 1 }]"
            @click="setKnown(word, 1)"
          >△</button>
          <button
            :class="['known-btn', 'mastered', { active: word.known === 2 }]"
            @click="setKnown(word, 2)"
          >◎</button>
        </div>
      </div>
    </div>

    <!-- ページング -->
    <div class="pagination">
      <button :disabled="page === 0" @click="page--">前</button>
      <span>{{ page * size + 1 }}-{{ Math.min((page + 1) * size, totalElements) }} / {{ totalElements }}語</span>
      <button :disabled="(page + 1) * size >= totalElements" @click="page++">次</button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { wordApi } from '../api/client'

const words = ref([])
const page = ref(0)
const size = 100
const totalElements = ref(0)

const filters = reactive({
  known: null,
  level: null,
  source: null
})

const knownOptions = [
  { label: '全て', value: null },
  { label: '未学習', value: 0 },
  { label: '学習中', value: 1 },
  { label: '習得済', value: 2 },
]

const levelOptions = [
  { label: '全Level', value: null },
  { label: 'Lv.1', value: 1 },
  { label: 'Lv.2', value: 2 },
]

const sourceOptions = [
  { label: '全ソース', value: null },
  { label: 'NGSL', value: 'NGSL' },
  { label: 'IT', value: 'IT' },
]

async function fetchWords() {
  const params = { page: page.value, size }
  if (filters.known !== null) params.known = filters.known
  if (filters.level !== null) params.level = filters.level
  if (filters.source !== null) params.source = filters.source
  const res = await wordApi.getWords(params)
  words.value = res.data.content
  totalElements.value = res.data.totalElements
}

async function setKnown(word, value) {
  await wordApi.updateKnown(word.id, value)
  word.known = value
}

watch([() => filters.known, () => filters.level, () => filters.source], () => {
  page.value = 0
  fetchWords()
})

watch(page, fetchWords)

fetchWords()
</script>

<style scoped>
.filter-bar {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.filter-group {
  display: flex;
  gap: 4px;
}

.filter-btn {
  padding: 4px 12px;
  background: #2a2a4a;
  color: #aaa;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
}

.filter-btn:hover { color: #fff; }
.filter-btn.active { background: #81c784; color: #1a1a2e; }

.word-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.word-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  background: #2a2a4a;
  border-radius: 4px;
}

.word-info {
  display: flex;
  align-items: baseline;
  gap: 8px;
  flex-wrap: wrap;
}

.word-text { font-weight: bold; color: #fff; }
.word-meaning { color: #aaa; font-size: 14px; }
.word-meaning-it { color: #64b5f6; font-size: 13px; }

.word-actions { display: flex; gap: 4px; }

.known-btn {
  padding: 4px 8px;
  border: none;
  border-radius: 3px;
  cursor: pointer;
  font-size: 12px;
  opacity: 0.5;
}

.known-btn.active { opacity: 1; }
.known-btn.unknown { background: #ef5350; color: #fff; }
.known-btn.learning { background: #ffb74d; color: #1a1a2e; }
.known-btn.mastered { background: #81c784; color: #1a1a2e; }

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 16px;
  color: #aaa;
  font-size: 14px;
}

.pagination button {
  padding: 6px 16px;
  background: #2a2a4a;
  color: #ccc;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.pagination button:disabled { opacity: 0.3; cursor: default; }
</style>
