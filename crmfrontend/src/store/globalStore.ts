import { create } from "zustand";

export const useGlobalStore = create((set) => ({
    users: [],
    segments: [],
    tags: [],

    setUsers: (list) => set({ users: list }),
    setSegments: (list) => set({ segments: list }),
    setTags: (list) => set({ tags: list }),
}));