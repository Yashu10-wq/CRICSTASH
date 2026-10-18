import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { ScoreUpdateMessage } from '../types';

export type ConnectionStatus = 'CONNECTED' | 'CONNECTING' | 'DISCONNECTED';
export type MessageListener = (message: ScoreUpdateMessage) => void;
export type StatusListener = (status: ConnectionStatus) => void;

class WebSocketService {
  private client: Client | null = null;
  private currentMatchId: number | null = null;
  private messageListeners: Set<MessageListener> = new Set();
  private statusListeners: Set<StatusListener> = new Set();
  private status: ConnectionStatus = 'DISCONNECTED';

  private setStatus(status: ConnectionStatus) {
    this.status = status;
    this.statusListeners.forEach((listener) => listener(status));
  }

  public getStatus(): ConnectionStatus {
    return this.status;
  }

  public subscribeToMatch(matchId: number, onMessage: MessageListener, onStatusChange?: StatusListener): () => void {
    this.messageListeners.add(onMessage);
    if (onStatusChange) {
      this.statusListeners.add(onStatusChange);
      onStatusChange(this.status);
    }

    if (this.currentMatchId !== matchId || !this.client || !this.client.connected) {
      this.disconnect();
      this.currentMatchId = matchId;
      this.connect(matchId);
    }

    return () => {
      this.messageListeners.delete(onMessage);
      if (onStatusChange) {
        this.statusListeners.delete(onStatusChange);
      }
      if (this.messageListeners.size === 0) {
        this.disconnect();
      }
    };
  }

  private connect(matchId: number) {
    const wsUrl = import.meta.env.VITE_WS_URL || 'http://localhost:8080/ws';
    this.setStatus('CONNECTING');

    this.client = new Client({
      webSocketFactory: () => new SockJS(wsUrl),
      reconnectDelay: 5000,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      onConnect: () => {
        this.setStatus('CONNECTED');
        if (this.client) {
          this.client.subscribe(`/topic/match/${matchId}`, (message: IMessage) => {
            try {
              const parsed: ScoreUpdateMessage = JSON.parse(message.body);
              this.messageListeners.forEach((listener) => listener(parsed));
            } catch (err) {
              console.error('Failed to parse WebSocket score message:', err);
            }
          });
        }
      },
      onDisconnect: () => {
        this.setStatus('DISCONNECTED');
      },
      onStompError: (frame) => {
        console.warn('STOMP Broker Error:', frame.headers['message']);
        this.setStatus('DISCONNECTED');
      },
      onWebSocketClose: () => {
        this.setStatus('DISCONNECTED');
      }
    });

    this.client.activate();
  }

  public disconnect() {
    if (this.client) {
      this.client.deactivate();
      this.client = null;
    }
    this.currentMatchId = null;
    this.setStatus('DISCONNECTED');
  }
}

export const websocketService = new WebSocketService();
