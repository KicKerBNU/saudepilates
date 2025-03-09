// This will be loaded in the main application
document.addEventListener('DOMContentLoaded', function() {
  const jsonLd = {
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
    }
  };

  const script = document.createElement('script');
  script.type = 'application/ld+json';
  script.text = JSON.stringify(jsonLd);
  document.head.appendChild(script);
});
