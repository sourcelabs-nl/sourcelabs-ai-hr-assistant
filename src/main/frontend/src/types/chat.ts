export interface Message {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  timestamp: Date;
}

export interface ChatSession {
  id: string;
  title: string;
  messages: Message[];
  sessionId: string | null;
  createdAt: Date;
  updatedAt: Date;
}

export interface ChatState {
  sessions: ChatSession[];
  activeSessionId: string | null;
}