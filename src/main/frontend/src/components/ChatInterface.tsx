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
import { ChatSession, Message } from '../types/chat';
import { chatSessionService } from '../services/chatSessionService';

interface ChatInterfaceProps {
  session: ChatSession;
  onSessionUpdate: () => void;
}

const ChatInterface: React.FC<ChatInterfaceProps> = ({ session, onSessionUpdate }) => {
  const [input, setInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [session.messages]);

  const handleSendMessage = async () => {
    if (!input.trim() || isLoading) return;

    const userMessage: Message = {
      id: Date.now().toString(),
      role: 'user',
      content: input,
      timestamp: new Date()
    };

    chatSessionService.addMessage(userMessage);
    setInput('');
    setIsLoading(true);
    setError(null);
    onSessionUpdate();

    try {
      const request: ChatRequest = {
        message: input,
        sessionId: session.sessionId || undefined
      };

      const response: ChatResponse = await chatService.sendMessage(request);
      
      if (!session.sessionId) {
        chatSessionService.updateSessionId(response.sessionId);
      }

      const assistantMessage: Message = {
        id: (Date.now() + 1).toString(),
        role: 'assistant',
        content: response.message,
        timestamp: new Date()
      };

      chatSessionService.addMessage(assistantMessage);
      onSessionUpdate();
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

  const showWelcomeMessage = session.messages.length === 1 && session.messages[0].role === 'assistant';

  const exampleQueries = [
    "Register 8 hours of sick leave for employee123 from 2025-06-13 to 2025-06-13",
    "Log 6 billable hours for ClientABC at Amsterdam office on 2025-06-13",
    "Show me my leave history for employee123",
    "How many billable hours did employee123 log this year?"
  ];

  return (
    <Box sx={{ height: '100vh', display: 'flex', flexDirection: 'column' }}>
      {/* Header */}
      <Box sx={{ 
        borderBottom: '1px solid #e0e0e0', 
        p: 3,
        backgroundColor: 'white'
      }}>
        <Typography variant="h5" component="h1" fontWeight="bold" color="text.primary">
          {session.title}
        </Typography>
        <Typography variant="body2" color="text.secondary">
          AI-Powered HR Assistant
        </Typography>
      </Box>

      {/* Welcome message with examples for new chats */}
      {showWelcomeMessage && (
        <Box sx={{ p: 3, backgroundColor: '#fafafa', borderBottom: '1px solid #e0e0e0' }}>
          <Typography variant="body1" color="text.primary" gutterBottom>
            Ask me about leave hours, billable client hours, and register your hours through natural conversation
          </Typography>
          <Typography variant="subtitle2" color="text.secondary" gutterBottom sx={{ mt: 2 }}>
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
      )}

      {/* Messages area */}
      <Box 
        sx={{ 
          flex: 1, 
          p: 3, 
          overflow: 'auto',
          backgroundColor: '#ffffff'
        }}
      >
        {session.messages.map((message) => (
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
      </Box>

      {/* Input area */}
      <Box sx={{ 
        borderTop: '1px solid #e0e0e0', 
        p: 3,
        backgroundColor: 'white'
      }}>
        {/* Error display */}
        {error && (
          <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
            {error}
          </Alert>
        )}

        <Box sx={{ display: 'flex', gap: 1 }}>
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
            sx={{
              '& .MuiOutlinedInput-root': {
                borderRadius: 2,
              }
            }}
          />
          <IconButton
            onClick={handleSendMessage}
            disabled={!input.trim() || isLoading}
            color="primary"
            sx={{ 
              alignSelf: 'flex-end',
              p: 1.5,
              borderRadius: 2
            }}
          >
            <SendIcon />
          </IconButton>
        </Box>
      </Box>
    </Box>
  );
};

export default ChatInterface;