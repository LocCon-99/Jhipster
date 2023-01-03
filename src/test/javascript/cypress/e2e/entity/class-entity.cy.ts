import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('ClassEntity e2e test', () => {
  const classEntityPageUrl = '/class-entity';
  const classEntityPageUrlPattern = new RegExp('/class-entity(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const classEntitySample = {};

  let classEntity;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/class-entities+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/class-entities').as('postEntityRequest');
    cy.intercept('DELETE', '/api/class-entities/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (classEntity) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/class-entities/${classEntity.id}`,
      }).then(() => {
        classEntity = undefined;
      });
    }
  });

  it('ClassEntities menu should load ClassEntities page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('class-entity');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ClassEntity').should('exist');
    cy.url().should('match', classEntityPageUrlPattern);
  });

  describe('ClassEntity page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(classEntityPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ClassEntity page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/class-entity/new$'));
        cy.getEntityCreateUpdateHeading('ClassEntity');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', classEntityPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/class-entities',
          body: classEntitySample,
        }).then(({ body }) => {
          classEntity = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/class-entities+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/class-entities?page=0&size=20>; rel="last",<http://localhost/api/class-entities?page=0&size=20>; rel="first"',
              },
              body: [classEntity],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(classEntityPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ClassEntity page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('classEntity');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', classEntityPageUrlPattern);
      });

      it('edit button click should load edit ClassEntity page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ClassEntity');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', classEntityPageUrlPattern);
      });

      it('edit button click should load edit ClassEntity page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ClassEntity');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', classEntityPageUrlPattern);
      });

      it('last delete button click should delete instance of ClassEntity', () => {
        cy.intercept('GET', '/api/class-entities/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('classEntity').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', classEntityPageUrlPattern);

        classEntity = undefined;
      });
    });
  });

  describe('new ClassEntity page', () => {
    beforeEach(() => {
      cy.visit(`${classEntityPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ClassEntity');
    });

    it('should create an instance of ClassEntity', () => {
      cy.get(`[data-cy="classId"]`).type('32152').should('have.value', '32152');

      cy.get(`[data-cy="name"]`).type('Directives').should('have.value', 'Directives');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        classEntity = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', classEntityPageUrlPattern);
    });
  });
});
