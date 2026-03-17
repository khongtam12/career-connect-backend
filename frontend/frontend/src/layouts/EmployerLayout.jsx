import React from 'react'
import { Outlet } from 'react-router-dom'
import RecruiterHeader from '../component/employer/RecruiterHeader'
import RecruiterSidebar from '../component/employer/RecruiterSidebar'

export default function EmployerLayout() {
    return (
        <div className="flex h-screen overflow-hidden">
            {/* Sidebar */}
            <RecruiterSidebar />

            {/* Main content */}
            <div className="flex flex-col flex-1">
                {/* Header */}
                <RecruiterHeader />

                {/* Content */}
                <main className="flex-1 p-4 overflow-y-auto bg-gray-100">
                    <Outlet />
                </main>
            </div>
        </div>
    )
}