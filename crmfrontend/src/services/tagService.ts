import api from './api';
import { Tag } from '../types/User';

export const tagService = {
  getAll: async (): Promise<Tag[]> => {
    const response = await api.get<Tag[]>('/tags');
    return response.data;
  },
};

