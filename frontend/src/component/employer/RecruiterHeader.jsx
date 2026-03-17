import React, { useState } from "react";
import { FiBell, FiSearch, FiPlus, FiChevronDown, FiMenu, FiCreditCard } from "react-icons/fi";

// Thêm prop setSidebarOpen
const RecruiterHeader = ({ setSidebarOpen}) => {
  const [showProfile, setShowProfile] = useState(false);

  return (
    <header className="sticky top-0 z-20 w-full bg-white border-b border-gray-200 shadow-sm h-16 flex items-center justify-between px-4 sm:px-6">
      
      {/* Nút Hamburger cho Mobile & Thanh Tìm Kiếm */}
      <div className="flex items-center gap-4 flex-1">
        {/* Nhấn nút này để mở Sidebar trên màn hình nhỏ */}
        <button 
          onClick={() => setSidebarOpen(true)}
          className="lg:hidden text-gray-500 hover:text-emerald-600 p-2 -ml-2 rounded-md transition-colors"
        >
          <FiMenu size={20} />
        </button>
        
        <div className="hidden md:flex relative max-w-md w-full">
          <FiSearch className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
          <input
            type="text"
            className="w-full pl-10 pr-3 py-2 border border-gray-200 rounded-lg bg-gray-50 placeholder-gray-400 focus:bg-white focus:ring-1 focus:ring-emerald-500 focus:border-emerald-500 text-sm transition-all"
            placeholder="Tìm kiếm CV, Tên ứng viên, Kỹ năng..."
          />
        </div>
      </div>

      {/* Các action bên phải (Giữ nguyên như trước) */}
      <div className="flex items-center gap-4">
        <div className="hidden lg:flex items-center gap-2 bg-emerald-50 text-emerald-700 px-3 py-1.5 rounded-md text-sm font-medium border border-emerald-100">
          <FiCreditCard size={16} />
          <span>Số dư: 5.000.000đ</span>
          <button className="ml-2 text-xs bg-emerald-600 text-white px-2 py-0.5 rounded hover:bg-emerald-700 transition">Nạp</button>
        </div>

        <button className="hidden sm:flex items-center gap-2 bg-emerald-500 text-white hover:bg-emerald-600 px-4 py-2 rounded-lg text-sm font-semibold transition-all shadow-sm">
          <FiPlus size={16} /><span>Tạo chiến dịch</span>
        </button>

        <div className="h-6 w-px bg-gray-200 hidden sm:block mx-1"></div>

        <button className="relative p-2 text-gray-400 hover:text-emerald-600 transition-colors">
          <span className="absolute top-1.5 right-1.5 h-2.5 w-2.5 rounded-full bg-red-500 border-2 border-white"></span>
          <FiBell size={20} />
        </button>

        <div className="relative">
          <button onClick={() => setShowProfile(!showProfile)} className="flex items-center gap-2 focus:outline-none">
            <img className="h-8 w-8 rounded-full object-cover border border-gray-200" src="https://ui-avatars.com/api/?name=HR&background=10B981&color=fff" alt="Avatar" />
            <div className="hidden md:flex flex-col items-start text-left">
              <span className="text-sm font-medium text-gray-700 leading-tight">Jane HR</span>
              <span className="text-[11px] text-gray-500 truncate">Tech Corp</span>
            </div>
            <FiChevronDown className="hidden md:block text-gray-400" size={16} />
          </button>
        </div>
      </div>
    </header>
  );
};

export default RecruiterHeader;