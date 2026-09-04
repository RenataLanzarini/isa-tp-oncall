import dayjs from 'dayjs/esm';

import { IIncidente, NewIncidente } from './incidente.model';

export const sampleWithRequiredData: IIncidente = {
  id: 10165,
  titulo: 'to but aha',
  severidad: 'SEV3',
  estado: 'ABIERTO',
  detectadoEn: dayjs('2023-12-04T06:33'),
};

export const sampleWithPartialData: IIncidente = {
  id: 6822,
  titulo: 'eek',
  severidad: 'SEV2',
  estado: 'CERRADO',
  detectadoEn: dayjs('2023-12-03T23:14'),
  reconocidoEn: dayjs('2023-12-03T23:16'),
  mitigadoEn: dayjs('2023-12-04T14:07'),
  usuariosAfectados: 5399,
};

export const sampleWithFullData: IIncidente = {
  id: 14089,
  titulo: 'embossing gosh pace',
  descripcion: '../fake-data/blob/hipster.txt',
  severidad: 'SEV1',
  estado: 'MITIGADO',
  detectadoEn: dayjs('2023-12-04T08:37'),
  reconocidoEn: dayjs('2023-12-04T14:11'),
  mitigadoEn: dayjs('2023-12-04T11:09'),
  resueltoEn: dayjs('2023-12-04T08:08'),
  usuariosAfectados: 9184,
  cumplioObjetivo: true,
};

export const sampleWithNewData: NewIncidente = {
  titulo: 'crest deplore',
  severidad: 'SEV3',
  estado: 'ABIERTO',
  detectadoEn: dayjs('2023-12-04T18:11'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
