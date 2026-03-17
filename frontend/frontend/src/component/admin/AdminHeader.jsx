import React, { useState } from "react";
import { 
  FiBell, 
  FiSearch, 
  FiSettings, 
  FiMenu, 
  FiActivity, 
  FiLogOut, 
  FiChevronDown 
} from "react-icons/fi";

const AdminHeader = ({ setSidebarOpen }) => {
  // Quản lý trạng thái hệ thống: "stable" (ổn định) hoặc "error" (có lỗi)
  const [systemStatus, setSystemStatus] = useState("stable"); 
  
  // Quản lý việc đóng/mở menu chọn trạng thái
  const [showStatusMenu, setShowStatusMenu] = useState(false);

  return (
    <header className="sticky top-0 z-20 w-full bg-slate-900 border-b border-slate-800 shadow-sm h-16 flex items-center justify-between px-4 sm:px-6">
      
      {/* LEFT: Nút mở Sidebar (Mobile) & Trạng thái Server & Thanh tìm kiếm */}
      <div className="flex items-center gap-4 flex-1">
        
        {/* Nút Hamburger mở Sidebar trên Mobile */}
        <button 
          onClick={() => setSidebarOpen(true)}
          className="lg:hidden text-slate-400 hover:text-white p-2 -ml-2 rounded-md transition-colors focus:outline-none focus:ring-2 focus:ring-slate-700"
          aria-label="Mở menu quản trị"
        >
          <FiMenu size={20} />
        </button>

        {/* Trạng thái Server (Có Dropdown để Admin tự thay đổi) */}
        <div className="hidden lg:block relative">
          <button 
            onClick={() => setShowStatusMenu(!showStatusMenu)}
            className="flex items-center gap-2 bg-slate-800 px-3 py-1.5 rounded-md border border-slate-700 hover:border-slate-500 transition-colors focus:outline-none"
            aria-haspopup="true"
            aria-expanded={showStatusMenu}
          >
             <FiActivity className={systemStatus === "stable" ? "text-green-400" : "text-red-400"} size={16} />
             <span className="text-xs text-slate-300 font-medium w-28 text-left">
               {systemStatus === "stable" ? "Hệ thống ổn định" : "Lỗi Server"}
             </span>
             <FiChevronDown className={`text-slate-500 transition-transform duration-200 ${showStatusMenu ? 'rotate-180' : ''}`} size={14} />
          </button>

          {/* Menu Dropdown chọn trạng thái giả lập */}
          {showStatusMenu && (
            <div className="absolute left-0 mt-2 w-40 rounded-md shadow-lg bg-slate-800 ring-1 ring-black ring-opacity-5 py-1 z-50 border border-slate-700">
              <button 
                onClick={() => { setSystemStatus("stable"); setShowStatusMenu(false); }} 
                className="w-full text-left px-4 py-2 text-xs text-slate-300 hover:bg-slate-700 hover:text-green-400 flex items-center gap-2 transition-colors"
              >
                <span className="w-2 h-2 rounded-full bg-green-400"></span> Ổn định
              </button>
              <button 
                onClick={() => { setSystemStatus("error"); setShowStatusMenu(false); }} 
                className="w-full text-left px-4 py-2 text-xs text-slate-300 hover:bg-slate-700 hover:text-red-400 flex items-center gap-2 transition-colors"
              >
                <span className="w-2 h-2 rounded-full bg-red-400"></span> Báo lỗi
              </button>
            </div>
          )}
        </div>
        
        {/* Ô Tìm kiếm toàn cục (Global Search) */}
        <div className="hidden md:flex relative max-w-sm w-full ml-4">
          <FiSearch className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
          <input
            type="text"
            className="w-full pl-10 pr-3 py-1.5 border border-slate-600 rounded bg-slate-800 text-slate-200 placeholder-slate-400 focus:outline-none focus:bg-slate-700 focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-all"
            placeholder="Tìm ID User, Giao dịch, Log..."
          />
        </div>
      </div>

      {/* RIGHT: Nút thao tác nhanh & Thông tin tài khoản (Profile) */}
      <div className="flex items-center gap-2 sm:gap-3">
        
        {/* Nút Cài đặt */}
        <button 
          className="p-2 text-slate-400 hover:text-white hover:bg-slate-800 rounded-md transition-colors focus:outline-none"
          aria-label="Cài đặt hệ thống"
        >
          <FiSettings size={18} />
        </button>

        {/* Nút Thông báo */}
        <button 
          className="relative p-2 text-slate-400 hover:text-white hover:bg-slate-800 rounded-md transition-colors focus:outline-none"
          aria-label="Thông báo"
        >
          {/* Chấm tròn báo hiệu có thông báo mới */}
          <span className="absolute top-1.5 right-1.5 h-2 w-2 rounded-full bg-amber-500 border-2 border-slate-900"></span>
          <FiBell size={18} />
        </button>

        {/* Đường gạch dọc phân cách */}
        <div className="h-6 w-px bg-slate-700 mx-1 sm:mx-2 hidden sm:block"></div>

        {/* Khối Profile của Admin */}
        <div className="flex items-center gap-3">
            {/* Thông tin Text (Ẩn trên Mobile) */}
            <div className="hidden md:flex flex-col items-end">
              <span className="text-sm font-semibold text-slate-200 leading-tight">Super Admin</span>
              <span className="text-[10px] text-blue-400 uppercase tracking-wider">Root Access</span>
            </div>
            
            {/* Avatar */}
            <img 
              className="h-8 w-8 rounded bg-slate-700 border border-slate-600 object-cover" 
              src="https://ui-avatars.com/api/?name=AD&background=2563EB&color=fff" 
              alt="Admin Avatar" 
            />
            
            {/* Nút Đăng xuất */}
            <button 
              className="p-1.5 text-slate-400 hover:text-red-400 hover:bg-slate-800 rounded-md transition-colors ml-1 focus:outline-none" 
              title="Đăng xuất"
              aria-label="Đăng xuất"
            >
                <FiLogOut size={18} />
            </button>
        </div>
      </div>

    </header>
  );
};

export default AdminHeader;