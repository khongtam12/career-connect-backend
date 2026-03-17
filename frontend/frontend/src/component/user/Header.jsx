import React from "react";

const Header = () => {
  return (
    <header className="sticky top-0 z-50 bg-white border-b border-gray-200 shadow-sm">
      <div className="w-full px-6">
        {/* FLEX CHA */}
        <div className="flex items-center justify-between h-16">

          {/* LEFT: Logo + Menu */}
          <div className="flex items-center space-x-10">
            {/* Logo */}
            <div className="flex items-center space-x-2">
              <div className="bg-green-500 w-2 h-2 rounded-full"></div>
              <div>
                <div className="font-bold text-green-600 text-lg">
                  CAREER CONNECT
                </div>
                <div className="text-xs text-gray-500">
                  Tiếp lợi thế - Nối thành công
                </div>
              </div>
            </div>

            {/* Menu */}
            <nav className="flex space-x-6 text-sm font-medium text-gray-700">
              <span className="hover:text-green-500 cursor-pointer font-bold">Việc làm</span>
              <span className="hover:text-green-500 cursor-pointer font-bold">Tạo CV</span>
              <span className="hover:text-green-500 cursor-pointer font-bold">Công cụ</span>
              <span className="hover:text-green-500 cursor-pointer font-bold">Cẩm nang nghề nghiệp</span>
              <span className="flex items-center space-x-1 font-bold hover:text-green-500 cursor-pointer">
                <span>Career Connect</span>
                <span className="bg-yellow-400 text-[10px] px-1 rounded text-white font-bold">
                  PRO
                </span>
              </span>
            </nav>
          </div>

          {/* RIGHT: Buttons */}
          <div className="flex items-center space-x-3">

            <button className="border border-green-500 text-green-500 px-4 py-2 rounded-md text-sm hover:bg-green-50">
              Đăng ký
            </button>

            <button className="bg-green-500 text-white px-4 py-2 rounded-md text-sm hover:bg-green-600">
              Đăng nhập
            </button>

            <button className="bg-gray-100 px-4 py-2 rounded-md text-sm hover:bg-gray-200 font-bold">
              Đăng tuyển & tìm hồ sơ
            </button>
          </div>

        </div>
      </div>
    </header>
  );
};

export default Header;