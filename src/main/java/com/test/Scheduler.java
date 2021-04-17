package com.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.ClientBuilder;
import io.micrometer.core.instrument.MeterRegistry;

@Component
public class Scheduler {
  private static Logger logger = LoggerFactory.getLogger(Scheduler.class);

  private final AtomicInteger myGauge;

  public Scheduler(MeterRegistry meterRegistry) {
    this.myGauge = meterRegistry.gauge("gauge-for-counting-number-of-pods", new AtomicInteger(0));
  }

  @Scheduled(fixedRateString = "500", initialDelayString = "0")
  public void schedulingTask() {
    myGauge.set(getAllPods().size());
  }

  private List<V1Pod> getAllPods() {
    try {
      ApiClient client = ClientBuilder.cluster().build();

      Configuration.setDefaultApiClient(client);
      CoreV1Api api = new CoreV1Api(client);

      /*
       * V1NamespaceList listNamespace = api.listNamespace(null, null, null, null,
       * null, 0, null, null, 5000, Boolean.FALSE);
       * 
       * List<String> nsList = listNamespace.getItems().stream().map(v1Namespace ->
       * v1Namespace.getMetadata().getName()) .collect(Collectors.toList());
       * List<V1Pod> pods = new ArrayList<>(); for (String ns : nsList) {
       * 
       * logger.info("Namespace-> " + ns);
       */
      List<V1Pod> pods = new ArrayList<>();
      V1PodList list = api.listNamespacedPod("default", null, null, null, null, null, 0, null, null, 5000,
          Boolean.FALSE);
      if (list != null && CollectionUtils.isNotEmpty(list.getItems())) {
        pods.addAll(list.getItems());
        logger.info("Pods Size  " + pods.size());
      }

      // }
      return pods;

    } catch (ApiException e) {
      ApiException e1 = e;
      logger.info(e1.getResponseBody());

      throw new IllegalStateException("Exception in finding podCount", e);
    } catch (IOException e) {
      throw new IllegalStateException("Exception in finding podCount", e);
    }

  }

}
