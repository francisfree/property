export interface PaymentWeeklyEntry {
  id: number
  publicId: string
  dateCreated: string
  weekCount: number
  weekName: string
  cash: string
  till: string
  mpesa: string
  totalPaid: string
}

export interface RentalPayment {
  id: number
  publicId: string
  dateCreated: string
  month: string
  blockName: string
  houseNumber: string
  occupantName: string
  occupantPhoneNumber: string
  receiptNumber: string | null
  rent: string
  garbage: string
  totalRentPaidPreviousMonth: string
  arrearsBroughtForward: string
  totalRentDue: string
  totalRentPaid: string
  previousWaterUnit: string
  currentWaterUnit: string
  unitsConsumed: string
  pricePerUnit: string
  waterBill: string
  arrearsCarriedForward: string | null
  weeklyEntries: PaymentWeeklyEntry[]
}
