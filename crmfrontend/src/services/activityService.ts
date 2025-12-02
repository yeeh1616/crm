import { Activity } from '../types/Activity';
import { PageResponse } from '../types/PageResponse';
import api from './api';

export const activityService = {
  getAllByLeadId: async (
    leadId: string | undefined
  ): Promise<PageResponse<Activity>> => {
    const params: any = {
      page: 0,
      size: 10,
      sortBy: 'id',
      sortDir: 'asc'
    };

    if (leadId !== undefined) {
      params.leadId = leadId;
    }
    
    const response = await api.get<PageResponse<Activity>>('/activities', { params });
    return response.data;
  },

  getAll: async (
    page: number = 0,
    size: number = 10,
    sortBy: string = 'id',
    sortDir: string = 'desc',
    ownerId?: number,
    customerId?: number,
    leadId?: number
  ): Promise<PageResponse<Activity>> => {
    const params: any = { page, size, sortBy, sortDir };
    if (ownerId) params.ownerId = ownerId;
    if (customerId) params.customerId = customerId;
    if (leadId) params.leadId = leadId;
    
    const response = await api.get<PageResponse<Activity>>('/activities', { params });
    return response.data;
  },

  getById: async (id: number): Promise<Activity> => {
    const response = await api.get<Activity>(`/activities/${id}`);
    return response.data;
  },

  create: async (activity: Partial<Activity>): Promise<Activity> => {
    const response = await api.post<Activity>('/activities', activity);
    return response.data;
  },

  update: async (id: number, activity: Partial<Activity>): Promise<Activity> => {
    const response = await api.put<Activity>(`/activities/${id}`, activity);
    return response.data;
  },

  delete: async (id: number, permanent: boolean = false): Promise<void> => {
    await api.delete(`/activities/${id}`, { params: { permanent } });
  },
};

