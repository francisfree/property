import { describe, expect, it } from "vitest"

import { formatDateTime, formatMoney } from "./format"

describe("formatDateTime", () => {
  it("formats ISO timestamps as dd-MMM-yyyy hh:mm:ss.SSS AM/PM", () => {
    expect(formatDateTime("2026-09-02T09:20:12.095")).toBe(
      "02-Sep-2026 09:20:12.095 AM",
    )
  })

  it("rolls hour into cluster", () => {
    expect(formatDateTime("2026-01-15T13:05:00.000")).toContain("01:05:00.000 PM")
  })
})

describe("formatMoney", () => {
  it("formats numeric strings with two decimals", () => {
    expect(formatMoney("1250")).toBe("1,250.00")
    expect(formatMoney(1250.5)).toBe("1,250.50")
  })

  it("returns — for null/empty", () => {
    expect(formatMoney(null)).toBe("—")
    expect(formatMoney("")).toBe("—")
  })
})