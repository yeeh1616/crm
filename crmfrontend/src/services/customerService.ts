import { ConversionRate } from '../types/ConversionRate';
import { Customer } from '../types/Customer';
import { PageResponse } from '../types/PageResponse';
import api from './api';

export const customerService = {
  getAll: async (
    page: number = 0,
    size: number = 10,
    sortBy: string = 'id',
    sortDir: string = 'asc',
    ownerId?: number,
    segment?: number,
    tags?: string[]
  ): Promise<PageResponse<Customer>> => {
    const params: any = { page, size, sortBy, sortDir };
    if (ownerId) params.ownerId = ownerId;
    if (segment) params.segment = segment;
    if (tags && tags.length > 0) params.tags = tags;
    
    const response = await api.get<PageResponse<Customer>>('/customers', { params });
    return response.data;
  },

  getTotalItems: async (ownerId?: number,
    segment?: number,
    tags?: string[]
  ): Promise<number> => {
    const params: any = {};
    if (ownerId) params.ownerId = ownerId;
    if (segment) params.segment = segment;
    if (tags && tags.length > 0) params.tags = tags;

    const response = await api.get<number>('/customers/total', { params });
    return response.data;
  },

  getById: async (id: number): Promise<Customer> => {
    const response = await api.get<Customer>(`/customers/${id}`);
    return response.data;
  },

  create: async (customer: Partial<Customer>): Promise<Customer> => {
    const response = await api.post<Customer>('/customers', customer);
    return response.data;
  },

  update: async (customer: Partial<Customer>): Promise<Customer> => {
    const response = await api.put<Customer>(`/customers`, customer);
    return response.data;
  },

  delete: async (id: number, permanent: boolean = false): Promise<void> => {
    await api.delete(`/customers/${id}`, { params: { permanent } });
  },


  getConversionRate: async (): Promise<ConversionRate[]> => {
    const response = await api.get<ConversionRate[]>(`/analytics/conversion-rates`);
    return response.data;
  },
};

