<template>
  <!-- This is a meta tags component - it doesn't render anything visible -->
</template>

<script setup>
import { onMounted, onUnmounted, watch } from 'vue';
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
  }
});

const route = useRoute();
const currentUrl = props.url || `https://saudepilates.com.br${route.path}`;

// Update meta tags
const updateMetaTags = () => {
  // Basic meta tags
  document.title = props.title;
  
  // Find and update or create meta tags
  const updateMetaTag = (name, content) => {
    let tag = document.querySelector(`meta[name="${name}"]`);
    if (tag) {
      tag.setAttribute('content', content);
    } else {
      tag = document.createElement('meta');
      tag.setAttribute('name', name);
      tag.setAttribute('content', content);
      document.head.appendChild(tag);
    }
  };

  const updateOpenGraphTag = (property, content) => {
    let tag = document.querySelector(`meta[property="${property}"]`);
    if (tag) {
      tag.setAttribute('content', content);
    } else {
      tag = document.createElement('meta');
      tag.setAttribute('property', property);
      tag.setAttribute('content', content);
      document.head.appendChild(tag);
    }
  };

  // Update basic meta tags
  updateMetaTag('description', props.description);
  updateMetaTag('keywords', props.keywords);
  
  // Update Open Graph tags
  updateOpenGraphTag('og:title', props.title);
  updateOpenGraphTag('og:description', props.description);
  updateOpenGraphTag('og:url', currentUrl);
  updateOpenGraphTag('og:image', props.image);
  
  // Update Twitter tags
  updateOpenGraphTag('twitter:title', props.title);
  updateOpenGraphTag('twitter:description', props.description);
  updateOpenGraphTag('twitter:url', currentUrl);
  updateOpenGraphTag('twitter:image', props.image);
  
  // Update canonical URL
  let canonicalTag = document.querySelector('link[rel="canonical"]');
  if (canonicalTag) {
    canonicalTag.setAttribute('href', currentUrl);
  } else {
    canonicalTag = document.createElement('link');
    canonicalTag.setAttribute('rel', 'canonical');
    canonicalTag.setAttribute('href', currentUrl);
    document.head.appendChild(canonicalTag);
  }
};

// Update meta tags when component mounts
onMounted(() => {
  updateMetaTags();
});

// Update meta tags when props change
watch(props, () => {
  updateMetaTags();
});

// Clean up when component unmounts (optional)
onUnmounted(() => {
  // Optionally reset to default meta tags
  // This is usually not necessary since the next page will set its own tags
});
</script>
