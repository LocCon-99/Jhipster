import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './class-entity.reducer';

export const ClassEntityDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const classEntityEntity = useAppSelector(state => state.classEntity.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="classEntityDetailsHeading">Class Entity</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{classEntityEntity.id}</dd>
          <dt>
            <span id="classId">Class Id</span>
          </dt>
          <dd>{classEntityEntity.classId}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{classEntityEntity.name}</dd>
        </dl>
        <Button tag={Link} to="/class-entity" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/class-entity/${classEntityEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default ClassEntityDetail;
