import { createBrowserRouter } from "react-router-dom";
import LayoutDefault from "../layouts/LayoutDefault";
import Home from "../pages/User/Home";
import Dashboard from "../pages/Admin/Dashboard";
import Home1 from "../pages/Employer/Home";
import Error from "../pages/Error.jsx/Error";
import AdminLayout from "../layouts/AdminLayout";
import EmployerLayout from "../layouts/EmployerLayout";
import Job from "../pages/Admin/Job";
import About from "../pages/Employer/About";
export const router = createBrowserRouter([
    {
        path: "/",
        element: <LayoutDefault />,
        children: [
            {
                index: true,
                element: <Home />
            }
        ]
    },
    {
        path: "/employer",
        element: (
            <EmployerLayout />
        ),
        children: [
            { index: true, element: <Home1 /> },
            {
                path: '/employer/job',
                element: <About />,
            }
        ],
    },
    {
        path: "/admin",
        element: (
            <AdminLayout />
        ),
        children: [
            { index: true, element: <Dashboard /> },
            {
                path: '/admin/job',
                element: <Job />,
            }
        ],
    },
    {
        path: "/404",
        element: <Error />
    },
    {
        path: "*",
        element: <Error />,
    }
])