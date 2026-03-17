import React from 'react'
import Header from '../component/user/Header'
import { Outlet } from 'react-router-dom'
import Footer from '../component/user/Footer'

export default function LayoutDefault() {
    return (
        <div className='overflow-x-hidden'>
            <Header />
            <div className='main'>
                <Outlet />
            </div>
            <Footer />

        </div>
    )
}
