import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import dayjs from 'dayjs/esm';
import { Subject, from, of } from 'rxjs';

import { TipoVentana } from 'app/entities/enumerations/tipo-ventana.model';
import { VentanaDeMantenimientoService } from '../service/ventana-de-mantenimiento.service';
import { IVentanaDeMantenimiento } from '../ventana-de-mantenimiento.model';

import { VentanaDeMantenimientoFormService } from './ventana-de-mantenimiento-form.service';
import { VentanaDeMantenimientoUpdate } from './ventana-de-mantenimiento-update';

describe('VentanaDeMantenimiento Management Update Component', () => {
  let comp: VentanaDeMantenimientoUpdate;
  let fixture: ComponentFixture<VentanaDeMantenimientoUpdate>;
  let activatedRoute: ActivatedRoute;
  let ventanaDeMantenimientoFormService: VentanaDeMantenimientoFormService;
  let ventanaDeMantenimientoService: VentanaDeMantenimientoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideTranslateService(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(VentanaDeMantenimientoUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    ventanaDeMantenimientoFormService = TestBed.inject(VentanaDeMantenimientoFormService);
    ventanaDeMantenimientoService = TestBed.inject(VentanaDeMantenimientoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const ventanaDeMantenimiento: IVentanaDeMantenimiento = { id: 27219 };

      activatedRoute.data = of({ ventanaDeMantenimiento });
      comp.ngOnInit();

      expect(comp.ventanaDeMantenimiento).toEqual(ventanaDeMantenimiento);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IVentanaDeMantenimiento>();
      const ventanaDeMantenimiento = { id: 31419 };
      vitest.spyOn(ventanaDeMantenimientoFormService, 'getVentanaDeMantenimiento').mockReturnValue(ventanaDeMantenimiento);
      vitest.spyOn(ventanaDeMantenimientoService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ventanaDeMantenimiento });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(ventanaDeMantenimiento);
      saveSubject.complete();

      // THEN
      expect(ventanaDeMantenimientoFormService.getVentanaDeMantenimiento).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(ventanaDeMantenimientoService.update).toHaveBeenCalledWith(expect.objectContaining(ventanaDeMantenimiento));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IVentanaDeMantenimiento>();
      const ventanaDeMantenimiento = { id: 31419 };
      vitest.spyOn(ventanaDeMantenimientoFormService, 'getVentanaDeMantenimiento').mockReturnValue({ id: null });
      vitest.spyOn(ventanaDeMantenimientoService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ventanaDeMantenimiento: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(ventanaDeMantenimiento);
      saveSubject.complete();

      // THEN
      expect(ventanaDeMantenimientoFormService.getVentanaDeMantenimiento).toHaveBeenCalled();
      expect(ventanaDeMantenimientoService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IVentanaDeMantenimiento>();
      const ventanaDeMantenimiento = { id: 31419 };
      vitest.spyOn(ventanaDeMantenimientoService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ventanaDeMantenimiento });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(ventanaDeMantenimientoService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });

    it('should send a coherent one-time payload', () => {
      const createSpy = vitest.spyOn(ventanaDeMantenimientoService, 'create').mockReturnValue(of({ id: 1 }));
      activatedRoute.data = of({ ventanaDeMantenimiento: null });
      comp.ngOnInit();
      comp.editForm.patchValue({ nombre: 'Ventana única', activa: true });

      comp.save();

      expect(createSpy).toHaveBeenCalledWith(
        expect.objectContaining({
          tipo: TipoVentana.UNICA,
          zonaHoraria: 'America/Argentina/Mendoza',
          diaSemana: null,
          minutoInicio: null,
          minutoFin: null,
          vigenteDesde: null,
          vigenteHasta: null,
        }),
      );
    });

    it('should send a coherent weekly payload', () => {
      const createSpy = vitest.spyOn(ventanaDeMantenimientoService, 'create').mockReturnValue(of({ id: 1 }));
      activatedRoute.data = of({ ventanaDeMantenimiento: null });
      comp.ngOnInit();
      comp.editForm.controls.tipo.setValue(TipoVentana.SEMANAL);
      comp.editForm.patchValue({
        nombre: 'Ventana semanal',
        diaSemana: 'MARTES',
        minutoInicio: 120,
        minutoFin: 240,
        vigenteDesde: dayjs('2026-11-06'),
        activa: true,
      });

      comp.save();

      expect(createSpy).toHaveBeenCalledWith(
        expect.objectContaining({
          tipo: TipoVentana.SEMANAL,
          inicio: null,
          fin: null,
          diaSemana: 'MARTES',
          minutoInicio: 120,
          minutoFin: 240,
        }),
      );
    });
  });

  describe('window type changes', () => {
    it('should clear weekly fields when switching to UNICA', () => {
      activatedRoute.data = of({ ventanaDeMantenimiento: null });
      comp.ngOnInit();
      comp.editForm.patchValue({
        diaSemana: 'MARTES',
        minutoInicio: 120,
        minutoFin: 240,
        vigenteDesde: dayjs('2026-11-06'),
      });

      comp.editForm.controls.tipo.setValue(TipoVentana.UNICA);

      expect(comp.editForm.controls.diaSemana.value).toBeNull();
      expect(comp.editForm.controls.minutoInicio.value).toBeNull();
      expect(comp.editForm.controls.minutoFin.value).toBeNull();
      expect(comp.editForm.controls.vigenteDesde.value).toBeNull();
    });

    it('should clear absolute instants when switching to SEMANAL', () => {
      activatedRoute.data = of({ ventanaDeMantenimiento: null });
      comp.ngOnInit();

      comp.editForm.controls.tipo.setValue(TipoVentana.SEMANAL);

      expect(comp.editForm.controls.inicio.value).toBeNull();
      expect(comp.editForm.controls.fin.value).toBeNull();
    });

    it('should preserve compatible values when editing a valid window', () => {
      const ventanaDeMantenimiento: IVentanaDeMantenimiento = {
        id: 1,
        nombre: 'Ventana existente',
        tipo: TipoVentana.UNICA,
        zonaHoraria: 'America/Argentina/Mendoza',
        inicio: dayjs('2026-11-06T01:00:00Z'),
        fin: dayjs('2026-11-06T02:00:00Z'),
        activa: true,
      };
      activatedRoute.data = of({ ventanaDeMantenimiento });

      comp.ngOnInit();

      expect(comp.editForm.controls.nombre.value).toBe('Ventana existente');
      expect(comp.editForm.controls.zonaHoraria.value).toBe('America/Argentina/Mendoza');
      expect(comp.editForm.controls.inicio.value).not.toBeNull();
      expect(comp.editForm.controls.fin.value).not.toBeNull();
    });
  });
});
