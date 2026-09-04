import dayjs from 'dayjs/esm';

import { IPostmortem, NewPostmortem } from './postmortem.model';

export const sampleWithRequiredData: IPostmortem = {
  id: 3653,
  titulo: 'promptly oxidise',
  resumen: 'up',
  causaRaiz: '../fake-data/blob/hipster.txt',
  publicado: true,
};

export const sampleWithPartialData: IPostmortem = {
  id: 12180,
  titulo: 'eek forenenst',
  resumen: 'mentor cutover',
  causaRaiz: '../fake-data/blob/hipster.txt',
  publicado: false,
  publicadoEn: dayjs('2023-12-04T14:28'),
};

export const sampleWithFullData: IPostmortem = {
  id: 18104,
  titulo: 'outrank polished mispronounce',
  resumen: 'bid',
  causaRaiz: '../fake-data/blob/hipster.txt',
  lineaDeTiempo: '../fake-data/blob/hipster.txt',
  leccionesAprendidas: '../fake-data/blob/hipster.txt',
  publicado: false,
  publicadoEn: dayjs('2023-12-04T11:18'),
};

export const sampleWithNewData: NewPostmortem = {
  titulo: 'variable',
  resumen: 'ew throughout lightly',
  causaRaiz: '../fake-data/blob/hipster.txt',
  publicado: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
