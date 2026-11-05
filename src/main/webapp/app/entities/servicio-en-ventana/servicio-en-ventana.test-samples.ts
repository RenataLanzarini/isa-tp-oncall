import { IServicioEnVentana, NewServicioEnVentana } from './servicio-en-ventana.model';

export const sampleWithRequiredData: IServicioEnVentana = {
  id: 21331,
  activo: false,
};

export const sampleWithPartialData: IServicioEnVentana = {
  id: 27649,
  activo: false,
  nota: 'beyond caring',
};

export const sampleWithFullData: IServicioEnVentana = {
  id: 6395,
  activo: false,
  nota: 'ashamed',
};

export const sampleWithNewData: NewServicioEnVentana = {
  activo: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
