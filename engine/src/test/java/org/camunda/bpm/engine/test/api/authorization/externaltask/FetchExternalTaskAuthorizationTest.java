/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.engine.test.api.authorization.externaltask;

import static org.camunda.bpm.engine.authorization.Authorization.ANY;
import static org.camunda.bpm.engine.authorization.Permissions.*;
import static org.camunda.bpm.engine.authorization.Resources.*;
import static org.junit.Assert.assertEquals;

import java.security.Permission;
import java.util.List;

import org.camunda.bpm.engine.authorization.Permissions;
import org.camunda.bpm.engine.externaltask.LockedExternalTask;
import org.camunda.bpm.engine.test.api.authorization.AuthorizationTest;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Thorben Lindhauer
 *
 */
public class FetchExternalTaskAuthorizationTest extends AuthorizationTest {

  public static final String WORKER_ID = "workerId";
  public static final long LOCK_TIME = 10000L;

  protected String instance1Id;
  protected String instance2Id;

  @Before
  public void setUp() throws Exception {
    testRule.deploy(
        "org/camunda/bpm/engine/test/api/externaltask/oneExternalTaskProcess.bpmn20.xml",
        "org/camunda/bpm/engine/test/api/externaltask/twoExternalTaskProcess.bpmn20.xml");

    instance1Id = startProcessInstanceByKey("oneExternalTaskProcess").getId();
    instance2Id = startProcessInstanceByKey("twoExternalTaskProcess").getId();
    super.setUp();
  }

  @Test
  public void testFetchWithoutAuthorization() {
    System.out.println("Test1: Without Authorization, external Task count: " + externalTaskService.createExternalTaskQuery().count());

    // when
    List<LockedExternalTask> tasks = externalTaskService.fetchAndLock(5, WORKER_ID)
      .topic("externalTaskTopic", LOCK_TIME)
      .execute();

    // then
    assertEquals(0, tasks.size());
  }



  @Test
  public void testFetchWithReadOnProcessInstance() {
    // given
    createGrantAuthorization(PROCESS_INSTANCE, instance1Id, userId, READ);
    System.out.println("Test2: With READ permission on Process instance, external Task count: " + externalTaskService.createExternalTaskQuery().count());


    // when
    List<LockedExternalTask> tasks = externalTaskService.fetchAndLock(5, WORKER_ID)
      .topic("externalTaskTopic", LOCK_TIME)
      .execute();

    // then
    assertEquals(0, tasks.size());
  }


  @Test
  public void testFetchWithUpdateOnProcessInstance() {
    // given
    createGrantAuthorization(PROCESS_INSTANCE, instance1Id, userId, UPDATE);
    System.out.println("Test3: With UPDATE permission on Process instance, external Task count: " + externalTaskService.createExternalTaskQuery().count());


    // when
    List<LockedExternalTask> tasks = externalTaskService.fetchAndLock(5, WORKER_ID)
      .topic("externalTaskTopic", LOCK_TIME)
      .execute();

    // then
    assertEquals(0, tasks.size());
  }

  @Test
  public void testFetchWithReadAndUpdateOnProcessInstance() {
    // given
    createGrantAuthorization(PROCESS_INSTANCE, instance1Id, userId, READ, UPDATE);
    System.out.println("Test4: With READ and UPDATE permission on Process instance, external Task count: " + externalTaskService.createExternalTaskQuery().count());


    // when
    List<LockedExternalTask> tasks = externalTaskService.fetchAndLock(5, WORKER_ID)
      .topic("externalTaskTopic", LOCK_TIME)
      .execute();

    // then
    assertEquals(1, tasks.size());
    assertEquals(instance1Id, tasks.get(0).getProcessInstanceId());

  }


  @Test
  public void testFetchWithReadInstanceOnProcessDefinition() {

    // given
    createGrantAuthorization(PROCESS_DEFINITION, "oneExternalTaskProcess", userId, READ_INSTANCE);
    System.out.println("Test5: With READ_INSTANCE permission on Process definition, external Task count: " + externalTaskService.createExternalTaskQuery().count());

    // when
    List<LockedExternalTask> tasks = externalTaskService.fetchAndLock(5, WORKER_ID)
      .topic("externalTaskTopic", LOCK_TIME)
      .execute();

    // then
    assertEquals(0, tasks.size());

  }

  @Test
  public void testFetchWithUpdateInstanceOnProcessDefinition() {
    // given
    createGrantAuthorization(PROCESS_DEFINITION, "oneExternalTaskProcess", userId, UPDATE_INSTANCE);
    System.out.println("Test6: With UPDATE_INSTANCE permission on Process definition, external Task count: " + externalTaskService.createExternalTaskQuery().count());

    // when
    List<LockedExternalTask> tasks = externalTaskService.fetchAndLock(5, WORKER_ID)
      .topic("externalTaskTopic", LOCK_TIME)
      .execute();

    // then
    assertEquals(0, tasks.size());
  }




  @Test
  public void testFetchWithReadAndUpdateInstanceOnProcessDefinition() {
    // given
    createGrantAuthorization(PROCESS_DEFINITION, "oneExternalTaskProcess", userId, READ_INSTANCE, UPDATE_INSTANCE);
    System.out.println("Test7: With UPDATE_INSTANCE and READ_INSTANCE permission on Process definition, external Task count: " + externalTaskService.createExternalTaskQuery().count());


    // when
    List<LockedExternalTask> tasks = externalTaskService.fetchAndLock(5, WORKER_ID)
      .topic("externalTaskTopic", LOCK_TIME)
      .execute();

    // then
    assertEquals(1, tasks.size());
    assertEquals(instance1Id, tasks.get(0).getProcessInstanceId());
  }

  @Test
  public void testFetchWithReadOnProcessInstanceAndUpdateInstanceOnProcessDefinition() {
    // given
    createGrantAuthorization(PROCESS_INSTANCE, instance1Id, userId, READ);
    createGrantAuthorization(PROCESS_DEFINITION, "oneExternalTaskProcess", userId, UPDATE_INSTANCE);
    System.out.println("Test8: With UPDATE_INSTANCE permission on Process definition adn READ permission on PROCESS_INSTANCE, external Task count: " + externalTaskService.createExternalTaskQuery().count());


    // when
    List<LockedExternalTask> tasks = externalTaskService.fetchAndLock(5, WORKER_ID)
      .topic("externalTaskTopic", LOCK_TIME)
      .execute();

    // then
    assertEquals(1, tasks.size());
    assertEquals(instance1Id, tasks.get(0).getProcessInstanceId());
  }

  @Test
  public void testFetchWithUpdateOnProcessInstanceAndReadInstanceOnProcessDefinition() {
    // given
    createGrantAuthorization(PROCESS_INSTANCE, instance1Id, userId, UPDATE);
    createGrantAuthorization(PROCESS_DEFINITION, "oneExternalTaskProcess", userId, READ_INSTANCE);
    System.out.println("Test9: With READ_INSTANCE permission on Process definition and UPDATE permission on Process instance, external Task count: " + externalTaskService.createExternalTaskQuery().count());


    // when
    List<LockedExternalTask> tasks = externalTaskService.fetchAndLock(5, WORKER_ID)
      .topic("externalTaskTopic", LOCK_TIME)
      .execute();

    // then
    assertEquals(1, tasks.size());
    assertEquals(instance1Id, tasks.get(0).getProcessInstanceId());
  }

  @Test
  public void testFetchWithReadAndUpdateOnAnyProcessInstance() {
    // given
    createGrantAuthorization(PROCESS_INSTANCE, ANY, userId, READ, UPDATE);
    System.out.println("Test10: With READ and UPDATE permission on ANY Process instance, external Task count: " + externalTaskService.createExternalTaskQuery().count());


    // when
    List<LockedExternalTask> tasks = externalTaskService.fetchAndLock(5, WORKER_ID)
      .topic("externalTaskTopic", LOCK_TIME)
      .execute();

    // then
    assertEquals(2, tasks.size());
  }

  @Test
  public void testFetchWithMultipleMatchingAuthorizations() {
    // given
    createGrantAuthorization(PROCESS_INSTANCE, ANY, userId, READ, UPDATE);
    createGrantAuthorization(PROCESS_INSTANCE, instance1Id, userId, READ, UPDATE);
    System.out.println("Test11: With READ and UPDATE permission on ANY Process instance and READ and UPDATE on process instance: multi matching, external Task count: " + externalTaskService.createExternalTaskQuery().count());


    // when
    List<LockedExternalTask> tasks = externalTaskService.fetchAndLock(5, WORKER_ID)
      .topic("externalTaskTopic", LOCK_TIME)
      .execute();

    // then
    assertEquals(2, tasks.size());
  }

  @Test
  public void testQueryWithReadAndUpdateInstanceOnAnyProcessDefinition() {
    // given
    createGrantAuthorization(PROCESS_DEFINITION, ANY, userId, READ_INSTANCE, UPDATE_INSTANCE);
    System.out.println("Test12: With READ_INSTANCE and UPDATE_INSTANCE permission on ANY Process Definition, external Task count: " + externalTaskService.createExternalTaskQuery().count());


    // when
    List<LockedExternalTask> tasks = externalTaskService.fetchAndLock(5, WORKER_ID)
      .topic("externalTaskTopic", LOCK_TIME)
      .execute();

    // then
    assertEquals(2, tasks.size());
  }

  @Test
  public void testQueryWithReadProcessInstanceAndUpdateInstanceOnAnyProcessDefinition() {
    // given
    createGrantAuthorization(PROCESS_DEFINITION, ANY, userId, UPDATE_INSTANCE);
    createGrantAuthorization(PROCESS_INSTANCE, instance1Id, userId, READ);
    System.out.println("Test13: With READ permission on Process instance and UPDATE_INSTANCE on any process definition, external Task count: " + externalTaskService.createExternalTaskQuery().count());


    // when
    List<LockedExternalTask> tasks = externalTaskService.fetchAndLock(5, WORKER_ID)
      .topic("externalTaskTopic", LOCK_TIME)
      .execute();

    // then
    assertEquals(1, tasks.size());
    assertEquals(instance1Id, tasks.get(0).getProcessInstanceId());
  }

  @Test
  public void testFetchWithReadProcessInstanceOnExternalTask(){
    // given
    createGrantAuthorization(EXTERNAL_TASK, "externalTaskTopic", userId, READ_INSTANCE);
    System.out.println("Test14: With READ_INSTANCE permission on External Task, external Task count: " + externalTaskService.createExternalTaskQuery().count());

    // when
    List<LockedExternalTask> tasks = externalTaskService.fetchAndLock(5, WORKER_ID)
            .topic("externalTaskTopic", LOCK_TIME)
            .execute();

    // then
    assertEquals(0, tasks.size());
  }

  @Test
  public void testFetchWithUpdateProcessInstanceOnExternalTask(){
    // given
    createGrantAuthorization(EXTERNAL_TASK, "oneExternalTaskProcess", userId, UPDATE_INSTANCE);
    System.out.println("Test14: With UPDATE_INSTANCE permission on External Task, external Task count: " + externalTaskService.createExternalTaskQuery().count());

    // when
    List<LockedExternalTask> tasks = externalTaskService.fetchAndLock(5, WORKER_ID)
            .topic("externalTaskTopic", LOCK_TIME)
            .execute();

    // then
    assertEquals(0, tasks.size());
  }

  @Test
  public void testFetchWithUpdateAndReadProcessInstanceOnExternalTask(){
    // given
    createGrantAuthorization(EXTERNAL_TASK, "oneExternalTaskProcess", userId, READ_INSTANCE,UPDATE_INSTANCE);
    System.out.println("Test14: With UPDATE_INSTANCE and READ_INSTANCE permission on External Task, external Task count: " + externalTaskService.createExternalTaskQuery().count());

    // when
    List<LockedExternalTask> tasks = externalTaskService.fetchAndLock(5, WORKER_ID)
            .topic("externalTaskTopic", LOCK_TIME)
            .execute();

    // then
    assertEquals(0, tasks.size());
  }

}
