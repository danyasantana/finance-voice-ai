const API = {
    baseUrl: '/api/v1',

    async chat(message, sessionId = 'default') {
        const response = await fetch(`${this.baseUrl}/chat`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ message, sessionId })
        });
        if (!response.ok) throw new Error('Erro ao enviar mensagem');
        return response.json();
    },

    async searchEmbedding(query, topK = 5) {
        const response = await fetch(`${this.baseUrl}/embedding/search`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ query, topK })
        });
        if (!response.ok) throw new Error('Erro na busca semantica');
        return response.json();
    },

    async moderate(text) {
        const response = await fetch(`${this.baseUrl}/moderation/check`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ text })
        });
        if (!response.ok) throw new Error('Erro na moderacao');
        return response.json();
    },

    async getTransactions() {
        const response = await fetch(`${this.baseUrl}/transactions`);
        if (!response.ok) throw new Error('Erro ao buscar transacoes');
        return response.json();
    },

    async createTransaction(data) {
        const response = await fetch(`${this.baseUrl}/transactions`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        if (!response.ok) throw new Error('Erro ao criar transacao');
        return response.json();
    }
};
