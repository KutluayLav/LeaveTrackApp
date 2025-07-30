'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useAuth } from '@/contexts/AuthContext';
import { leaveAPI, departmentAPI } from '@/services/api';
import { LeaveType, Department } from '@/types';
import { ArrowLeft, Calendar, Clock, AlertCircle } from 'lucide-react';
import toast from 'react-hot-toast';
import Link from 'next/link';

const leaveSchema = z.object({
  startDate: z.string().min(1, 'Başlangıç tarihi gereklidir'),
  endDate: z.string().min(1, 'Bitiş tarihi gereklidir'),
  reason: z.string().optional(),
  leaveType: z.nativeEnum(LeaveType),
  departmentId: z.number().optional(),
}).refine((data) => {
  if (data.startDate && data.endDate) {
    return new Date(data.startDate) <= new Date(data.endDate);
  }
  return true;
}, {
  message: 'Başlangıç tarihi bitiş tarihinden sonra olamaz',
  path: ['endDate'],
});

type LeaveFormData = z.infer<typeof leaveSchema>;

export default function NewLeavePage() {
  const { user, isAuthenticated } = useAuth();
  const router = useRouter();
  const [departments, setDepartments] = useState<Department[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [calculatedWorkDays, setCalculatedWorkDays] = useState<number>(0);

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
    setValue,
  } = useForm<LeaveFormData>({
    resolver: zodResolver(leaveSchema),
    defaultValues: {
      leaveType: LeaveType.YILLIK,
    },
  });

  const watchedStartDate = watch('startDate');
  const watchedEndDate = watch('endDate');

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }

    const fetchDepartments = async () => {
      try {
        const deps = await departmentAPI.getAllDepartments();
        setDepartments(deps);
      } catch (error) {
        console.error('Departments fetch error:', error);
      }
    };

    fetchDepartments();
  }, [isAuthenticated, router]);

  useEffect(() => {
    const calculateWorkDays = async () => {
      if (watchedStartDate && watchedEndDate) {
        try {
          const workDays = await leaveAPI.calculateWorkDays(watchedStartDate, watchedEndDate);
          setCalculatedWorkDays(workDays);
        } catch (error) {
          console.error('Work days calculation error:', error);
        }
      }
    };

    calculateWorkDays();
  }, [watchedStartDate, watchedEndDate]);

  const onSubmit = async (data: LeaveFormData) => {
    try {
      setIsLoading(true);
      await leaveAPI.createLeave(data);
      toast.success('İzin talebi başarıyla oluşturuldu!');
      router.push('/dashboard');
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'İzin talebi oluşturulurken bir hata oluştu';
      toast.error(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  const getLeaveTypeText = (type: LeaveType) => {
    switch (type) {
      case LeaveType.YILLIK:
        return 'Yıllık İzin';
      case LeaveType.HASTALIK:
        return 'Hastalık İzni';
      case LeaveType.UCRETSIZ:
        return 'Ücretsiz İzin';
      default:
        return type;
    }
  };

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
                Yeni İzin Talebi
              </h1>
            </div>
          </div>
        </div>
      </header>

      <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="bg-white rounded-lg shadow p-6">
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
            {/* İzin Türü */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                İzin Türü
              </label>
              <select
                {...register('leaveType')}
                className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
              >
                {Object.values(LeaveType).map((type) => (
                  <option key={type} value={type}>
                    {getLeaveTypeText(type)}
                  </option>
                ))}
              </select>
              {errors.leaveType && (
                <p className="mt-1 text-sm text-red-600">{errors.leaveType.message}</p>
              )}
            </div>

            {/* Tarih Seçimi */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Başlangıç Tarihi
                </label>
                <input
                  {...register('startDate')}
                  type="date"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                />
                {errors.startDate && (
                  <p className="mt-1 text-sm text-red-600">{errors.startDate.message}</p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Bitiş Tarihi
                </label>
                <input
                  {...register('endDate')}
                  type="date"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                />
                {errors.endDate && (
                  <p className="mt-1 text-sm text-red-600">{errors.endDate.message}</p>
                )}
              </div>
            </div>

            {/* İş Günü Hesaplama */}
            {calculatedWorkDays > 0 && (
              <div className="bg-blue-50 border border-blue-200 rounded-md p-4">
                <div className="flex items-center">
                  <Clock className="h-5 w-5 text-blue-600 mr-2" />
                  <span className="text-sm font-medium text-blue-800">
                    Hesaplanan İş Günü: {calculatedWorkDays} gün
                  </span>
                </div>
              </div>
            )}

            {/* Departman Seçimi */}
            {departments.length > 0 && (
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Departman (Opsiyonel)
                </label>
                <select
                  {...register('departmentId', { valueAsNumber: true })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                >
                  <option value="">Departman seçiniz</option>
                  {departments.map((dept) => (
                    <option key={dept.id} value={dept.id}>
                      {dept.name}
                    </option>
                  ))}
                </select>
              </div>
            )}

            {/* Açıklama */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Açıklama (Opsiyonel)
              </label>
              <textarea
                {...register('reason')}
                rows={4}
                className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 placeholder-gray-500"
                placeholder="İzin talebinizle ilgili açıklama ekleyebilirsiniz..."
              />
            </div>

            {/* Uyarı */}
            <div className="bg-yellow-50 border border-yellow-200 rounded-md p-4">
              <div className="flex items-start">
                <AlertCircle className="h-5 w-5 text-yellow-600 mr-2 mt-0.5" />
                <div className="text-sm text-yellow-800">
                  <p className="font-medium">Önemli Bilgiler:</p>
                  <ul className="mt-1 list-disc list-inside space-y-1">
                    <li>İzin talebiniz onay sürecine girecektir</li>
                    <li>Hafta sonları iş günü olarak sayılmaz</li>
                    <li>Yıllık izin limitinizi aşmamaya dikkat ediniz</li>
                  </ul>
                </div>
              </div>
            </div>

            {/* Butonlar */}
            <div className="flex justify-end space-x-4">
              <Link
                href="/dashboard"
                className="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                İptal
              </Link>
              <button
                type="submit"
                disabled={isLoading}
                className="px-4 py-2 bg-blue-600 text-white rounded-md text-sm font-medium hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {isLoading ? (
                  <div className="flex items-center">
                    <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                    Oluşturuluyor...
                  </div>
                ) : (
                  'İzin Talebi Oluştur'
                )}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
} 