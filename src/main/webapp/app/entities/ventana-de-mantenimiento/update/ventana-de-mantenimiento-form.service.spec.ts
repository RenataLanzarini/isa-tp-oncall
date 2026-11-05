import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import dayjs from 'dayjs/esm';

import { TipoVentana } from 'app/entities/enumerations/tipo-ventana.model';
import { sampleWithNewData, sampleWithRequiredData } from '../ventana-de-mantenimiento.test-samples';

import { VentanaDeMantenimientoFormService } from './ventana-de-mantenimiento-form.service';

describe('VentanaDeMantenimiento Form Service', () => {
  let service: VentanaDeMantenimientoFormService;

  beforeEach(() => {
    service = TestBed.inject(VentanaDeMantenimientoFormService);
  });

  describe('Service methods', () => {
    describe('createVentanaDeMantenimientoFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createVentanaDeMantenimientoFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nombre: expect.any(Object),
            descripcion: expect.any(Object),
            tipo: expect.any(Object),
            zonaHoraria: expect.any(Object),
            inicio: expect.any(Object),
            fin: expect.any(Object),
            diaSemana: expect.any(Object),
            minutoInicio: expect.any(Object),
            minutoFin: expect.any(Object),
            vigenteDesde: expect.any(Object),
            vigenteHasta: expect.any(Object),
            activa: expect.any(Object),
          }),
        );
      });

      it('passing IVentanaDeMantenimiento should create a new form with FormGroup', () => {
        const formGroup = service.createVentanaDeMantenimientoFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nombre: expect.any(Object),
            descripcion: expect.any(Object),
            tipo: expect.any(Object),
            zonaHoraria: expect.any(Object),
            inicio: expect.any(Object),
            fin: expect.any(Object),
            diaSemana: expect.any(Object),
            minutoInicio: expect.any(Object),
            minutoFin: expect.any(Object),
            vigenteDesde: expect.any(Object),
            vigenteHasta: expect.any(Object),
            activa: expect.any(Object),
          }),
        );
      });
    });

    describe('getVentanaDeMantenimiento', () => {
      it('should return NewVentanaDeMantenimiento for default VentanaDeMantenimiento initial value', () => {
        const formGroup = service.createVentanaDeMantenimientoFormGroup(sampleWithNewData);

        const ventanaDeMantenimiento = service.getVentanaDeMantenimiento(formGroup);

        expect(ventanaDeMantenimiento).toMatchObject(sampleWithNewData);
      });

      it('should return NewVentanaDeMantenimiento for empty VentanaDeMantenimiento initial value', () => {
        const formGroup = service.createVentanaDeMantenimientoFormGroup();

        const ventanaDeMantenimiento = service.getVentanaDeMantenimiento(formGroup);

        expect(ventanaDeMantenimiento).toMatchObject({
          id: null,
          tipo: TipoVentana.UNICA,
          zonaHoraria: 'America/Argentina/Mendoza',
          activa: false,
        });
        const { inicio, fin } = ventanaDeMantenimiento;
        expect(inicio).toBeDefined();
        expect(fin).toBeDefined();
        if (!inicio || !fin) {
          throw new Error('The default one-time interval is missing');
        }
        expect(fin.isAfter(inicio)).toBe(true);
      });

      it('should return IVentanaDeMantenimiento', () => {
        const formGroup = service.createVentanaDeMantenimientoFormGroup(sampleWithRequiredData);

        const ventanaDeMantenimiento = service.getVentanaDeMantenimiento(formGroup);

        expect(ventanaDeMantenimiento).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IVentanaDeMantenimiento should not enable id FormControl', () => {
        const formGroup = service.createVentanaDeMantenimientoFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewVentanaDeMantenimiento should disable id FormControl', () => {
        const formGroup = service.createVentanaDeMantenimientoFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });

    describe('type-specific behavior', () => {
      it('should build a valid one-time payload and clear weekly fields', () => {
        const formGroup = service.createVentanaDeMantenimientoFormGroup();
        formGroup.patchValue({
          nombre: 'Ventana única',
          diaSemana: 'LUNES',
          minutoInicio: 60,
          minutoFin: 120,
          vigenteDesde: dayjs('2026-11-06'),
        });

        service.updateFormForType(formGroup, TipoVentana.UNICA, true);
        const payload = service.getVentanaDeMantenimiento(formGroup);

        expect(formGroup.valid).toBe(true);
        expect(payload).toMatchObject({
          tipo: TipoVentana.UNICA,
          zonaHoraria: 'America/Argentina/Mendoza',
          diaSemana: null,
          minutoInicio: null,
          minutoFin: null,
          vigenteDesde: null,
          vigenteHasta: null,
        });
        const { inicio, fin } = payload;
        expect(inicio).toBeDefined();
        expect(fin).toBeDefined();
        if (!inicio || !fin) {
          throw new Error('The one-time payload interval is missing');
        }
        expect(fin.isAfter(inicio)).toBe(true);
      });

      it('should require weekly fields and clear absolute instants', () => {
        const formGroup = service.createVentanaDeMantenimientoFormGroup();
        formGroup.controls.tipo.setValue(TipoVentana.SEMANAL);
        service.updateFormForType(formGroup, TipoVentana.SEMANAL, true);

        expect(formGroup.controls.inicio.value).toBeNull();
        expect(formGroup.controls.fin.value).toBeNull();
        expect(formGroup.controls.diaSemana.hasError('required')).toBe(true);
        expect(formGroup.controls.minutoInicio.hasError('required')).toBe(true);
        expect(formGroup.controls.minutoFin.hasError('required')).toBe(true);
        expect(formGroup.controls.vigenteDesde.hasError('required')).toBe(true);

        formGroup.patchValue({
          nombre: 'Ventana semanal',
          diaSemana: 'MARTES',
          minutoInicio: 120,
          minutoFin: 240,
          vigenteDesde: dayjs('2026-11-06'),
        });
        const payload = service.getVentanaDeMantenimiento(formGroup);

        expect(formGroup.valid).toBe(true);
        expect(payload.inicio).toBeNull();
        expect(payload.fin).toBeNull();
      });

      it('should reject invalid time zones and incoherent intervals', () => {
        const formGroup = service.createVentanaDeMantenimientoFormGroup();
        formGroup.patchValue({ zonaHoraria: 'desk', fin: formGroup.controls.inicio.value });

        expect(formGroup.controls.zonaHoraria.hasError('invalidTimeZone')).toBe(true);
        expect(formGroup.hasError('invalidInterval')).toBe(true);
      });

      it('should reject an inverted weekly validity range', () => {
        const formGroup = service.createVentanaDeMantenimientoFormGroup();
        formGroup.controls.tipo.setValue(TipoVentana.SEMANAL);
        service.updateFormForType(formGroup, TipoVentana.SEMANAL, true);
        formGroup.patchValue({
          nombre: 'Ventana semanal',
          diaSemana: 'MARTES',
          minutoInicio: 120,
          minutoFin: 240,
          vigenteDesde: dayjs('2026-11-07'),
          vigenteHasta: dayjs('2026-11-06'),
        });

        expect(formGroup.hasError('invalidValidity')).toBe(true);
      });
    });
  });
});
