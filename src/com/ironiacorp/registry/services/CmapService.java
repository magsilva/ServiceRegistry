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

import java.net.InetAddress;
import java.net.URL;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import com.ironiacorp.miner.acquisition.cmaptools.IHMCCmap;
import com.ironiacorp.network.protocol.slp.SLPServiceType;

public class CmapService extends BaseService
{
	public static final String DEFAULT_SERVICE_TYPE = "cmapV3";
	
	public static final int CMAPSERVER_DEFAULT_NATIVE_PORT = 4447;
	
	public int port = CMAPSERVER_DEFAULT_NATIVE_PORT;
	
	public static final int CMAPSERVER_DEFAULT_WS_PORT = 8080;
	
	public static final int[] CMAPSERVER_ALTERNATIVE_WS_PORT = { 18080, 10888, 8888 };
	
	public Set<Integer> wsPorts;
	
	public CmapService()
	{
		wsPorts = new LinkedHashSet<Integer>();
		wsPorts.add(CMAPSERVER_DEFAULT_WS_PORT);
		for (Integer p : CMAPSERVER_ALTERNATIVE_WS_PORT) {
			wsPorts.add(p);
		}
	}
	
	public CmapService(SLPServiceType serviceType, InetAddress address)
	{
		this();

		if (! serviceType.getAbstractType().equals(DEFAULT_SERVICE_TYPE)) {
			throw new IllegalArgumentException("Could not discover a Cmap service in the given address");
		}
		
		IHMCCmap cmapService = new IHMCCmap();
		for (int wsPort : wsPorts) {
			try {
				URL endPoint = new URL("http://" + address.getHostAddress() + ":" + wsPort + "/services/CmapWebService");
				cmapService.setEndpoint(endPoint);
				cmapService.getResourcesAtRoot();
				setUrl(endPoint);
				break;
			} catch (Exception e) {
			}					
		}
			
		if (url == null) {
			throw new IllegalArgumentException("Could not detect a valid Cmap service in the given address");
		}
		
		setFirstSeenDate(new Date());
		refresh();
	}
}
