<template>
   <i-frame v-model:src="url"></i-frame>
</template>

<script setup>
import iFrame from '@/components/iFrame'

// swagger地址：http://localhost/dev-api/swagger-ui/index.html
const url = ref(import.meta.env.VITE_APP_BASE_API + "/swagger-ui/index.html")
</script>
