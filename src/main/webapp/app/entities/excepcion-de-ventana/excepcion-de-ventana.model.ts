import dayjs from 'dayjs/esm';

import { TipoExcepcionVentana } from 'app/entities/enumerations/tipo-excepcion-ventana.model';
import { IVentanaDeMantenimiento } from 'app/entities/ventana-de-mantenimiento/ventana-de-mantenimiento.model';

export interface IExcepcionDeVentana {
  id: number;
  tipo?: keyof typeof TipoExcepcionVentana | null;
  desde?: dayjs.Dayjs | null;
  hasta?: dayjs.Dayjs | null;
  motivo?: string | null;
  ventana?: Pick<IVentanaDeMantenimiento, 'id' | 'nombre'> | null;
}

export type NewExcepcionDeVentana = Omit<IExcepcionDeVentana, 'id'> & { id: null };
