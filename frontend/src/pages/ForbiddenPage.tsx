import { useNavigate } from "react-router-dom"
import { ShieldOff } from "lucide-react"
import { Button } from "@/components/ui/button"

export function ForbiddenPage() {
  const navigate = useNavigate()

  return (
    <div className="flex min-h-screen flex-col items-center justify-center gap-4">
      <ShieldOff className="h-16 w-16 text-muted-foreground" />
      <h1 className="text-2xl font-bold">Access Denied</h1>
      <p className="text-muted-foreground">
        You don't have permission to access this page.
      </p>
      <Button onClick={() => navigate("/properties")}>Go to Dashboard</Button>
    </div>
  )
}
