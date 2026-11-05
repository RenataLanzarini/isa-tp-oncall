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

describe('VentanaDeMantenimiento e2e test', () => {
  const ventanaDeMantenimientoPageUrl = '/ventana-de-mantenimiento';
  let username: string;
  let password: string;
  const ventanaDeMantenimientoSample = { nombre: 'swill', tipo: 'SEMANAL', zonaHoraria: 'why amongst intently', activa: true };

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
    cy.intercept('GET', '/api/ventana-de-mantenimientos+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/ventana-de-mantenimientos').as('postEntityRequest');
    cy.intercept('DELETE', '/api/ventana-de-mantenimientos/*').as('deleteEntityRequest');
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

  it('VentanaDeMantenimientos menu should load VentanaDeMantenimientos page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('ventana-de-mantenimiento');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('VentanaDeMantenimiento').should('exist');
    cy.location('pathname').should('eq', ventanaDeMantenimientoPageUrl);
  });

  describe('VentanaDeMantenimiento page', () => {
    it('should have translated page title', () => {
      cy.visit(ventanaDeMantenimientoPageUrl);
      cy.getEntityHeading('VentanaDeMantenimiento').should('not.contain', 'oncallApp.ventanaDeMantenimiento.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(ventanaDeMantenimientoPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create VentanaDeMantenimiento page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${ventanaDeMantenimientoPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('VentanaDeMantenimiento');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', ventanaDeMantenimientoPageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/ventana-de-mantenimientos',
          body: ventanaDeMantenimientoSample,
        }).then(({ body }) => {
          ventanaDeMantenimiento = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/ventana-de-mantenimientos+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [ventanaDeMantenimiento],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(ventanaDeMantenimientoPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details VentanaDeMantenimiento page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('ventanaDeMantenimiento');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', ventanaDeMantenimientoPageUrl);
      });

      it('edit button click should load edit VentanaDeMantenimiento page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('VentanaDeMantenimiento');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', ventanaDeMantenimientoPageUrl);
      });

      it('edit button click should load edit VentanaDeMantenimiento page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('VentanaDeMantenimiento');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', ventanaDeMantenimientoPageUrl);
      });

      it('last delete button click should delete instance of VentanaDeMantenimiento', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('ventanaDeMantenimiento').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', ventanaDeMantenimientoPageUrl);

        ventanaDeMantenimiento = undefined;
      });
    });
  });

  describe('new VentanaDeMantenimiento page', () => {
    beforeEach(() => {
      cy.visit(ventanaDeMantenimientoPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('VentanaDeMantenimiento');
    });

    it('should create an instance of VentanaDeMantenimiento', () => {
      cy.get(`[data-cy="nombre"]`).type('loyally wide although');
      cy.get(`[data-cy="nombre"]`).should('have.value', 'loyally wide although');

      cy.get(`[data-cy="descripcion"]`).type('distinction large than');
      cy.get(`[data-cy="descripcion"]`).should('have.value', 'distinction large than');

      cy.get(`[data-cy="tipo"]`).select('UNICA');

      cy.get(`[data-cy="zonaHoraria"]`).type('earnest gah');
      cy.get(`[data-cy="zonaHoraria"]`).should('have.value', 'earnest gah');

      cy.get(`[data-cy="inicio"]`).type('2023-12-04T01:40');
      cy.get(`[data-cy="inicio"]`).blur();
      cy.get(`[data-cy="inicio"]`).should('have.value', '2023-12-04T01:40');

      cy.get(`[data-cy="fin"]`).type('2023-12-04T11:32');
      cy.get(`[data-cy="fin"]`).blur();
      cy.get(`[data-cy="fin"]`).should('have.value', '2023-12-04T11:32');

      cy.get(`[data-cy="diaSemana"]`).select('LUNES');

      cy.get(`[data-cy="minutoInicio"]`).type('524');
      cy.get(`[data-cy="minutoInicio"]`).should('have.value', '524');

      cy.get(`[data-cy="minutoFin"]`).type('1009');
      cy.get(`[data-cy="minutoFin"]`).should('have.value', '1009');

      cy.get(`[data-cy="vigenteDesde"]`).type('2023-12-04');
      cy.get(`[data-cy="vigenteDesde"]`).blur();
      cy.get(`[data-cy="vigenteDesde"]`).should('have.value', '2023-12-04');

      cy.get(`[data-cy="vigenteHasta"]`).type('2023-12-04');
      cy.get(`[data-cy="vigenteHasta"]`).blur();
      cy.get(`[data-cy="vigenteHasta"]`).should('have.value', '2023-12-04');

      cy.get(`[data-cy="activa"]`).should('not.be.checked');
      cy.get(`[data-cy="activa"]`).click();
      cy.get(`[data-cy="activa"]`).should('be.checked');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        ventanaDeMantenimiento = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', ventanaDeMantenimientoPageUrl);
    });
  });
});
