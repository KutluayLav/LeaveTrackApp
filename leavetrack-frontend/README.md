# İzin Takip Sistemi - Frontend

Bu proje, çalışanların izin taleplerini yönetebilecekleri modern bir web uygulamasıdır.

## 🚀 Özellikler

- **Kullanıcı Girişi**: Güvenli JWT tabanlı kimlik doğrulama
- **Dashboard**: İzin özeti ve istatistikler
- **İzin Yönetimi**: Yeni izin talebi oluşturma ve mevcut izinleri görüntüleme
- **Filtreleme**: Durum ve tür bazlı filtreleme
- **Responsive Tasarım**: Mobil ve masaüstü uyumlu
- **Gerçek Zamanlı Hesaplama**: İş günü hesaplama
- **Modern UI**: Tailwind CSS ile modern arayüz

## 🛠️ Teknolojiler

- **Next.js 14**: App Router pattern
- **TypeScript**: Tip güvenliği
- **Tailwind CSS**: Modern CSS framework
- **React Hook Form**: Form yönetimi
- **Zod**: Form validasyonu
- **Axios**: HTTP client
- **Lucide React**: İkonlar
- **React Hot Toast**: Bildirimler

## 📦 Kurulum

1. Bağımlılıkları yükleyin:
```bash
npm install
```

2. Environment dosyasını oluşturun:
```bash
cp .env.example .env.local
```

3. API URL'ini ayarlayın:
```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

4. Geliştirme sunucusunu başlatın:
```bash
npm run dev
```

5. Tarayıcıda [http://localhost:3000](http://localhost:3000) adresini açın.

## 🏗️ Proje Yapısı

```
src/
├── app/                    # Next.js App Router
│   ├── dashboard/         # Dashboard sayfası
│   ├── leaves/           # İzin yönetimi sayfaları
│   ├── login/            # Giriş sayfası
│   ├── globals.css       # Global stiller
│   ├── layout.tsx        # Ana layout
│   └── page.tsx          # Ana sayfa
├── contexts/             # React Context'ler
│   └── AuthContext.tsx   # Kimlik doğrulama context'i
├── services/             # API servisleri
│   └── api.ts           # API client ve endpoint'ler
└── types/               # TypeScript tip tanımları
    └── index.ts         # Tüm tip tanımları
```

## 🔧 API Entegrasyonu

Frontend, backend API'si ile aşağıdaki endpoint'leri kullanır:

- `POST /api/auth/login` - Kullanıcı girişi
- `GET /api/leaves/user` - Kullanıcının izinleri
- `POST /api/leaves` - Yeni izin oluşturma
- `GET /api/leaves/summary` - İzin özeti
- `DELETE /api/leaves/{id}` - İzin silme

## 🎨 UI Bileşenleri

- **Login Form**: Email ve şifre ile giriş
- **Dashboard**: İstatistik kartları ve son izinler
- **Leave Form**: İzin talebi oluşturma formu
- **Leave List**: Filtrelenebilir izin listesi
- **Navigation**: Responsive header ve navigasyon

## 🔐 Güvenlik

- JWT token tabanlı kimlik doğrulama
- Token yenileme mekanizması
- Güvenli API istekleri
- Form validasyonu

## 📱 Responsive Tasarım

- Mobil öncelikli tasarım
- Tablet ve masaüstü uyumlu
- Touch-friendly arayüz
- Modern UI/UX prensipleri

## 🚀 Production

Production build için:

```bash
npm run build
npm start
```

## 📄 Lisans

Bu proje MIT lisansı altında lisanslanmıştır.
