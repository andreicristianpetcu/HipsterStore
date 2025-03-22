export interface IPrice {
  id: number;
  value?: number | null;
}

export type NewPrice = Omit<IPrice, 'id'> & { id: null };
