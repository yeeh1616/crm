export interface Activity {
  id: number;
  leadId?: number;
  customerId?: number;
  ownerId: number;
  type: 'CALL' | 'EMAIL' | 'MEETING' | 'NOTE';
  content?: string;
  outcome?: string;
  nextFollowUpAt?: string;
  createdAt?: string;
  sentReminder?: boolean;
  subscribedReminder?: boolean;
}