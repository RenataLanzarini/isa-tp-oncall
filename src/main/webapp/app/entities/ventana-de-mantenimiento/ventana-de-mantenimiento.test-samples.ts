import dayjs from 'dayjs/esm';

import { IVentanaDeMantenimiento, NewVentanaDeMantenimiento } from './ventana-de-mantenimiento.model';

export const sampleWithRequiredData: IVentanaDeMantenimiento = {
  id: 27630,
  nombre: 'yet debit',
  tipo: 'SEMANAL',
  zonaHoraria: 'between',
  activa: false,
};

export const sampleWithPartialData: IVentanaDeMantenimiento = {
  id: 3181,
  nombre: 'to',
  tipo: 'SEMANAL',
  zonaHoraria: 'brr ew',
  fin: dayjs('2023-12-04T10:06'),
  minutoFin: 143,
  vigenteDesde: dayjs('2023-12-04'),
  activa: false,
};

export const sampleWithFullData: IVentanaDeMantenimiento = {
  id: 21275,
  nombre: 'ha while',
  descripcion: 'where aha engage',
  tipo: 'SEMANAL',
  zonaHoraria: 'triumphantly excluding',
  inicio: dayjs('2023-12-04T16:37'),
  fin: dayjs('2023-12-04T01:20'),
  diaSemana: 'MARTES',
  minutoInicio: 86,
  minutoFin: 1118,
  vigenteDesde: dayjs('2023-12-04'),
  vigenteHasta: dayjs('2023-12-04'),
  activa: false,
};

export const sampleWithNewData: NewVentanaDeMantenimiento = {
  nombre: 'as yawningly excluding',
  tipo: 'SEMANAL',
  zonaHoraria: 'er reluctantly',
  activa: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
