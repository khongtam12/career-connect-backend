import React from 'react';
import { 
  FiMonitor, FiUsers, FiBriefcase, FiDollarSign, 
  FiAlertTriangle, FiFileText, FiSettings, FiDatabase 
} from 'react-icons/fi';

const AdminSidebar = ({ isOpen, setIsOpen }) => {
  const menuGroups = [
    {
      title: 'HỆ THỐNG',
      items: [
        { name: 'Dashboard', icon: <FiMonitor />, path: '/admin', active: true },
        { name: 'Phân tích Data', icon: <FiDatabase />, path: '/admin/analytics', active: false },
      ]
    },
    {
      title: 'QUẢN LÝ DỮ LIỆU',
      items: [
        { name: 'Nhà tuyển dụng (B2B)', icon: <FiBriefcase />, path: '/admin/companies', active: false },
        { name: 'Ứng viên (B2C)', icon: <FiUsers />, path: '/admin/candidates', active: false },
        { name: 'Tài chính & Hóa đơn', icon: <FiDollarSign />, path: '/admin/billing', active: false },
      ]
    },
    {
      title: 'BẢO MẬT & CẤU HÌNH',
      items: [
        { name: 'Báo cáo vi phạm', icon: <FiAlertTriangle />, path: '/admin/reports', badge: '3', active: false },
        { name: 'System Logs', icon: <FiFileText />, path: '/admin/logs', active: false },
        { name: 'Cài đặt hệ thống', icon: <FiSettings />, path: '/admin/settings', active: false },
      ]
    }
  ];

  return (
    <>
      {isOpen && (
        <div className="fixed inset-0 bg-black/60 z-40 lg:hidden" onClick={() => setIsOpen(false)} />
      )}

      <aside className={`fixed top-0 left-0 h-full w-64 bg-slate-900 border-r border-slate-700 shadow-lg z-50 transform transition-transform duration-300 ease-in-out flex flex-col ${isOpen ? 'translate-x-0' : '-translate-x-full'} lg:translate-x-0 lg:static lg:h-screen lg:z-30`}>
        
        <div className="h-16 flex items-center gap-3 px-6 border-b border-slate-800 shrink-0">
          <div className="bg-blue-600 w-8 h-8 rounded-lg flex items-center justify-center text-white font-bold shadow-[0_0_10px_rgba(37,99,235,0.5)]">
            A
          </div>
          <span className="font-bold text-slate-100 text-lg tracking-wide uppercase">
            Admin<span className="text-blue-500 font-light">Panel</span>
          </span>
        </div>

        <nav className="flex-1 overflow-y-auto py-4 scrollbar-thin scrollbar-thumb-slate-700 scrollbar-track-transparent">
          {menuGroups.map((group, groupIdx) => (
            <div key={groupIdx} className="mb-6">
              <div className="px-6 mb-2 text-[10px] font-bold text-slate-500 tracking-widest">
                {group.title}
              </div>
              <ul className="space-y-0.5 px-3">
                {group.items.map((item, idx) => (
                  <li key={idx}>
                    <a
                      href={item.path}
                      className={`flex items-center justify-between px-3 py-2 text-sm font-medium rounded-md transition-colors ${
                        item.active 
                          ? 'bg-blue-600/10 text-blue-400 border-l-2 border-blue-500' 
                          : 'text-slate-400 hover:bg-slate-800 hover:text-slate-200 border-l-2 border-transparent'
                      }`}
                    >
                      <div className="flex items-center gap-3">
                        <span className={item.active ? 'text-blue-500' : 'text-slate-500'}>
                          {item.icon}
                        </span>
                        {item.name}
                      </div>
                      {item.badge && (
                        <span className="bg-amber-500/20 text-amber-400 py-0.5 px-2 rounded-full text-[10px] font-bold">
                          {item.badge}
                        </span>
                      )}
                    </a>
                  </li>
                ))}
              </ul>
            </div>
          ))}
        </nav>
      </aside>
    </>
  );
};

export default AdminSidebar;