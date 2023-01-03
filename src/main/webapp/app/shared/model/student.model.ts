export interface IStudent {
  id?: number;
  studentId?: number | null;
  name?: string | null;
  age?: number | null;
  classNam?: string | null;
  address?: string | null;
}

export const defaultValue: Readonly<IStudent> = {};
