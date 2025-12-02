export interface Customer {
  id: number;
  name: string;
  email: string;
  phone?: string;
  company?: string;
  title?: string;
  source?: string;
  country?: string;
  city?: string;
  tags?: string[];
  ownerId: number;
  ownerName?: string;
  createdAt?: string;
  updatedAt?: string;
  activityScore?: number;
}