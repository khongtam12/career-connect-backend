import React from "react";

const Footer = () => {
  return (
    <footer className="bg-white border-t border-gray-200 pt-16 pb-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Grid chia 5 cột trên Desktop, 1 cột trên Mobile */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-10 lg:gap-8">

          {/* CỘT 1: Thông tin thương hiệu & Liên hệ (Chiếm 2/5 không gian) */}
          <div className="lg:col-span-2 lg:pr-12">
            {/* Logo */}
            <a href="/" className="flex items-center gap-2 mb-4 group cursor-pointer">
              <div className="bg-emerald-500 w-3 h-3 rounded-full"></div>
              <div className="flex flex-col">
                <span className="font-extrabold text-gray-900 text-2xl tracking-tight leading-none group-hover:text-emerald-600 transition-colors">
                  CAREER CONNECT
                </span>
                <span className="text-[13px] font-medium text-gray-500 mt-1 uppercase tracking-wider">
                  Tiếp lợi thế - Nối thành công
                </span>
              </div>
            </a>

            {/* Badges Chứng nhận (Placeholder) */}
            <div className="flex items-center gap-4 mb-8">
              <div className="h-8 px-3 bg-gray-50 border border-gray-200 rounded flex items-center justify-center text-xs text-gray-500 font-medium">
                Google for Startups
              </div>
              <div className="h-8 px-3 bg-emerald-50 border border-emerald-100 rounded flex items-center justify-center text-xs text-emerald-700 font-bold">
                DMCA PROTECTED
              </div>
            </div>

            {/* Liên hệ */}
            <div className="space-y-2 mb-8 text-sm">
              <p className="font-bold text-gray-900">Liên hệ</p>
              <p className="text-gray-600">
                Hotline: <span className="font-semibold text-gray-900">(024) 6680 5588</span> (Giờ hành chính)
              </p>
              <p className="text-gray-600">
                Email: <span className="text-emerald-600 hover:underline cursor-pointer">hotro@careerconnect.vn</span>
              </p>
            </div>

            {/* Tải ứng dụng */}
            <div className="mb-8">
              <p className="font-bold text-gray-900 text-sm mb-3">Ứng dụng tải xuống</p>
              <div className="flex gap-3">
                <button className="bg-gray-900 hover:bg-gray-800 text-white px-4 py-2 rounded-lg flex items-center gap-2 transition-colors">
                  <div className="w-5 h-5 bg-white/20 rounded-full"></div> {/* Icon Placeholder */}
                  <div className="text-left">
                    <div className="text-[10px] leading-none text-gray-300">Download on the</div>
                    <div className="text-sm font-semibold leading-tight">App Store</div>
                  </div>
                </button>
                <button className="bg-gray-900 hover:bg-gray-800 text-white px-4 py-2 rounded-lg flex items-center gap-2 transition-colors">
                  <div className="w-5 h-5 bg-white/20 rounded-sm"></div> {/* Icon Placeholder */}
                  <div className="text-left">
                    <div className="text-[10px] leading-none text-gray-300">GET IT ON</div>
                    <div className="text-sm font-semibold leading-tight">Google Play</div>
                  </div>
                </button>
              </div>
            </div>

            {/* Mạng xã hội */}
            <div>
              <p className="font-bold text-gray-900 text-sm mb-3">Cộng đồng Career Connect</p>
              <div className="flex gap-3">
                {['FB', 'YT', 'IN', 'TK'].map((social, idx) => (
                  <a key={idx} href="#" className="w-8 h-8 rounded-full bg-gray-500 hover:bg-emerald-500 text-white flex items-center justify-center text-xs font-bold transition-colors">
                    {social}
                  </a>
                ))}
              </div>
            </div>
          </div>

          {/* CỘT 2: Về công ty & Đối tác */}
          <div>
            <div className="mb-10">
              <h3 className="font-bold text-gray-900 text-[15px] mb-4">Về Career Connect</h3>
              <ul className="space-y-3">
                {['Giới thiệu', 'Góc báo chí', 'Tuyển dụng', 'Liên hệ', 'Hỏi đáp', 'Chính sách bảo mật', 'Điều khoản dịch vụ'].map((item) => (
                  <li key={item}>
                    <a href="#" className="text-sm text-gray-600 hover:text-emerald-600 transition-colors">{item}</a>
                  </li>
                ))}
              </ul>
            </div>
            <div>
              <h3 className="font-bold text-gray-900 text-[15px] mb-4">Đối tác</h3>
              <ul className="space-y-3">
                {['TestCenter', 'TopHR', 'ViecNgay', 'Happy Time'].map((item) => (
                  <li key={item}>
                    <a href="#" className="text-sm text-gray-600 hover:text-emerald-600 transition-colors">{item}</a>
                  </li>
                ))}
              </ul>
            </div>
          </div>

          {/* CỘT 3: Hồ sơ & Khám phá */}
          <div>
            <div className="mb-10">
              <h3 className="font-bold text-gray-900 text-[15px] mb-4">Hồ sơ và CV</h3>
              <ul className="space-y-3">
                {['Quản lý CV của bạn', 'Hướng dẫn viết CV', 'Thư viện CV theo ngành nghề', 'Review CV'].map((item) => (
                  <li key={item}>
                    <a href="#" className="text-sm text-gray-600 hover:text-emerald-600 transition-colors">{item}</a>
                  </li>
                ))}
              </ul>
            </div>
            <div>
              <h3 className="font-bold text-gray-900 text-[15px] mb-4">Khám phá</h3>
              <ul className="space-y-3">
                {['Ứng dụng di động', 'Tính lương Gross - Net', 'Tính lãi suất kép', 'Lập kế hoạch tiết kiệm', 'Tính bảo hiểm thất nghiệp', 'Tính bảo hiểm xã hội một lần', 'Trắc nghiệm MBTI', 'Trắc nghiệm MI'].map((item) => (
                  <li key={item}>
                    <a href="#" className="text-sm text-gray-600 hover:text-emerald-600 transition-colors">{item}</a>
                  </li>
                ))}
              </ul>
            </div>
          </div>

          {/* CỘT 4: Xây dựng sự nghiệp & Quy tắc */}
          <div>
            <div className="mb-10">
              <h3 className="font-bold text-gray-900 text-[15px] mb-4">Xây dựng sự nghiệp</h3>
              <ul className="space-y-3">
                {['Việc làm tốt nhất', 'Việc làm lương cao', 'Việc làm quản lý', 'Việc làm IT', 'Việc làm Senior', 'Việc làm bán thời gian'].map((item) => (
                  <li key={item}>
                    <a href="#" className="text-sm text-gray-600 hover:text-emerald-600 transition-colors">{item}</a>
                  </li>
                ))}
              </ul>
            </div>
            <div>
              <h3 className="font-bold text-gray-900 text-[15px] mb-4">Quy tắc chung</h3>
              <ul className="space-y-3">
                {['Điều kiện giao dịch chung', 'Giá dịch vụ & Cách thanh toán', 'Thông tin về vận chuyển'].map((item) => (
                  <li key={item}>
                    <a href="#" className="text-sm text-gray-600 hover:text-emerald-600 transition-colors">{item}</a>
                  </li>
                ))}
              </ul>
            </div>
          </div>

        </div>

        {/* Bản quyền */}
        <div className="mt-16 pt-8 border-t border-gray-200 text-center md:text-left flex flex-col md:flex-row justify-between items-center gap-4">
          <p className="text-xs text-gray-500">
            © 2026 Career Connect. All rights reserved.
          </p>
          <div className="text-xs text-gray-500">
            Trụ sở: TP. Hồ Chí Minh, Việt Nam
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;