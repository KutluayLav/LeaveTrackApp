export interface User {
  id: string;
  name: string;
  firstName: string;
  lastName: string;
  email: string;
  phoneNo: string;
  department?: Department;
  lastLoginDate?: string;
  authorities: string[];
}

export interface Department {
  id: number;
  name: string;
}

export interface Leave {
  id: number;
  startDate: string;
  endDate: string;
  reason?: string;
  leaveType: LeaveType;
  status: LeaveStatus;
  workDays: number;
  year: number;
  createdAt: string;
  updatedAt?: string;
  user: User;
  department: Department;
}

export interface LeaveSummary {
  totalWorkDays: number;
  usedWorkDays: number;
  remainingWorkDays: number;
  year: number;
  userId: string;
  userName: string;
  departmentName?: string;
  isLimitExceeded: boolean;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  user: User;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LeaveRequest {
  startDate: string;
  endDate: string;
  reason?: string;
  leaveType: LeaveType;
  departmentId?: number;
}

export interface LeaveUpdateRequest {
  startDate?: string;
  endDate?: string;
  reason?: string;
  leaveType?: LeaveType;
  status?: LeaveStatus;
  workDays?: number;
  departmentId?: number;
}

export enum LeaveType {
  YILLIK = 'YILLIK',
  HASTALIK = 'HASTALIK',
  UCRETSIZ = 'UCRETSIZ'
}

export enum LeaveStatus {
  PENDING = 'PENDING',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED'
}

export interface ApiResponse<T> {
  data?: T;
  message?: string;
  success?: boolean;
} 