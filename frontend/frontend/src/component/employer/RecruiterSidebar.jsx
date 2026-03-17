import React from 'react';
import { 
  FiHome, FiBriefcase, FiUsers, FiCalendar, 
  FiPieChart, FiCreditCard, FiSettings 
} from 'react-icons/fi';

const RecruiterSidebar = ({ isOpen, setIsOpen }) => {
  const menuItems = [
    { name: 'Tổng quan', icon: <FiHome />, path: '/recruiter/dashboard', active: true },
    { name: 'Quản lý tin đăng', icon: <FiBriefcase />, path: '/recruiter/jobs', active: false },
    { name: 'Hồ sơ ứng viên', icon: <FiUsers />, path: '/recruiter/candidates', badge: '12', active: false },
    { name: 'Lịch phỏng vấn', icon: <FiCalendar />, path: '/recruiter/interviews', active: false },
    { name: 'Báo cáo hiệu quả', icon: <FiPieChart />, path: '/recruiter/reports', active: false },
    { name: 'Gói dịch vụ', icon: <FiCreditCard />, path: '/recruiter/billing', active: false },
    { name: 'Cài đặt tài khoản', icon: <FiSettings />, path: '/recruiter/settings', active: false },
  ];

  return (
    <>
      {/* Overlay cho Mobile khi Sidebar mở */}
      {isOpen && (
        <div 
          className="fixed inset-0 bg-gray-900/50 z-40 lg:hidden"
          onClick={() => setIsOpen(false)}
        />
      )}

      {/* Sidebar Content */}
      <aside className={`fixed top-0 left-0 h-full w-64 bg-white border-r border-gray-200 shadow-sm z-50 transform transition-transform duration-300 ease-in-out flex flex-col ${isOpen ? 'translate-x-0' : '-translate-x-full'} lg:translate-x-0 lg:static lg:h-screen lg:z-30`}>
        
        {/* Logo/Brand Area (Nằm trong Sidebar thay vì Header) */}
        <div className="h-16 flex items-center gap-3 px-6 border-b border-gray-200 shrink-0">
          <div className="bg-emerald-500 w-8 h-8 rounded-lg flex items-center justify-center text-white font-bold shadow-sm">
            C
          </div>
          <span className="font-bold text-gray-800 text-lg">
            Employer<span className="text-emerald-500">Pro</span>
          </span>
        </div>

        {/* Menu Navigation */}
        <nav className="flex-1 overflow-y-auto py-6 px-4 space-y-1">
          {menuItems.map((item, index) => (
            <a
              key={index}
              href={item.path}
              className={`flex items-center justify-between px-4 py-2.5 text-sm font-medium rounded-lg transition-colors ${
                item.active 
                  ? 'bg-emerald-50 text-emerald-700' 
                  : 'text-gray-600 hover:bg-gray-50 hover:text-emerald-600'
              }`}
            >
              <div className="flex items-center gap-3">
                <span className={item.active ? 'text-emerald-600' : 'text-gray-400'}>
                  {item.icon}
                </span>
                {item.name}
              </div>
              {/* Hiển thị số lượng báo cáo/CV mới nếu có */}
              {item.badge && (
                <span className="bg-red-100 text-red-600 py-0.5 px-2 rounded-full text-[10px] font-bold">
                  {item.badge}
                </span>
              )}
            </a>
          ))}
        </nav>

        {/* Nút nâng cấp gói (Upsell area thường thấy ở nền tảng tuyển dụng) */}
        <div className="p-4 border-t border-gray-100 shrink-0">
          <div className="from-emerald-500 to-teal-500 rounded-xl p-4 text-white text-sm shadow-md">
            <p className="font-semibold mb-1">Gói Premium</p>
            <p className="text-emerald-50 text-xs mb-3">Tìm ứng viên IT cấp cao nhanh hơn gấp 3 lần.</p>
            <button className="w-full bg-white text-emerald-600 py-1.5 rounded-lg font-medium text-xs hover:bg-emerald-50 transition">
              Nâng cấp ngay
            </button>
          </div>
        </div>
      </aside>
    </>
  );
};

export default RecruiterSidebar;