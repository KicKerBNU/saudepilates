document.addEventListener('DOMContentLoaded', function() {
  const currentPath = window.location.pathname;
  
  const organizationSchema = {
    "@context": "https://schema.org",
    "@type": "Organization",
    "name": "SaúdePilates",
    "url": "https://saudepilates.com.br",
    "logo": "https://saudepilates.com.br/lotussvg.svg",
    "description": "Software para estúdios de Pilates. Gestão completa de alunos, professores, aulas e finanças.",
    "contactPoint": {
      "@type": "ContactPoint",
      "contactType": "customer support",
      "url": "https://saudepilates.com.br/contact",
      "availableLanguage": ["Portuguese", "English", "Spanish", "French"]
    },
    "sameAs": []
  };

  const softwareSchema = {
    "@context": "https://schema.org",
    "@type": "SoftwareApplication",
    "name": "SaúdePilates",
    "url": "https://saudepilates.com.br",
    "applicationCategory": "BusinessApplication",
    "applicationSubCategory": "Studio Management Software",
    "operatingSystem": "Web",
    "offers": {
      "@type": "AggregateOffer",
      "lowPrice": "49.99",
      "highPrice": "399.99",
      "priceCurrency": "BRL",
      "offerCount": "3"
    },
    "description": "Software para estúdios de Pilates completo. Gerencie alunos, professores, aulas, presença, pagamentos e comissões em uma única plataforma online.",
    "aggregateRating": {
      "@type": "AggregateRating",
      "ratingValue": "4.8",
      "ratingCount": "127",
      "bestRating": "5"
    },
    "applicationSuite": "Gestão de Estúdios de Pilates",
    "screenshot": "https://saudepilates.com.br/og-image.jpg",
    "featureList": "Gerenciamento de alunos, Controle de professores, Agendamento de aulas, Controle de presença, Gestão financeira, Comissões de professores, Evolução dos alunos, Aulas experimentais, Multi-idiomas",
    "softwareVersion": "2.0",
    "inLanguage": ["pt-BR", "en", "es", "fr"],
    "creator": organizationSchema
  };

  const homeFaqSchema = {
    "@context": "https://schema.org",
    "@type": "FAQPage",
    "mainEntity": [
      {
        "@type": "Question",
        "name": "O que é o SaúdePilates?",
        "acceptedAnswer": {
          "@type": "Answer",
          "text": "O SaúdePilates é um software para estúdios de Pilates que permite gerenciar toda a operação do seu estúdio em uma única plataforma. Inclui gestão de alunos, agendamento de aulas, controle de presença, pagamentos, comissões de professores e relatórios financeiros."
        }
      },
      {
        "@type": "Question",
        "name": "Preciso instalar alguma coisa no computador?",
        "acceptedAnswer": {
          "@type": "Answer",
          "text": "Não. O SaúdePilates é 100% online e funciona em qualquer navegador, computador, tablet ou celular. Basta acessar o site e fazer login para começar a usar o software de gestão do seu estúdio de Pilates."
        }
      },
      {
        "@type": "Question",
        "name": "Como funciona o controle de presença e comissões?",
        "acceptedAnswer": {
          "@type": "Answer",
          "text": "O professor registra a presença dos alunos diretamente no calendário do software. Com base na presença e no plano do aluno, o sistema calcula automaticamente as comissões do professor, facilitando a gestão financeira do estúdio de Pilates."
        }
      },
      {
        "@type": "Question",
        "name": "Quantos alunos e professores posso cadastrar?",
        "acceptedAnswer": {
          "@type": "Answer",
          "text": "Não há limite de alunos cadastrados. Você pode registrar até 10 professores no plano atual. O software para estúdios de Pilates foi pensado para atender desde estúdios pequenos até operações maiores."
        }
      },
      {
        "@type": "Question",
        "name": "Posso testar o software antes de assinar?",
        "acceptedAnswer": {
          "@type": "Answer",
          "text": "Sim! Oferecemos 30 dias de teste grátis com acesso completo a todas as funcionalidades. Você pode experimentar o software para estúdios de Pilates sem compromisso e sem precisar cadastrar cartão de crédito."
        }
      },
      {
        "@type": "Question",
        "name": "O sistema funciona em outros idiomas?",
        "acceptedAnswer": {
          "@type": "Answer",
          "text": "Sim. O SaúdePilates está disponível em Português, Inglês, Espanhol e Francês. Você pode trocar o idioma a qualquer momento nas configurações, ideal para estúdios que atendem clientes internacionais."
        }
      }
    ]
  };

  const guideFaqSchema = {
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

  const schemas = [];

  if (currentPath === '/' || currentPath === '') {
    schemas.push(softwareSchema, homeFaqSchema);
  } else if (currentPath === '/guia' || currentPath === '/guia.html') {
    schemas.push(softwareSchema, guideFaqSchema);
  } else if (currentPath === '/pricing') {
    schemas.push(softwareSchema);
  } else {
    schemas.push(softwareSchema);
  }

  schemas.forEach(function(schema) {
    var script = document.createElement('script');
    script.type = 'application/ld+json';
    script.text = JSON.stringify(schema);
    document.head.appendChild(script);
  });
});
