import React from 'react';
import {
  Box,
  Button,
  List,
  ListItem,
  ListItemButton,
  ListItemText,
  Typography,
  IconButton,
  Paper
} from '@mui/material';
import {
  Add as AddIcon,
  Chat as ChatIcon,
  Delete as DeleteIcon,
  Work as WorkIcon
} from '@mui/icons-material';
import { ChatSession } from '../types/chat';

interface SidebarProps {
  sessions: ChatSession[];
  activeSessionId: string | null;
  onNewChat: () => void;
  onSelectSession: (sessionId: string) => void;
  onDeleteSession: (sessionId: string) => void;
}

const Sidebar: React.FC<SidebarProps> = ({
  sessions,
  activeSessionId,
  onNewChat,
  onSelectSession,
  onDeleteSession
}) => {
  const formatDate = (date: Date): string => {
    const now = new Date();
    const diff = now.getTime() - date.getTime();
    const diffDays = Math.floor(diff / (1000 * 60 * 60 * 24));

    if (diffDays === 0) {
      return 'Today';
    } else if (diffDays === 1) {
      return 'Yesterday';
    } else if (diffDays < 7) {
      return `${diffDays} days ago`;
    } else {
      return date.toLocaleDateString();
    }
  };

  return (
    <Box
      sx={{
        width: 300,
        height: '100vh',
        backgroundColor: '#f8f9fa',
        borderRight: '1px solid #e0e0e0',
        display: 'flex',
        flexDirection: 'column'
      }}
    >
      {/* Header */}
      <Box sx={{ p: 3, borderBottom: '1px solid #e0e0e0' }}>
        <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
          <WorkIcon sx={{ mr: 1, color: 'primary.main' }} />
          <Typography variant="h6" component="h1" color="primary.main" fontWeight="bold">
            SourceChat
          </Typography>
        </Box>
        
        <Button
          fullWidth
          variant="contained"
          startIcon={<AddIcon />}
          onClick={onNewChat}
          sx={{
            borderRadius: 2,
            textTransform: 'none',
            fontWeight: 'medium',
            py: 1.5
          }}
        >
          New Chat
        </Button>
      </Box>

      {/* Chat History */}
      <Box sx={{ flex: 1, overflow: 'auto' }}>
        <Box sx={{ p: 2 }}>
          <Typography variant="subtitle2" color="text.secondary" gutterBottom>
            Recent Chats
          </Typography>
        </Box>

        {sessions.length === 0 ? (
          <Box sx={{ p: 3, textAlign: 'center' }}>
            <Typography variant="body2" color="text.secondary">
              No chat history yet
            </Typography>
          </Box>
        ) : (
          <List sx={{ px: 1 }}>
            {sessions.map((session) => (
              <ListItem
                key={session.id}
                disablePadding
                sx={{ mb: 0.5 }}
              >
                <Paper
                  elevation={0}
                  sx={{
                    width: '100%',
                    backgroundColor: activeSessionId === session.id ? 'primary.light' : 'transparent',
                    '&:hover': {
                      backgroundColor: activeSessionId === session.id ? 'primary.light' : 'action.hover'
                    },
                    borderRadius: 1
                  }}
                >
                  <ListItemButton
                    onClick={() => onSelectSession(session.id)}
                    sx={{
                      px: 2,
                      py: 1.5,
                      borderRadius: 1
                    }}
                  >
                    <ChatIcon
                      sx={{
                        mr: 1.5,
                        fontSize: 18,
                        color: activeSessionId === session.id ? 'primary.main' : 'text.secondary'
                      }}
                    />
                    <ListItemText
                      primary={
                        <Typography
                          variant="body2"
                          sx={{
                            fontWeight: activeSessionId === session.id ? 'medium' : 'normal',
                            color: activeSessionId === session.id ? 'primary.main' : 'text.primary',
                            overflow: 'hidden',
                            textOverflow: 'ellipsis',
                            whiteSpace: 'nowrap'
                          }}
                        >
                          {session.title}
                        </Typography>
                      }
                      secondary={
                        <Typography variant="caption" color="text.secondary">
                          {formatDate(session.updatedAt)}
                        </Typography>
                      }
                    />
                    <IconButton
                      size="small"
                      onClick={(e) => {
                        e.stopPropagation();
                        onDeleteSession(session.id);
                      }}
                      sx={{
                        opacity: 0.6,
                        '&:hover': { opacity: 1 }
                      }}
                    >
                      <DeleteIcon fontSize="small" />
                    </IconButton>
                  </ListItemButton>
                </Paper>
              </ListItem>
            ))}
          </List>
        )}
      </Box>

      {/* Footer */}
      <Box sx={{ p: 2, borderTop: '1px solid #e0e0e0' }}>
        <Typography variant="caption" color="text.secondary" textAlign="center" display="block">
          AI-Powered HR Assistant
        </Typography>
      </Box>
    </Box>
  );
};

export default Sidebar;