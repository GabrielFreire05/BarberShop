BarberShop
Este é o repositório para o aplicativo "BarberShop", um projeto que estou desenvolvendo para explorar e aplicar conceitos de desenvolvimento Android, com foco em Firebase para autenticação e banco de dados. A ideia é criar uma aplicação simples para agendamento de serviços de barbearia.

Visão Geral do Projeto
O objetivo principal é permitir que usuários:

Registrem-se e façam login no aplicativo.
Visualizem uma lista de serviços oferecidos pela barbearia.
Agendem horários para serviços específicos.
Visualizem e cancelem seus agendamentos.
Estou utilizando Kotlin para o desenvolvimento Android e Firebase como backend para gerenciar usuários (Authentication) e dados (Firestore para agendamentos e serviços, Storage para imagens).

Estrutura do Projeto (BarberApp)

Principais Componentes:
Activities:

  SplashActivity.kt: A tela de carregamento inicial que verifica o status de login do usuário.
  LoginActivity.kt: Lida com o login de usuários existentes e a redefinição de senha.
  RegisterActivity.kt: Permite que novos usuários se cadastrem, incluindo validação de e-mail e atualização do perfil do usuário.
  ServicosActivity.kt: Exibe a lista de serviços disponíveis na barbearia usando um RecyclerView.
  AgendamentoActivity.kt: Permite ao usuário selecionar uma data no CalendarView e um horário disponível para agendar um serviço. Os horários são carregados   dinamicamente do Firestore, desabilitando horários já ocupados ou passados.
  MeusAgendamentosActivity.kt: Exibe os agendamentos feitos pelo usuário logado e permite o cancelamento.
  Models:

Servico.kt: Classe de dados que representa um serviço da barbearia.
Agendamento.kt: Classe de dados para armazenar informações de um agendamento.
Horario.kt: Classe de dados simples para representar um horário e sua disponibilidade.
Adapters:

ServicoAdapter.kt: Adapta a lista de Servico para exibição em um RecyclerView. Utiliza Glide para carregamento de imagens.
HorarioAdapter.kt: Adapta a lista de Horario para exibição em um RecyclerView, controlando a disponibilidade dos horários.
AgendamentoAdapter.kt: Adapta a lista de Agendamento para exibição em um RecyclerView na tela "Meus Agendamentos".
Layouts (XML):

Os layouts estão em BarberApp/app/src/main/res/layout/ e definem a interface do usuário para cada tela, como activity_login.xml, activity_servicos.xml, activity_agendamento.xml, etc.
