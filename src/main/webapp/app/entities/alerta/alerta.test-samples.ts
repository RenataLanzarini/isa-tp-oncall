import dayjs from 'dayjs/esm';

import { IAlerta, NewAlerta } from './alerta.model';

export const sampleWithRequiredData: IAlerta = {
  id: 12423,
  fingerprint: 'who whether',
  origen: 'DATADOG',
  resumen: 'justly lounge more',
  recibidaEn: dayjs('2023-12-04T09:33'),
  procesada: true,
};

export const sampleWithPartialData: IAlerta = {
  id: 25527,
  fingerprint: 'meh drive',
  origen: 'CLOUDWATCH',
  resumen: 'inasmuch shrilly',
  payload: '../fake-data/blob/hipster.txt',
  recibidaEn: dayjs('2023-12-04T11:00'),
  procesada: false,
};

export const sampleWithFullData: IAlerta = {
  id: 15845,
  fingerprint: 'interviewer minus patiently',
  origen: 'PROMETHEUS',
  resumen: 'oh wicked',
  payload: '../fake-data/blob/hipster.txt',
  recibidaEn: dayjs('2023-12-04T09:54'),
  procesada: true,
};

export const sampleWithNewData: NewAlerta = {
  fingerprint: 'rosemary sonar',
  origen: 'PROMETHEUS',
  resumen: 'investigate deduce',
  recibidaEn: dayjs('2023-12-04T16:01'),
  procesada: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
