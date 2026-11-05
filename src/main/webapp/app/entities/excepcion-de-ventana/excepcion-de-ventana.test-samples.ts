import dayjs from 'dayjs/esm';

import { IExcepcionDeVentana, NewExcepcionDeVentana } from './excepcion-de-ventana.model';

export const sampleWithRequiredData: IExcepcionDeVentana = {
  id: 25752,
  tipo: 'CANCELACION',
  desde: dayjs('2023-12-04T18:43'),
  hasta: dayjs('2023-12-04T21:46'),
};

export const sampleWithPartialData: IExcepcionDeVentana = {
  id: 9174,
  tipo: 'CANCELACION',
  desde: dayjs('2023-12-04T01:37'),
  hasta: dayjs('2023-12-04T15:39'),
};

export const sampleWithFullData: IExcepcionDeVentana = {
  id: 16446,
  tipo: 'INCLUSION',
  desde: dayjs('2023-12-04T16:03'),
  hasta: dayjs('2023-12-04T00:06'),
  motivo: 'disbar',
};

export const sampleWithNewData: NewExcepcionDeVentana = {
  tipo: 'INCLUSION',
  desde: dayjs('2023-12-04T00:26'),
  hasta: dayjs('2023-12-04T00:55'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
