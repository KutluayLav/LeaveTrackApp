'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/contexts/AuthContext';
import { leaveAPI, departmentAPI, userAPI, adminAPI, authAPI } from '@/services/api';
import { Leave, Department, User } from '@/types';
import { 
  ArrowLeft, 
  Calendar, 
  Users, 
  Settings, 
  CheckCircle, 
  XCircle, 
  Clock,
  BarChart3,
  Filter,
  Search,
  Plus,
  Trash2,
  X,
  LogOut
} from 'lucide-react';
import toast from 'react-hot-toast';
import Link from 'next/link';

export default function AdminDashboardPage() {
  const { user, isAuthenticated, logout } = useAuth();
  const router = useRouter();
  const [allLeaves, setAllLeaves] = useState<Leave[]>([]);
  const [departments, setDepartments] = useState<Department[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isFiltering, setIsFiltering] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('all');
  const [departmentFilter, setDepartmentFilter] = useState<string>('all');
  const [startDateFilter, setStartDateFilter] = useState<string>('');
  const [endDateFilter, setEndDateFilter] = useState<string>('');
  const [configData, setConfigData] = useState({
    maxDays: 20,
    limitCheckEnabled: true,
    workDayCalculationEnabled: true
  });
  const [activeTab, setActiveTab] = useState<'leaves' | 'departments' | 'users'>('leaves');
  const [showAddDepartment, setShowAddDepartment] = useState(false);
  const [showAddUser, setShowAddUser] = useState(false);
  const [showUserDetail, setShowUserDetail] = useState(false);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [userLeaves, setUserLeaves] = useState<Leave[]>([]);
  const [editingDepartment, setEditingDepartment] = useState<Department | null>(null);
  const [editingUser, setEditingUser] = useState<User | null>(null);
  const [newDepartment, setNewDepartment] = useState({ name: '' });
  const [newUser, setNewUser] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    phoneNo: '',
    departmentId: 1
  });

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }

    // Admin yetkisi kontrolü
    if (!user?.authorities?.includes('ROLE_ADMIN')) {
      router.push('/dashboard');
      return;
    }

    fetchAdminData();
  }, [isAuthenticated, router, user]);

  const fetchAdminData = async () => {
    try {
      setIsLoading(true);
      const [leaves, deps, usrs, maxDays, limitCheck, workDayCalc] = await Promise.all([
        leaveAPI.getAllLeaves(),
        departmentAPI.getAllDepartments(),
        userAPI.getAllUsers(),
        adminAPI.getMaxYearlyLeaveDays(),
        adminAPI.getLeaveLimitCheckStatus(),
        adminAPI.getWorkDayCalculationStatus()
      ]);
      setAllLeaves(leaves);
      setDepartments(deps);
      setUsers(usrs);
      setConfigData({
        maxDays,
        limitCheckEnabled: limitCheck,
        workDayCalculationEnabled: workDayCalc
      });
    } catch (error) {
      console.error('Admin data fetch error:', error);
      toast.error('Veriler yüklenirken bir hata oluştu');
    } finally {
      setIsLoading(false);
    }
  };

  const fetchFilteredLeaves = async () => {
    try {
      setIsFiltering(true);
      const filters: any = {};
      
      if (searchTerm) filters.search = searchTerm;
      if (statusFilter !== 'all') filters.status = statusFilter;
      if (departmentFilter !== 'all') filters.departmentId = departmentFilter;
      if (startDateFilter) filters.startDate = startDateFilter;
      if (endDateFilter) filters.endDate = endDateFilter;
      
      const leaves = await leaveAPI.getLeavesWithFilters(filters);
      setAllLeaves(leaves);
    } catch (error) {
      console.error('Filtered leaves fetch error:', error);
      toast.error('İzinler yüklenirken bir hata oluştu');
    } finally {
      setIsFiltering(false);
    }
  };

  const handleApproveLeave = async (leaveId: number) => {
    try {
      await leaveAPI.approveLeave(leaveId);
      toast.success('İzin onaylandı');
      fetchAdminData();
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'İzin onaylanırken bir hata oluştu';
      toast.error(errorMessage);
    }
  };

  const handleRejectLeave = async (leaveId: number) => {
    try {
      await leaveAPI.rejectLeave(leaveId);
      toast.success('İzin reddedildi');
      fetchAdminData();
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'İzin reddedilirken bir hata oluştu';
      toast.error(errorMessage);
    }
  };

  const handleUpdateConfig = async (key: string, value: any) => {
    try {
      switch (key) {
        case 'maxDays':
          await adminAPI.updateMaxYearlyLeaveDays(value);
          break;
        case 'limitCheckEnabled':
          await adminAPI.updateLeaveLimitCheck(value);
          break;
        case 'workDayCalculationEnabled':
          await adminAPI.updateWorkDayCalculation(value);
          break;
      }
      
      setConfigData(prev => ({ ...prev, [key]: value }));
      toast.success('Konfigürasyon güncellendi');
    } catch (error) {
      console.error('Config update error:', error);
      toast.error('Konfigürasyon güncellenirken bir hata oluştu');
    }
  };

  const handleDeleteDepartment = async (id: number) => {
    try {
      await departmentAPI.deleteDepartment(id);
      toast.success('Departman silindi');
      fetchAdminData();
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Departman silinirken bir hata oluştu';
      toast.error(errorMessage);
    }
  };

  const handleDeleteUser = async (id: string) => {
    try {
      await userAPI.deleteUser(id);
      toast.success('Kullanıcı silindi');
      fetchAdminData();
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Kullanıcı silinirken bir hata oluştu';
      toast.error(errorMessage);
    }
  };

  const handleEditDepartment = (department: Department) => {
    setEditingDepartment(department);
    setNewDepartment({ name: department.name });
  };

  const handleEditUser = (user: User) => {
    setEditingUser(user);
    setNewUser({
      firstName: user.firstName,
      lastName: user.lastName,
      email: user.email,
      password: '', // Güvenlik için şifre gösterilmez
      phoneNo: user.phoneNo,
      departmentId: user.department?.id || 1
    });
  };

  const handleViewUserDetail = async (user: User) => {
    try {
      setSelectedUser(user);
      // Sadece izin bilgilerini çek
      const leaves = await leaveAPI.getLeavesByUserId(user.id);
      setUserLeaves(leaves);
      setShowUserDetail(true);
    } catch (error) {
      console.error('İzin bilgileri yüklenirken hata:', error);
      toast.error('İzin bilgileri yüklenirken bir hata oluştu');
    }
  };

  const handleSaveDepartment = async () => {
    try {
      if (editingDepartment) {
        await departmentAPI.updateDepartment(editingDepartment.id, newDepartment.name);
        toast.success('Departman güncellendi');
      } else {
        await departmentAPI.createDepartment(newDepartment.name);
        toast.success('Departman eklendi');
      }
      setShowAddDepartment(false);
      setEditingDepartment(null);
      setNewDepartment({ name: '' });
      fetchAdminData();
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Departman işlemi sırasında bir hata oluştu';
      toast.error(errorMessage);
    }
  };

  const handleSaveUser = async () => {
    try {
      if (editingUser) {
        await userAPI.updateUser(editingUser.id, newUser);
        toast.success('Kullanıcı güncellendi');
      } else {
        await userAPI.createUser(newUser);
        toast.success('Kullanıcı eklendi');
      }
      setShowAddUser(false);
      setEditingUser(null);
      setNewUser({
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        phoneNo: '',
        departmentId: 1
      });
      fetchAdminData();
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Kullanıcı işlemi sırasında bir hata oluştu';
      toast.error(errorMessage);
    }
  };

  const handleLogout = async () => {
    try {
      await logout();
      router.push('/login');
      toast.success('Başarıyla çıkış yapıldı');
    } catch (error: any) {
      console.error('Logout error:', error);
      router.push('/login');
      toast.success('Başarıyla çıkış yapıldı');
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'APPROVED':
        return 'bg-green-100 text-green-800';
      case 'REJECTED':
        return 'bg-red-100 text-red-800';
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getStatusText = (status: string) => {
    switch (status) {
      case 'APPROVED':
        return 'Onaylandı';
      case 'REJECTED':
        return 'Reddedildi';
      case 'PENDING':
        return 'Beklemede';
      default:
        return status;
    }
  };

  const getLeaveTypeText = (type: string) => {
    switch (type) {
      case 'YILLIK':
        return 'Yıllık İzin';
      case 'HASTALIK':
        return 'Hastalık İzni';
      case 'UCRETSIZ':
        return 'Ücretsiz İzin';
      default:
        return type;
    }
  };

  const pendingLeaves = allLeaves.filter(leave => leave.status === 'PENDING');
  const approvedLeaves = allLeaves.filter(leave => leave.status === 'APPROVED');
  const rejectedLeaves = allLeaves.filter(leave => leave.status === 'REJECTED');

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center">
              <Link
                href="/dashboard"
                className="flex items-center text-gray-600 hover:text-gray-900 mr-4"
              >
                <ArrowLeft className="h-5 w-5 mr-2" />
                Geri
              </Link>
              <h1 className="text-xl font-semibold text-gray-900">
                Admin Dashboard
              </h1>
            </div>
            <div className="flex items-center space-x-4">
              <div className="flex items-center space-x-2">
                <Users className="h-5 w-5 text-gray-400" />
                <span className="text-sm text-gray-700">
                  {user?.name} (Admin)
                </span>
              </div>
              <button
                onClick={handleLogout}
                className="flex items-center space-x-1 text-sm text-gray-600 hover:text-gray-900"
              >
                <LogOut className="h-4 w-4" />
                <span>Çıkış</span>
              </button>
            </div>
          </div>
        </div>
      </header>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center">
              <div className="p-2 bg-blue-100 rounded-lg">
                <Calendar className="h-6 w-6 text-blue-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Toplam İzin</p>
                <p className="text-2xl font-bold text-gray-900">{allLeaves.length}</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center">
              <div className="p-2 bg-yellow-100 rounded-lg">
                <Clock className="h-6 w-6 text-yellow-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Bekleyen</p>
                <p className="text-2xl font-bold text-gray-900">{pendingLeaves.length}</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center">
              <div className="p-2 bg-green-100 rounded-lg">
                <CheckCircle className="h-6 w-6 text-green-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Onaylanan</p>
                <p className="text-2xl font-bold text-gray-900">{approvedLeaves.length}</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center">
              <div className="p-2 bg-red-100 rounded-lg">
                <XCircle className="h-6 w-6 text-red-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Reddedilen</p>
                <p className="text-2xl font-bold text-gray-900">{rejectedLeaves.length}</p>
              </div>
            </div>
          </div>
        </div>

        {/* Tab Navigation */}
        <div className="bg-white rounded-lg shadow mb-6">
          <div className="border-b border-gray-200">
            <nav className="-mb-px flex space-x-8 px-6">
              <button
                onClick={() => setActiveTab('leaves')}
                className={`py-4 px-1 border-b-2 font-medium text-sm ${
                  activeTab === 'leaves'
                    ? 'border-blue-500 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                İzinler ({allLeaves.length})
              </button>
              <button
                onClick={() => setActiveTab('departments')}
                className={`py-4 px-1 border-b-2 font-medium text-sm ${
                  activeTab === 'departments'
                    ? 'border-blue-500 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                Departmanlar ({departments.length})
              </button>
              <button
                onClick={() => setActiveTab('users')}
                className={`py-4 px-1 border-b-2 font-medium text-sm ${
                  activeTab === 'users'
                    ? 'border-blue-500 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                Kullanıcılar ({users.length})
              </button>
            </nav>
          </div>
        </div>

        {/* Configuration Section */}
        <div className="bg-white rounded-lg shadow p-6 mb-8">
          <h3 className="text-lg font-medium text-gray-900 mb-4 flex items-center">
            <Settings className="h-5 w-5 mr-2" />
            Sistem Konfigürasyonu
          </h3>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Maksimum Yıllık İzin Günü
              </label>
              <div className="flex items-center space-x-2">
                <input
                  type="number"
                  value={configData.maxDays}
                  onChange={(e) => setConfigData(prev => ({ ...prev, maxDays: parseInt(e.target.value) }))}
                  className="w-20 px-3 py-2 border border-gray-300 rounded-md"
                />
                <button
                  onClick={() => handleUpdateConfig('maxDays', configData.maxDays)}
                  className="px-3 py-2 bg-blue-600 text-white rounded-md text-sm hover:bg-blue-700"
                >
                  Güncelle
                </button>
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Limit Kontrolü
              </label>
              <button
                onClick={() => handleUpdateConfig('limitCheckEnabled', !configData.limitCheckEnabled)}
                className={`px-4 py-2 rounded-md text-sm font-medium ${
                  configData.limitCheckEnabled
                    ? 'bg-green-600 text-white hover:bg-green-700'
                    : 'bg-gray-600 text-white hover:bg-gray-700'
                }`}
              >
                {configData.limitCheckEnabled ? 'Aktif' : 'Devre Dışı'}
              </button>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                İş Günü Hesaplama
              </label>
              <button
                onClick={() => handleUpdateConfig('workDayCalculationEnabled', !configData.workDayCalculationEnabled)}
                className={`px-4 py-2 rounded-md text-sm font-medium ${
                  configData.workDayCalculationEnabled
                    ? 'bg-green-600 text-white hover:bg-green-700'
                    : 'bg-gray-600 text-white hover:bg-gray-700'
                }`}
              >
                {configData.workDayCalculationEnabled ? 'Aktif' : 'Devre Dışı'}
              </button>
            </div>
          </div>
        </div>

        {/* Tab Content */}
        {activeTab === 'leaves' && (
          <>
            {/* Filters */}
            <div className="bg-white rounded-lg shadow p-6 mb-6">
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-6 gap-4">
                {/* Search */}
                <div className="relative">
                  <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
                  <input
                    type="text"
                    placeholder="Kullanıcı adı veya açıklama ara..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 placeholder-gray-500"
                  />
                </div>

                {/* Status Filter */}
                <div>
                  <select
                    value={statusFilter}
                    onChange={(e) => setStatusFilter(e.target.value)}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-700"
                  >
                    <option value="all">Tüm Durumlar</option>
                    <option value="PENDING">Beklemede</option>
                    <option value="APPROVED">Onaylandı</option>
                    <option value="REJECTED">Reddedildi</option>
                  </select>
                </div>

                {/* Department Filter */}
                <div>
                  <select
                    value={departmentFilter}
                    onChange={(e) => setDepartmentFilter(e.target.value)}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-700"
                  >
                    <option value="all">Tüm Departmanlar</option>
                    {departments.map((dept) => (
                      <option key={dept.id} value={dept.id}>
                        {dept.name}
                      </option>
                    ))}
                  </select>
                </div>

                {/* Start Date Filter */}
                <div>
                  <input
                    type="date"
                    value={startDateFilter}
                    onChange={(e) => setStartDateFilter(e.target.value)}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm text-gray-700"
                    placeholder="Başlangıç tarihi"
                  />
                </div>

                {/* End Date Filter */}
                <div>
                  <input
                    type="date"
                    value={endDateFilter}
                    onChange={(e) => setEndDateFilter(e.target.value)}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm text-gray-700"
                    placeholder="Bitiş tarihi"
                  />
                </div>

                {/* Search Button */}
                <div>
                  <button
                    onClick={fetchFilteredLeaves}
                    disabled={isFiltering}
                    className="w-full px-4 py-2 bg-blue-600 text-white rounded-md text-sm font-medium hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center"
                  >
                    {isFiltering ? (
                      <>
                        <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                        Aranıyor...
                      </>
                    ) : (
                      <>
                        <Search className="h-4 w-4 mr-2" />
                        Ara
                      </>
                    )}
                  </button>
                </div>
              </div>

              {/* Clear Filters Button */}
              <div className="mt-4 flex justify-end">
                <button
                  onClick={() => {
                    setSearchTerm('');
                    setStatusFilter('all');
                    setDepartmentFilter('all');
                    setStartDateFilter('');
                    setEndDateFilter('');
                    // Filtreleri temizledikten sonra tüm izinleri çek
                    fetchAdminData();
                  }}
                  className="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <Filter className="h-4 w-4 mr-2" />
                  Filtreleri Temizle
                </button>
              </div>
            </div>

            {/* Leaves List */}
            <div className="bg-white rounded-lg shadow">
              <div className="px-6 py-4 border-b border-gray-200">
                <h3 className="text-lg font-medium text-gray-900">
                  Tüm İzin Talepleri ({allLeaves.length})
                </h3>
              </div>

              {allLeaves.length > 0 ? (
                <div className="divide-y divide-gray-200">
                  {allLeaves.map((leave) => (
                    <div key={leave.id} className="px-6 py-4">
                      <div className="flex items-center justify-between">
                        <div className="flex items-center space-x-4">
                          <div className="flex-shrink-0">
                            <Calendar className="h-5 w-5 text-gray-400" />
                          </div>
                          <div className="flex-1">
                            <div className="flex items-center space-x-2">
                              <p className="text-sm font-medium text-gray-900">
                                {leave.user.name}
                              </p>
                              <span className="text-sm text-gray-500">
                                ({leave.department?.name || 'Departman Yok'})
                              </span>
                              <span
                                className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(
                                  leave.status
                                )}`}
                              >
                                {getStatusText(leave.status)}
                              </span>
                            </div>
                            <p className="text-sm text-gray-500 mt-1">
                              {getLeaveTypeText(leave.leaveType)} - {leave.workDays} gün
                            </p>
                            <p className="text-sm text-gray-500">
                              {new Date(leave.startDate).toLocaleDateString('tr-TR')} -{' '}
                              {new Date(leave.endDate).toLocaleDateString('tr-TR')}
                            </p>
                            {leave.reason && (
                              <p className="text-sm text-gray-600 mt-1">{leave.reason}</p>
                            )}
                          </div>
                        </div>
                        <div className="flex items-center space-x-2">
                          {leave.status === 'PENDING' && (
                            <>
                              <button
                                onClick={() => handleApproveLeave(leave.id)}
                                className="p-2 text-green-600 hover:bg-green-100 rounded-md transition-colors"
                                title="Onayla"
                              >
                                <CheckCircle className="h-4 w-4" />
                              </button>
                              <button
                                onClick={() => handleRejectLeave(leave.id)}
                                className="p-2 text-red-600 hover:bg-red-100 rounded-md transition-colors"
                                title="Reddet"
                              >
                                <XCircle className="h-4 w-4" />
                              </button>
                            </>
                          )}
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="px-6 py-8 text-center">
                  <Calendar className="mx-auto h-12 w-12 text-gray-400" />
                  <h3 className="mt-2 text-sm font-medium text-gray-900">
                    {allLeaves.length === 0 ? 'Henüz izin talebi yok' : 'Filtrelere uygun izin bulunamadı'}
                  </h3>
                </div>
              )}
            </div>
          </>
        )}

        {activeTab === 'departments' && (
          <div className="bg-white rounded-lg shadow">
            <div className="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
              <h3 className="text-lg font-medium text-gray-900">Departmanlar</h3>
              <button
                onClick={() => setShowAddDepartment(true)}
                className="inline-flex items-center px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
              >
                <Plus className="h-4 w-4 mr-2" />
                Yeni Departman
              </button>
            </div>
            <div className="divide-y divide-gray-200">
              {departments.map((dept) => (
                <div key={dept.id} className="px-6 py-4 flex justify-between items-center">
                  <div>
                    <h4 className="text-sm font-medium text-gray-900">{dept.name}</h4>
                    <p className="text-sm text-gray-500">ID: {dept.id}</p>
                  </div>
                  <div className="flex items-center space-x-2">
                    <button
                      onClick={() => handleEditDepartment(dept)}
                      className="p-2 text-blue-600 hover:bg-blue-100 rounded-md transition-colors"
                      title="Düzenle"
                    >
                      <Settings className="h-4 w-4" />
                    </button>
                    <button
                      onClick={() => handleDeleteDepartment(dept.id)}
                      className="p-2 text-red-600 hover:bg-red-100 rounded-md transition-colors"
                      title="Sil"
                    >
                      <Trash2 className="h-4 w-4" />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {activeTab === 'users' && (
          <div className="bg-white rounded-lg shadow">
            <div className="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
              <h3 className="text-lg font-medium text-gray-900">Kullanıcılar</h3>
              <button
                onClick={() => setShowAddUser(true)}
                className="inline-flex items-center px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
              >
                <Plus className="h-4 w-4 mr-2" />
                Yeni Kullanıcı
              </button>
            </div>
            <div className="divide-y divide-gray-200">
              {users.map((user) => (
                <div key={user.id} className="px-6 py-4 flex justify-between items-center">
                  <div>
                    <h4 className="text-sm font-medium text-gray-900">{user.name}</h4>
                    <p className="text-sm text-gray-500">{user.email}</p>
                    <p className="text-sm text-gray-500">{user.phoneNo}</p>
                    <p className="text-sm text-gray-500">
                      Departman: {user.department?.name || 'Belirtilmemiş'}
                    </p>
                    <p className="text-xs text-gray-400">
                      Yetkiler: {user.authorities.join(', ')}
                    </p>
                    <p className="text-xs text-gray-400">
                      Son Giriş: {user.lastLoginDate ? new Date(user.lastLoginDate).toLocaleString('tr-TR') : 'Hiç giriş yapılmadı'}
                    </p>
                  </div>
                  <div className="flex items-center space-x-2">
                    <button
                      onClick={() => handleViewUserDetail(user)}
                      className="p-2 text-green-600 hover:bg-green-100 rounded-md transition-colors"
                      title="Detay"
                    >
                      <BarChart3 className="h-4 w-4" />
                    </button>
                    <button
                      onClick={() => handleEditUser(user)}
                      className="p-2 text-blue-600 hover:bg-blue-100 rounded-md transition-colors"
                      title="Düzenle"
                    >
                      <Settings className="h-4 w-4" />
                    </button>
                    <button
                      onClick={() => handleDeleteUser(user.id)}
                      className="p-2 text-red-600 hover:bg-red-100 rounded-md transition-colors"
                      title="Sil"
                    >
                      <Trash2 className="h-4 w-4" />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>

      {/* Department Modal */}
      {(showAddDepartment || editingDepartment) && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
          <div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
            <div className="mt-3">
              <div className="flex justify-between items-center mb-4">
                <h3 className="text-lg font-medium text-gray-900">
                  {editingDepartment ? 'Departman Düzenle' : 'Yeni Departman Ekle'}
                </h3>
                <button
                  onClick={() => {
                    setShowAddDepartment(false);
                    setEditingDepartment(null);
                    setNewDepartment({ name: '' });
                  }}
                  className="text-gray-400 hover:text-gray-600"
                >
                  <X className="h-6 w-6" />
                </button>
              </div>
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Departman Adı
                </label>
                <input
                  type="text"
                  value={newDepartment.name}
                  onChange={(e) => setNewDepartment({ name: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 placeholder-gray-500"
                  placeholder="Departman adını girin"
                />
              </div>
              <div className="flex justify-end space-x-3">
                <button
                  onClick={() => {
                    setShowAddDepartment(false);
                    setEditingDepartment(null);
                    setNewDepartment({ name: '' });
                  }}
                  className="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50"
                >
                  İptal
                </button>
                <button
                  onClick={handleSaveDepartment}
                  className="px-4 py-2 bg-blue-600 text-white rounded-md text-sm font-medium hover:bg-blue-700"
                >
                  {editingDepartment ? 'Güncelle' : 'Ekle'}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* User Detail Modal */}
      {showUserDetail && selectedUser && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
          <div className="relative top-4 sm:top-10 mx-auto p-4 sm:p-5 border w-11/12 sm:w-4/5 max-w-4xl shadow-lg rounded-md bg-white max-h-[90vh] overflow-y-auto">
            <div className="mt-3">
              <div className="flex justify-between items-center mb-4">
                <h3 className="text-lg font-medium text-gray-900 truncate pr-2">
                  {selectedUser.name} - İzin Detayları
                </h3>
                <button
                  onClick={() => {
                    setShowUserDetail(false);
                    setSelectedUser(null);
                    setUserLeaves([]);
                  }}
                  className="text-gray-400 hover:text-gray-600 flex-shrink-0"
                >
                  <X className="h-6 w-6" />
                </button>
              </div>
              
              {/* Kullanıcı Bilgileri */}
              <div className="mb-6 p-4 bg-gray-50 rounded-lg">
                <h4 className="text-md font-medium text-gray-900 mb-3">Kullanıcı Bilgileri</h4>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 sm:gap-4 text-sm">
                  <div className="flex flex-col sm:flex-row sm:items-center">
                    <span className="font-medium text-gray-700 mb-1 sm:mb-0 sm:mr-2">Ad Soyad:</span>
                    <span className="text-gray-900 break-words">{selectedUser.name}</span>
                  </div>
                  <div className="flex flex-col sm:flex-row sm:items-center">
                    <span className="font-medium text-gray-700 mb-1 sm:mb-0 sm:mr-2">Email:</span>
                    <span className="text-gray-900 break-all">{selectedUser.email}</span>
                  </div>
                  <div className="flex flex-col sm:flex-row sm:items-center">
                    <span className="font-medium text-gray-700 mb-1 sm:mb-0 sm:mr-2">Telefon:</span>
                    <span className="text-gray-900">{selectedUser.phoneNo}</span>
                  </div>
                  <div className="flex flex-col sm:flex-row sm:items-center">
                    <span className="font-medium text-gray-700 mb-1 sm:mb-0 sm:mr-2">Departman:</span>
                    <span className="text-gray-900">{selectedUser.department?.name || 'Belirtilmemiş'}</span>
                  </div>
                  <div className="flex flex-col sm:flex-row sm:items-center sm:col-span-2">
                    <span className="font-medium text-gray-700 mb-1 sm:mb-0 sm:mr-2">Yetkiler:</span>
                    <span className="text-gray-900">{selectedUser.authorities.join(', ')}</span>
                  </div>
                  <div className="flex flex-col sm:flex-row sm:items-center sm:col-span-2">
                    <span className="font-medium text-gray-700 mb-1 sm:mb-0 sm:mr-2">Son Giriş:</span>
                    <span className="text-gray-900">
                      {selectedUser.lastLoginDate ? new Date(selectedUser.lastLoginDate).toLocaleString('tr-TR') : 'Hiç giriş yapılmadı'}
                    </span>
                  </div>
                </div>
              </div>

              {/* İzin Listesi */}
              <div>
                <h4 className="text-md font-medium text-gray-900 mb-3">İzin Geçmişi</h4>
                {userLeaves.length === 0 ? (
                  <p className="text-gray-500 text-center py-4">Bu kullanıcının henüz izin talebi bulunmuyor.</p>
                ) : (
                  <div className="overflow-x-auto">
                    {/* Desktop Table */}
                    <div className="hidden sm:block">
                      <table className="min-w-full divide-y divide-gray-200">
                        <thead className="bg-gray-50">
                          <tr>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                              Tarih
                            </th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                              Tür
                            </th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                              İş Günü
                            </th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                              Durum
                            </th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                              Sebep
                            </th>
                          </tr>
                        </thead>
                        <tbody className="bg-white divide-y divide-gray-200">
                          {userLeaves.map((leave) => (
                            <tr key={leave.id}>
                              <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                                {new Date(leave.startDate).toLocaleDateString('tr-TR')} - {new Date(leave.endDate).toLocaleDateString('tr-TR')}
                              </td>
                              <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                                {getLeaveTypeText(leave.leaveType)}
                              </td>
                              <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                                {leave.workDays} gün
                              </td>
                              <td className="px-6 py-4 whitespace-nowrap">
                                <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getStatusColor(leave.status)}`}>
                                  {getStatusText(leave.status)}
                                </span>
                              </td>
                              <td className="px-6 py-4 text-sm text-gray-900 max-w-xs truncate">
                                {leave.reason || '-'}
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>

                    {/* Mobile Cards */}
                    <div className="sm:hidden space-y-3">
                      {userLeaves.map((leave) => (
                        <div key={leave.id} className="bg-white border border-gray-200 rounded-lg p-4 shadow-sm">
                          <div className="flex justify-between items-start mb-2">
                            <div className="flex-1">
                              <div className="text-sm font-medium text-gray-900 mb-1">
                                {new Date(leave.startDate).toLocaleDateString('tr-TR')} - {new Date(leave.endDate).toLocaleDateString('tr-TR')}
                              </div>
                              <div className="text-sm text-gray-600 mb-2">
                                {getLeaveTypeText(leave.leaveType)} • {leave.workDays} gün
                              </div>
                            </div>
                            <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getStatusColor(leave.status)}`}>
                              {getStatusText(leave.status)}
                            </span>
                          </div>
                          {leave.reason && (
                            <div className="text-sm text-gray-700">
                              <span className="font-medium">Sebep:</span> {leave.reason}
                            </div>
                          )}
                        </div>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* User Modal */}
      {(showAddUser || editingUser) && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
          <div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
            <div className="mt-3">
              <div className="flex justify-between items-center mb-4">
                <h3 className="text-lg font-medium text-gray-900">
                  {editingUser ? 'Kullanıcı Düzenle' : 'Yeni Kullanıcı Ekle'}
                </h3>
                <button
                  onClick={() => {
                    setShowAddUser(false);
                    setEditingUser(null);
                    setNewUser({
                      firstName: '',
                      lastName: '',
                      email: '',
                      password: '',
                      phoneNo: '',
                      departmentId: 1
                    });
                  }}
                  className="text-gray-400 hover:text-gray-600"
                >
                  <X className="h-6 w-6" />
                </button>
              </div>
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Ad
                  </label>
                  <input
                    type="text"
                    value={newUser.firstName}
                    onChange={(e) => setNewUser(prev => ({ ...prev, firstName: e.target.value }))}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 placeholder-gray-500"
                    placeholder="Ad"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Soyad
                  </label>
                  <input
                    type="text"
                    value={newUser.lastName}
                    onChange={(e) => setNewUser(prev => ({ ...prev, lastName: e.target.value }))}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 placeholder-gray-500"
                    placeholder="Soyad"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Email
                  </label>
                  <input
                    type="email"
                    value={newUser.email}
                    onChange={(e) => setNewUser(prev => ({ ...prev, email: e.target.value }))}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 placeholder-gray-500"
                    placeholder="email@example.com"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Şifre {editingUser && '(Boş bırakın değiştirmek istemiyorsanız)'}
                  </label>
                  <input
                    type="password"
                    value={newUser.password}
                    onChange={(e) => setNewUser(prev => ({ ...prev, password: e.target.value }))}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 placeholder-gray-500"
                    placeholder={editingUser ? 'Yeni şifre' : 'Şifre'}
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Telefon
                  </label>
                  <input
                    type="text"
                    value={newUser.phoneNo}
                    onChange={(e) => setNewUser(prev => ({ ...prev, phoneNo: e.target.value }))}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 placeholder-gray-500"
                    placeholder="Telefon numarası"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Departman
                  </label>
                  <select
                    value={newUser.departmentId}
                    onChange={(e) => setNewUser(prev => ({ ...prev, departmentId: parseInt(e.target.value) }))}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  >
                    {departments.map((dept) => (
                      <option key={dept.id} value={dept.id}>
                        {dept.name}
                      </option>
                    ))}
                  </select>
                </div>
              </div>
              <div className="flex justify-end space-x-3 mt-6">
                <button
                  onClick={() => {
                    setShowAddUser(false);
                    setEditingUser(null);
                    setNewUser({
                      firstName: '',
                      lastName: '',
                      email: '',
                      password: '',
                      phoneNo: '',
                      departmentId: 1
                    });
                  }}
                  className="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50"
                >
                  İptal
                </button>
                <button
                  onClick={handleSaveUser}
                  className="px-4 py-2 bg-blue-600 text-white rounded-md text-sm font-medium hover:bg-blue-700"
                >
                  {editingUser ? 'Güncelle' : 'Ekle'}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
} 