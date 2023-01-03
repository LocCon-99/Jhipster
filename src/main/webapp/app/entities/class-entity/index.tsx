import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ClassEntity from './class-entity';
import ClassEntityDetail from './class-entity-detail';
import ClassEntityUpdate from './class-entity-update';
import ClassEntityDeleteDialog from './class-entity-delete-dialog';

const ClassEntityRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ClassEntity />} />
    <Route path="new" element={<ClassEntityUpdate />} />
    <Route path=":id">
      <Route index element={<ClassEntityDetail />} />
      <Route path="edit" element={<ClassEntityUpdate />} />
      <Route path="delete" element={<ClassEntityDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ClassEntityRoutes;
