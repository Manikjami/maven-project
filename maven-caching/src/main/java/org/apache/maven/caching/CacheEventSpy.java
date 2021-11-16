package org.apache.maven.caching;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.caching.xml.CacheConfig;
import org.apache.maven.caching.xml.CacheConfigFactory;
import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.MavenSession;

/**
 * Triggers cache report generation on build completion
 */
@Singleton
@Named
public class CacheEventSpy extends AbstractEventSpy
{
    private final CacheConfigFactory cacheConfigFactory;
    private final CacheControllerFactory cacheControllerFactory;

    @Inject
    public CacheEventSpy( CacheConfigFactory cacheConfigFactory, CacheControllerFactory cacheControllerFactory )
    {
        this.cacheConfigFactory = cacheConfigFactory;
        this.cacheControllerFactory = cacheControllerFactory;
    }

    @Override
    public void onEvent( Object event ) throws Exception
    {
        if ( event instanceof ExecutionEvent )
        {
            ExecutionEvent executionEvent = (ExecutionEvent) event;
            if ( executionEvent.getType() == ExecutionEvent.Type.SessionEnded )
            {
                MavenSession session = executionEvent.getSession();
                CacheConfig cacheConfig = cacheConfigFactory.getCacheConfig( session );
                if ( cacheConfig.isEnabled() )
                {
                    CacheController cacheController = cacheControllerFactory.getCacheContoller( session );
                    cacheController.saveCacheReport( executionEvent.getSession() );
                }
            }
        }
    }
}