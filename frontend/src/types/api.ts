export interface PagedResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export type Floor =
  | "Floor Ground"
  | "Floor 1"
  | "Floor 2"
  | "Floor 3"
  | "Floor 4"
  | "Floor 5"
  | "Floor 6"
  | "Floor 7"
  | "Floor 8"
  | "Floor 9"

export type HouseStatus = "Vacant" | "Occupied"
export type IdentificationType = "NATIONAL_ID" | "PASSPORT"
export type RentalAccountStatus = "Active" | "Closed"
export type RentalArrearStatus = "Vacant" | "Occupied" | "None" | "HasArrears" | "NoArrears"

export interface Property {
  id: string
  name: string
  location: string
  area: string
  dateCreated: string
  dateModified: string
}

export interface House {
  id: string
  number: string
  floor: Floor
  status: HouseStatus
  currentMonthlyRent: string | null
  propertyId: string
  propertyName: string
  dateCreated: string
  dateModified: string
}

export interface Person {
  id: string
  firstName: string
  lastName: string
  otherName: string | null
  identificationType: IdentificationType
  identificationNumber: string
  nationality: string | null
  phoneNumber: string
  dateCreated: string
  dateModified: string
}

export interface Rental {
  id: string
  amount: string
  accountStatus: RentalAccountStatus
  arrearStatus: RentalArrearStatus
  person: Person
  house: House
  dateCreated: string
}