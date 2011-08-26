/*
Copyright (C) 2011 Marco Aurélio Graciotto Silva <magsilva@ironiacorp.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.ironiacorp.registry;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

import com.ironiacorp.registry.services.CmapService;

public class ServiceRegistryTest
{
	private ServiceRegistry registry;
	
	@Test
	public void testStart() throws Exception
	{
		registry = new ServiceRegistry();
		registry.addPort(4747);
		registry.start();
		while (true) {
			Thread.sleep(2000);
			Iterator<Service> services = registry.getServices(CmapService.class);
			while (services.hasNext()) {
				System.out.println(services.next().toString());
			}
		}
	}

}
