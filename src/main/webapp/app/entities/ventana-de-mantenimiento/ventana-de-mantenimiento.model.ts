import dayjs from 'dayjs/esm';

import { DiaSemana } from 'app/entities/enumerations/dia-semana.model';
import { TipoVentana } from 'app/entities/enumerations/tipo-ventana.model';

export interface IVentanaDeMantenimiento {
  id: number;
  nombre?: string | null;
  descripcion?: string | null;
  tipo?: keyof typeof TipoVentana | null;
  zonaHoraria?: string | null;
  inicio?: dayjs.Dayjs | null;
  fin?: dayjs.Dayjs | null;
  diaSemana?: keyof typeof DiaSemana | null;
  minutoInicio?: number | null;
  minutoFin?: number | null;
  vigenteDesde?: dayjs.Dayjs | null;
  vigenteHasta?: dayjs.Dayjs | null;
  activa?: boolean | null;
}

export type NewVentanaDeMantenimiento = Omit<IVentanaDeMantenimiento, 'id'> & { id: null };
