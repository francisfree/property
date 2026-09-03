import { NavLink } from "react-router-dom"
import { Building2, DoorOpen, Users, FileText } from "lucide-react"
import { cn } from "@/lib/utils"

const navigation = [
  { to: "/properties", label: "Properties", icon: Building2 },
  { to: "/houses", label: "Houses", icon: DoorOpen },
  { to: "/persons", label: "Persons", icon: Users },
  { to: "/rentals", label: "Rentals", icon: FileText },
]

export function Sidebar() {
  return (
    <aside className="flex h-full w-56 flex-col border-r bg-muted/40">
      <div className="flex h-14 items-center border-b px-4">
        <h2 className="text-sm font-semibold tracking-tight">
          Castle Property Mgmt
        </h2>
      </div>
      <nav className="flex-1 space-y-1 p-2">
        {navigation.map((item) => {
          const Icon = item.icon
          return (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                cn(
                  "flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors",
                  "hover:bg-accent hover:text-accent-foreground",
                  isActive
                    ? "bg-primary text-primary-foreground hover:bg-primary hover:text-primary-foreground"
                    : "text-muted-foreground",
                )
              }
            >
              <Icon className="h-4 w-4" />
              {item.label}
            </NavLink>
          )
        })}
      </nav>
    </aside>
  )
}