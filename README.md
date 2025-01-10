# Website para Gestão de Eventos para Empresas
O projeto utiliza a arquitetura Spring Boot Maven no backend e, no frontend, recorre a HTML, CSS, Bootstrap e JavaScript. A base de dados usada é MySQL.

Funcionalidades principais:
- Autenticação de utilizadores (Login).
- Calendário para visualização e organização de eventos.
- Gestão de eventos (CRUD): criar, editar, consultar e eliminar eventos.
- Kanban e tabela de tempo por evento:
    - A tabela Kanban ajuda na execução e gestão das tarefas do evento.
    - A tabela de tempo organiza as tarefas a realizar durante o período do evento.
- Registo de Logs para auditoria de ações.
- Chat em tempo real (um-para-um) para comunicação direta entre utilizadores.
- Atualização instantânea da interface:
    - Quando dois utilizadores estão na mesma página, se um realizar alterações nos dados do evento, o outro vê as mudanças sem necessidade de atualizar a página.
    - Esta funcionalidade está disponível apenas na rota /event/{event_id}.
