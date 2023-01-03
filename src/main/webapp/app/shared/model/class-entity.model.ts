export interface IClassEntity {
  id?: number;
  classId?: number | null;
  name?: string | null;
}

export const defaultValue: Readonly<IClassEntity> = {};
