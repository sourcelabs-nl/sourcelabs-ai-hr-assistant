import { ChatSession, Message, ChatState } from '../types/chat';

const STORAGE_KEY = 'sourcechat_sessions';

export class ChatSessionService {
  private static instance: ChatSessionService;
  private state: ChatState;

  private constructor() {
    this.state = this.loadFromStorage();
  }

  public static getInstance(): ChatSessionService {
    if (!ChatSessionService.instance) {
      ChatSessionService.instance = new ChatSessionService();
    }
    return ChatSessionService.instance;
  }

  private loadFromStorage(): ChatState {
    try {
      const stored = localStorage.getItem(STORAGE_KEY);
      if (stored) {
        const parsed = JSON.parse(stored);
        return {
          sessions: parsed.sessions.map((session: any) => ({
            ...session,
            createdAt: new Date(session.createdAt),
            updatedAt: new Date(session.updatedAt),
            messages: session.messages.map((msg: any) => ({
              ...msg,
              timestamp: new Date(msg.timestamp)
            }))
          })),
          activeSessionId: parsed.activeSessionId
        };
      }
    } catch (error) {
      console.error('Error loading chat sessions from storage:', error);
    }
    
    return {
      sessions: [],
      activeSessionId: null
    };
  }

  private saveToStorage(): void {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(this.state));
    } catch (error) {
      console.error('Error saving chat sessions to storage:', error);
    }
  }

  public createNewSession(): ChatSession {
    const newSession: ChatSession = {
      id: `session_${Date.now()}`,
      title: 'New Chat',
      messages: [{
        id: '1',
        role: 'assistant',
        content: 'Hello! I\'m your Sourcelabs HR assistant. I can help you register leave hours, billable client hours, and answer questions about the employee manual. How can I assist you today?',
        timestamp: new Date()
      }],
      sessionId: null,
      createdAt: new Date(),
      updatedAt: new Date()
    };

    this.state.sessions.unshift(newSession);
    this.state.activeSessionId = newSession.id;
    this.saveToStorage();
    
    return newSession;
  }

  public getActiveSession(): ChatSession | null {
    if (!this.state.activeSessionId) {
      return null;
    }
    return this.state.sessions.find(s => s.id === this.state.activeSessionId) || null;
  }

  public setActiveSession(sessionId: string): ChatSession | null {
    const session = this.state.sessions.find(s => s.id === sessionId);
    if (session) {
      this.state.activeSessionId = sessionId;
      this.saveToStorage();
      return session;
    }
    return null;
  }

  public addMessage(message: Message): void {
    const activeSession = this.getActiveSession();
    if (activeSession) {
      activeSession.messages.push(message);
      activeSession.updatedAt = new Date();
      
      // Update title based on first user message
      if (message.role === 'user' && activeSession.title === 'New Chat') {
        activeSession.title = this.generateTitle(message.content);
      }
      
      this.saveToStorage();
    }
  }

  public updateSessionId(sessionId: string): void {
    const activeSession = this.getActiveSession();
    if (activeSession) {
      activeSession.sessionId = sessionId;
      this.saveToStorage();
    }
  }

  public getAllSessions(): ChatSession[] {
    return [...this.state.sessions].sort((a, b) => b.updatedAt.getTime() - a.updatedAt.getTime());
  }

  public deleteSession(sessionId: string): void {
    this.state.sessions = this.state.sessions.filter(s => s.id !== sessionId);
    if (this.state.activeSessionId === sessionId) {
      this.state.activeSessionId = this.state.sessions.length > 0 ? this.state.sessions[0].id : null;
    }
    this.saveToStorage();
  }

  private generateTitle(firstMessage: string): string {
    const words = firstMessage.trim().split(' ');
    if (words.length <= 4) {
      return firstMessage;
    }
    return words.slice(0, 4).join(' ') + '...';
  }

  public getState(): ChatState {
    return { ...this.state };
  }
}

export const chatSessionService = ChatSessionService.getInstance();