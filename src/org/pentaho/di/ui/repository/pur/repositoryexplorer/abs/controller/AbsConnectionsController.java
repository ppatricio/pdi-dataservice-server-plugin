/*!
* The Pentaho proprietary code is licensed under the terms and conditions
* of the software license agreement entered into between the entity licensing
* such code and Pentaho Corporation.
*
* This software costs money - it is not free
*
* Copyright 2002 - 2013 Pentaho Corporation.  All rights reserved.
*/

package org.pentaho.di.ui.repository.pur.repositoryexplorer.abs.controller;

import java.util.List;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.ui.repository.pur.services.IAbsSecurityProvider;
import org.pentaho.di.ui.repository.repositoryexplorer.controllers.ConnectionsController;
import org.pentaho.di.ui.repository.repositoryexplorer.model.UIDatabaseConnection;

/**
 * This class acts as a controller in the Connections Repository Explorer tab, for managing the ACLs of 
 * each database connection.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class AbsConnectionsController extends ConnectionsController implements java.io.Serializable {

  private static final long serialVersionUID = 9193044362018565483L; /* EESOURCE: UPDATE SERIALVERUID */
  IAbsSecurityProvider service;
  boolean isAllowed = false;
  
  @Override
  protected boolean doLazyInit() {
    boolean superSucceeded = super.doLazyInit();
    if (!superSucceeded) {
      return false;
    }
    try {
      if(repository.hasService(IAbsSecurityProvider.class)) {
        service = (IAbsSecurityProvider) repository.getService(IAbsSecurityProvider.class);
        setAllowed(allowedActionsContains(service, IAbsSecurityProvider.CREATE_CONTENT_ACTION));
      }
    } catch (KettleException e) {
      throw new RuntimeException(e);
    }
    return true;
  }

  public boolean isAllowed() {
    return isAllowed;
  }

  public void setAllowed(boolean isAllowed) {
    this.isAllowed = isAllowed;
    this.firePropertyChange("allowed", null, isAllowed);
  }

  @Override
  public void setSelectedConnections(List<UIDatabaseConnection> connections) {
      if(isAllowed) {
        super.setSelectedConnections(connections);
      } else {
        enableButtons(false, false, false);
      }
  }
  
  private boolean allowedActionsContains(IAbsSecurityProvider service, String action) throws KettleException {
    List<String> allowedActions = service.getAllowedActions(IAbsSecurityProvider.NAMESPACE);
    for (String actionName : allowedActions) {
      if (action != null && action.equals(actionName)) {
        return true;
      }
    }
    return false;
  }

}
