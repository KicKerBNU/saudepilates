// This will be loaded in the main application
document.addEventListener('DOMContentLoaded', function() {
  // Get the current path
  const currentPath = window.location.pathname;
  
  // Base software application schema for the entire site
  const baseJsonLd = {
    "@context": "https://schema.org",
    "@type": "SoftwareApplication",
    "name": "SaúdePilates",
    "applicationCategory": "BusinessApplication",
    "operatingSystem": "Web",
    "offers": {
      "@type": "Offer",
      "price": "129.90",
      "priceCurrency": "BRL"
    },
    "description": "Sistema completo de gestão para estúdios de Pilates. Gerencie alunos, professores, aulas e finanças em uma única plataforma.",
    "aggregateRating": {
      "@type": "AggregateRating",
      "ratingValue": "4.8",
      "ratingCount": "127"
    },
    "applicationSuite": "Gestão de Pilates",
    "screenshot": "https://saudepilates.com.br/og-image.jpg",
    "featureList": "Gerenciamento de alunos, Controle de professores, Agendamento de aulas, Gestão financeira, Evolução dos alunos"
  };

  // FAQ Page schema specifically for the guide page
  const guideJsonLd = {
    "@context": "https://schema.org",
    "@type": "FAQPage",
    "mainEntity": [
      {
        "@type": "Question",
        "name": "Quais são as principais funcionalidades para administradores?",
        "acceptedAnswer": {
          "@type": "Answer",
          "text": "Os administradores podem gerenciar planos, professores, alunos, registrar pagamentos, e acompanhar métricas do estúdio através do dashboard."
        }
      },
      {
        "@type": "Question",
        "name": "O que os professores podem fazer na plataforma?",
        "acceptedAnswer": {
          "@type": "Answer",
          "text": "Os professores podem gerenciar seus alunos, acompanhar ganhos, controlar presença, registrar evolução dos alunos e gerenciar sua agenda de aulas."
        }
      },
      {
        "@type": "Question",
        "name": "Quais recursos estão disponíveis para os alunos?",
        "acceptedAnswer": {
          "@type": "Answer",
          "text": "Os alunos podem visualizar suas aulas agendadas, histórico de presença, acompanhar sua evolução e verificar o status de pagamentos."
        }
      },
      {
        "@type": "Question",
        "name": "Como funciona o controle de pagamentos?",
        "acceptedAnswer": {
          "@type": "Answer",
          "text": "O sistema permite registrar pagamentos, visualizar pagamentos pendentes e histórico, com relatórios detalhados sobre a situação financeira do estúdio."
        }
      }
    ]
  };

  let jsonLdToUse;
  
  // Apply different schema based on the current page
  if (currentPath === '/guia' || currentPath === '/guia.html') {
    jsonLdToUse = guideJsonLd;
  } else {
    jsonLdToUse = baseJsonLd;
  }

  const script = document.createElement('script');
  script.type = 'application/ld+json';
  script.text = JSON.stringify(jsonLdToUse);
  document.head.appendChild(script);
});
