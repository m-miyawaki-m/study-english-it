<template>
  <div class="grammar-page">
    <div class="grammar-layout">
      <aside class="topic-sidebar">
        <div
          v-for="topic in topics"
          :key="topic.id"
          :class="['topic-item', {
            completed: topic.completed,
            active: selectedId === topic.id,
            next: !topic.completed && !selectedId && isNextTopic(topic)
          }]"
          @click="selectTopic(topic)"
        >
          <span class="topic-icon">
            {{ topic.completed ? '✓' : (isNextTopic(topic) ? '▶' : '○') }}
          </span>
          <span class="topic-title">{{ topic.sortOrder }}. {{ topic.title }}</span>
        </div>
      </aside>
      <div class="topic-content">
        <div v-if="!selectedId" class="empty-state">
          左のリストからトピックを選んでください
        </div>
        <div v-else-if="loading" class="loading">読み込み中...</div>
        <div v-else>
          <div class="content-header">
            <label class="complete-check">
              <input
                type="checkbox"
                :checked="selectedTopic?.completed"
                @change="toggleCompleted"
              />
              完了
            </label>
          </div>
          <div class="markdown-body" v-html="content"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { grammarApi } from '../api/client'

const topics = ref([])
const selectedId = ref(null)
const content = ref('')
const loading = ref(false)

const selectedTopic = computed(() =>
  topics.value.find(t => t.id === selectedId.value)
)

function isNextTopic(topic) {
  const incomplete = topics.value.filter(t => !t.completed)
  return incomplete.length > 0 && incomplete[0].id === topic.id
}

async function fetchTopics() {
  const res = await grammarApi.getTopics()
  topics.value = res.data
}

async function selectTopic(topic) {
  selectedId.value = topic.id
  loading.value = true
  const res = await grammarApi.getContent(topic.id)
  content.value = res.data
  loading.value = false
}

async function toggleCompleted() {
  const topic = selectedTopic.value
  const newValue = topic.completed ? 0 : 1
  await grammarApi.updateCompleted(topic.id, newValue)
  topic.completed = newValue
}

fetchTopics()
</script>

<style scoped>
.grammar-layout {
  display: flex;
  gap: 16px;
  min-height: calc(100vh - 120px);
}

.topic-sidebar {
  flex: 0 0 240px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.topic-item {
  padding: 8px 10px;
  background: #2a2a4a;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #888;
  border-left: 3px solid #444;
}

.topic-item:hover { color: #ccc; }
.topic-item.completed { color: #81c784; background: #2a3a2a; border-left-color: #81c784; }
.topic-item.active { color: #fff; font-weight: bold; background: #3a3a6a; border-left-color: #64b5f6; }
.topic-item.next { color: #64b5f6; border-left-color: #64b5f6; }

.topic-icon { font-size: 12px; flex-shrink: 0; }

.topic-content {
  flex: 1;
  background: #2a2a4a;
  border-radius: 4px;
  padding: 24px;
  overflow-y: auto;
}

.content-header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 16px;
}

.complete-check {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #aaa;
  font-size: 14px;
  cursor: pointer;
}

.empty-state, .loading {
  color: #666;
  text-align: center;
  padding: 48px;
}

.markdown-body {
  color: #e0e0e0;
  line-height: 1.8;
}

.markdown-body :deep(h1) { font-size: 24px; color: #fff; margin-bottom: 16px; }
.markdown-body :deep(h2) { font-size: 20px; color: #e0e0e0; margin-top: 24px; margin-bottom: 12px; }
.markdown-body :deep(h3) { font-size: 16px; color: #ccc; margin-top: 16px; margin-bottom: 8px; }
.markdown-body :deep(code) { background: #1a1a2e; padding: 2px 6px; border-radius: 3px; font-size: 13px; }
.markdown-body :deep(pre) { background: #1a1a2e; padding: 16px; border-radius: 4px; overflow-x: auto; }
.markdown-body :deep(table) { width: 100%; border-collapse: collapse; margin: 12px 0; }
.markdown-body :deep(th), .markdown-body :deep(td) { border: 1px solid #444; padding: 8px; text-align: left; }
.markdown-body :deep(th) { background: #1a1a2e; }
</style>
