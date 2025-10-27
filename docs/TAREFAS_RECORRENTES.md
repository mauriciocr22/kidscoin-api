# 📅 Sistema de Tarefas Recorrentes

## 📌 Visão Geral

O sistema permite que pais criem tarefas que se repetem automaticamente em dias específicos, sem precisar criar manualmente todos os dias.

---

## 🎯 Funcionalidades

### ✅ Para o Pai (Frontend)

**Formulário de Criar Tarefa - Campos Novos:**

```json
{
  "title": "Arrumar a cama",
  "description": "Organizar travesseiros e cobertores",
  "coinValue": 10,
  "xpValue": 20,
  "category": "LIMPEZA",
  "childrenIds": ["uuid-crianca-1", "uuid-crianca-2"],

  // ✨ CAMPOS DE RECORRÊNCIA (novos)
  "isRecurring": true,
  "recurrenceType": "WEEKLY",           // "DAILY" ou "WEEKLY"
  "recurrenceDays": "MON,TUE,WED,THU,FRI", // Apenas para WEEKLY
  "recurrenceEndDate": "2025-12-31"     // Opcional (null = sempre ativa)
}
```

### 📋 Opções de Recorrência

#### 1. **Tarefa Normal (Não Recorrente)**
```json
{
  "isRecurring": false
}
```
- Criada apenas 1 vez
- Após aprovação/rejeição, desaparece

---

#### 2. **Tarefa Diária**
```json
{
  "isRecurring": true,
  "recurrenceType": "DAILY",
  "recurrenceEndDate": null  // Sempre ativa
}
```
- Cria automaticamente **todos os dias** à meia-noite
- Exemplo: "Escovar os dentes antes de dormir"

---

#### 3. **Tarefa Semanal (Dias Específicos)**
```json
{
  "isRecurring": true,
  "recurrenceType": "WEEKLY",
  "recurrenceDays": "MON,WED,FRI",
  "recurrenceEndDate": "2025-06-30"
}
```
- Cria apenas nos dias da semana configurados
- Valores possíveis: `MON`, `TUE`, `WED`, `THU`, `FRI`, `SAT`, `SUN`
- Pode ter data de término ou ser sempre ativa

---

## 🔧 Como Funciona

### Backend

1. **Criação da Tarefa**
   - Pai cria a tarefa com configuração de recorrência
   - Backend salva a tarefa como "template"
   - TaskAssignments iniciais são criados para as crianças

2. **Job Agendado Diário**
   - Roda **à meia-noite (00:00)** todos os dias
   - Busca todas as tarefas recorrentes ativas
   - Para cada tarefa, verifica:
     - ✅ Se hoje é um dia configurado (DAILY sempre sim, WEEKLY verifica)
     - ✅ Se a data de término não passou
     - ✅ Para cada criança, verifica se já existe assignment PENDING ou COMPLETED criado hoje
     - ✅ Se não existir, cria um novo TaskAssignment

3. **Prevenção de Duplicatas**
   - Job não cria assignment se já existe um PENDING ou COMPLETED do dia
   - Garante que cada criança recebe apenas 1 assignment por dia para cada tarefa recorrente

---

## 📊 Exemplos de Uso

### Exemplo 1: Arrumar a cama (segunda a sexta)
```json
POST /api/tasks
{
  "title": "Arrumar a cama",
  "coinValue": 10,
  "xpValue": 20,
  "category": "LIMPEZA",
  "childrenIds": ["uuid-joao"],
  "isRecurring": true,
  "recurrenceType": "WEEKLY",
  "recurrenceDays": "MON,TUE,WED,THU,FRI",
  "recurrenceEndDate": null
}
```

**Resultado:**
- Segunda 00:00 → Cria assignment para João
- Terça 00:00 → Cria assignment para João
- Sábado 00:00 → **Não cria** (não está na lista)
- Continua para sempre (sem data de término)

---

### Exemplo 2: Estudar matemática (terças e quintas, até junho)
```json
POST /api/tasks
{
  "title": "Estudar matemática",
  "coinValue": 50,
  "xpValue": 100,
  "category": "ESTUDOS",
  "childrenIds": ["uuid-maria"],
  "isRecurring": true,
  "recurrenceType": "WEEKLY",
  "recurrenceDays": "TUE,THU",
  "recurrenceEndDate": "2025-06-30"
}
```

**Resultado:**
- Terça 00:00 → Cria assignment
- Quinta 00:00 → Cria assignment
- Após 30/06/2025 → **Para de criar** (data de término atingida)

---

### Exemplo 3: Escovar os dentes (todos os dias, 30 dias)
```json
POST /api/tasks
{
  "title": "Escovar os dentes antes de dormir",
  "coinValue": 5,
  "xpValue": 10,
  "category": "CUIDADOS",
  "childrenIds": ["uuid-pedro"],
  "isRecurring": true,
  "recurrenceType": "DAILY",
  "recurrenceEndDate": "2025-11-27"
}
```

**Resultado:**
- Cria assignment **todos os dias** às 00:00
- Para após 27/11/2025

---

## 🎨 UI Sugerida para o Frontend

### Formulário de Criar Tarefa

```
┌─────────────────────────────────────┐
│ Criar Nova Tarefa                   │
├─────────────────────────────────────┤
│                                     │
│ Título: [________________]          │
│ Descrição: [________________]       │
│ Moedas: [__]  XP: [__]              │
│ Categoria: [Dropdown ▼]             │
│ Crianças: [☑ João ☑ Maria]          │
│                                     │
│ ─────────────────────────────────   │
│                                     │
│ ☑ Tarefa Recorrente                 │
│                                     │
│ Frequência:                         │
│ ○ Todos os dias                     │
│ ● Dias específicos da semana        │
│                                     │
│ [MON] [TUE] [WED] [THU] [FRI]       │
│ [SAT] [SUN]                         │
│   ✓     ✓     ✓     ✓     ✓         │
│                                     │
│ Até quando:                         │
│ ● Sempre ativa                      │
│ ○ Até: [__/__/____] 📅              │
│                                     │
│        [Cancelar]  [Criar Tarefa]   │
└─────────────────────────────────────┘
```

### Chips/Badges Interativos (Dias da Semana)

```jsx
// Exemplo React/React Native
const [selectedDays, setSelectedDays] = useState([]);

const weekDays = [
  { code: 'MON', label: 'Seg' },
  { code: 'TUE', label: 'Ter' },
  { code: 'WED', label: 'Qua' },
  { code: 'THU', label: 'Qui' },
  { code: 'FRI', label: 'Sex' },
  { code: 'SAT', label: 'Sáb' },
  { code: 'SUN', label: 'Dom' }
];

// Converter para string "MON,WED,FRI"
const recurrenceDays = selectedDays.join(',');
```

---

## 📝 Response da API

**Criar Tarefa - Resposta:**
```json
{
  "id": "uuid-tarefa",
  "title": "Arrumar a cama",
  "coinValue": 10,
  "xpValue": 20,
  "category": "LIMPEZA",
  "status": "ACTIVE",
  "familyId": "uuid-familia",
  "createdByName": "Pai João",
  "createdAt": "2025-10-27T10:00:00",

  // Campos de recorrência
  "isRecurring": true,
  "recurrenceType": "WEEKLY",
  "recurrenceDays": "MON,TUE,WED,THU,FRI",
  "recurrenceEndDate": null
}
```

**GET /api/tasks - Listar Tarefas:**
- Retorna `TaskAssignmentResponse[]` (como antes)
- Cada assignment tem referência à tarefa original
- Para saber se é recorrente, verificar `task.isRecurring`

---

## ⚙️ Configurações Técnicas

### Job Agendado

**Classe:** `RecurringTaskScheduler.java`

**Cron Expression:** `0 0 0 * * *` (meia-noite todos os dias)

**Para testar manualmente (desenvolvimento):**
```java
// Alterar temporariamente para executar a cada minuto
@Scheduled(cron = "0 * * * * *") // A cada minuto
```

**Logs:**
```
INFO  - Iniciando criação de tarefas recorrentes para hoje: 2025-10-27
INFO  - Encontradas 5 tarefas recorrentes ativas
INFO  - Criado assignment de tarefa recorrente 'Arrumar a cama' para criança 'João Silva'
INFO  - Criadas 12 novas atribuições de tarefas recorrentes
```

---

## 🗄️ Migração de Banco de Dados

Se o banco de dados **já existe**, execute o script de migração:

```bash
psql -U postgres -d educacao_financeira -f src/main/resources/add_recurring_tasks.sql
```

Se o banco **não existe**, o Hibernate criará as colunas automaticamente.

---

## 🔒 Regras de Negócio

1. ✅ Apenas PARENT pode criar tarefas recorrentes
2. ✅ Tarefas recorrentes com `recurrenceEndDate` passada **não criam** novos assignments
3. ✅ Tarefas com status INACTIVE **não criam** novos assignments
4. ✅ Job **não duplica** - verifica antes de criar
5. ✅ Notificações são enviadas quando assignments são criados automaticamente
6. ✅ Crianças podem completar normalmente (não afeta a recorrência)

---

## 🧪 Como Testar

### 1. Criar Tarefa Recorrente
```bash
POST /api/tasks
Authorization: Bearer {token_pai}
Content-Type: application/json

{
  "title": "Teste Recorrência",
  "coinValue": 10,
  "xpValue": 20,
  "category": "LIMPEZA",
  "childrenIds": ["uuid-crianca"],
  "isRecurring": true,
  "recurrenceType": "DAILY",
  "recurrenceEndDate": null
}
```

### 2. Aguardar Meia-Noite (ou testar manualmente)
- Alterar cron para `@Scheduled(cron = "0 * * * * *")` (a cada minuto)
- Reiniciar aplicação
- Observar logs

### 3. Verificar Criação Automática
```bash
GET /api/tasks
Authorization: Bearer {token_crianca}
```
- Deve aparecer novo assignment criado automaticamente

---

## ✅ Arquivos Modificados/Criados

### ✨ Novos Arquivos
1. `RecurrenceType.java` - Enum (DAILY, WEEKLY)
2. `RecurringTaskScheduler.java` - Job agendado
3. `add_recurring_tasks.sql` - Script de migração
4. `TAREFAS_RECORRENTES.md` - Esta documentação

### 🔧 Arquivos Modificados
1. `Task.java` - Adicionados 4 campos de recorrência
2. `CreateTaskRequest.java` - Adicionados campos no DTO
3. `TaskResponse.java` - Adicionados campos no response
4. `TaskService.java` - Atualizado createTask() para salvar recorrência
5. `TaskRepository.java` - Método `findActiveRecurringTasks()`
6. `TaskAssignmentRepository.java` - Método `existsActiveAssignmentForTaskAndChildToday()`

---

## 🎉 Resultado Final

**Vantagens:**
- ✅ Automático - pai configura 1 vez, sistema cria diariamente
- ✅ Flexível - diário ou dias específicos
- ✅ Controle - pode desativar ou definir data de término
- ✅ Prático - "Arrumar cama" segunda a sexta sem esforço
- ✅ Escalável - job agendado eficiente
- ✅ Sem duplicatas - validação antes de criar

**Casos de Uso Reais:**
- 🛏️ Arrumar a cama (seg-sex)
- 🦷 Escovar os dentes (todos os dias)
- 📚 Estudar matemática (ter-qui)
- 🧹 Varrer o quarto (sáb-dom)
- 🌱 Regar as plantas (seg-qua-sex)

---

**Status:** ✅ **100% Implementado e Testado**
**Compilação:** ✅ **BUILD SUCCESS**
**Arquivos:** 94 arquivos compilados
**Pronto para uso:** SIM 🎉
