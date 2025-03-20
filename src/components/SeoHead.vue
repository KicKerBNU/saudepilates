<script setup>
import { useHead } from '@vueuse/head';
import { computed } from 'vue';

// Define props for customizable SEO data
const props = defineProps({
  title: {
    type: String,
    default: 'SaúdePilates - Sistema de Gestão para Estúdios de Pilates'
  },
  description: {
    type: String,
    default: 'Gerencie seu estúdio de Pilates com eficiência. Controle de alunos, agendamento de aulas, gestão financeira e muito mais em uma única plataforma.'
  },
  keywords: {
    type: String,
    default: 'pilates, gestão de estúdio, sistema para pilates, agendamento pilates, gerenciamento de alunos, pilates brasil'
  },
  ogType: {
    type: String,
    default: 'website'
  },
  ogImage: {
    type: String,
    default: 'https://saudepilates.com.br/og-image.jpg'
  },
  canonicalPath: {
    type: String,
    default: ''
  }
});

// Calculate canonical URL
const canonicalUrl = computed(() => {
  const baseUrl = 'https://saudepilates.com.br';
  return props.canonicalPath ? `${baseUrl}${props.canonicalPath}` : baseUrl;
});

// Apply head metadata
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
      property: 'og:type',
      content: props.ogType
    },
    {
      property: 'og:url',
      content: canonicalUrl.value
    },
    {
      property: 'og:image',
      content: props.ogImage
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
      property: 'twitter:image',
      content: props.ogImage
    }
  ],
  link: [
    {
      rel: 'canonical',
      href: canonicalUrl.value
    }
  ]
});
</script>

<template>
  <!-- This component doesn't render anything visibly -->
</template> 