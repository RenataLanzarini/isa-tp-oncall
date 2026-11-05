import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IExcepcionDeVentana, NewExcepcionDeVentana } from '../excepcion-de-ventana.model';

export type PartialUpdateExcepcionDeVentana = Partial<IExcepcionDeVentana> & Pick<IExcepcionDeVentana, 'id'>;

type RestOf<T extends IExcepcionDeVentana | NewExcepcionDeVentana> = Omit<T, 'desde' | 'hasta'> & {
  desde?: string | null;
  hasta?: string | null;
};

export type RestExcepcionDeVentana = RestOf<IExcepcionDeVentana>;

export type NewRestExcepcionDeVentana = RestOf<NewExcepcionDeVentana>;

export type PartialUpdateRestExcepcionDeVentana = RestOf<PartialUpdateExcepcionDeVentana>;

@Injectable()
export class ExcepcionDeVentanasService {
  readonly excepcionDeVentanasParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly excepcionDeVentanasResource = httpResource<RestExcepcionDeVentana[]>(() => {
    const params = this.excepcionDeVentanasParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of excepcionDeVentana that have been fetched. It is updated when the excepcionDeVentanasResource emits a new value.
   * In case of error while fetching the excepcionDeVentanas, the signal is set to an empty array.
   */
  readonly excepcionDeVentanas = computed(() =>
    (this.excepcionDeVentanasResource.hasValue() ? this.excepcionDeVentanasResource.value() : []).map(item =>
      this.convertValueFromServer(item),
    ),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/excepcion-de-ventanas');

  protected convertValueFromServer(restExcepcionDeVentana: RestExcepcionDeVentana): IExcepcionDeVentana {
    return {
      ...restExcepcionDeVentana,
      desde: restExcepcionDeVentana.desde ? dayjs(restExcepcionDeVentana.desde) : undefined,
      hasta: restExcepcionDeVentana.hasta ? dayjs(restExcepcionDeVentana.hasta) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class ExcepcionDeVentanaService extends ExcepcionDeVentanasService {
  protected readonly http = inject(HttpClient);

  create(excepcionDeVentana: NewExcepcionDeVentana): Observable<IExcepcionDeVentana> {
    const copy = this.convertValueFromClient(excepcionDeVentana);
    return this.http.post<RestExcepcionDeVentana>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(excepcionDeVentana: IExcepcionDeVentana): Observable<IExcepcionDeVentana> {
    const copy = this.convertValueFromClient(excepcionDeVentana);
    return this.http
      .put<RestExcepcionDeVentana>(
        `${this.resourceUrl}/${encodeURIComponent(this.getExcepcionDeVentanaIdentifier(excepcionDeVentana))}`,
        copy,
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(excepcionDeVentana: PartialUpdateExcepcionDeVentana): Observable<IExcepcionDeVentana> {
    const copy = this.convertValueFromClient(excepcionDeVentana);
    return this.http
      .patch<RestExcepcionDeVentana>(
        `${this.resourceUrl}/${encodeURIComponent(this.getExcepcionDeVentanaIdentifier(excepcionDeVentana))}`,
        copy,
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IExcepcionDeVentana> {
    return this.http
      .get<RestExcepcionDeVentana>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IExcepcionDeVentana[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestExcepcionDeVentana[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getExcepcionDeVentanaIdentifier(excepcionDeVentana: Pick<IExcepcionDeVentana, 'id'>): number {
    return excepcionDeVentana.id;
  }

  compareExcepcionDeVentana(o1: Pick<IExcepcionDeVentana, 'id'> | null, o2: Pick<IExcepcionDeVentana, 'id'> | null): boolean {
    return o1 && o2 ? this.getExcepcionDeVentanaIdentifier(o1) === this.getExcepcionDeVentanaIdentifier(o2) : o1 === o2;
  }

  addExcepcionDeVentanaToCollectionIfMissing<Type extends Pick<IExcepcionDeVentana, 'id'>>(
    excepcionDeVentanaCollection: Type[],
    ...excepcionDeVentanasToCheck: (Type | null | undefined)[]
  ): Type[] {
    const excepcionDeVentanas: Type[] = excepcionDeVentanasToCheck.filter(isPresent);
    if (excepcionDeVentanas.length > 0) {
      const excepcionDeVentanaCollectionIdentifiers = excepcionDeVentanaCollection.map(excepcionDeVentanaItem =>
        this.getExcepcionDeVentanaIdentifier(excepcionDeVentanaItem),
      );
      const excepcionDeVentanasToAdd = excepcionDeVentanas.filter(excepcionDeVentanaItem => {
        const excepcionDeVentanaIdentifier = this.getExcepcionDeVentanaIdentifier(excepcionDeVentanaItem);
        if (excepcionDeVentanaCollectionIdentifiers.includes(excepcionDeVentanaIdentifier)) {
          return false;
        }
        excepcionDeVentanaCollectionIdentifiers.push(excepcionDeVentanaIdentifier);
        return true;
      });
      return [...excepcionDeVentanasToAdd, ...excepcionDeVentanaCollection];
    }
    return excepcionDeVentanaCollection;
  }

  protected convertValueFromClient<T extends IExcepcionDeVentana | NewExcepcionDeVentana | PartialUpdateExcepcionDeVentana>(
    excepcionDeVentana: T,
  ): RestOf<T> {
    return {
      ...excepcionDeVentana,
      desde: excepcionDeVentana.desde?.toJSON() ?? null,
      hasta: excepcionDeVentana.hasta?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestExcepcionDeVentana): IExcepcionDeVentana {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestExcepcionDeVentana[]): IExcepcionDeVentana[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
