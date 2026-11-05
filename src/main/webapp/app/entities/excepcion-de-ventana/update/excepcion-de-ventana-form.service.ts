import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IExcepcionDeVentana, NewExcepcionDeVentana } from '../excepcion-de-ventana.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IExcepcionDeVentana for edit and NewExcepcionDeVentanaFormGroupInput for create.
 */
type ExcepcionDeVentanaFormGroupInput = IExcepcionDeVentana | PartialWithRequiredKeyOf<NewExcepcionDeVentana>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IExcepcionDeVentana | NewExcepcionDeVentana> = Omit<T, 'desde' | 'hasta'> & {
  desde?: string | null;
  hasta?: string | null;
};

type ExcepcionDeVentanaFormRawValue = FormValueOf<IExcepcionDeVentana>;

type NewExcepcionDeVentanaFormRawValue = FormValueOf<NewExcepcionDeVentana>;

type ExcepcionDeVentanaFormDefaults = Pick<NewExcepcionDeVentana, 'id' | 'desde' | 'hasta'>;

type ExcepcionDeVentanaFormGroupContent = {
  id: FormControl<ExcepcionDeVentanaFormRawValue['id'] | NewExcepcionDeVentana['id']>;
  tipo: FormControl<ExcepcionDeVentanaFormRawValue['tipo']>;
  desde: FormControl<ExcepcionDeVentanaFormRawValue['desde']>;
  hasta: FormControl<ExcepcionDeVentanaFormRawValue['hasta']>;
  motivo: FormControl<ExcepcionDeVentanaFormRawValue['motivo']>;
  ventana: FormControl<ExcepcionDeVentanaFormRawValue['ventana']>;
};

export type ExcepcionDeVentanaFormGroup = FormGroup<ExcepcionDeVentanaFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ExcepcionDeVentanaFormService {
  createExcepcionDeVentanaFormGroup(excepcionDeVentana?: ExcepcionDeVentanaFormGroupInput): ExcepcionDeVentanaFormGroup {
    const excepcionDeVentanaRawValue = this.convertExcepcionDeVentanaToExcepcionDeVentanaRawValue({
      ...this.getFormDefaults(),
      ...(excepcionDeVentana ?? { id: null }),
    });

    return new FormGroup<ExcepcionDeVentanaFormGroupContent>({
      id: new FormControl(
        { value: excepcionDeVentanaRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      tipo: new FormControl(excepcionDeVentanaRawValue.tipo, {
        validators: [Validators.required],
      }),
      desde: new FormControl(excepcionDeVentanaRawValue.desde, {
        validators: [Validators.required],
      }),
      hasta: new FormControl(excepcionDeVentanaRawValue.hasta, {
        validators: [Validators.required],
      }),
      motivo: new FormControl(excepcionDeVentanaRawValue.motivo, {
        validators: [Validators.maxLength(255)],
      }),
      ventana: new FormControl(excepcionDeVentanaRawValue.ventana, {
        validators: [Validators.required],
      }),
    });
  }

  getExcepcionDeVentana(form: ExcepcionDeVentanaFormGroup): IExcepcionDeVentana | NewExcepcionDeVentana {
    return this.convertExcepcionDeVentanaRawValueToExcepcionDeVentana(form.getRawValue());
  }

  resetForm(form: ExcepcionDeVentanaFormGroup, excepcionDeVentana: ExcepcionDeVentanaFormGroupInput): void {
    const excepcionDeVentanaRawValue = this.convertExcepcionDeVentanaToExcepcionDeVentanaRawValue({
      ...this.getFormDefaults(),
      ...excepcionDeVentana,
    });
    form.reset({
      ...excepcionDeVentanaRawValue,
      id: { value: excepcionDeVentanaRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ExcepcionDeVentanaFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      desde: currentTime,
      hasta: currentTime,
    };
  }

  private convertExcepcionDeVentanaRawValueToExcepcionDeVentana(
    rawExcepcionDeVentana: ExcepcionDeVentanaFormRawValue | NewExcepcionDeVentanaFormRawValue,
  ): IExcepcionDeVentana | NewExcepcionDeVentana {
    return {
      ...rawExcepcionDeVentana,
      desde: dayjs(rawExcepcionDeVentana.desde, DATE_TIME_FORMAT),
      hasta: dayjs(rawExcepcionDeVentana.hasta, DATE_TIME_FORMAT),
    };
  }

  private convertExcepcionDeVentanaToExcepcionDeVentanaRawValue(
    excepcionDeVentana: IExcepcionDeVentana | (Partial<NewExcepcionDeVentana> & ExcepcionDeVentanaFormDefaults),
  ): ExcepcionDeVentanaFormRawValue | PartialWithRequiredKeyOf<NewExcepcionDeVentanaFormRawValue> {
    return {
      ...excepcionDeVentana,
      desde: excepcionDeVentana.desde ? excepcionDeVentana.desde.format(DATE_TIME_FORMAT) : undefined,
      hasta: excepcionDeVentana.hasta ? excepcionDeVentana.hasta.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
