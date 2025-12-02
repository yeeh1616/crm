export interface Lead {
  id: number;
  customerId?: number;
  customerName?: string;
  contactName: string;
  contactEmail: string;
  contactPhone?: string;
  stage: 'NEW' | 'CONTACTED' | 'QUALIFIED' | 'PROPOSAL' | 'WON' | 'LOST';
  status: 'ACTIVE' | 'LOST' | 'ARCHIVED';
  source?: string;
  ownerId: number;
  ownerName?: string;
  value?: number;
  expectedCloseDate?: string;
  convertedAt?: string;
  createdAt?: string;
  updatedAt?: string;
}