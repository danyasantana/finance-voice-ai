# Finance Voice AI

API Inteligente com Spring Boot e Spring AI - Orçamento Financeiro com Voz

## 📋 Visão Geral

Sistema completo de orçamento financeiro que interpreta comandos de voz, transforma áudio em texto, executa funções reais e gera respostas áudio. Utiliza 100% de providers gratuitos (Ollama local).

## 🚀 Funcionalidades Spring AI

| Feature | Status | Descrição |
|---------|--------|-----------|
| **Chat Completion** | ✅ | Conversa com IA usando Ollama (llama3.2) |
| **Embedding** | ✅ | Busca semântica de transações |
| **Text to Image** | ⏳ | Geração de imagens (fase futura) |
| **Audio Transcription** | ✅ | Transcrição via Web Speech API |
| **Text to Speech** | ✅ | Síntese via Web Speech API |
| **Moderation** | ✅ | Filtragem de conteúdo ofensivo |

## 🛠 Stack Tecnológica

- **Backend:** Java 21, Spring Boot 3.4.1, Spring AI 1.0.0
- **Database:** PostgreSQL 16
- **AI Provider:** Ollama (100% gratuito e local)
- **Frontend:** Vanilla HTML/CSS/JavaScript
- **Arquitetura:** DDD + Clean Architecture + SOLID

## 📁 Estrutura do Projeto

```
src/main/java/com/finance/voice/
├── FinanceVoiceApplication.java
├── domain/
│   ├── shared/
│   │   ├── Money.java
│   │   ├── Currency.java
│   │   ├── CategoryId.java
│   │   └── TransactionId.java
│   ├── category/
│   │   ├── Category.java
│   │   └── CategoryRepository.java
│   └── transaction/
│       ├── Transaction.java
│       ├── TransactionType.java
│       └── TransactionRepository.java
├── infrastructure/
│   ├── ai/
│   │   ├── ChatService.java
│   │   ├── EmbeddingService.java
│   │   ├── ModerationService.java
│   │   └── tools/FinancialTools.java
│   ├── config/AiConfig.java
│   ├── exception/GlobalExceptionHandler.java
│   └── persistence/
│       ├── JdbcCategoryRepository.java
│       └── JdbcTransactionRepository.java
└── interfaces/
    └── rest/
        ├── ChatController.java
        ├── TransactionController.java
        ├── VoiceController.java
        ├── EmbeddingController.java
        ├── ModerationController.java
        └── dto/
            └── (10 DTOs)
```

## 🏃 Como Executar

### Pré-requisitos
- Java 21
- Docker e Docker Compose
- Ollama instalado

### 1. Iniciar Infraestrutura
```bash
docker-compose up -d
```

### 2. Baixar Modelos Ollama
```bash
ollama pull llama3.2
ollama pull nomic-embed-text
```

### 3. Executar Aplicação
```bash
./mvnw spring-boot:run
```

### 4. Acessar Frontend
```
http://localhost:8080
```

## 📡 API Endpoints

### Chat
- `POST /api/chat` - Envia mensagem para IA

### Transactions
- `GET /api/transactions` - Lista transações
- `POST /api/transactions` - Cria transação
- `DELETE /api/transactions/{id}` - Remove transação

### Voice
- `POST /api/voice/process` - Processa comando de voz

### Embedding
- `POST /api/embedding/search` - Busca semântica

### Moderation
- `POST /api/moderation` - Verifica conteúdo

## 🧪 Testes

```bash
./mvnw test
```

## 📚 Arquitetura

### DDD (Domain-Driven Design)
- **Domain:** Entidades, Value Objects, Repositories (portas)
- **Infrastructure:** Implementações (AI, Persistence)
- **Interfaces:** Controllers e DTOs

### Clean Architecture
- Dependências apontam para dentro
- Domain não depende de infraestrutura
- Testes unitários isolados

## 🔧 Tool Calling (FinancialTools)

5 ferramentas registradas para a IA:

1. `registerTransaction` - Registra receita/despesa
2. `checkBalance` - Consulta saldo
3. `listTransactions` - Lista por período
4. `getMonthlySummary` - Resumo mensal
5. `deleteTransaction` - Remove transação

## 📝 Licença

MIT
