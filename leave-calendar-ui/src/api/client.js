import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

export const membersApi = {
  getAll: () => api.get('/members'),
};

export const leavesApi = {
  getAll: (params) => api.get('/leaves', { params }),
  create: (data) => api.post('/leaves', data),
  updateStatus: (id, status) => api.patch(`/leaves/${id}/status`, null, { params: { status } }),
  delete: (id) => api.delete(`/leaves/${id}`),
};

export const onCallApi = {
  getSchedule: (weeks = 8) => api.get('/oncall', { params: { weeks } }),
};

export default api;
