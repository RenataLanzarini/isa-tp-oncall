import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('ExcepcionDeVentana e2e test', () => {
  const excepcionDeVentanaPageUrl = '/excepcion-de-ventana';
  let username: string;
  let password: string;
  const excepcionDeVentanaSample = { tipo: 'INCLUSION', desde: '2023-12-04T21:26:53.347Z', hasta: '2023-12-04T03:50:03.555Z' };

  let excepcionDeVentana;
  let ventanaDeMantenimiento;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/ventana-de-mantenimientos',
      body: {
        nombre: 'um steel',
        descripcion: 'wonderfully whether which',
        tipo: 'UNICA',
        zonaHoraria: 'weary daintily',
        inicio: '2023-12-04T06:44:48.465Z',
        fin: '2023-12-04T08:15:12.971Z',
        diaSemana: 'LUNES',
        minutoInicio: 1053,
        minutoFin: 556,
        vigenteDesde: '2023-12-04',
        vigenteHasta: '2023-12-04',
        activa: true,
      },
    }).then(({ body }) => {
      ventanaDeMantenimiento = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/excepcion-de-ventanas+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/excepcion-de-ventanas').as('postEntityRequest');
    cy.intercept('DELETE', '/api/excepcion-de-ventanas/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/ventana-de-mantenimientos', {
      statusCode: 200,
      body: [ventanaDeMantenimiento],
    });
  });

  afterEach(() => {
    if (excepcionDeVentana) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/excepcion-de-ventanas/${excepcionDeVentana.id}`,
      }).then(() => {
        excepcionDeVentana = undefined;
      });
    }
  });

  afterEach(() => {
    if (ventanaDeMantenimiento) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/ventana-de-mantenimientos/${ventanaDeMantenimiento.id}`,
      }).then(() => {
        ventanaDeMantenimiento = undefined;
      });
    }
  });

  it('ExcepcionDeVentanas menu should load ExcepcionDeVentanas page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('excepcion-de-ventana');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ExcepcionDeVentana').should('exist');
    cy.location('pathname').should('eq', excepcionDeVentanaPageUrl);
  });

  describe('ExcepcionDeVentana page', () => {
    it('should have translated page title', () => {
      cy.visit(excepcionDeVentanaPageUrl);
      cy.getEntityHeading('ExcepcionDeVentana').should('not.contain', 'oncallApp.excepcionDeVentana.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(excepcionDeVentanaPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ExcepcionDeVentana page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${excepcionDeVentanaPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('ExcepcionDeVentana');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', excepcionDeVentanaPageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/excepcion-de-ventanas',
          body: {
            ...excepcionDeVentanaSample,
            ventana: ventanaDeMantenimiento,
          },
        }).then(({ body }) => {
          excepcionDeVentana = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/excepcion-de-ventanas+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [excepcionDeVentana],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(excepcionDeVentanaPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ExcepcionDeVentana page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('excepcionDeVentana');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', excepcionDeVentanaPageUrl);
      });

      it('edit button click should load edit ExcepcionDeVentana page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ExcepcionDeVentana');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', excepcionDeVentanaPageUrl);
      });

      it('edit button click should load edit ExcepcionDeVentana page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ExcepcionDeVentana');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', excepcionDeVentanaPageUrl);
      });

      it('last delete button click should delete instance of ExcepcionDeVentana', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('excepcionDeVentana').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', excepcionDeVentanaPageUrl);

        excepcionDeVentana = undefined;
      });
    });
  });

  describe('new ExcepcionDeVentana page', () => {
    beforeEach(() => {
      cy.visit(excepcionDeVentanaPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ExcepcionDeVentana');
    });

    it('should create an instance of ExcepcionDeVentana', () => {
      cy.get(`[data-cy="tipo"]`).select('CANCELACION');

      cy.get(`[data-cy="desde"]`).type('2023-12-04T21:20');
      cy.get(`[data-cy="desde"]`).blur();
      cy.get(`[data-cy="desde"]`).should('have.value', '2023-12-04T21:20');

      cy.get(`[data-cy="hasta"]`).type('2023-12-04T12:02');
      cy.get(`[data-cy="hasta"]`).blur();
      cy.get(`[data-cy="hasta"]`).should('have.value', '2023-12-04T12:02');

      cy.get(`[data-cy="motivo"]`).type('equate woot gah');
      cy.get(`[data-cy="motivo"]`).should('have.value', 'equate woot gah');

      cy.get(`[data-cy="ventana"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        excepcionDeVentana = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', excepcionDeVentanaPageUrl);
    });
  });
});
