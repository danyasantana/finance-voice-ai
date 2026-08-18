class AudioHandler {
    constructor() {
        this.recognition = null;
        this.synthesis = window.speechSynthesis;
        this.isRecording = false;
        this.onTranscript = null;
        this.onError = null;
        this.onEnd = null;

        this.initRecognition();
    }

    initRecognition() {
        if (!('webkitSpeechRecognition' in window) && !('SpeechRecognition' in window)) {
            console.warn('Speech Recognition not supported');
            return;
        }

        const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
        this.recognition = new SpeechRecognition();
        this.recognition.lang = 'pt-BR';
        this.recognition.continuous = false;
        this.recognition.interimResults = false;
        this.recognition.maxAlternatives = 1;

        this.recognition.onresult = (event) => {
            const transcript = event.results[0][0].transcript;
            console.log('Transcript:', transcript);
            if (this.onTranscript) {
                this.onTranscript(transcript);
            }
        };

        this.recognition.onerror = (event) => {
            console.error('Speech recognition error:', event.error);
            this.isRecording = false;
            if (this.onError) {
                this.onError(event.error);
            }
        };

        this.recognition.onend = () => {
            console.log('Speech recognition ended');
            this.isRecording = false;
            if (this.onEnd) {
                this.onEnd();
            }
        };
    }

    startRecording() {
        if (!this.recognition) {
            console.error('Speech Recognition not available');
            if (this.onError) {
                this.onError('Speech Recognition not available');
            }
            return false;
        }

        if (this.isRecording) {
            this.stopRecording();
            return false;
        }

        try {
            this.recognition.start();
            this.isRecording = true;
            console.log('Recording started');
            return true;
        } catch (error) {
            console.error('Error starting recording:', error);
            return false;
        }
    }

    stopRecording() {
        if (this.recognition && this.isRecording) {
            this.recognition.stop();
            this.isRecording = false;
            console.log('Recording stopped');
        }
    }

    speak(text, options = {}) {
        return new Promise((resolve, reject) => {
            if (!this.synthesis) {
                console.warn('Speech Synthesis not supported');
                reject('Speech Synthesis not available');
                return;
            }

            // Cancel any ongoing speech
            this.synthesis.cancel();

            const utterance = new SpeechSynthesisUtterance(text);
            utterance.lang = options.lang || 'pt-BR';
            utterance.rate = options.rate || 1.0;
            utterance.pitch = options.pitch || 1.0;
            utterance.volume = options.volume || 1.0;

            // Try to find a Portuguese voice
            const voices = this.synthesis.getVoices();
            const ptVoice = voices.find(v => v.lang.startsWith('pt'));
            if (ptVoice) {
                utterance.voice = ptVoice;
            }

            utterance.onend = () => {
                console.log('Speech synthesis ended');
                resolve();
            };

            utterance.onerror = (event) => {
                console.error('Speech synthesis error:', event.error);
                reject(event.error);
            };

            this.synthesis.speak(utterance);
        });
    }

    stopSpeaking() {
        if (this.synthesis) {
            this.synthesis.cancel();
        }
    }

    isAvailable() {
        return {
            recognition: !!this.recognition,
            synthesis: !!this.synthesis
        };
    }
}

// Export for use in other modules
window.AudioHandler = AudioHandler;
