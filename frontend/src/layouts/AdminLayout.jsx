import React from 'react'
import { Outlet } from 'react-router-dom'
import AdminHeader from '../component/admin/AdminHeader'
import AdminSidebar from '../component/admin/AdminSidebar'

export default function AdminLayout() {
    return (
        <div className="flex h-screen overflow-hidden">
            {/* Sidebar */}
            <AdminSidebar />

            {/* Main content */}
            <div className="flex flex-col flex-1">
                {/* Header */}
                <AdminHeader />

                {/* Content */}
                <main className="flex-1 p-4 overflow-y-auto bg-gray-100">
                    <Outlet />
                </main>
            </div>
        </div>
    )
}