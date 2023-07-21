package com.symphony.wdk;

import com.symphony.bdk.workflow.api.v1.dto.SwadlView;
import com.symphony.bdk.workflow.engine.executor.SecretKeeper;
import com.symphony.bdk.workflow.management.WorkflowManagementService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;

@Slf4j
@Service
@RequiredArgsConstructor
public class WdkLoader {
  @Value("${wdk.loader.path}")
  private String path;
  private final WorkflowManagementService managementService;
  private final SecretKeeper secretKeeper;

  @PostConstruct
  public void init() throws IOException {
    // Deploy workflows
    log.info("Loading workflows from: {}", path);
    for (String file : listFiles(path)) {
      log.info("Loading: {}", file);
      Path swadlPath = Path.of(path + "/" + file);
      String swadl = String.join("\n", Files.readAllLines(swadlPath));
      SwadlView swadlView = SwadlView.builder().description("WDK Loader").swadl(swadl).build();
      managementService.deploy(swadlView);
    }

    // Deploy secrets
    System.getenv().entrySet().stream()
      .filter(e -> e.getKey().startsWith("WDK_"))
      .forEach(e -> {
        log.info("Loading secret: {}", e.getKey());
        String key = e.getKey().substring(4);
        byte[] secret = e.getValue().getBytes(StandardCharsets.UTF_8);
        secretKeeper.save(key, secret);
      });

    log.info("Loader complete");
  }

  private Set<String> listFiles(String path) {
    File[] files = new File(path).listFiles();
    return Stream.of(Objects.requireNonNull(files))
        .filter(file -> !file.isDirectory())
        .map(File::getName)
        .collect(Collectors.toSet());
  }
}
