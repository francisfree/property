const MONTHS = [
  "Jan", "Feb", "Mar", "Apr", "May", "Jun",
  "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
]

function pad(value: number): string {
  return value.toString().padStart(2, "0")
}

function toDate(value: string): Date {
  const date = new Date(value)
  return isNaN(date.getTime()) ? new Date(0) : date
}

function format12Hour(hours: number): string {
  return hours % 12 === 0 ? "12" : pad(hours % 12)
}

function meridiem(hours: number): string {
  return hours < 12 ? "AM" : "PM"
}

function padMilliseconds(value: number): string {
  return value.toString().padStart(3, "0")
}

export function formatDateTime(value: string): string {
  const d = toDate(value)
  return (
    `${pad(d.getDate())}-${MONTHS[d.getMonth()]}-${d.getFullYear()} ${format12Hour(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}.${padMilliseconds(d.getMilliseconds())} ${meridiem(d.getHours())}`
  )
}

export function formatDate(value: string): string {
  const d = toDate(value)
  return `${pad(d.getDate())}-${MONTHS[d.getMonth()]}-${d.getFullYear()}`
}

export function formatMoney(value: string | number | null | undefined): string {
  if (value === null || value === undefined || value === "") return "—"
  const num = typeof value === "string" ? Number(value.replace(",", "")) : value
  if (isNaN(num)) return "—"
  return num.toLocaleString("en-US", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  })
}

export function formatUuid(value: string): string {
  if (!value) return value
  return value.slice(0, 8) + "…"
}

export function formatFullName(firstName: string, lastName: string, otherName?: string | null): string {
  return [firstName, otherName, lastName].filter(Boolean).join(" ")
}