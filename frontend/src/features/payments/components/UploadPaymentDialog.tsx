import { useRef, useState } from "react"
import { Upload } from "lucide-react"

import { Button } from "@/components/ui/button"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"

interface UploadPaymentDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  onSubmit: (file: File, month: string, blockName: string) => void
  submitting: boolean
}

export function UploadPaymentDialog({
  open,
  onOpenChange,
  onSubmit,
  submitting,
}: UploadPaymentDialogProps) {
  const [file, setFile] = useState<File | null>(null)
  const [month, setMonth] = useState("")
  const [blockName, setBlockName] = useState("")
  const fileInputRef = useRef<HTMLInputElement>(null)

  const canSubmit = file !== null && month !== "" && blockName.trim() !== "" && !submitting

  const handleSubmit = () => {
    if (!canSubmit) return
    onSubmit(file, month, blockName.trim())
    setFile(null)
    setMonth("")
    setBlockName("")
    if (fileInputRef.current) fileInputRef.current.value = ""
  }

  const handleOpenChange = (nextOpen: boolean) => {
    if (!nextOpen) {
      setFile(null)
      setMonth("")
      setBlockName("")
    }
    onOpenChange(nextOpen)
  }

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>Upload Payment File</DialogTitle>
          <DialogDescription>
            Upload an Excel (.xlsx) file containing payment data.
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-4 py-2">
          <div className="space-y-2">
            <Label htmlFor="payment-file">File</Label>
            <div className="flex items-center gap-2">
              <Input
                ref={fileInputRef}
                id="payment-file"
                type="file"
                accept=".xlsx"
                className="hidden"
                onChange={(e) => {
                  const selected = e.target.files?.[0]
                  if (selected) setFile(selected)
                }}
              />
              <Button
                variant="outline"
                type="button"
                onClick={() => fileInputRef.current?.click()}
              >
                <Upload className="mr-2 h-4 w-4" />
                Choose File
              </Button>
              {file && (
                <span className="truncate text-sm text-muted-foreground">
                  {file.name}
                </span>
              )}
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="payment-month">Month</Label>
            <Input
              id="payment-month"
              type="month"
              value={month}
              onChange={(e) => setMonth(e.target.value)}
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="payment-block">Block Name</Label>
            <Input
              id="payment-block"
              placeholder="e.g. Block A"
              value={blockName}
              onChange={(e) => setBlockName(e.target.value)}
            />
          </div>
        </div>

        <DialogFooter>
          <Button variant="outline" onClick={() => handleOpenChange(false)}>
            Cancel
          </Button>
          <Button onClick={handleSubmit} disabled={!canSubmit}>
            {submitting ? "Uploading..." : "Upload"}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}
