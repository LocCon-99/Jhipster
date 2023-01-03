import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IClassEntity } from 'app/shared/model/class-entity.model';
import { getEntity, updateEntity, createEntity, reset } from './class-entity.reducer';

export const ClassEntityUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const classEntityEntity = useAppSelector(state => state.classEntity.entity);
  const loading = useAppSelector(state => state.classEntity.loading);
  const updating = useAppSelector(state => state.classEntity.updating);
  const updateSuccess = useAppSelector(state => state.classEntity.updateSuccess);

  const handleClose = () => {
    navigate('/class-entity' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...classEntityEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...classEntityEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="demoJhipsterApp.classEntity.home.createOrEditLabel" data-cy="ClassEntityCreateUpdateHeading">
            Create or edit a Class Entity
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="class-entity-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField label="Class Id" id="class-entity-classId" name="classId" data-cy="classId" type="text" />
              <ValidatedField label="Name" id="class-entity-name" name="name" data-cy="name" type="text" />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/class-entity" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default ClassEntityUpdate;
