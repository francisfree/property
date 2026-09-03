export interface PaymentWeeklyEntry {
  id: number
  publicId: string
  dateCreated: string
  weekCount: number
  weekName: string
  cash: string
  till: string
  mpesa: string
}

export interface PaymentMonth {
  id: number
  publicId: string
  dateCreated: string
  month: string
  blockName: string
  houseNumber: string
  occupantName: string
  occupantPhoneNumber: string
  rentCurrentMonth: string
  rentPreviousMonth: string
  arrearsBroughtForward: string
  totalPayment: string
  previousWaterUnit: string
  currentWaterUnit: string
  pricePerUnit: string
  unitsConsumed: string
  waterBill: string
  weeklyEntries: PaymentWeeklyEntry[]
}
