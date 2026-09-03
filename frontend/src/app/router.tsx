import { createBrowserRouter, Navigate } from "react-router-dom"
import { DashboardLayout } from "@/features/layout/DashboardLayout"
import { LoginPage } from "@/features/authentication/pages/LoginPage"
import { PropertiesPage } from "@/features/properties/pages/PropertiesPage"
import { HousesPage } from "@/features/houses/pages/HousesPage"
import { PersonsPage } from "@/features/persons/pages/PersonsPage"
import { RentalsPage } from "@/features/rentals/pages/RentalsPage"
import { PaymentsPage } from "@/features/payments/pages/PaymentsPage"
import { UsersPage } from "@/features/users/pages/UsersPage"
import { ProtectedRoute } from "@/components/ProtectedRoute"
import { ForbiddenPage } from "@/pages/ForbiddenPage"

const basename = import.meta.env.BASE_URL

export const router = createBrowserRouter(
  [
    {
      path: "/login",
      element: <LoginPage />,
    },
    {
      path: "/forbidden",
      element: <ForbiddenPage />,
    },
    {
      element: (
        <ProtectedRoute>
          <DashboardLayout />
        </ProtectedRoute>
      ),
      children: [
        { index: true, element: <Navigate to="/properties" replace /> },
        { path: "properties", element: <PropertiesPage /> },
        { path: "houses", element: <HousesPage /> },
        { path: "persons", element: <PersonsPage /> },
        { path: "rentals", element: <RentalsPage /> },
        { path: "payments", element: <PaymentsPage /> },
        {
          path: "admin/users",
          element: (
            <ProtectedRoute requiredRoles={["ADMIN"]}>
              <UsersPage />
            </ProtectedRoute>
          ),
        },
        { path: "*", element: <Navigate to="/properties" replace /> },
      ],
    },
  ],
  { basename },
)