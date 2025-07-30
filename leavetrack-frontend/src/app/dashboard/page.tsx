'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/contexts/AuthContext';
import { LeaveSummary, Leave } from '@/types';
import { leaveAPI } from '@/services/api';
import { Calendar, Clock, User, LogOut, Plus, Filter, BarChart3, Settings } from 'lucide-react';
import toast from 'react-hot-toast';
import Link from 'next/link';

export default function DashboardPage() {
  const { user, logout, isAuthenticated } = useAuth();
  const router = useRouter();
  const [leaveSummary, setLeaveSummary] = useState<LeaveSummary | null>(null);
  const [recentLeaves, setRecentLeaves] = useState<Leave[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }

    if (user?.authorities?.includes('ROLE_ADMIN')) {
      router.push('/admin');
      return;
    }

    const fetchData = async () => {
      try {
        setIsLoading(true);
        const [summary, leaves] = await Promise.all([
          leaveAPI.getLeaveSummary(),
          leaveAPI.getUserLeaves(),
        ]);
        setLeaveSummary(summary);
        setRecentLeaves(leaves.slice(0, 5)); // Son 5 izin
      } catch (error) {
        console.error('Dashboard data fetch error:', error);
        toast.error('Veriler yüklenirken bir hata oluştu');
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, [isAuthenticated, router, user]);

  const handleLogout = () => {
    logout();
    router.push('/login');
    toast.success('Başarıyla çıkış yapıldı');
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
              <Calendar className="h-8 w-8 text-blue-600 mr-3" />
              <h1 className="text-xl font-semibold text-gray-900">
                İzin Takip Sistemi
              </h1>
            </div>
            <div className="flex items-center space-x-4">
              <div className="flex items-center space-x-2">
                <User className="h-5 w-5 text-gray-400" />
                <span className="text-sm text-gray-700">
                  {user?.name}
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
        {/* Welcome Section */}
        <div className="mb-8">
          <h2 className="text-2xl font-bold text-gray-900 mb-2">
            Hoş geldiniz, {user?.name}!
          </h2>
          <p className="text-gray-600">
            İzin durumunuzu kontrol edin ve yeni izin taleplerinde bulunun.
          </p>
        </div>

        {/* Stats Grid */}
        {leaveSummary && (
          <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
            <div className="bg-white rounded-lg shadow p-6">
              <div className="flex items-center">
                <div className="p-2 bg-blue-100 rounded-lg">
                  <Calendar className="h-6 w-6 text-blue-600" />
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-gray-600">Toplam İzin</p>
                  <p className="text-2xl font-bold text-gray-900">
                    {leaveSummary.totalWorkDays} gün
                  </p>
                </div>
              </div>
            </div>

            <div className="bg-white rounded-lg shadow p-6">
              <div className="flex items-center">
                <div className="p-2 bg-green-100 rounded-lg">
                  <Clock className="h-6 w-6 text-green-600" />
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-gray-600">Kullanılan</p>
                  <p className="text-2xl font-bold text-gray-900">
                    {leaveSummary.usedWorkDays} gün
                  </p>
                </div>
              </div>
            </div>

            <div className="bg-white rounded-lg shadow p-6">
              <div className="flex items-center">
                <div className="p-2 bg-yellow-100 rounded-lg">
                  <BarChart3 className="h-6 w-6 text-yellow-600" />
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-gray-600">Kalan</p>
                  <p className="text-2xl font-bold text-gray-900">
                    {leaveSummary.remainingWorkDays} gün
                  </p>
                </div>
              </div>
            </div>

            <div className="bg-white rounded-lg shadow p-6">
              <div className="flex items-center">
                <div className="p-2 bg-purple-100 rounded-lg">
                  <User className="h-6 w-6 text-purple-600" />
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-gray-600">Departman</p>
                  <p className="text-lg font-semibold text-gray-900">
                    {leaveSummary.departmentName || 'Belirtilmemiş'}
                  </p>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Action Buttons */}
        <div className="flex flex-wrap gap-4 mb-8">
          <Link
            href="/leaves/new"
            className="inline-flex items-center px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <Plus className="h-4 w-4 mr-2" />
            Yeni İzin Talebi
          </Link>
          <Link
            href="/leaves"
            className="inline-flex items-center px-4 py-2 bg-gray-600 text-white rounded-md hover:bg-gray-700 focus:outline-none focus:ring-2 focus:ring-gray-500"
          >
            <Filter className="h-4 w-4 mr-2" />
            Tüm İzinlerim
          </Link>
          {user?.authorities?.includes('ROLE_ADMIN') && (
            <Link
              href="/admin"
              className="inline-flex items-center px-4 py-2 bg-purple-600 text-white rounded-md hover:bg-purple-700 focus:outline-none focus:ring-2 focus:ring-purple-500"
            >
              <Settings className="h-4 w-4 mr-2" />
              Admin Panel
            </Link>
          )}
        </div>

        {/* Recent Leaves */}
        <div className="bg-white rounded-lg shadow">
          <div className="px-6 py-4 border-b border-gray-200">
            <h3 className="text-lg font-medium text-gray-900">Son İzinlerim</h3>
          </div>
          <div className="divide-y divide-gray-200">
            {recentLeaves.length > 0 ? (
              recentLeaves.map((leave) => (
                <div key={leave.id} className="px-6 py-4">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center space-x-4">
                      <div className="flex-shrink-0">
                        <Calendar className="h-5 w-5 text-gray-400" />
                      </div>
                      <div>
                        <p className="text-sm font-medium text-gray-900">
                          {getLeaveTypeText(leave.leaveType)}
                        </p>
                        <p className="text-sm text-gray-500">
                          {new Date(leave.startDate).toLocaleDateString('tr-TR')} -{' '}
                          {new Date(leave.endDate).toLocaleDateString('tr-TR')}
                        </p>
                        {leave.reason && (
                          <p className="text-sm text-gray-500 mt-1">{leave.reason}</p>
                        )}
                      </div>
                    </div>
                    <div className="flex items-center space-x-4">
                      <span className="text-sm text-gray-500">{leave.workDays} gün</span>
                      <span
                        className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(
                          leave.status
                        )}`}
                      >
                        {leave.status === 'APPROVED' && 'Onaylandı'}
                        {leave.status === 'REJECTED' && 'Reddedildi'}
                        {leave.status === 'PENDING' && 'Beklemede'}
                      </span>
                    </div>
                  </div>
                </div>
              ))
            ) : (
              <div className="px-6 py-8 text-center">
                <Calendar className="mx-auto h-12 w-12 text-gray-400" />
                <h3 className="mt-2 text-sm font-medium text-gray-900">Henüz izin talebiniz yok</h3>
                <p className="mt-1 text-sm text-gray-500">
                  İlk izin talebinizi oluşturmak için yukarıdaki butonu kullanın.
                </p>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
} 