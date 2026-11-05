import { Injectable } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { TipoVentana } from 'app/entities/enumerations/tipo-ventana.model';
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

type VentanaDeMantenimientoFormDefaults = Pick<NewVentanaDeMantenimiento, 'id' | 'tipo' | 'zonaHoraria' | 'inicio' | 'fin' | 'activa'>;

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

    const form = new FormGroup<VentanaDeMantenimientoFormGroupContent>(
      {
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
          validators: [Validators.required, Validators.maxLength(50), this.ianaTimeZoneValidator()],
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
      },
      { validators: [this.coherentIntervalValidator()] },
    );

    this.updateFormForType(form, ventanaDeMantenimientoRawValue.tipo, false);
    return form;
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
    this.updateFormForType(form, ventanaDeMantenimientoRawValue.tipo, false);
  }

  updateFormForType(
    form: VentanaDeMantenimientoFormGroup,
    tipo: keyof typeof TipoVentana | null | undefined,
    clearIncompatible: boolean,
  ): void {
    const { inicio, fin, diaSemana, minutoInicio, minutoFin, vigenteDesde, vigenteHasta } = form.controls;

    inicio.clearValidators();
    fin.clearValidators();
    diaSemana.clearValidators();
    minutoInicio.setValidators([Validators.min(0), Validators.max(1439)]);
    minutoFin.setValidators([Validators.min(0), Validators.max(1439)]);
    vigenteDesde.clearValidators();

    if (tipo === TipoVentana.UNICA) {
      inicio.setValidators([Validators.required]);
      fin.setValidators([Validators.required]);
      if (clearIncompatible) {
        form.patchValue(
          { diaSemana: null, minutoInicio: null, minutoFin: null, vigenteDesde: null, vigenteHasta: null },
          { emitEvent: false },
        );
      }
    } else if (tipo === TipoVentana.SEMANAL) {
      diaSemana.setValidators([Validators.required]);
      minutoInicio.setValidators([Validators.required, Validators.min(0), Validators.max(1439)]);
      minutoFin.setValidators([Validators.required, Validators.min(0), Validators.max(1439)]);
      vigenteDesde.setValidators([Validators.required]);
      if (clearIncompatible) {
        form.patchValue({ inicio: null, fin: null }, { emitEvent: false });
      }
    }

    [inicio, fin, diaSemana, minutoInicio, minutoFin, vigenteDesde, vigenteHasta].forEach(control =>
      control.updateValueAndValidity({ emitEvent: false }),
    );
    form.updateValueAndValidity({ emitEvent: false });
  }

  private getFormDefaults(): VentanaDeMantenimientoFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      tipo: TipoVentana.UNICA,
      zonaHoraria: 'America/Argentina/Mendoza',
      inicio: currentTime,
      fin: currentTime.add(1, 'hour'),
      activa: false,
    };
  }

  private convertVentanaDeMantenimientoRawValueToVentanaDeMantenimiento(
    rawVentanaDeMantenimiento: VentanaDeMantenimientoFormRawValue | NewVentanaDeMantenimientoFormRawValue,
  ): IVentanaDeMantenimiento | NewVentanaDeMantenimiento {
    return {
      ...rawVentanaDeMantenimiento,
      inicio: rawVentanaDeMantenimiento.inicio ? dayjs(rawVentanaDeMantenimiento.inicio, DATE_TIME_FORMAT) : null,
      fin: rawVentanaDeMantenimiento.fin ? dayjs(rawVentanaDeMantenimiento.fin, DATE_TIME_FORMAT) : null,
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

  private coherentIntervalValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const tipo = control.get('tipo')?.value as keyof typeof TipoVentana | null;
      if (tipo === TipoVentana.UNICA) {
        const inicio = control.get('inicio')?.value as string | null;
        const fin = control.get('fin')?.value as string | null;
        if (inicio && fin && !dayjs(fin, DATE_TIME_FORMAT).isAfter(dayjs(inicio, DATE_TIME_FORMAT))) {
          return { invalidInterval: true };
        }
      }

      if (tipo === TipoVentana.SEMANAL) {
        const vigenteDesde = control.get('vigenteDesde')?.value as dayjs.Dayjs | null;
        const vigenteHasta = control.get('vigenteHasta')?.value as dayjs.Dayjs | null;
        if (vigenteDesde && vigenteHasta?.isBefore(vigenteDesde, 'day')) {
          return { invalidValidity: true };
        }
      }
      return null;
    };
  }

  private ianaTimeZoneValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const zonaHoraria = control.value as string | null;
      if (!zonaHoraria) {
        return null;
      }
      try {
        new Intl.DateTimeFormat('en-US', { timeZone: zonaHoraria }).format();
        return null;
      } catch {
        return { invalidTimeZone: true };
      }
    };
  }
}
