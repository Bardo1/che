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
package org.eclipse.che.plugin.testing.ide.model.event;

import org.eclipse.che.plugin.testing.ide.messages.TestSuiteFinished;

/** Event which informs about finishing test suite. */
public class TestSuiteFinishedEvent extends TestNodeEvent {
  public TestSuiteFinishedEvent(TestSuiteFinished message) {
    super(getNodeId(message), message.getSuiteName());
  }
}
