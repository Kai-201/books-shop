<template>
  <div :class="shellClass">
    <router-view />
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();

const shellClass = computed(() => {
  const path = route.path;
  if (path.startsWith("/admin") && path !== "/admin/login") {
    return "admin-shell";
  }
  if (path === "/admin/login") {
    return "admin-shell";
  }
  return "app-shell";
});
</script>
