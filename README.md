# Website para Gestão de Eventos para Empresas

Este projeto é um sistema para gestão de eventos empresariais.
Utiliza uma arquitetura robusta no backend e um frontend moderno para oferecer uma experiência eficiente e intuitiva.

## Tecnologias Usadas
- **Backend**: Spring Boot Maven
- **Frontend**: HTML, CSS, Bootstrap, JavaScript
- **Base de Dados**: MySQL

## Funcionalidades Principais
- **Autenticação de utilizadores**:
  - Login para acesso seguro.
- **Calendário**:
  - Organização e visualização de eventos.
- **Gestão de eventos (CRUD)**:
  - Criar, editar, consultar e eliminar eventos.
- **Editores, Kanban e tabela de tempo por evento**:
  - Adicionar **Editores** para permitir que várias pessoas possam editar ao mesmo tempo.
  - Tabela **Kanban** para gerir a execução e tarefas do evento.
  - Tabela de **tempo** para planear tarefas no período do evento.
- **Registo de Logs**:
  - Auditoria de todas as ações realizadas na aplicação.
- **Chat em tempo real (um-para-um)**:
  - Comunicação direta entre utilizadores.
- **Atualização instantânea da interface**:
  - Alterações feitas por um utilizador são refletidas em tempo real para outro utilizador na mesma página, sem necessidade de atualização manual.
  - Disponível na rota `/event/{event_id}`.
