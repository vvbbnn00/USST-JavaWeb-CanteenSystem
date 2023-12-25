<script setup lang="ts">
import {updateCurrentUser} from "../api/user";
import {useRouter} from "vue-router";

await updateCurrentUser();

const router = useRouter();

router.push('/dashboard');
setTimeout(() => {
  window.location.reload();
}, 100);

</script>

<template>

</template>

<style scoped>

</style>
