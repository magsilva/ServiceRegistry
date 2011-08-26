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

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ironiacorp.network.InterfaceDiscoverer;
import com.ironiacorp.network.protocol.slp.SLPServiceRequestMessage;
import com.ironiacorp.network.protocol.slp.SLPServiceType;
import com.ironiacorp.network.tool.listener.BroadcastListener;
import com.ironiacorp.patterns.observer.Change;
import com.ironiacorp.patterns.observer.ChangeSet;
import com.ironiacorp.patterns.observer.ObjectChange;
import com.ironiacorp.patterns.observer.ObservationSubject;
import com.ironiacorp.patterns.observer.Observer;
import com.ironiacorp.registry.services.CmapService;


public class ServiceRegistry implements Observer
{
	private static Logger log = LoggerFactory.getLogger(ServiceRegistry.class);
	
	private List<BroadcastListener> listeners;
	
	private ConcurrentHashMap<URL, Service> services;
	
	private Set<Integer> ports;
	
	private int bufferSize;
	
	public ServiceRegistry()
	{
		listeners = new ArrayList<BroadcastListener>();
		services = new ConcurrentHashMap<URL, Service>(10, 0.2f, 1);
		ports = new TreeSet<Integer>();
		guessBufferSize();
	}
	
	public void addPort(int port)
	{
		ports.add(port);
	}
	
	public void removePort(int port)
	{
		ports.remove(port);
	}
	
	private void guessBufferSize()
	{
		InterfaceDiscoverer id = new InterfaceDiscoverer();
		Map<NetworkInterface, Set<InetAddress>> interfaces;
		Iterator<NetworkInterface> i;
		
		bufferSize = 0;
		id.setIpv4(true);
		id.setIpv6(true);
		id.setLocalhost(true);
		interfaces = id.discover();
		i = interfaces.keySet().iterator();
		while (i.hasNext()) {
			NetworkInterface nic = i.next();
			try {
				int nicMTU = nic.getMTU();
				if (bufferSize < nicMTU) {
					bufferSize = nicMTU;
				}
			} catch (Exception e) {}
		}
	}
	
	public synchronized void start()
	{
		if (listeners.size() != 0) {
			throw new UnsupportedOperationException("Service has already been started");
		}
		
		Iterator<Integer> i = ports.iterator();
		while (i.hasNext()) {
			int port = i.next();
			BroadcastListener listener = new BroadcastListener();
			listener.setPort(port);
			listener.addObserver(this);
			listener.startListening();
			listeners.add(listener);
		}
	}
	
	public synchronized void stop()
	{
		Iterator<BroadcastListener> i = listeners.iterator();
		while (i.hasNext()) {
			BroadcastListener listener = i.next();
			listener.stopListening();
		}
		listeners.clear();
	}

	@Override
	public void notify(ChangeSet changeSet)
	{
		ObservationSubject subject = changeSet.getSubject();
		if (subject instanceof BroadcastListener) {
			Iterator<Change> i = changeSet.iterator();
			while (i.hasNext()) {
				Change change = i.next();
				if (change instanceof ObjectChange) {
					ObjectChange objectChange = (ObjectChange) change;
					Object object = objectChange.getObject();
					if (object instanceof DatagramPacket) {
						DatagramPacket packet = (DatagramPacket) object;
						byte[] data = packet.getData();
						SLPServiceRequestMessage msg = new SLPServiceRequestMessage();
						Iterator<InetAddress> addresses;
						Iterator<SLPServiceType> serviceTypes;
						Service service = null;
						
						msg.parse(data);
						serviceTypes = msg.getServiceTypes().iterator();
						while (serviceTypes.hasNext()) {
							SLPServiceType serviceType = serviceTypes.next();
							addresses = msg.getAddresses().iterator();

							while (addresses.hasNext()) {
								InetAddress address = addresses.next();
								try {
									service = new CmapService(serviceType, address);
									break;
								} catch (Exception e) {
								}
							}
							
							if (service != null) {
								if (services.containsKey(service.getURL())) {
									log.info("Found registered service, refreshing: " + service);
									service = services.get(service.getURL());
									service.refresh();
								} else {
									log.info("Found new service: " + service);
									services.put(service.getURL(), service);
								}
								service = null;
							}
						}
					}
				}
			}
		}
	}
	
	public Iterator<Service> getServices()
	{
		return services.values().iterator();
	}
	
	public Iterator<Service> getServices(Class<? extends Service> serviceType)
	{
		List<Service> result = new ArrayList<Service>();
		for (Service service : services.values()) {
			if (service.getClass().equals(serviceType)) {
				result.add(service);
			}
		}
		
		return result.iterator();
	}
}
