/*
 * Copyright (c) 2012-2017 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.api.git.shared;

import org.eclipse.che.dto.shared.DTO;

/** @author Mykola Morhun */
@DTO
public interface RepositoryDeletedEventDto {
  /** Name of project in which git repository was deleted. */
  String getProjectName();

  void setProjectName(String projectName);

  RepositoryDeletedEventDto withProjectName(String projectName);
}