'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/contexts/AuthContext';
import { leaveAPI } from '@/services/api';
import { Leave, LeaveStatus, LeaveType } from '@/types';
import { ArrowLeft, Calendar, Filter, Search, Eye, Trash2, Check, X } from 'lucide-react';
import toast from 'react-hot-toast';
import Link from 'next/link';

export default function LeavesPage() {
  const { user, isAuthenticated } = useAuth();
  const router = useRouter();
  const [leaves, setLeaves] = useState<Leave[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('all');
  const [typeFilter, setTypeFilter] = useState<string>('all');

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }

    fetchLeaves();
  }, [isAuthenticated, router]);

  const fetchLeaves = async () => {
    try {
      setIsLoading(true);
      const userLeaves = await leaveAPI.getUserLeaves();
      setLeaves(userLeaves);
    } catch (error) {
      console.error('Leaves fetch error:', error);
      toast.error('İzinler yüklenirken bir hata oluştu');
    } finally {
      setIsLoading(false);
    }
  };

  // Frontend filtreleme
  const filteredLeaves = leaves.filter((leave) => {
    const matchesSearch = 
      leave.reason?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      getLeaveTypeText(leave.leaveType).toLowerCase().includes(searchTerm.toLowerCase());
    
    const matchesStatus = statusFilter === 'all' || leave.status === statusFilter;
    const matchesType = typeFilter === 'all' || leave.leaveType === typeFilter;

    return matchesSearch && matchesStatus && matchesType;
  });

  const handleDeleteLeave = async (leaveId: number) => {
    if (!confirm('Bu izin talebini silmek istediğinizden emin misiniz?')) {
      return;
    }

    try {
      await leaveAPI.deleteLeave(leaveId);
      toast.success('İzin talebi başarıyla silindi');
      fetchLeaves();
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'İzin silinirken bir hata oluştu';
      toast.error(errorMessage);
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
                İzinlerim
              </h1>
            </div>
            <Link
              href="/leaves/new"
              className="inline-flex items-center px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <Calendar className="h-4 w-4 mr-2" />
              Yeni İzin
            </Link>
          </div>
        </div>
      </header>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Filters */}
        <div className="bg-white rounded-lg shadow p-6 mb-6">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {/* Search */}
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
              <input
                type="text"
                placeholder="Açıklama veya tür ara..."
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
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="all">Tüm Durumlar</option>
                <option value="PENDING">Beklemede</option>
                <option value="APPROVED">Onaylandı</option>
                <option value="REJECTED">Reddedildi</option>
              </select>
            </div>

            {/* Type Filter */}
            <div>
              <select
                value={typeFilter}
                onChange={(e) => setTypeFilter(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="all">Tüm Türler</option>
                <option value="YILLIK">Yıllık İzin</option>
                <option value="HASTALIK">Hastalık İzni</option>
                <option value="UCRETSIZ">Ücretsiz İzin</option>
              </select>
            </div>
          </div>

          {/* Clear Filters Button */}
          <div className="mt-4 flex justify-end">
            <button
              onClick={() => {
                setSearchTerm('');
                setStatusFilter('all');
                setTypeFilter('all');
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
              İzin Taleplerim ({filteredLeaves.length})
            </h3>
          </div>

          {filteredLeaves.length > 0 ? (
            <div className="divide-y divide-gray-200">
              {filteredLeaves.map((leave) => (
                <div key={leave.id} className="px-6 py-4">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center space-x-4">
                      <div className="flex-shrink-0">
                        <Calendar className="h-5 w-5 text-gray-400" />
                      </div>
                      <div className="flex-1">
                        <div className="flex items-center space-x-2">
                          <p className="text-sm font-medium text-gray-900">
                            {getLeaveTypeText(leave.leaveType)}
                          </p>
                          <span
                            className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(
                              leave.status
                            )}`}
                          >
                            {getStatusText(leave.status)}
                          </span>
                        </div>
                        <p className="text-sm text-gray-500 mt-1">
                          {new Date(leave.startDate).toLocaleDateString('tr-TR')} -{' '}
                          {new Date(leave.endDate).toLocaleDateString('tr-TR')}
                        </p>
                        {leave.reason && (
                          <p className="text-sm text-gray-600 mt-1">{leave.reason}</p>
                        )}
                        <p className="text-xs text-gray-400 mt-1">
                          Oluşturulma: {new Date(leave.createdAt).toLocaleDateString('tr-TR')}
                        </p>
                      </div>
                    </div>
                    <div className="flex items-center space-x-2">
                      <span className="text-sm text-gray-500">{leave.workDays} gün</span>
                      <button
                        onClick={() => handleDeleteLeave(leave.id)}
                        className="p-1 text-gray-400 hover:text-red-600 transition-colors"
                        title="İzni Sil"
                      >
                        <Trash2 className="h-4 w-4" />
                      </button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="px-6 py-8 text-center">
              <Calendar className="mx-auto h-12 w-12 text-gray-400" />
              <h3 className="mt-2 text-sm font-medium text-gray-900">
                {filteredLeaves.length === 0 ? 'Henüz izin talebiniz yok' : 'Filtrelere uygun izin bulunamadı'}
              </h3>
              <p className="mt-1 text-sm text-gray-500">
                {filteredLeaves.length === 0 
                  ? 'İlk izin talebinizi oluşturmak için yukarıdaki butonu kullanın.'
                  : 'Farklı filtreler deneyebilirsiniz.'
                }
              </p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
} 