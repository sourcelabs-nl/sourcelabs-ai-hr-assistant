import React from 'react';
import { Container, Box, Typography, Paper } from '@mui/material';
import ChatInterface from './components/ChatInterface';

const App: React.FC = () => {
  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Paper elevation={3} sx={{ p: 4, minHeight: '80vh' }}>
        <Box textAlign="center" mb={4}>
          <Typography variant="h3" component="h1" color="primary" gutterBottom>
            SourceChat
          </Typography>
          <Typography variant="h5" component="h2" color="text.secondary" gutterBottom>
            AI-Powered HR Assistant
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Ask me about leave hours, billable client hours, and register your hours through natural conversation
          </Typography>
        </Box>
        
        <ChatInterface />
      </Paper>
    </Container>
  );
};

export default App;