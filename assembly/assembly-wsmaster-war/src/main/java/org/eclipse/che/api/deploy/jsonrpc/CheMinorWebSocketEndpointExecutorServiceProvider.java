/*
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.api.deploy.jsonrpc;

import org.eclipse.che.commons.lang.execution.ExecutorServiceProvider;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.concurrent.ExecutorService;

/** * {@link ExecutorService} provider used in {@link CheMinorWebSocketEndpoint}. */
@Singleton
public class CheMinorWebSocketEndpointExecutorServiceProvider extends ExecutorServiceProvider {

  @Inject
  public CheMinorWebSocketEndpointExecutorServiceProvider(
      @Named("che.core.jsonrpc.minor_processor_core_pool_size") int corePoolSize,
      @Named("che.core.jsonrpc.minor_processor_max_pool_size") int maxPoolSize,
      @Named("che.core.jsonrpc.minor_processor_queue_capacity") int queueCapacity) {

    super(corePoolSize, maxPoolSize, queueCapacity);
  }
}
