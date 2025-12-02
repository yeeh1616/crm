import { Lead } from '../types/Lead';
import { PageResponse } from '../types/PageResponse';
import api from './api';

export const leadService = {
  getAllByCustomerId: async (
    customerId?: string
  ): Promise<PageResponse<Lead>> => {
    const params: any = {
      page: 0,
      size: 10,
      sortBy: 'id',
      sortDir: 'asc'
    };

    if (customerId !== undefined) {
      params.customerId = customerId;
    }

    const response = await api.get<PageResponse<Lead>>('/leads', { params });
    return response.data;
  },

  getAll: async (
    page: number = 0,
    size: number = 10,
    sortBy: string = 'id',
    sortDir: string = 'asc',
    customerId?: number
  ): Promise<PageResponse<Lead>> => {
    const params: any = { page, size, sortBy, sortDir };
    if (customerId) {
      params.customerId = customerId;
    }
    
    const response = await api.get<PageResponse<Lead>>('/leads', { params });
    return response.data;
  },

  getById: async (id: number): Promise<Lead> => {
    const response = await api.get<Lead>(`/leads/${id}`);
    return response.data;
  },

  create: async (lead: Partial<Lead>): Promise<Lead> => {
    const response = await api.post<Lead>('/leads', lead);
    return response.data;
  },

  update: async (id: number, lead: Partial<Lead>): Promise<Lead> => {
    const response = await api.put<Lead>(`/leads/${id}`, lead);
    return response.data;
  },

  delete: async (id: number, permanent: boolean = false): Promise<void> => {
    await api.delete(`/leads/${id}`, { params: { permanent } });
  },
};

