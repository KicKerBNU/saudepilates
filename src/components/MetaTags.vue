<template>
  <!-- This is a meta tags component - it doesn't render anything visible -->
</template>

<script setup>
import { useHead } from '@vueuse/head';
import { computed } from 'vue';
import { useRoute } from 'vue-router';

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    required: true
  },
  keywords: {
    type: String,
    default: 'pilates, gestão de estúdio, sistema para pilates, agendamento pilates'
  },
  image: {
    type: String,
    default: 'https://saudepilates.com.br/og-image.jpg'
  },
  url: {
    type: String,
    default: null
  },
  type: {
    type: String,
    default: 'website'
  }
});

const route = useRoute();
const currentUrl = computed(() => props.url || `https://saudepilates.com.br${route.path}`);

// Use @vueuse/head to manage meta tags
useHead({
  title: props.title,
  meta: [
    {
      name: 'description',
      content: props.description
    },
    {
      name: 'keywords',
      content: props.keywords
    },
    {
      property: 'og:title',
      content: props.title
    },
    {
      property: 'og:description',
      content: props.description
    },
    {
      property: 'og:url',
      content: currentUrl.value
    },
    {
      property: 'og:image',
      content: props.image
    },
    {
      property: 'og:type',
      content: props.type
    },
    {
      property: 'twitter:card',
      content: 'summary_large_image'
    },
    {
      property: 'twitter:title',
      content: props.title
    },
    {
      property: 'twitter:description',
      content: props.description
    },
    {
      property: 'twitter:url',
      content: currentUrl.value
    },
    {
      property: 'twitter:image',
      content: props.image
    }
  ],
  link: [
    {
      rel: 'canonical',
      href: currentUrl.value
    }
  ]
});
</script>
