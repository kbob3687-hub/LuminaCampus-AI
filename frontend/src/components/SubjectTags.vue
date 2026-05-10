<script setup>
const props = defineProps({
  active: { type: String, default: '' },
})
const emit = defineEmits(['change'])

const subjects = [
  { name: '数据结构', icon: '📊', desc: '树、图、排序与查找' },
  { name: '计算机网络', icon: '🌐', desc: 'TCP/IP、HTTP、路由' },
  { name: '高等数学', icon: '📐', desc: '微积分、级数、方程' },
]

function handleClick(name) {
  emit('change', name === props.active ? '' : name)
}
</script>

<template>
  <div class="subject-tags">
    <div
      v-for="s in subjects"
      :key="s.name"
      class="tag"
      :class="{ active: active === s.name }"
      @click="handleClick(s.name)"
    >
      <span class="tag-icon">{{ s.icon }}</span>
      <span class="tag-name">{{ s.name }}</span>
    </div>
  </div>
</template>

<style scoped>
.subject-tags {
  display: flex;
  gap: 8px;
}

.tag {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  background: var(--bg);
  border: 1px solid var(--border);
  border-radius: 20px;
  cursor: pointer;
  font-size: 13px;
  color: var(--text-secondary);
  transition: all 0.15s;
  user-select: none;
}

.tag:hover {
  border-color: var(--primary);
  color: var(--primary);
  background: var(--primary-bg);
}

.tag.active {
  background: var(--primary);
  color: white;
  border-color: var(--primary);
  box-shadow: 0 2px 8px rgba(74, 144, 217, 0.25);
}

.tag-icon {
  font-size: 14px;
}

.tag-name {
  font-weight: 500;
}
</style>
