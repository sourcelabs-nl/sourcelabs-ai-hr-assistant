import React, { useState, useRef, useEffect } from 'react';
import {
  Box,
  Paper,
  TextField,
  IconButton,
  Typography,
  CircularProgress,
  Alert,
  Chip
} from '@mui/material';
import { Send as SendIcon, Person as PersonIcon, SmartToy as BotIcon } from '@mui/icons-material';
import { chatService, ChatRequest, ChatResponse } from '../services/chatService';

interface Message {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  timestamp: Date;
}

const ChatInterface: React.FC = () => {
  const [messages, setMessages] = useState<Message[]>([
    {
      id: '1',
      role: 'assistant',
      content: 'Hello! I\'m your Sourcelabs HR assistant. I can help you register leave hours, billable client hours, and answer questions about the employee manual. How can I assist you today?',
      timestamp: new Date()
    }
  ]);
  const [input, setInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [sessionId, setSessionId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSendMessage = async () => {
    if (!input.trim() || isLoading) return;

    const userMessage: Message = {
      id: Date.now().toString(),
      role: 'user',
      content: input,
      timestamp: new Date()
    };

    setMessages(prev => [...prev, userMessage]);
    setInput('');
    setIsLoading(true);
    setError(null);

    try {
      const request: ChatRequest = {
        message: input,
        sessionId: sessionId || undefined
      };

      const response: ChatResponse = await chatService.sendMessage(request);
      
      if (!sessionId) {
        setSessionId(response.sessionId);
      }

      const assistantMessage: Message = {
        id: (Date.now() + 1).toString(),
        role: 'assistant',
        content: response.message,
        timestamp: new Date()
      };

      setMessages(prev => [...prev, assistantMessage]);
    } catch (err) {
      console.error('Error sending message:', err);
      setError('Sorry, I encountered an error. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleKeyPress = (event: React.KeyboardEvent) => {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      handleSendMessage();
    }
  };

  const exampleQueries = [
    "Register 8 hours of sick leave for employee123 from 2025-06-13 to 2025-06-13",
    "Log 6 billable hours for ClientABC at Amsterdam office on 2025-06-13",
    "Show me my leave history for employee123",
    "How many billable hours did employee123 log this year?"
  ];

  return (
    <Box sx={{ height: '600px', display: 'flex', flexDirection: 'column' }}>
      {/* Example queries */}
      <Box sx={{ mb: 2 }}>
        <Typography variant="subtitle2" color="text.secondary" gutterBottom>
          Try these examples:
        </Typography>
        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
          {exampleQueries.map((query, index) => (
            <Chip
              key={index}
              label={query}
              variant="outlined"
              size="small"
              onClick={() => setInput(query)}
              sx={{ cursor: 'pointer', fontSize: '0.75rem' }}
            />
          ))}
        </Box>
      </Box>

      {/* Messages area */}
      <Paper 
        variant="outlined" 
        sx={{ 
          flex: 1, 
          p: 2, 
          overflow: 'auto',
          backgroundColor: '#f8f9fa'
        }}
      >
        {messages.map((message) => (
          <Box
            key={message.id}
            sx={{
              display: 'flex',
              mb: 2,
              justifyContent: message.role === 'user' ? 'flex-end' : 'flex-start'
            }}
          >
            <Box
              sx={{
                display: 'flex',
                alignItems: 'flex-start',
                gap: 1,
                maxWidth: '80%',
                flexDirection: message.role === 'user' ? 'row-reverse' : 'row'
              }}
            >
              <Box
                sx={{
                  p: 1,
                  borderRadius: '50%',
                  backgroundColor: message.role === 'user' ? 'primary.main' : 'secondary.main',
                  color: 'white',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  minWidth: 40,
                  height: 40
                }}
              >
                {message.role === 'user' ? <PersonIcon /> : <BotIcon />}
              </Box>
              <Paper
                sx={{
                  p: 2,
                  backgroundColor: message.role === 'user' ? 'primary.main' : 'white',
                  color: message.role === 'user' ? 'white' : 'text.primary',
                  borderRadius: 2,
                  whiteSpace: 'pre-wrap'
                }}
              >
                <Typography variant="body1">
                  {message.content}
                </Typography>
                <Typography 
                  variant="caption" 
                  sx={{ 
                    opacity: 0.7,
                    display: 'block',
                    mt: 0.5
                  }}
                >
                  {message.timestamp.toLocaleTimeString()}
                </Typography>
              </Paper>
            </Box>
          </Box>
        ))}
        
        {isLoading && (
          <Box sx={{ display: 'flex', justifyContent: 'flex-start', mb: 2 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <Box
                sx={{
                  p: 1,
                  borderRadius: '50%',
                  backgroundColor: 'secondary.main',
                  color: 'white',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  minWidth: 40,
                  height: 40
                }}
              >
                <BotIcon />
              </Box>
              <Paper sx={{ p: 2, borderRadius: 2 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  <CircularProgress size={16} />
                  <Typography variant="body1">HR Assistant is thinking...</Typography>
                </Box>
              </Paper>
            </Box>
          </Box>
        )}
        
        <div ref={messagesEndRef} />
      </Paper>

      {/* Error display */}
      {error && (
        <Alert severity="error" sx={{ mt: 1 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      {/* Input area */}
      <Box sx={{ mt: 2, display: 'flex', gap: 1 }}>
        <TextField
          fullWidth
          multiline
          maxRows={3}
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyPress={handleKeyPress}
          placeholder="Ask me about hours registration or the employee manual..."
          disabled={isLoading}
          variant="outlined"
        />
        <IconButton
          onClick={handleSendMessage}
          disabled={!input.trim() || isLoading}
          color="primary"
          sx={{ alignSelf: 'flex-end', mb: 1 }}
        >
          <SendIcon />
        </IconButton>
      </Box>
    </Box>
  );
};

export default ChatInterface;