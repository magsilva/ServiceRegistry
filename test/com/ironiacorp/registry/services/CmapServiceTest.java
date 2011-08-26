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

package com.ironiacorp.registry.services;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.URL;

import org.junit.Test;

import com.ironiacorp.network.protocol.slp.SLPServiceType;

public class CmapServiceTest
{
	@Test
	public void testCmapServiceSLPServiceTypeInetAddress() throws Exception
	{
		URL endPoint = new URL("http://10.6.208.1:10888/services/CmapWebService");
		SLPServiceType serviceType = new SLPServiceType("cmapV3");
		CmapService service = new CmapService(serviceType, InetAddress.getByName("10.6.208.1"));
		assertEquals(endPoint, service.getURL());
	}
}
