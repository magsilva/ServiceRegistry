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

import java.net.URL;
import java.util.Date;

import com.ironiacorp.registry.Service;

public class BaseService implements Service 
{
	protected String name;
	
	protected URL url;

	protected Date firstSeenDate;
	
	protected Date lastSeenDate;

	@Override
	public URL getURL()
	{
		return url;
	}

	public void setUrl(URL url)
	{
		this.url = url;
	}

	
	@Override
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public Date getFirstSeenDate()
	{
		return firstSeenDate;
	}

	public void setFirstSeenDate(Date firstSeenDate)
	{
		this.firstSeenDate = firstSeenDate;
	}

	
	@Override
	public Date getLastSeenDate()
	{
		return lastSeenDate;
	}

	public void setLastSeenDate(Date lastSeenDate)
	{
		this.lastSeenDate = lastSeenDate;
	}

	@Override
	public void refresh()
	{
		lastSeenDate = new Date();
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" at ");
		sb.append(url.toString());
		sb.append(" since ");
		sb.append(getFirstSeenDate());
		sb.append(" (last seen on ");
		sb.append(getLastSeenDate());
		sb.append(")");
		
		return sb.toString();
	}
}
