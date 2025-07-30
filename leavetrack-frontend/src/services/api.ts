import axios from 'axios';
import { AuthResponse, LoginRequest, Leave, LeaveRequest, LeaveUpdateRequest, LeaveSummary, User, Department } from '@/types';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

// Token süresini kontrol eden utility fonksiyon
const isTokenExpired = (token: string): boolean => {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const currentTime = Date.now() / 1000;
    return payload.exp < currentTime;
  } catch (error) {
    console.error('Token parsing error:', error);
    return true;
  }
};

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor - token ekleme ve süre kontrolü
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      // Token süresini kontrol et
      if (isTokenExpired(token)) {
        console.log('⚠️ Token expired in request interceptor, will trigger refresh...');
      }
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor - token yenileme
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Token süresi dolmuşsa ve henüz retry yapılmamışsa
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshToken = localStorage.getItem('refreshToken');
        if (refreshToken) {
          console.log('🔄 Token expired, attempting to refresh...');
          console.log('📡 Request URL:', originalRequest.url);
          
          const response = await axios.post(`${API_BASE_URL}/auth/refresh-token`, {
            refreshToken,
          });
          
          const { accessToken, refreshToken: newRefreshToken } = response.data.data;
          
          // Yeni tokenları kaydet
          localStorage.setItem('accessToken', accessToken);
          if (newRefreshToken) {
            localStorage.setItem('refreshToken', newRefreshToken);
          }
          
          // Orijinal isteği yeni token ile tekrar dene
          originalRequest.headers.Authorization = `Bearer ${accessToken}`;
          console.log('✅ Token refreshed successfully, retrying request...');
          return api(originalRequest);
        } else {
          console.log('❌ No refresh token available, redirecting to login...');
          throw new Error('No refresh token');
        }
      } catch (refreshError) {
        console.error('💥 Token refresh failed:', refreshError);
        console.log('🧹 Clearing all auth data and redirecting to login...');
        
        // Tüm auth verilerini temizle ve login sayfasına yönlendir
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('user');
        
        // Eğer login sayfasında değilsek yönlendir
        if (window.location.pathname !== '/login') {
          window.location.href = '/login';
        }
      }
    }

    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  login: async (credentials: LoginRequest): Promise<AuthResponse> => {
    const response = await api.post('/auth/login', credentials);
    return response.data.data; 
  },

  refresh: async (refreshToken: string): Promise<AuthResponse> => {
    const response = await api.post('/auth/refresh-token', { refreshToken });
    return response.data.data; 
  },

  logout: async (): Promise<void> => {
    await api.post('/auth/logout');
  },
};

// Leave API
export const leaveAPI = {
  // İzin oluşturma
  createLeave: async (leaveData: LeaveRequest): Promise<Leave> => {
    const response = await api.post('/leaves', leaveData);
    return response.data.data;
  },

  // İzin güncelleme
  updateLeave: async (id: number, leaveData: LeaveUpdateRequest): Promise<Leave> => {
    const response = await api.put(`/leaves/${id}`, leaveData);
    return response.data.data;
  },

  // İzin detayı
  getLeaveById: async (id: number): Promise<Leave> => {
    const response = await api.get(`/leaves/${id}`);
    return response.data.data;
  },

  // Tüm izinler
  getAllLeaves: async (): Promise<Leave[]> => {
    const response = await api.get('/leaves');
    return response.data.data;
  },

  // Gelişmiş filtreleme
  getLeavesWithFilters: async (filters: {
    search?: string;
    status?: string;
    departmentId?: string;
    leaveType?: string;
    startDate?: string;
    endDate?: string;
    userId?: string;
    page?: number;
    size?: number;
  }): Promise<Leave[]> => {
    const params = new URLSearchParams();
    Object.entries(filters).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        params.append(key, value.toString());
      }
    });
    
    const response = await api.get(`/leaves/filter?${params.toString()}`);
    return response.data.data;
  },

  // Kullanıcının izinleri
  getUserLeaves: async (): Promise<Leave[]> => {
    const response = await api.get('/leaves/user');
    return response.data.data;
  },

  // Belirli kullanıcının izinleri
  getLeavesByUserId: async (userId: string): Promise<Leave[]> => {
    const response = await api.get(`/leaves/user/${userId}`);
    return response.data.data;
  },

  // Departman izinleri
  getDepartmentLeaves: async (departmentId: number): Promise<Leave[]> => {
    const response = await api.get(`/leaves/department/${departmentId}`);
    return response.data.data;
  },

  // Tarih aralığı izinleri
  getLeavesByDateRange: async (startDate: string, endDate: string): Promise<Leave[]> => {
    const response = await api.get('/leaves/date-range', {
      params: { startDate, endDate },
    });
    return response.data.data;
  },

  // Durum bazlı izinler
  getLeavesByStatus: async (status: string): Promise<Leave[]> => {
    const response = await api.get(`/leaves/status/${status}`);
    return response.data.data;
  },

  // Tür bazlı izinler
  getLeavesByType: async (type: string): Promise<Leave[]> => {
    const response = await api.get(`/leaves/type/${type}`);
    return response.data.data;
  },

  // İzin özeti
  getLeaveSummary: async (year?: number): Promise<LeaveSummary> => {
    const response = await api.get('/leaves/summary', {
      params: { year },
    });
    return response.data.data;
  },

  // İzin silme
  deleteLeave: async (id: number): Promise<void> => {
    await api.delete(`/leaves/${id}`);
  },

  // İzin onaylama
  approveLeave: async (id: number): Promise<Leave> => {
    const response = await api.put(`/leaves/${id}/approve`);
    return response.data.data;
  },

  // İzin reddetme
  rejectLeave: async (id: number): Promise<Leave> => {
    const response = await api.put(`/leaves/${id}/reject`);
    return response.data.data;
  },

  // İş günü hesaplama
  calculateWorkDays: async (startDate: string, endDate: string): Promise<number> => {
    const response = await api.get('/leaves/calculate-workdays', {
      params: { startDate, endDate },
    });
    return response.data.data;
  },

  // Limit kontrolü
  checkLeaveLimit: async (year: number, requestedDays: number): Promise<boolean> => {
    const response = await api.get('/leaves/check-limit', {
      params: { year, requestedDays },
    });
    return response.data.data;
  },
};

// Department API
export const departmentAPI = {
  getAllDepartments: async (): Promise<Department[]> => {
    const response = await api.get('/departments');
    return response.data.data;
  },

  getDepartmentById: async (id: number): Promise<Department> => {
    const response = await api.get(`/departments/${id}`);
    return response.data.data;
  },

  createDepartment: async (name: string): Promise<Department> => {
    const response = await api.post('/departments', { name });
    return response.data.data;
  },

  updateDepartment: async (id: number, name: string): Promise<Department> => {
    const response = await api.put(`/departments/${id}`, { name });
    return response.data.data;
  },

  deleteDepartment: async (id: number): Promise<void> => {
    await api.delete(`/departments/${id}`);
  },
};

// User API
export const userAPI = {
  getAllUsers: async (): Promise<User[]> => {
    const response = await api.get('/users');
    return response.data.data;
  },

  getUserById: async (id: string): Promise<User> => {
    const response = await api.get(`/users/${id}`);
    return response.data.data;
  },

  createUser: async (userData: any): Promise<User> => {
    const response = await api.post('/users', userData);
    return response.data.data;
  },

  updateUser: async (id: string, userData: any): Promise<User> => {
    const response = await api.put(`/users/${id}`, userData);
    return response.data.data;
  },

  deleteUser: async (id: string): Promise<void> => {
    await api.delete(`/users/${id}`);
  },
};

// Admin API
export const adminAPI = {
  // Konfigürasyon yönetimi
  updateMaxYearlyLeaveDays: async (maxDays: number): Promise<any> => {
    const response = await api.put('/admin/leaves/config/max-days', null, {
      params: { maxDays }
    });
    return response.data.data;
  },

  getMaxYearlyLeaveDays: async (): Promise<number> => {
    const response = await api.get('/admin/leaves/config/max-days');
    return response.data.data;
  },

  updateLeaveLimitCheck: async (enable: boolean): Promise<any> => {
    const response = await api.put('/admin/leaves/config/enable-limit-check', null, {
      params: { enable }
    });
    return response.data.data;
  },

  getLeaveLimitCheckStatus: async (): Promise<boolean> => {
    const response = await api.get('/admin/leaves/config/enable-limit-check');
    return response.data.data;
  },

  updateWorkDayCalculation: async (enable: boolean): Promise<any> => {
    const response = await api.put('/admin/leaves/config/enable-work-day-calculation', null, {
      params: { enable }
    });
    return response.data.data;
  },

  getWorkDayCalculationStatus: async (): Promise<boolean> => {
    const response = await api.get('/admin/leaves/config/enable-work-day-calculation');
    return response.data.data;
  },
};

export default api; 