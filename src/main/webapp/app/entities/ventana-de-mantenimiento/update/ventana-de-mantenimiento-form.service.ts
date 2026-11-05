import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IVentanaDeMantenimiento, NewVentanaDeMantenimiento } from '../ventana-de-mantenimiento.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IVentanaDeMantenimiento for edit and NewVentanaDeMantenimientoFormGroupInput for create.
 */
type VentanaDeMantenimientoFormGroupInput = IVentanaDeMantenimiento | PartialWithRequiredKeyOf<NewVentanaDeMantenimiento>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IVentanaDeMantenimiento | NewVentanaDeMantenimiento> = Omit<T, 'inicio' | 'fin'> & {
  inicio?: string | null;
  fin?: string | null;
};

type VentanaDeMantenimientoFormRawValue = FormValueOf<IVentanaDeMantenimiento>;

type NewVentanaDeMantenimientoFormRawValue = FormValueOf<NewVentanaDeMantenimiento>;

type VentanaDeMantenimientoFormDefaults = Pick<NewVentanaDeMantenimiento, 'id' | 'inicio' | 'fin' | 'activa'>;

type VentanaDeMantenimientoFormGroupContent = {
  id: FormControl<VentanaDeMantenimientoFormRawValue['id'] | NewVentanaDeMantenimiento['id']>;
  nombre: FormControl<VentanaDeMantenimientoFormRawValue['nombre']>;
  descripcion: FormControl<VentanaDeMantenimientoFormRawValue['descripcion']>;
  tipo: FormControl<VentanaDeMantenimientoFormRawValue['tipo']>;
  zonaHoraria: FormControl<VentanaDeMantenimientoFormRawValue['zonaHoraria']>;
  inicio: FormControl<VentanaDeMantenimientoFormRawValue['inicio']>;
  fin: FormControl<VentanaDeMantenimientoFormRawValue['fin']>;
  diaSemana: FormControl<VentanaDeMantenimientoFormRawValue['diaSemana']>;
  minutoInicio: FormControl<VentanaDeMantenimientoFormRawValue['minutoInicio']>;
  minutoFin: FormControl<VentanaDeMantenimientoFormRawValue['minutoFin']>;
  vigenteDesde: FormControl<VentanaDeMantenimientoFormRawValue['vigenteDesde']>;
  vigenteHasta: FormControl<VentanaDeMantenimientoFormRawValue['vigenteHasta']>;
  activa: FormControl<VentanaDeMantenimientoFormRawValue['activa']>;
};

export type VentanaDeMantenimientoFormGroup = FormGroup<VentanaDeMantenimientoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class VentanaDeMantenimientoFormService {
  createVentanaDeMantenimientoFormGroup(ventanaDeMantenimiento?: VentanaDeMantenimientoFormGroupInput): VentanaDeMantenimientoFormGroup {
    const ventanaDeMantenimientoRawValue = this.convertVentanaDeMantenimientoToVentanaDeMantenimientoRawValue({
      ...this.getFormDefaults(),
      ...(ventanaDeMantenimiento ?? { id: null }),
    });

    return new FormGroup<VentanaDeMantenimientoFormGroupContent>({
      id: new FormControl(
        { value: ventanaDeMantenimientoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nombre: new FormControl(ventanaDeMantenimientoRawValue.nombre, {
        validators: [Validators.required, Validators.maxLength(80)],
      }),
      descripcion: new FormControl(ventanaDeMantenimientoRawValue.descripcion, {
        validators: [Validators.maxLength(500)],
      }),
      tipo: new FormControl(ventanaDeMantenimientoRawValue.tipo, {
        validators: [Validators.required],
      }),
      zonaHoraria: new FormControl(ventanaDeMantenimientoRawValue.zonaHoraria, {
        validators: [Validators.required, Validators.maxLength(50)],
      }),
      inicio: new FormControl(ventanaDeMantenimientoRawValue.inicio),
      fin: new FormControl(ventanaDeMantenimientoRawValue.fin),
      diaSemana: new FormControl(ventanaDeMantenimientoRawValue.diaSemana),
      minutoInicio: new FormControl(ventanaDeMantenimientoRawValue.minutoInicio, {
        validators: [Validators.min(0), Validators.max(1439)],
      }),
      minutoFin: new FormControl(ventanaDeMantenimientoRawValue.minutoFin, {
        validators: [Validators.min(0), Validators.max(1439)],
      }),
      vigenteDesde: new FormControl(ventanaDeMantenimientoRawValue.vigenteDesde),
      vigenteHasta: new FormControl(ventanaDeMantenimientoRawValue.vigenteHasta),
      activa: new FormControl(ventanaDeMantenimientoRawValue.activa, {
        validators: [Validators.required],
      }),
    });
  }

  getVentanaDeMantenimiento(form: VentanaDeMantenimientoFormGroup): IVentanaDeMantenimiento | NewVentanaDeMantenimiento {
    return this.convertVentanaDeMantenimientoRawValueToVentanaDeMantenimiento(form.getRawValue());
  }

  resetForm(form: VentanaDeMantenimientoFormGroup, ventanaDeMantenimiento: VentanaDeMantenimientoFormGroupInput): void {
    const ventanaDeMantenimientoRawValue = this.convertVentanaDeMantenimientoToVentanaDeMantenimientoRawValue({
      ...this.getFormDefaults(),
      ...ventanaDeMantenimiento,
    });
    form.reset({
      ...ventanaDeMantenimientoRawValue,
      id: { value: ventanaDeMantenimientoRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): VentanaDeMantenimientoFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      inicio: currentTime,
      fin: currentTime,
      activa: false,
    };
  }

  private convertVentanaDeMantenimientoRawValueToVentanaDeMantenimiento(
    rawVentanaDeMantenimiento: VentanaDeMantenimientoFormRawValue | NewVentanaDeMantenimientoFormRawValue,
  ): IVentanaDeMantenimiento | NewVentanaDeMantenimiento {
    return {
      ...rawVentanaDeMantenimiento,
      inicio: dayjs(rawVentanaDeMantenimiento.inicio, DATE_TIME_FORMAT),
      fin: dayjs(rawVentanaDeMantenimiento.fin, DATE_TIME_FORMAT),
    };
  }

  private convertVentanaDeMantenimientoToVentanaDeMantenimientoRawValue(
    ventanaDeMantenimiento: IVentanaDeMantenimiento | (Partial<NewVentanaDeMantenimiento> & VentanaDeMantenimientoFormDefaults),
  ): VentanaDeMantenimientoFormRawValue | PartialWithRequiredKeyOf<NewVentanaDeMantenimientoFormRawValue> {
    return {
      ...ventanaDeMantenimiento,
      inicio: ventanaDeMantenimiento.inicio ? ventanaDeMantenimiento.inicio.format(DATE_TIME_FORMAT) : undefined,
      fin: ventanaDeMantenimiento.fin ? ventanaDeMantenimiento.fin.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
