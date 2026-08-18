class ChatManager {
    constructor() {
        this.messagesContainer = document.getElementById('chat-messages');
        this.userInput = document.getElementById('user-input');
        this.messages = [];
    }

    addMessage(text, sender) {
        const message = {
            text,
            sender,
            timestamp: new Date()
        };
        this.messages.push(message);
        this.renderMessage(message);
        this.scrollToBottom();
    }

    renderMessage(message) {
        const messageDiv = document.createElement('div');
        messageDiv.className = `message ${message.sender}`;

        const content = document.createElement('p');
        content.textContent = message.text;
        messageDiv.appendChild(content);

        this.messagesContainer.appendChild(messageDiv);
    }

    addLoadingMessage() {
        const messageDiv = document.createElement('div');
        messageDiv.className = 'message assistant loading-message';

        const content = document.createElement('p');
        content.innerHTML = '<span class="loading"></span> Processando...';
        messageDiv.appendChild(content);

        this.messagesContainer.appendChild(messageDiv);
        this.scrollToBottom();

        return messageDiv;
    }

    removeLoadingMessage() {
        const loadingMsg = this.messagesContainer.querySelector('.loading-message');
        if (loadingMsg) {
            loadingMsg.remove();
        }
    }

    clearMessages() {
        this.messages = [];
        this.messagesContainer.innerHTML = '';

        // Add welcome message
        this.addMessage('Bem-vindo ao Finance Voice AI! Como posso ajudar?', 'system');
    }

    scrollToBottom() {
        this.messagesContainer.scrollTop = this.messagesContainer.scrollHeight;
    }

    getUserInput() {
        return this.userInput.value.trim();
    }

    clearInput() {
        this.userInput.value = '';
    }

    setInputValue(text) {
        this.userInput.value = text;
    }

    disableInput() {
        this.userInput.disabled = true;
    }

    enableInput() {
        this.userInput.disabled = false;
        this.userInput.focus();
    }
}

// Export for use in other modules
window.ChatManager = ChatManager;
