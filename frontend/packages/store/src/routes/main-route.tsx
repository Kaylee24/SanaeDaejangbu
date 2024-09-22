import type { RouteObject } from 'react-router-dom'

import paths from '@/configs/paths'
import AppLayout from '@/layouts/app-layout'
import SignInView from '@/pages/auth/sign-in-view'
import SignUpView from '@/pages/auth/sign-up-view'
import SignInLayout from '@/layouts/sign-in-layout'
import SignUpLayout from '@/layouts/sign-up-layout'
import TotalDashbaordView from '@/pages/dashboard/total-dashboard-view'
import SalesMenuDashboardView from '@/pages/dashboard/sales-menu-dashboard-view'
import SalesTimeDashboardView from '@/pages/dashboard/sales-time-dashboard-view'
import SettlementDashboardView from '@/pages/dashboard/settlement-dashbaord-view'
import { PaymentManagementView } from '@/pages/management/payment-management-view'
import SalesCompanyDashboardView from '@/pages/dashboard/sales-company-dashboard-view'

const mainRoute: RouteObject[] = [
  {
    element: <SignInLayout />,
    children: [
      {
        path: paths.auth.signIn,
        element: <SignInView />,
      },
    ],
  },
  {
    element: <SignUpLayout />,
    children: [
      {
        path: paths.auth.signUp,
        element: <SignUpView />,
      },
    ],
  },
  {
    element: <AppLayout />,
    children: [
      // Dashboard
      {
        path: paths.dashboard.total,
        element: <TotalDashbaordView />,
      },
      {
        path: paths.dashboard.sales.menu,
        element: <SalesMenuDashboardView />,
      },
      {
        path: paths.dashboard.sales.company,
        element: <SalesCompanyDashboardView />,
      },
      {
        path: paths.dashboard.sales.time,
        element: <SalesTimeDashboardView />,
      },
      {
        path: paths.dashboard.settlement,
        element: <SettlementDashboardView />,
      },

      // Management
      {
        path: paths.management.payment,
        element: <PaymentManagementView />,
      },
    ],
  },
]

export default mainRoute