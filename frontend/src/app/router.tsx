import { createBrowserRouter, Navigate } from "react-router-dom"
import { DashboardLayout } from "@/features/layout/DashboardLayout"
import { PropertiesPage } from "@/features/properties/pages/PropertiesPage"
import { HousesPage } from "@/features/houses/pages/HousesPage"
import { PersonsPage } from "@/features/persons/pages/PersonsPage"
import { RentalsPage } from "@/features/rentals/pages/RentalsPage"

const basename = import.meta.env.BASE_URL

export const router = createBrowserRouter(
  [
    {
      element: <DashboardLayout />,
      children: [
        { index: true, element: <Navigate to="/properties" replace /> },
        { path: "properties", element: <PropertiesPage /> },
        { path: "houses", element: <HousesPage /> },
        { path: "persons", element: <PersonsPage /> },
        { path: "rentals", element: <RentalsPage /> },
        { path: "*", element: <Navigate to="/properties" replace /> },
      ],
    },
  ],
  { basename },
)