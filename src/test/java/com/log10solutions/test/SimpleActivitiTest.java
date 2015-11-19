package com.log10solutions.test;

import static org.junit.Assert.assertNotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;

import com.jayway.awaitility.Awaitility;

public class SimpleActivitiTest {

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule();

	@Test
	@Deployment(resources = { "com/log10solutions/test/SimpleAsync.bpmn" })
	public void testSimpleAsync() {
		ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("simpleAsync");
		assertNotNull(processInstance);

		final ProcessInstance finalInstance = processInstance;
		Awaitility.await().pollDelay(5, TimeUnit.SECONDS).atMost(15, TimeUnit.SECONDS).pollInterval(1, TimeUnit.SECONDS)
				.until(new Callable<Boolean>() {
					public Boolean call() throws Exception {
						HistoricProcessInstance history = activitiRule.getHistoryService()
								.createHistoricProcessInstanceQuery().processInstanceId(finalInstance.getId())
								.singleResult();

						if (history.getEndTime() != null) {
							return true;
						}
						return false;
					}
				});
	}
}
