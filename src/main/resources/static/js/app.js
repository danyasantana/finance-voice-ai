document.addEventListener('DOMContentLoaded', () => {
    // Initialize handlers
    const chat = new ChatManager();
    const audio = new AudioHandler();

    // DOM Elements
    const btnSend = document.getElementById('btn-send');
    const btnRecord = document.getElementById('btn-record');
    const btnClear = document.getElementById('btn-clear');
    const btnRefresh = document.getElementById('btn-refresh');
    const btnModerate = document.getElementById('btn-moderate');
    const statusSpan = document.getElementById('status');
    const navButtons = document.querySelectorAll('.nav-btn');

    // Areas
    const chatArea = document.getElementById('chat-area');
    const imageArea = document.getElementById('image-area');
    const moderationArea = document.getElementById('moderation-area');

    let currentAction = 'chat';
    let sessionId = 'default';

    // Update status
    function setStatus(text) {
        statusSpan.textContent = text;
    }

    // Show/hide areas
    function showArea(action) {
        chatArea.classList.add('hidden');
        imageArea.classList.add('hidden');
        moderationArea.classList.add('hidden');

        switch (action) {
            case 'chat':
            case 'voice':
                chatArea.classList.remove('hidden');
                break;
            case 'image':
                imageArea.classList.remove('hidden');
                break;
            case 'moderation':
                moderationArea.classList.remove('hidden');
                break;
            case 'search':
                chatArea.classList.remove('hidden');
                break;
        }
    }

    // Navigation
    navButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            navButtons.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            currentAction = btn.dataset.action;
            showArea(currentAction);
        });
    });

    // Send message
    async function sendMessage(text) {
        if (!text) return;

        chat.addMessage(text, 'user');
        chat.clearInput();
        setStatus('Processando...');

        const loadingMsg = chat.addLoadingMessage();

        try {
            let response;

            if (currentAction === 'search') {
                // Semantic search
                response = await API.searchEmbedding(text);
                chat.removeLoadingMessage();

                if (response.length === 0) {
                    chat.addMessage('Nenhum resultado encontrado.', 'assistant');
                } else {
                    const results = response.map(r => {
                        const t = r.transaction;
                        return `${t.type.description}: R$ ${t.money.amount} - ${t.description || 'Sem descricao'} (similaridade: ${(r.score * 100).toFixed(1)}%)`;
                    }).join('\n');
                    chat.addMessage(`Resultados da busca:\n${results}`, 'assistant');
                }
            } else {
                // Regular chat
                response = await API.chat(text, sessionId);
                chat.removeLoadingMessage();
                chat.addMessage(response.response || response.message || response, 'assistant');

                // Try to speak the response
                try {
                    await audio.speak(response.response || response.message || response);
                } catch (e) {
                    console.log('Could not speak response:', e);
                }
            }
        } catch (error) {
            chat.removeLoadingMessage();
            chat.addMessage(`Erro: ${error.message}`, 'error');
        }

        setStatus('Pronto');
    }

    // Send button click
    btnSend.addEventListener('click', () => {
        const text = chat.getUserInput();
        sendMessage(text);
    });

    // Enter key to send
    document.getElementById('user-input').addEventListener('keydown', (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            const text = chat.getUserInput();
            sendMessage(text);
        }
    });

    // Record button
    btnRecord.addEventListener('click', async () => {
        if (audio.isRecording) {
            audio.stopRecording();
            btnRecord.classList.remove('recording');
            btnRecord.textContent = 'Gravar';
            setStatus('Pronto');
        } else {
            const started = audio.startRecording();
            if (started) {
                btnRecord.classList.add('recording');
                btnRecord.textContent = 'Parar';
                setStatus('Gravando...');
            }
        }
    });

    // Audio transcript callback
    audio.onTranscript = async (transcript) => {
        btnRecord.classList.remove('recording');
        btnRecord.textContent = 'Gravar';
        setStatus('Transcrevendo...');

        chat.addMessage(transcript, 'user');
        setStatus('Processando...');

        const loadingMsg = chat.addLoadingMessage();

        try {
            const response = await API.chat(transcript, sessionId);
            chat.removeLoadingMessage();
            const responseText = response.response || response.message || response;
            chat.addMessage(responseText, 'assistant');

            // Speak response
            try {
                await audio.speak(responseText);
            } catch (e) {
                console.log('Could not speak response:', e);
            }
        } catch (error) {
            chat.removeLoadingMessage();
            chat.addMessage(`Erro: ${error.message}`, 'error');
        }

        setStatus('Pronto');
    };

    audio.onError = (error) => {
        btnRecord.classList.remove('recording');
        btnRecord.textContent = 'Gravar';
        chat.addMessage(`Erro de audio: ${error}`, 'error');
        setStatus('Pronto');
    };

    audio.onEnd = () => {
        btnRecord.classList.remove('recording');
        btnRecord.textContent = 'Gravar';
    };

    // Clear chat
    btnClear.addEventListener('click', () => {
        chat.clearMessages();
    });

    // Refresh transactions
    btnRefresh.addEventListener('click', async () => {
        try {
            const transactions = await API.getTransactions();
            const list = document.getElementById('transaction-list');
            list.innerHTML = '';

            transactions.forEach(t => {
                const li = document.createElement('li');
                li.className = `transaction-item ${t.type.toLowerCase()}`;
                li.innerHTML = `
                    <div>${t.description || 'Sem descricao'}</div>
                    <div class="transaction-amount ${t.type.toLowerCase()}">
                        ${t.type === 'INCOME' ? '+' : '-'} R$ ${t.money.amount}
                    </div>
                `;
                list.appendChild(li);
            });
        } catch (error) {
            console.error('Error loading transactions:', error);
        }
    });

    // Moderation
    if (btnModerate) {
        btnModerate.addEventListener('click', async () => {
            const text = document.getElementById('moderation-input').value.trim();
            if (!text) return;

            setStatus('Verificando...');
            const resultDiv = document.getElementById('moderation-result');

            try {
                const result = await API.moderate(text);
                resultDiv.className = `moderation-result ${result.approved ? 'approved' : 'rejected'}`;
                resultDiv.innerHTML = result.approved
                    ? `<strong>Aprovado!</strong> Conteudo apropriado.`
                    : `<strong>Rejeitado!</strong> Motivo: ${result.reason || 'Conteudo nao apropriado'}`;
            } catch (error) {
                resultDiv.className = 'moderation-result rejected';
                resultDiv.innerHTML = `<strong>Erro:</strong> ${error.message}`;
            }

            setStatus('Pronto');
        });
    }

    // Initial load
    showArea('chat');
    setStatus('Pronto');
});
