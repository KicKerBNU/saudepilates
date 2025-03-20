<script setup>
import SeoHead from '../components/SeoHead.vue';
</script>

<template>
  <SeoHead 
    title="Guia Completo - SaúdePilates | Como utilizar nossa plataforma"
    description="Guia completo do SaúdePilates para administradores, professores e alunos. Aprenda a gerenciar planos, usuários, pagamentos e todas as funcionalidades da nossa plataforma."
    keywords="guia pilates, gestão estúdio pilates, administrador pilates, professor pilates, aluno pilates, gerenciamento pagamentos, agendamento aulas"
    ogType="article"
    canonicalPath="/guia"
  />
  
  <div class="guide-container max-w-5xl mx-auto px-4 py-8">
    <h1 class="text-3xl md:text-4xl font-bold mb-6 text-center">Guia Completo da Plataforma SaúdePilates</h1>
    
    <div class="mt-8">
      <div class="flex justify-center mb-8">
        <nav class="bg-gray-100 p-2 rounded-lg">
          <ul class="flex space-x-4">
            <li><a href="#admin" class="px-3 py-2 rounded hover:bg-gray-200">Administrador</a></li>
            <li><a href="#professor" class="px-3 py-2 rounded hover:bg-gray-200">Professor</a></li>
            <li><a href="#student" class="px-3 py-2 rounded hover:bg-gray-200">Aluno</a></li>
          </ul>
        </nav>
      </div>

      <!-- Introdução -->
      <section class="mb-12">
        <p class="mb-4">
          Bem-vindo ao guia completo da plataforma SaúdePilates! Aqui você encontrará todas as informações necessárias 
          para utilizar nosso sistema, independentemente do seu papel: administrador de estúdio, professor ou aluno.
          Nossa plataforma foi desenvolvida para facilitar a gestão de estúdios de Pilates, otimizando processos
          e melhorando a experiência de todos os envolvidos.
        </p>
      </section>

      <!-- Jornada do Administrador -->
      <section id="admin" class="mb-12">
        <h2 class="text-2xl md:text-3xl font-semibold mb-4 border-b pb-2">Jornada do Administrador</h2>
        
        <div class="space-y-6">
          <div>
            <h3 class="text-xl font-semibold mb-2">Dashboard</h3>
            <p>
              O Dashboard do administrador oferece uma visão geral do seu estúdio, com métricas importantes como:
              número de alunos ativos, professores, pagamentos do mês, aulas agendadas e receita mensal.
              Todos os dados são apresentados em formato visual para facilitar a compreensão rápida do desempenho do estúdio.
            </p>
          </div>

          <div>
            <h3 class="text-xl font-semibold mb-2">Gerenciamento de Planos</h3>
            <p>
              Crie e gerencie diferentes planos para seu estúdio. Defina valores, quantidade de aulas por semana,
              duração do plano e benefícios inclusos. Os planos são a base do seu modelo de negócio e podem ser
              personalizados de acordo com a necessidade do seu estúdio.
            </p>
          </div>

          <div>
            <h3 class="text-xl font-semibold mb-2">Gerenciamento de Professores</h3>
            <p>
              Adicione novos professores ao sistema, defina suas especialidades, disponibilidade de horários
              e percentual de comissão. É possível acompanhar o desempenho de cada professor, incluindo número de aulas ministradas
              e avaliações dos alunos.
            </p>
          </div>

          <div>
            <h3 class="text-xl font-semibold mb-2">Gerenciamento de Alunos</h3>
            <p>
              Cadastre novos alunos, vincule a planos, visualize histórico de pagamentos e progresso.
              Monitore a assiduidade e os resultados obtidos por cada aluno, facilitando o acompanhamento
              e fidelização.
            </p>
          </div>

          <div>
            <h3 class="text-xl font-semibold mb-2">Registro de Pagamentos</h3>
            <p>
              Registre e acompanhe todos os pagamentos realizados pelos alunos. O sistema oferece relatórios
              detalhados sobre pagamentos pendentes, atrasados e realizados, ajudando na gestão financeira do estúdio.
            </p>
          </div>

          <div>
            <h3 class="text-xl font-semibold mb-2">Pagamentos Mensais</h3>
            <p>
              Visualize os pagamentos agendados para o mês atual, com status de cada pagamento e opções para
              registrar novos pagamentos ou modificar existentes. Esta funcionalidade facilita o controle de fluxo
              de caixa mensal.
            </p>
          </div>
        </div>
      </section>

      <!-- Jornada do Professor -->
      <section id="professor" class="mb-12">
        <h2 class="text-2xl md:text-3xl font-semibold mb-4 border-b pb-2">Jornada do Professor</h2>
        
        <div class="space-y-6">
          <div>
            <h3 class="text-xl font-semibold mb-2">Dashboard</h3>
            <p>
              O Dashboard do professor apresenta informações relevantes como próximas aulas agendadas,
              número de alunos ativos, ganhos no mês atual e avaliações recebidas. Esta visão centralizada
              ajuda o professor a organizar seu dia de trabalho.
            </p>
          </div>

          <div>
            <h3 class="text-xl font-semibold mb-2">Gerenciamento de Alunos</h3>
            <p>
              Visualize a lista de alunos sob sua responsabilidade, histórico de aulas, evolução e observações.
              Adicione notas sobre o progresso de cada aluno e recomendações para futuras aulas.
            </p>
          </div>

          <div>
            <h3 class="text-xl font-semibold mb-2">Histórico de Ganhos</h3>
            <p>
              Acompanhe seus ganhos ao longo do tempo, com detalhamento por períodos, alunos e tipo de aula.
              Relatórios visuais ajudam a entender padrões de rendimento e identificar oportunidades de crescimento.
            </p>
          </div>

          <div>
            <h3 class="text-xl font-semibold mb-2">Controle de Presença</h3>
            <p>
              Registre a presença dos alunos nas aulas, faça anotações sobre cada sessão e registre o progresso.
              O sistema permite marcar presenças, faltas justificadas ou não justificadas, mantendo um histórico
              completo da assiduidade.
            </p>
          </div>

          <div>
            <h3 class="text-xl font-semibold mb-2">Evolução dos Alunos</h3>
            <p>
              Acompanhe a evolução de cada aluno, registrando marcos importantes, mudanças em condições físicas
              e objetivos alcançados. Esta ferramenta é fundamental para demonstrar valor e resultados aos alunos.
            </p>
          </div>

          <div>
            <h3 class="text-xl font-semibold mb-2">Agenda</h3>
            <p>
              Visualize sua agenda de aulas, com detalhes de cada horário, aluno e tipo de aula. Gerencie sua
              disponibilidade e receba notificações sobre alterações na agenda.
            </p>
          </div>
        </div>
      </section>

      <!-- Jornada do Aluno -->
      <section id="student" class="mb-12">
        <h2 class="text-2xl md:text-3xl font-semibold mb-4 border-b pb-2">Jornada do Aluno</h2>
        
        <div class="space-y-6">
          <div>
            <h3 class="text-xl font-semibold mb-2">Dashboard</h3>
            <p>
              O Dashboard do aluno mostra informações como próximas aulas agendadas, histórico de presença,
              plano atual e status de pagamento. Esta visão personalizada ajuda o aluno a se manter informado
              sobre seu progresso e compromissos.
            </p>
          </div>

          <div>
            <h3 class="text-xl font-semibold mb-2">Agendamento de Aulas</h3>
            <p>
              Agende novas aulas conforme a disponibilidade do professor e seu plano. Visualize horários disponíveis
              e faça reservas de forma simples e intuitiva.
            </p>
          </div>

          <div>
            <h3 class="text-xl font-semibold mb-2">Histórico de Aulas</h3>
            <p>
              Acesse o histórico completo de aulas realizadas, incluindo data, professor, tipo de aula e observações.
              Este registro permite acompanhar a consistência da prática ao longo do tempo.
            </p>
          </div>

          <div>
            <h3 class="text-xl font-semibold mb-2">Progresso e Evolução</h3>
            <p>
              Visualize seu progresso ao longo do tempo, com marcos de evolução registrados pelo professor.
              Acompanhe objetivos alcançados e próximos desafios.
            </p>
          </div>

          <div>
            <h3 class="text-xl font-semibold mb-2">Pagamentos</h3>
            <p>
              Verifique o status dos seus pagamentos, histórico e próximos vencimentos. O sistema oferece
              transparência total sobre a situação financeira do aluno com o estúdio.
            </p>
          </div>
        </div>
      </section>

      <!-- Conclusão -->
      <section class="mb-8">
        <h2 class="text-2xl font-semibold mb-4">Conclusão</h2>
        <p>
          A plataforma SaúdePilates foi desenvolvida para proporcionar uma experiência completa e integrada
          para todos os envolvidos no processo de ensino e prática do Pilates. Ao conectar administradores,
          professores e alunos em um único sistema, facilitamos a comunicação, o acompanhamento e a gestão,
          permitindo que todos se concentrem no que realmente importa: resultados e bem-estar.
        </p>
        <p class="mt-4">
          Caso tenha dúvidas adicionais sobre como utilizar qualquer funcionalidade da plataforma,
          entre em contato com nosso suporte através da <a href="/contact" class="text-blue-600 hover:underline">página de contato</a>.
        </p>
      </section>
    </div>
  </div>
</template>

<style scoped>
.guide-container h2 {
  color: #1e40af;
}

.guide-container h3 {
  color: #2563eb;
}

.guide-container a {
  color: #3b82f6;
  text-decoration: none;
}

.guide-container a:hover {
  text-decoration: underline;
}
</style> 