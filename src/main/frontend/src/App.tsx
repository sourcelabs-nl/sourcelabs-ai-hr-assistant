import React, { useState, useEffect } from 'react';
import { Box, CssBaseline } from '@mui/material';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import Sidebar from './components/Sidebar';
import ChatInterface from './components/ChatInterface';
import { ChatSession } from './types/chat';
import { chatSessionService } from './services/chatSessionService';

const theme = createTheme({
  palette: {
    primary: {
      main: '#2563eb',
      light: '#e0f2fe',
    },
    secondary: {
      main: '#64748b',
    },
    background: {
      default: '#ffffff',
    },
  },
  typography: {
    fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none',
        },
      },
    },
  },
});

const App: React.FC = () => {
  const [sessions, setSessions] = useState<ChatSession[]>([]);
  const [activeSession, setActiveSession] = useState<ChatSession | null>(null);

  useEffect(() => {
    // Load initial state
    const allSessions = chatSessionService.getAllSessions();
    setSessions(allSessions);
    
    const currentActive = chatSessionService.getActiveSession();
    if (currentActive) {
      setActiveSession(currentActive);
    } else if (allSessions.length === 0) {
      // Create initial session if none exist
      handleNewChat();
    }
  }, []);

  const handleNewChat = () => {
    const newSession = chatSessionService.createNewSession();
    setSessions(chatSessionService.getAllSessions());
    setActiveSession(newSession);
  };

  const handleSelectSession = (sessionId: string) => {
    const session = chatSessionService.setActiveSession(sessionId);
    if (session) {
      setActiveSession(session);
    }
  };

  const handleDeleteSession = (sessionId: string) => {
    chatSessionService.deleteSession(sessionId);
    const updatedSessions = chatSessionService.getAllSessions();
    setSessions(updatedSessions);
    
    const newActive = chatSessionService.getActiveSession();
    setActiveSession(newActive);
    
    // If no sessions left, create a new one
    if (updatedSessions.length === 0) {
      handleNewChat();
    }
  };

  const refreshSessions = () => {
    setSessions(chatSessionService.getAllSessions());
    setActiveSession(chatSessionService.getActiveSession());
  };

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Box sx={{ display: 'flex', height: '100vh', overflow: 'hidden' }}>
        <Sidebar
          sessions={sessions}
          activeSessionId={activeSession?.id || null}
          onNewChat={handleNewChat}
          onSelectSession={handleSelectSession}
          onDeleteSession={handleDeleteSession}
        />
        
        <Box sx={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
          {activeSession ? (
            <ChatInterface
              session={activeSession}
              onSessionUpdate={refreshSessions}
            />
          ) : (
            <Box
              sx={{
                flex: 1,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                backgroundColor: '#fafafa'
              }}
            >
              Loading...
            </Box>
          )}
        </Box>
      </Box>
    </ThemeProvider>
  );
};

export default App;