import api from './api';
import { Segment } from '../types/User';

export const segmentService = {
  getAll: async (): Promise<Segment[]> => {
    const response = await api.get<Segment[]>('/segments');
    return response.data;
  },
};

