<template>
  <div class="dashboard">
    <div class="mission-card">
      <div class="mission-label">今日のミッション</div>
      <div class="mission-text">{{ mission.text }}</div>
      <router-link :to="mission.link" class="mission-btn">学習を始める →</router-link>
    </div>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-number mastered">{{ stats.mastered }}</div>
        <div class="stat-label">習得済</div>
      </div>
      <div class="stat-card">
        <div class="stat-number learning">{{ stats.learning }}</div>
        <div class="stat-label">学習中</div>
      </div>
      <div class="stat-card">
        <div class="stat-number unknown">{{ stats.unknown }}</div>
        <div class="stat-label">未学習</div>
      </div>
    </div>

    <div class="grammar-progress" v-if="grammarStats.total > 0">
      <span class="grammar-label">文法:</span>
      <span class="grammar-count">{{ grammarStats.completed }}/{{ grammarStats.total }} 完了</span>
      <div class="grammar-bar">
        <div class="grammar-bar-fill" :style="{ width: grammarPercent + '%' }"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { wordApi, grammarApi } from '../api/client'

const stats = ref({ total: 0, unknown: 0, learning: 0, mastered: 0 })
const grammarStats = ref({ total: 0, completed: 0 })

const grammarPercent = computed(() => {
  if (grammarStats.value.total === 0) return 0
  return Math.round((grammarStats.value.completed / grammarStats.value.total) * 100)
})

const mission = computed(() => {
  const s = stats.value
  if (s.total === 0) return { text: 'データを読み込み中...', link: '/' }

  const masteredPercent = s.total > 0 ? (s.mastered / s.total) * 100 : 0
  if (masteredPercent >= 80) {
    return {
      text: '次の文法トピックに進もう',
      link: '/grammar'
    }
  }
  return {
    text: 'Level 1 の単語を20個覚えよう',
    link: '/vocabulary?known=0&level=1'
  }
})

onMounted(async () => {
  const [wordRes, grammarRes] = await Promise.all([
    wordApi.getStats(),
    grammarApi.getTopics()
  ])
  stats.value = wordRes.data
  const topics = grammarRes.data
  grammarStats.value = {
    total: topics.length,
    completed: topics.filter(t => t.completed).length
  }
})
</script>

<style scoped>
.dashboard {
  max-width: 600px;
  margin: 0 auto;
}

.mission-card {
  background: linear-gradient(135deg, #2a2a4a, #1a1a3e);
  padding: 32px;
  border-radius: 12px;
  text-align: center;
  margin-bottom: 24px;
}

.mission-label {
  color: #ffb74d;
  font-size: 14px;
  margin-bottom: 8px;
}

.mission-text {
  color: #fff;
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 16px;
}

.mission-btn {
  display: inline-block;
  background: #81c784;
  color: #1a1a2e;
  padding: 10px 24px;
  border-radius: 6px;
  font-weight: bold;
  text-decoration: none;
  font-size: 14px;
}

.mission-btn:hover { background: #a5d6a7; }

.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 12px;
  margin-bottom: 16px;
}

.stat-card {
  background: #2a2a4a;
  padding: 16px;
  border-radius: 8px;
  text-align: center;
}

.stat-number { font-size: 28px; font-weight: bold; }
.stat-number.mastered { color: #81c784; }
.stat-number.learning { color: #ffb74d; }
.stat-number.unknown { color: #ef5350; }
.stat-label { color: #aaa; font-size: 13px; margin-top: 4px; }

.grammar-progress {
  background: #2a2a4a;
  padding: 12px 16px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.grammar-label { color: #64b5f6; font-size: 14px; }
.grammar-count { color: #aaa; font-size: 13px; }

.grammar-bar {
  flex: 1;
  height: 6px;
  background: #333;
  border-radius: 3px;
}

.grammar-bar-fill {
  height: 6px;
  background: #64b5f6;
  border-radius: 3px;
  transition: width 0.3s;
}
</style>
