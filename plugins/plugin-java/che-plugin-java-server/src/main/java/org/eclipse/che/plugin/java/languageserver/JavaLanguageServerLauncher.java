/*
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.plugin.java.languageserver;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ProcessBuilder.Redirect;
import java.lang.reflect.Proxy;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.che.api.languageserver.LanguageServerConfig;
import org.eclipse.che.api.languageserver.ProcessCommunicationProvider;
import org.eclipse.che.api.languageserver.service.FileContentAccess;
import org.eclipse.che.api.languageserver.util.DynamicWrapper;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Evgen Vidolob
 * @author Anatolii Bazko
 * @author Thomas Mäder
 */
@Singleton
public class JavaLanguageServerLauncher implements LanguageServerConfig {
  private static final Logger LOG = LoggerFactory.getLogger(JavaLanguageServerLauncher.class);

  private final Path launchScript;
  private ProcessorJsonRpcCommunication processorJsonRpcCommunication;

  @Inject
  public JavaLanguageServerLauncher(ProcessorJsonRpcCommunication processorJsonRpcCommunication) {
    this.processorJsonRpcCommunication = processorJsonRpcCommunication;
    launchScript = Paths.get(System.getenv("HOME"), "che/ls-java/launch.sh");
  }

  public void sendStatusReport(StatusReport report) {
    LOG.info("{}: {}", report.getType(), report.getMessage());
  }

  /**
   * The show message notification is sent from a server to a client to ask the client to display a
   * particular message in the user interface.
   *
   * @param report information about report
   */
  public void sendProgressReport(ProgressReport report) {
    processorJsonRpcCommunication.sendProgressNotification(report);
  }

  @Override
  public RegexProvider getRegexpProvider() {
    return new RegexProvider() {

      @Override
      public Map<String, String> getLanguageRegexes() {
        HashMap<String, String> regex = new HashMap<>();
        regex.put("java", "(^jdt://.*|^chelib://.*|.*\\.java|.*\\.class)");
        return regex;
      }

      @Override
      public Set<String> getFileWatchPatterns() {
        Set<String> regex = new HashSet<>();
        regex.add("glob:**/*.java");
        regex.add("glob:**/pom.xml");
        regex.add("glob:**/*.gradle");
        regex.add("glob:**/.project");
        regex.add("glob:**/.classpath");
        regex.add("glob:**/settings/*.prefs");

        return regex;
      }
    };
  }

  @Override
  public CommunicationProvider getCommunicationProvider() {
    ProcessBuilder processBuilder = new ProcessBuilder(launchScript.toString());
    processBuilder.directory(launchScript.getParent().toFile());
    processBuilder.redirectInput(ProcessBuilder.Redirect.PIPE);
    processBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);
    processBuilder.redirectError(Redirect.INHERIT);

    return new ProcessCommunicationProvider(processBuilder, "Che-LS-JDT");
  }

  @Override
  public InstallerStatusProvider getInstallerStatusProvider() {
    return new InstallerStatusProvider() {
      @Override
      public boolean isSuccessfullyInstalled() {
        return launchScript.toFile().exists();
      }

      @Override
      public String getCause() {
        return isSuccessfullyInstalled() ? null : "Launch script file does not exist";
      }
    };
  }

  @Override
  public InstanceProvider getInstanceProvider() {
    return new InstanceProvider() {

      @Override
      public LanguageServer get(LanguageClient client, InputStream in, OutputStream out) {
        Object javaLangClient =
            Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[] {LanguageClient.class, JavaLanguageClient.class},
                new DynamicWrapper(JavaLanguageServerLauncher.this, client));

        Launcher<JavaLanguageServer> launcher =
            Launcher.createLauncher(javaLangClient, JavaLanguageServer.class, in, out);
        launcher.startListening();
        JavaLanguageServer proxy = launcher.getRemoteProxy();
        LanguageServer wrapped =
            (LanguageServer)
                Proxy.newProxyInstance(
                    getClass().getClassLoader(),
                    new Class[] {LanguageServer.class, FileContentAccess.class},
                    new DynamicWrapper(new JavaLSWrapper(proxy), proxy));
        return wrapped;
      }
    };
  }
}